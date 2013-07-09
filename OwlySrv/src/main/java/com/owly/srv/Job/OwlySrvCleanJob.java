/*
* Copyright 2013- Antonio Menendez Lopez (tonimenen)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.owly.srv.Job;



import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.owly.srv.RemoteBasicStatMongoDAOImpl;
import com.owly.srv.RemoteServerMongoDAOImpl;
import com.owly.srv.StatsServer;
import com.owly.srv.StatsServerMongoDAOImpl;



/**
 * This class is executed by QuartzScheduler : and will execute periodically 
 * cleaning of the system
 *  
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class OwlySrvCleanJob implements Job {

	Logger logger = Logger.getLogger(OwlySrvCleanJob.class);

	public void execute(JobExecutionContext jExeCtx)
			throws JobExecutionException {
		try {
			logger.debug("Main Scheduled Job Start successfully...");

			logger.debug("Reading Context Variables related to access to Database");

			// Get values of variables saved in the context when the Appl.
			// Server is initiallized.
			String dbName = (String) jExeCtx.getScheduler().getContext()
					.get("DBNAME");
			logger.info("DBNAME is " + dbName);

			String ipServer = (String) jExeCtx.getScheduler().getContext()
					.get("IPSERVER");
			logger.info("IPSERVER is " + ipServer);

			Integer portServer = (Integer) jExeCtx.getScheduler().getContext()
					.get("PORTSERVER");
			logger.info("PORTSERVER is " + portServer);

			String typeDatabase = (String) jExeCtx.getScheduler().getContext()
					.get("DATABASETYPE");
			logger.info("DATABASETYPE is " + typeDatabase);

			logger.debug("Clean Scheduled Job End successfully...");
			
			
			if (!typeDatabase.equals("MONGODB")) {
				// So far only MongoDB is implemented
				logger.error("Only MongoDB Database is implemented so far !");
			} else {
				if (!ipServer.equals("127.0.0.1")) {
					// So far our Appl Server needs to have the mongoDB
					// installed locally.
					logger.info("IP not 127.0.0.1 --> Database is remote database !");
				} else {
					logger.info("IP is 127.0.0.1 --> Database is local database !");
				}
					// Execution of the JOb for normal stats
					CleanStats(dbName, ipServer, portServer,typeDatabase);
			}

			logger.debug("Main Scheduled Job End successfully...");

		} catch (SchedulerException e) {

			logger.error("An exception ocuured accessing Scheduler Context : "
					+ e.toString());
			logger.error("Exception ::", e);

		}

	}

	void CleanStats(String dbName, String ipServer, int portServer, String typeDatabase) {
		
		//Get date for this executin
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();

		SimpleDateFormat actualDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.debug("Actual date is " + actualDateFormat.format(nowDate));
		
		TimeZone tz = TimeZone.getDefault(); 
		int offset= tz.getOffset(new Date().getTime());
		
		logger.debug("This is my offset " + offset);
		
		/**** Connect to MongoDB ****/
		// Since 2.10.0, uses MongoClient
		try {
			MongoClient mongoClient = new MongoClient(ipServer, portServer);
			/**** Get database ****/
			logger.info("MONGODB : Start MongoClient");
			DB statsDB = mongoClient.getDB(dbName);
	
			// Object to get the stats server in database
			StatsServerMongoDAOImpl statsServerDAO = new StatsServerMongoDAOImpl(
					statsDB);
			StatsServer srv = new StatsServer();
			srv = statsServerDAO.getStatsServer(dbName, ipServer, portServer, typeDatabase);
			
			//Number of days to keep in database 
			int number_days_to_save=srv.getSavedDays();
			logger.debug("Days to save in database = "+number_days_to_save);
			
			//GEt the past date
			cal.add(Calendar.DAY_OF_MONTH, -number_days_to_save);
			cal.add(Calendar.MILLISECOND, +offset);
			Date pastDate = cal.getTime();
			
			logger.debug("Date to drop before = "+ pastDate);
			
			String StatsCollection=srv.getStatsCollect();
			logger.debug("Collection is  = "+StatsCollection);

			//Get an instance of the stadistics dao 
			RemoteBasicStatMongoDAOImpl basicStatDAO = new RemoteBasicStatMongoDAOImpl(
					statsDB, StatsCollection);
			
			//Get number of stats in databe for this condition
			long nb_stats = basicStatDAO.getNumberStatsBefore(pastDate);
			
			logger.info("Number of stats to drop = " + nb_stats);
			
			if (nb_stats > 0){
				basicStatDAO.cleanStatsbefore(pastDate);
			}

			// Close the connection to database
			logger.info("MONGODB : Closing MongoClient");
			mongoClient.close();
			
			} catch (UnknownHostException e) {
				logger.error("mongoDB Error : " + e.getMessage());
				logger.error("Exception ::", e);

				
			}

		
		
	}
	
}

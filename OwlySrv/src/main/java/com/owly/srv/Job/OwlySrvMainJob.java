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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.owly.srv.RemoteBasicStat;
import com.owly.srv.RemoteBasicStatItfImpl;
import com.owly.srv.RemoteBasicStatMongoDAOImpl;
import com.owly.srv.RemoteServer;
import com.owly.srv.RemoteServerMongoDAOImpl;
import com.owly.srv.StatsServer;
import com.owly.srv.StatsServerMongoDAOImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class is executed by QuartzScheduler : and will execute periodically all
 * stats configured in the system. Period of execution is configured in the
 * quartz properties file and the schedule.xml file
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class OwlySrvMainJob implements Job {

	Logger logger = Logger.getLogger(OwlySrvMainJob.class);
	RemoteServerMongoDAOImpl remoteServer = null;
	RemoteServer rmtServer = null;
	ArrayList<String> srvIP = new ArrayList<String>();
	ArrayList<String> listStats = new ArrayList<String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
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
					BasicStatsPerfMainJob(dbName, ipServer, portServer);
			}

			logger.debug("Main Scheduled Job End successfully...");

		} catch (SchedulerException e) {

			logger.error("An exception ocuured accessing Scheduler Context : "
					+ e.toString());
			logger.error("Exception ::", e);

		}

	}

	/**
	 * This method is executed by Scheduler Job in order to get Stadistics from
	 * a remote server, based in the information saved in Database related to
	 * Remote Server configuration.
	 * 
	 * @param dbName
	 *            is the DataBase Name
	 * @param ipServer
	 *            is the IP address for Stat Server ( normally 127.0.0.1)
	 * @param portServer
	 *            in port where is running the MongoDatabase
	 */
	void BasicStatsPerfMainJob(String dbName, String ipServer, int portServer) {

		//Get date for this executin
		GregorianCalendar currentDate = new GregorianCalendar();
		Date actualDate = currentDate.getTime();

		SimpleDateFormat actualDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.debug("Actual date is " + actualDateFormat.format(actualDate));

		
		// Class for executing tasks in the remote server
		RemoteBasicStatItfImpl remoteExec = new RemoteBasicStatItfImpl();

		/**** Connect to MongoDB ****/
		// Since 2.10.0, uses MongoClient
		try {
			MongoClient mongoClient = new MongoClient(ipServer, portServer);
			/**** Get database ****/
			logger.info("MONGODB : Start MongoClient");
			DB statsDB = mongoClient.getDB(dbName);
			// Class for managing the remote server object.
			remoteServer = new RemoteServerMongoDAOImpl(statsDB);
			// Object to get the stats server in database
			StatsServerMongoDAOImpl statsServerDAO = new StatsServerMongoDAOImpl(
					statsDB);

			// Get the name of collection where stats are going to be saved.
			String StatsCollection = statsServerDAO.getStatCollectStatsServer();

			// Check is Stat Server is enabled
			Boolean statusServer = statsServerDAO
					.getMonitoringEnabledStatsServer();

			if (statusServer) {
				// Status Server is enabled for Monitoring

				// Get the list of IP of all remote Servers
				srvIP = remoteServer.listIPRemoteServer();

				Iterator<String> it = srvIP.iterator();

				while (it.hasNext()) {

					String valueIP = it.next();
					logger.debug("Ip of server to analyze :" + valueIP);
					rmtServer = remoteServer.getRemoteServerbyIP(valueIP);
					logger.debug("Remote Server Readed = "
							+ rmtServer.toString());

					//Check if server is enabled
					boolean srvEnabled = rmtServer.isEnabled();
					
					// Check if Server is accessible
					boolean status = remoteExec.getStatusRemoteServer(valueIP,
							rmtServer.getClientPort());


					logger.debug("Status of server received = " + status);
					logger.debug("Update Status in remote Server = " + status);
					remoteServer.updateStatus(status, rmtServer);

					// If the remote server it is not accesible or it is not eanbled do not continue
					// asking for further statistics.
					if (status && srvEnabled) {

						// For each server ( IP address) get the list of Stats
						// to get.

						listStats = rmtServer.getListTypeOfStats();

						Iterator<String> it2 = listStats.iterator();
						while (it2.hasNext()) {
							String typeStat = it2.next();
							logger.debug("Executing remote Stadistics for type  = "
									+ typeStat);
							// Execute a Remote Execution to the the Stadistic
							// on the
							// remote server, based on IP, server type and type
							// of
							// stadistic.

							// Statistic received from remote server
							RemoteBasicStat basicStat = remoteExec
									.getRemoteStatistic(rmtServer.getName(),
											valueIP, rmtServer.getSrvType(),
											typeStat, rmtServer.getClientPort());
							
							if (basicStat.getTypeOfStat().equals("NOK")){
								logger.error("Metric received is not OK, not save to database");
							}else{
								//Setup the date when this metric is executed.
								basicStat.setstatServerDate(actualDate);
								// Save statistic in Database
								RemoteBasicStatMongoDAOImpl basicStatDAO = new RemoteBasicStatMongoDAOImpl(
										statsDB, StatsCollection);

								basicStatDAO.insertRemoteBasicStat(basicStat);
							}
							

						}
					} else {
						// remote server is disabled becosue not access to HTTP
						logger.info("Remote Server is not accesible for getting stadistics");

					}

				}

			} else {
				// Stadistic server is disabled for monitoring
				logger.info("Stadistics Server is disabled for Monitoring");
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

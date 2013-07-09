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
package com.owly.srv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.bson.BSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class RemoteBasicStatMongoDAOImpl implements RemoteBasicStatMongoDAO {

	DBCollection remoteStatCollection;

	private static Logger logger = Logger
			.getLogger(RemoteBasicStatMongoDAOImpl.class);

	
	public RemoteBasicStatMongoDAOImpl(DB statsDB, String CollectionName) {

		String CollectionStat = CollectionName;
		remoteStatCollection = statsDB.getCollection(CollectionStat);
	}

	public void insertRemoteBasicStat(RemoteBasicStat remoteBasicStat) {

		logger.info("MONGODB : Insert Statistic in DataBase");

		BasicDBObject obj = new BasicDBObject("StatDate",
				remoteBasicStat.getDateStatServerDate())
				.append("ServerDate", remoteBasicStat.getDateRmtServerDate())
				.append("StatType", remoteBasicStat.getTypeOfStat())
				.append("NameServer", remoteBasicStat.getNameServer())
				.append("Hostname", remoteBasicStat.getHostname())
				.append("ServerIP", remoteBasicStat.getIpServer())
				.append("ServerType", remoteBasicStat.getTypeServer())
				.append("Metrics", remoteBasicStat.getMetrics())
				.append("MetricType", remoteBasicStat.getMetricType());

		logger.info("MONGODB : Data to insert : " + obj.toString());
		remoteStatCollection.insert(obj);
		logger.info("MONGODB :Statistic inserted");

	}

	public List<String> getTypeStatFromServer(
			ShortRemoteServerId shortRemoteServer, Integer minutesToCheck) {

		logger.info("MONGODB : get info of type of stats for an specific server");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));
		
		
		List<String> typeStat = new ArrayList<String>();
		
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())		
		.append("StatDate", new BasicDBObject("$gt", beforeDate));
		
		
		typeStat = remoteStatCollection.distinct("StatType",query);
		
		logger.info("MONGODB : List obtained");
		return typeStat;
				
	}
	
	
	public List<String> getTypeStatFromServer(
			ShortRemoteServerId shortRemoteServer, Date startDate, Date endDate) {
		logger.info("MONGODB : get info of type of stats for an specific server");
		logger.info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		List<String> typeStat = new ArrayList<String>();
		
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())		
		.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
		
		
		typeStat = remoteStatCollection.distinct("StatType",query);
		
		logger.info("MONGODB : List obtained");
		return typeStat;

	}
	
	
	public List<String> getTypeStatFromServer(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			Integer minutesToCheck) {
		
		logger.info("MONGODB : get info of type of stats for an specific server");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));
		
		
		List<String> typeStat = new ArrayList<String>();

		//Varaibles to generat a query with or clause
		BasicDBObject query = new BasicDBObject();
		BasicDBList or = new BasicDBList();
			
		//Check all severs that arrive to the method and generate the or clause
		Iterator<ShortRemoteServerId> iterator = listShortRemoteServerId.iterator();
		while (iterator.hasNext()) {
			ShortRemoteServerId shortRmt = iterator.next();
			BasicDBObject clause = new BasicDBObject("ServerIP", shortRmt.getNodeIPAddress()).append("NameServer", shortRmt.getName());
			or.add(clause);
		}
		query = new BasicDBObject("$or", or)
			.append("StatDate", new BasicDBObject("$gt", beforeDate));
			
		logger.info("MONGODB : Get type of stats in the database for this clause"+query.toString());
		
		typeStat = remoteStatCollection.distinct("StatType",query);
		
		logger.info("MONGODB : List obtained");
		return typeStat;
	}

	public List<String> getTypeStatFromServer(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			Date startDate, Date endDate) {
		logger.info("MONGODB : get info of type of stats for an specific server");
		logger.info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		List<String> typeStat = new ArrayList<String>();
		
		//Varaibles to generat a query with or clause
		BasicDBObject query = new BasicDBObject();
		BasicDBList or = new BasicDBList();
			
		//Check all severs that arrive to the method and generate the or clause
		Iterator<ShortRemoteServerId> iterator = listShortRemoteServerId.iterator();
		while (iterator.hasNext()) {
			ShortRemoteServerId shortRmt = iterator.next();
			BasicDBObject clause = new BasicDBObject("ServerIP", shortRmt.getNodeIPAddress()).append("NameServer", shortRmt.getName());
			or.add(clause);
		}
		query = new BasicDBObject("$or", or)
		.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
		
		typeStat = remoteStatCollection.distinct("StatType",query);
		
		logger.info("MONGODB : List obtained");
		return typeStat;

	}
	

	public Set<String> getTypeMetricFromStat(
			ShortRemoteServerId shortRemoteServer, String typeOfStat, Integer minutesToCheck) {
		
		logger.info("MONGODB : get info of type of metrcis for an specific server and specific stat");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		//Create the query
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate));
		
		//logger.debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics", true).append(
				"_id", false);
		
		//Create the ordeby
		BasicDBObject orderBy = new BasicDBObject("StatDate", -1);

		DBObject res =  remoteStatCollection.findOne(query, obj,orderBy);

		Set<String> typeMetric = (((BSONObject) res.get("Metrics")).toMap()).keySet();
		
		//logger.debug("MONGODB : query result"+typeMetric.toString());
		logger.info("MONGODB : List obtained");
		
		return typeMetric;

	}

	public Set<String> getTypeMetricFromStat(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, Integer minutesToCheck) {
		
		logger.info("MONGODB : get info of type of metrcis for an specific server and specific stat");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		
		//Varaibles to generat a query with or clause
		BasicDBObject query = new BasicDBObject();
		BasicDBList or = new BasicDBList();
			
		//Check all severs that arrive to the method and generate the or clause
		Iterator<ShortRemoteServerId> iterator = listShortRemoteServerId.iterator();
		while (iterator.hasNext()) {
			ShortRemoteServerId shortRmt = iterator.next();
			BasicDBObject clause = new BasicDBObject("ServerIP", shortRmt.getNodeIPAddress()).append("NameServer", shortRmt.getName());
			or.add(clause);
		}
		query = new BasicDBObject("$or", or)
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate));
			
		logger.info("MONGODB : Get type of stats in the database for this clause"+query.toString());
				
		//logger.debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics", true).append(
				"_id", false);
		
		//Create the ordeby
		BasicDBObject orderBy = new BasicDBObject("StatDate", -1);

		DBObject res =  remoteStatCollection.findOne(query, obj,orderBy);

		Set<String> typeMetric = (((BSONObject) res.get("Metrics")).toMap()).keySet();
		
		//logger.debug("MONGODB : query result"+typeMetric.toString());
		logger.info("MONGODB : List obtained");
		
		return typeMetric;
	}

	
	
	public Set<String> getTypeMetricFromStat(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			Date startDate, Date endDate) {
		
			logger.info("MONGODB : get info of type of stats for an specific server");
			logger.info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
			
			//Create the query
			BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
			.append("ServerIP", shortRemoteServer.getNodeIPAddress())
			.append("StatType", typeOfStat)
			.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
			
			//logger.debug("MONGODB : query ="+query.toString());
			//Create the selector
			BasicDBObject obj = new BasicDBObject("Metrics", true).append(
					"_id", false);
			
			//Create the ordeby
			BasicDBObject orderBy = new BasicDBObject("StatDate", -1);
			
			DBObject res =  remoteStatCollection.findOne(query, obj,orderBy);
			Set<String> typeMetric = (((BSONObject) res.get("Metrics")).toMap()).keySet();

			//logger.debug("MONGODB : query result"+typeMetric.toString());
			logger.info("MONGODB : List obtained");
			
			return typeMetric;
	}
	
	public Set<String> getTypeMetricFromStat(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, Date startDate, Date endDate) {
		
		logger.info("MONGODB : get info of type of stats for an specific server");
		logger.info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		//Varaibles to generat a query with or clause
		BasicDBObject query = new BasicDBObject();
		BasicDBList or = new BasicDBList();
			
		//Check all severs that arrive to the method and generate the or clause
		Iterator<ShortRemoteServerId> iterator = listShortRemoteServerId.iterator();
		while (iterator.hasNext()) {
			ShortRemoteServerId shortRmt = iterator.next();
			BasicDBObject clause = new BasicDBObject("ServerIP", shortRmt.getNodeIPAddress()).append("NameServer", shortRmt.getName());
			or.add(clause);
		}
		query = new BasicDBObject("$or", or)
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
		
		//logger.debug("MONGODB : query ="+query.toString());
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics", true).append(
				"_id", false);
		
		//Create the ordeby
		BasicDBObject orderBy = new BasicDBObject("StatDate", -1);
		
		DBObject res =  remoteStatCollection.findOne(query, obj,orderBy);
		Set<String> typeMetric = (((BSONObject) res.get("Metrics")).toMap()).keySet();

		//logger.debug("MONGODB : query result"+typeMetric.toString());
		logger.info("MONGODB : List obtained");
		
		return typeMetric;
	}

	
	
	public ArrayList<BasicStat> getMetricDetails(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			String typeOfMetric, Integer minutesToCheck) {

		logger.info("MONGODB : Get a metric in a period of time vbased in minutes");
		
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		logger.info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		//Create a List BasicStat Object
		ArrayList<BasicStat> ListMetrics=new ArrayList<BasicStat>();
		
		//Create the query
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate).append("$lt", nowDate));
		
		logger.debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
		.append("StatDate", true).append("_id", false);
		
		logger.debug("MONGODB : selector ="+obj.toString());
		
		//Create the ordeby
		BasicDBObject orderBy = new BasicDBObject("StatDate", -1);
				
		DBCursor cursor =  remoteStatCollection.find(query, obj).sort(orderBy);

		while (cursor.hasNext()) {
			DBObject cur = cursor.next();	
			
			BasicStat basicStat = new BasicStat();
			
			//logger.debug("MONGODB : query result"+cur.toString());
			//logger.debug("MONGODB : query result"+((BSONObject)cur.get("Metrics")).get(typeOfMetric));
			basicStat.setValue((String) ((BSONObject)cur.get("Metrics")).get(typeOfMetric).toString());			
			basicStat.setDate( (Date) cur.get("StatDate"));
		
			// Add this object into the list.
			ListMetrics.add(basicStat);		
		}
		logger.debug("MONGODB : List obtained"+ListMetrics.toString());
		cursor.close();
		
		logger.info("MONGODB : List obtained");
		
		return ListMetrics;

	}
	
	
	public ArrayList<BasicStat> getMetricDetails(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			String typeOfMetric, Date startDate, Date endDate) {
		
		logger.info("MONGODB : get info of type of stats for an specific server");
		logger.info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		//Create a List BasicStat Object
				ArrayList<BasicStat> ListMetrics=new ArrayList<BasicStat>();
				
				//Create the query
				BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
				.append("ServerIP", shortRemoteServer.getNodeIPAddress())
				.append("StatType", typeOfStat)
				.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
				
				logger.debug("MONGODB : query ="+query.toString());
				
				//Create the selector
				BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
				.append("StatDate", true).append("_id", false);
				
				logger.debug("MONGODB : selector ="+obj.toString());
				
				//Create the ordeby
				BasicDBObject orderBy = new BasicDBObject("StatDate", -1);
						
				DBCursor cursor =  remoteStatCollection.find(query, obj).sort(orderBy);

				while (cursor.hasNext()) {
					DBObject cur = cursor.next();	
					
					BasicStat basicStat = new BasicStat();
					
					//logger.debug("MONGODB : query result"+cur.toString());
					//logger.debug("MONGODB : query result"+((BSONObject)cur.get("Metrics")).get(typeOfMetric));
					basicStat.setValue((String) ((BSONObject)cur.get("Metrics")).get(typeOfMetric).toString());			
					basicStat.setDate( (Date) cur.get("StatDate"));
				
					// Add this object into the list.
					ListMetrics.add(basicStat);		
				}
				logger.debug("MONGODB : List obtained"+ListMetrics.toString());
				cursor.close();
				
				logger.info("MONGODB : List obtained");
				
				return ListMetrics;

	}

	
	

	public boolean isThereMetricDetails(ShortRemoteServerId shortRemoteServer,
			String typeOfStat, String typeOfMetric, Integer minutesToCheck) {
		
		logger.info("MONGODB : Get availability of metrics in a period of time vbased in minutes");
		
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		logger.info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		//Create the query
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate).append("$lt", nowDate));
		
		logger.debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
		.append("StatDate", true).append("_id", false);
		
		logger.debug("MONGODB : selector ="+obj.toString());
		
				
		int  nb_metrics =  remoteStatCollection.find(query, obj).count();

		if (nb_metrics > 0){
			logger.info("MONGODB : Metrics exist in this period of time");
			return true;
			
		}else{
			logger.info("MONGODB : Metrics do not exist in this period of time");
			return false;
			
		}
		
	}

	public boolean isThereMetricDetails(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, String typeOfMetric, Integer minutesToCheck) {
		logger.info("MONGODB : Get availability of metrics in a period of time vbased in minutes");
		
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		logger.info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		//Varaibles to generat a query with or clause
		BasicDBObject query = new BasicDBObject();
		BasicDBList or = new BasicDBList();
			
		//Check all severs that arrive to the method and generate the or clause
		Iterator<ShortRemoteServerId> iterator = listShortRemoteServerId.iterator();
		while (iterator.hasNext()) {
			ShortRemoteServerId shortRmt = iterator.next();
			BasicDBObject clause = new BasicDBObject("ServerIP", shortRmt.getNodeIPAddress()).append("NameServer", shortRmt.getName());
			or.add(clause);
		}
		query = new BasicDBObject("$or", or)
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate).append("$lt", nowDate));
		
		logger.debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
		.append("StatDate", true).append("_id", false);
		
		logger.debug("MONGODB : selector ="+obj.toString());
		
				
		int  nb_metrics =  remoteStatCollection.find(query, obj).count();

		if (nb_metrics > 0){
			logger.info("MONGODB : Metrics exist in this period of time");
			return true;
			
		}else{
			logger.info("MONGODB : Metrics do not exist in this period of time");
			return false;
			
		}
	}

	
	
	public boolean isThereMetricDetails(ShortRemoteServerId shortRemoteServer,
			String typeOfStat, String typeOfMetric, Date startDate, Date endDate) {

		logger.info("MONGODB : get info of type of stats for an specific server");
		logger.info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		//Create the query
				BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
				.append("ServerIP", shortRemoteServer.getNodeIPAddress())
				.append("StatType", typeOfStat)
				.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
				
				logger.debug("MONGODB : query ="+query.toString());
				
				//Create the selector
				BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
				.append("StatDate", true).append("_id", false);
				
				logger.debug("MONGODB : selector ="+obj.toString());
				
						
				int  nb_metrics =  remoteStatCollection.find(query, obj).count();

				if (nb_metrics > 0){
					logger.info("MONGODB : Metrics exist in this period of time");
					return true;
					
				}else{
					logger.info("MONGODB : Metrics do not exist in this period of time");
					return false;
					
				}
		
	}


	public boolean isThereMetricDetails(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, String typeOfMetric, Date startDate, Date endDate) {
		
		logger.info("MONGODB : get info of type of stats for an specific server");
		logger.info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		//Varaibles to generat a query with or clause
		BasicDBObject query = new BasicDBObject();
		BasicDBList or = new BasicDBList();
			
		//Check all severs that arrive to the method and generate the or clause
		Iterator<ShortRemoteServerId> iterator = listShortRemoteServerId.iterator();
		while (iterator.hasNext()) {
			ShortRemoteServerId shortRmt = iterator.next();
			BasicDBObject clause = new BasicDBObject("ServerIP", shortRmt.getNodeIPAddress()).append("NameServer", shortRmt.getName());
			or.add(clause);
		}
		query = new BasicDBObject("$or", or)
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
		

		
				
				logger.debug("MONGODB : query ="+query.toString());
				
				//Create the selector
				BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
				.append("StatDate", true).append("_id", false);
				
				logger.debug("MONGODB : selector ="+obj.toString());
				
						
				int  nb_metrics =  remoteStatCollection.find(query, obj).count();

				if (nb_metrics > 0){
					logger.info("MONGODB : Metrics exist in this period of time");
					return true;
					
				}else{
					logger.info("MONGODB : Metrics do not exist in this period of time");
					return false;
					
				}
	}

	public void cleanStatsbefore( Date beforeDate) {
		logger.info("MONGODB : Drop old statistics from database before than " + beforeDate);
		
		//Create the query
		BasicDBObject query = new BasicDBObject("StatDate", new BasicDBObject("$lt", beforeDate));
		
		long res =  remoteStatCollection.count(query);
		
		if (res > 0){
			
			logger.debug("MONGODB : stadistics to drop = " + res);
			remoteStatCollection.remove(query);
		}
		
		long res_1 =  remoteStatCollection.count(query);
		logger.debug("MONGODB : stadistics after drop = " + res_1);
		
	}

	public long getNumberStatsBefore(Date beforeDate) {
				
		logger.info("MONGODB : Get number of stats before : "+ beforeDate);
		
		//Create the query
		BasicDBObject query = new BasicDBObject("StatDate", new BasicDBObject("$lt", beforeDate));
		
		long res =  remoteStatCollection.count(query);

		
		logger.debug("Number of stats = "+ res);
		
		return res;
	}

	
}

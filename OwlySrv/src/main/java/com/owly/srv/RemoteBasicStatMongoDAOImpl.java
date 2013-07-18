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
		this.setLogger(Logger
				.getLogger(RemoteBasicStatMongoDAOImpl.class));
	}

	public ArrayList<BasicStat> addValuesToList(
			ArrayList<BasicStat> listBasicStat, long repeatInterval,
			double value_inteporlation) {
		
		ArrayList<BasicStat> newlstBasicStat= new ArrayList<BasicStat> ();
		
		// Number of elements in the array
		int nbElements = listBasicStat.size();
		getLogger().debug("nbElements = " + nbElements);
		
		if (nbElements>0){
			//Initialize first value of the interpolation
			newlstBasicStat.add(0, listBasicStat.get(0));
			
			for (int i = 1; i < nbElements; i++) {
				// Obtain data related to date for actual index and prvious one
				Date lastDate = listBasicStat.get(i-1).getDate();
				Date actualDate = listBasicStat.get(i).getDate();
				double actualValue = Double.parseDouble((listBasicStat.get(i).getValue()));
				
				if ( actualDate.getTime() < (lastDate.getTime() - 5*repeatInterval)){
					// there is a big gap in metrcis, let´s refill
					long interpolated = lastDate.getTime()-repeatInterval; // Value interpolated
					long oldDate=actualDate.getTime();
					while (interpolated > oldDate){
						
						// Add this new metric
						BasicStat tmpBasicStat= new BasicStat();
						tmpBasicStat.setValue(Double.toString(value_inteporlation));
						Date tmpdate = new Date();
						tmpdate.setTime(interpolated);
						tmpBasicStat.setDate(tmpdate);
						newlstBasicStat.add(tmpBasicStat);
						
						// This is the new value to add into the list in millisces
						interpolated = interpolated - repeatInterval;
					}
					//Add value from original list
					BasicStat tmpBasicStat= new BasicStat();
					tmpBasicStat.setValue(Double.toString(actualValue));
					tmpBasicStat.setDate(actualDate);
					newlstBasicStat.add(tmpBasicStat);
					
					
				}else{
					// In this case we don´t have to do time interpolation.
					// Add this new metric
					BasicStat tmpBasicStat= new BasicStat();
					tmpBasicStat.setValue(Double.toString(actualValue));
					tmpBasicStat.setDate(actualDate);
					newlstBasicStat.add(tmpBasicStat);
					
				}
			}
			logger.info("Original number Elements = " + nbElements+";Interpolated elements = " + newlstBasicStat.size());
		}else{
			// No data to process
			newlstBasicStat=listBasicStat;

		}
		return newlstBasicStat;
	}
	
	
	public void insertRemoteBasicStat(RemoteBasicStat remoteBasicStat) {

		getLogger().info("MONGODB : Insert Statistic in DataBase");

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

		getLogger().info("MONGODB : Data to insert : " + obj.toString());
		remoteStatCollection.insert(obj);
		getLogger().info("MONGODB :Statistic inserted");

	}

	public List<String> getTypeStatFromServer(
			ShortRemoteServerId shortRemoteServer, Integer minutesToCheck) {

		getLogger().info("MONGODB : get info of type of stats for an specific server");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));
		
		
		List<String> typeStat = new ArrayList<String>();
		
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())		
		.append("StatDate", new BasicDBObject("$gt", beforeDate));
		
		
		typeStat = remoteStatCollection.distinct("StatType",query);
		
		getLogger().info("MONGODB : List obtained");
		return typeStat;
				
	}
	
	
	public List<String> getTypeStatFromServer(
			ShortRemoteServerId shortRemoteServer, Date startDate, Date endDate) {
		getLogger().info("MONGODB : get info of type of stats for an specific server");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		List<String> typeStat = new ArrayList<String>();
		
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())		
		.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
		
		
		typeStat = remoteStatCollection.distinct("StatType",query);
		
		getLogger().info("MONGODB : List obtained");
		return typeStat;

	}
	
	
	public List<String> getTypeStatFromServer(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			Integer minutesToCheck) {
		
		getLogger().info("MONGODB : get info of type of stats for an specific server");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));
		
		
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
			
		getLogger().info("MONGODB : Get type of stats in the database for this clause"+query.toString());
		
		typeStat = remoteStatCollection.distinct("StatType",query);
		
		getLogger().info("MONGODB : List obtained");
		return typeStat;
	}

	public List<String> getTypeStatFromServer(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			Date startDate, Date endDate) {
		getLogger().info("MONGODB : get info of type of stats for an specific server");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
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
		
		getLogger().info("MONGODB : List obtained");
		return typeStat;

	}
	

	public Set<String> getTypeMetricFromStat(
			ShortRemoteServerId shortRemoteServer, String typeOfStat, Integer minutesToCheck) {
		
		getLogger().info("MONGODB : get info of type of metrcis for an specific server and specific stat");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

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
		getLogger().info("MONGODB : List obtained");
		
		return typeMetric;

	}

	public Set<String> getTypeMetricFromStat(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, Integer minutesToCheck) {
		
		getLogger().info("MONGODB : get info of type of metrcis for an specific server and specific stat");
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		
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
			
		getLogger().info("MONGODB : Get type of stats in the database for this clause"+query.toString());
				
		//logger.debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics", true).append(
				"_id", false);
		
		//Create the ordeby
		BasicDBObject orderBy = new BasicDBObject("StatDate", -1);

		DBObject res =  remoteStatCollection.findOne(query, obj,orderBy);

		Set<String> typeMetric = (((BSONObject) res.get("Metrics")).toMap()).keySet();
		
		//logger.debug("MONGODB : query result"+typeMetric.toString());
		getLogger().info("MONGODB : List obtained");
		
		return typeMetric;
	}

	
	
	public Set<String> getTypeMetricFromStat(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			Date startDate, Date endDate) {
		
			getLogger().info("MONGODB : get info of type of stats for an specific server");
			getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
			
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
			getLogger().info("MONGODB : List obtained");
			
			return typeMetric;
	}
	
	public Set<String> getTypeMetricFromStat(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, Date startDate, Date endDate) {
		
		getLogger().info("MONGODB : get info of type of stats for an specific server");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
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
		getLogger().info("MONGODB : List obtained");
		
		return typeMetric;
	}

	
	
	public ArrayList<BasicStat> getMetricDetails(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			String typeOfMetric, Integer minutesToCheck) {

		getLogger().info("MONGODB : Get a metric in a period of time vbased in minutes");
		
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		//Create a List BasicStat Object
		ArrayList<BasicStat> ListMetrics=new ArrayList<BasicStat>();
		
		//Create the query
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate).append("$lt", nowDate));
		
		getLogger().debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
		.append("StatDate", true).append("_id", false);
		
		getLogger().debug("MONGODB : selector ="+obj.toString());
		
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
		getLogger().debug("MONGODB : List obtained"+ListMetrics.toString());
		cursor.close();
		
		getLogger().info("MONGODB : List obtained");
		
		return ListMetrics;

	}
	
	
	public ArrayList<BasicStat> getMetricDetails(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			String typeOfMetric, Integer minutesToCheck, Integer numerOfMetrics, long repeatInterval, double value_inteporlation) {
		
		getLogger().info("MONGODB : Get a metric in a period of time vbased in minutes");
		
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		//Create a List BasicStat Object
		ArrayList<BasicStat> ListMetrics=new ArrayList<BasicStat>();
		
		//Create the query
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate).append("$lt", nowDate));
		
		getLogger().debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
		.append("StatDate", true).append("_id", false);
		
		getLogger().debug("MONGODB : selector ="+obj.toString());
		
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
		
		//Execute the interpolation of data
		//Execute the interpolation of data				
		ArrayList<BasicStat> AddZerosListMetrics = new ArrayList<BasicStat>();
		AddZerosListMetrics = addValuesToList(ListMetrics,repeatInterval,value_inteporlation);
		
		
		Integer numberMetricsCollected=AddZerosListMetrics.size();
		getLogger().debug("Number of metricis collected = "+numberMetricsCollected+" and number of metrics to return = " + numerOfMetrics);
		
 
		if ( numerOfMetrics < numberMetricsCollected ) {
			// New List of metrics with number of metrics selected in  numerOfMetrics
			//Create a List BasicStat Object
			ArrayList<BasicStat> AvgListMetrics=new ArrayList<BasicStat>();
			double  gapBetweenMetrics = (double)numberMetricsCollected/(double)numerOfMetrics; // Number of metrics in the original List between two metrics to exported to the new Genereated List.
			getLogger().debug("gapBetweenMetrics = " + gapBetweenMetrics);
			//Initialize first value
			getLogger().debug("We need to create a new List with Avg");
			//getLogger().debug("initialize firts value = " + AddZerosListMetrics.get(0));
			AvgListMetrics.add(0, AddZerosListMetrics.get(0));
			 
			for (int j=1;j < numerOfMetrics -1;j++){
				//getLogger().debug("Previous metrics is = "+ (int)Math.round((j-1)*gapBetweenMetrics) + ";Next metric to get = " + (int)Math.round(j*gapBetweenMetrics));
				
				ArrayList<BasicStat> tmpListMetrics=new ArrayList<BasicStat>();
				for (int k=((int)Math.round((j-1)*gapBetweenMetrics))+1;k<=((int)Math.round(j*gapBetweenMetrics));k++){
					//getLogger().debug("Adding this indexes to the tmp List ="+k);
					tmpListMetrics.add(AddZerosListMetrics.get(k));
				}
				BasicStat AvgMetric=new BasicStat();
				AvgMetric.setAvgBasicStatList(tmpListMetrics);
				
				//getLogger().debug("Avg generated is  = "+AvgMetric.toString());

				AvgListMetrics.add(j,AvgMetric);
				
			}
			//Last value
			//getLogger().debug("Last metric to get = " + (numberMetricsCollected -1));
			AvgListMetrics.add(numerOfMetrics -1,AddZerosListMetrics.get(numberMetricsCollected -1));
			
			//Result with Avg
			getLogger().debug("MONGODB : List obtained"+AvgListMetrics.toString());
			
			cursor.close();
			
			getLogger().info("MONGODB : List obtained");
			
			return AvgListMetrics;

		}
		else{
			getLogger().debug("MONGODB : List obtained"+AddZerosListMetrics.toString());
			
			cursor.close();
			
			getLogger().info("MONGODB : List obtained");
			
			return AddZerosListMetrics;

		}
		

	}
	
	public ArrayList<BasicStat> getMetricDetails(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			String typeOfMetric, Date startDate, Date endDate) {
		
		getLogger().info("MONGODB : get info of type of stats for an specific server");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		//Create a List BasicStat Object
				ArrayList<BasicStat> ListMetrics=new ArrayList<BasicStat>();
				
				//Create the query
				BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
				.append("ServerIP", shortRemoteServer.getNodeIPAddress())
				.append("StatType", typeOfStat)
				.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
				
				getLogger().debug("MONGODB : query ="+query.toString());
				
				//Create the selector
				BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
				.append("StatDate", true).append("_id", false);
				
				getLogger().debug("MONGODB : selector ="+obj.toString());
				
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
				getLogger().debug("MONGODB : List obtained"+ListMetrics.toString());
				cursor.close();
				
				getLogger().info("MONGODB : List obtained");
				
				return ListMetrics;

	}

	public ArrayList<BasicStat> getMetricDetails(
			ShortRemoteServerId shortRemoteServer, String typeOfStat,
			String typeOfMetric, Date startDate, Date endDate,
			Integer numerOfMetrics, long repeatInterval, double value_inteporlation) {

		getLogger().info("MONGODB : get info of type of stats for an specific server");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		//Create a List BasicStat Object
				ArrayList<BasicStat> ListMetrics=new ArrayList<BasicStat>();
				
				//Create the query
				BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
				.append("ServerIP", shortRemoteServer.getNodeIPAddress())
				.append("StatType", typeOfStat)
				.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
				
				getLogger().debug("MONGODB : query ="+query.toString());
				
				//Create the selector
				BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
				.append("StatDate", true).append("_id", false);
				
				getLogger().debug("MONGODB : selector ="+obj.toString());
				
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
				
				//Execute the interpolation of data				
				ArrayList<BasicStat> AddZerosListMetrics = new ArrayList<BasicStat>();
				AddZerosListMetrics = addValuesToList(ListMetrics,repeatInterval,value_inteporlation);

				
				Integer numberMetricsCollected=AddZerosListMetrics.size();
				getLogger().debug("Number of metricis collected = "+numberMetricsCollected+" and number of metrics to return = " + numerOfMetrics);
				
		 
				if ( numerOfMetrics < numberMetricsCollected ) {
					// New List of metrics with number of metrics selected in  numerOfMetrics
					//Create a List BasicStat Object
					ArrayList<BasicStat> AvgListMetrics=new ArrayList<BasicStat>();
					double  gapBetweenMetrics = (double)numberMetricsCollected/(double)numerOfMetrics; // Number of metrics in the original List between two metrics to exported to the new Genereated List.
					getLogger().debug("gapBetweenMetrics = " + gapBetweenMetrics);
					//Initialize first value
					getLogger().debug("We need to create a new List with Avg");
					//getLogger().debug("initialize firts value = " + AddZerosListMetrics.get(0));
					AvgListMetrics.add(0, AddZerosListMetrics.get(0));
					 
					for (int j=1;j < numerOfMetrics -1;j++){
						//getLogger().debug("Previous metrics is = "+ (int)Math.round((j-1)*gapBetweenMetrics) + ";Next metric to get = " + (int)Math.round(j*gapBetweenMetrics));
						
						ArrayList<BasicStat> tmpListMetrics=new ArrayList<BasicStat>();
						for (int k=((int)Math.round((j-1)*gapBetweenMetrics))+1;k<=((int)Math.round(j*gapBetweenMetrics));k++){
							//getLogger().debug("Adding this indexes to the tmp List ="+k);
							tmpListMetrics.add(AddZerosListMetrics.get(k));
						}
						BasicStat AvgMetric=new BasicStat();
						AvgMetric.setAvgBasicStatList(tmpListMetrics);
						
						//getLogger().debug("Avg generated is  = "+AvgMetric.toString());

						AvgListMetrics.add(j,AvgMetric);
						
					}
					//Last value
					//getLogger().debug("Last metric to get = " + (numberMetricsCollected -1));
					AvgListMetrics.add(numerOfMetrics -1,AddZerosListMetrics.get(numberMetricsCollected -1));
					
					//Result with Avg
					getLogger().debug("MONGODB : List obtained"+AvgListMetrics.toString());
					
					cursor.close();
					
					getLogger().info("MONGODB : List obtained");
					
					return AvgListMetrics;

				}
				else{
					getLogger().debug("MONGODB : List obtained"+AddZerosListMetrics.toString());
					
					cursor.close();
					
					getLogger().info("MONGODB : List obtained");
					
					return AddZerosListMetrics;

				}
		
	}

	
	

	public boolean isThereMetricDetails(ShortRemoteServerId shortRemoteServer,
			String typeOfStat, String typeOfMetric, Integer minutesToCheck) {
		
		getLogger().info("MONGODB : Get availability of metrics in a period of time vbased in minutes");
		
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

		//Create the query
		BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
		.append("ServerIP", shortRemoteServer.getNodeIPAddress())
		.append("StatType", typeOfStat)
		.append("StatDate", new BasicDBObject("$gt", beforeDate).append("$lt", nowDate));
		
		getLogger().debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
		.append("StatDate", true).append("_id", false);
		
		getLogger().debug("MONGODB : selector ="+obj.toString());
		
				
		int  nb_metrics =  remoteStatCollection.find(query, obj).count();

		if (nb_metrics > 0){
			getLogger().info("MONGODB : Metrics exist in this period of time");
			return true;
			
		}else{
			getLogger().info("MONGODB : Metrics do not exist in this period of time");
			return false;
			
		}
		
	}

	public boolean isThereMetricDetails(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, String typeOfMetric, Integer minutesToCheck) {
		getLogger().info("MONGODB : Get availability of metrics in a period of time vbased in minutes");
		
		GregorianCalendar cal = new GregorianCalendar();
		Date nowDate = cal.getTime();
		cal.add(Calendar.MINUTE, -minutesToCheck);
		Date beforeDate = cal.getTime();
		SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+dtformat.format(beforeDate)+" and ending : "+dtformat.format(nowDate));

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
		
		getLogger().debug("MONGODB : query ="+query.toString());
		
		//Create the selector
		BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
		.append("StatDate", true).append("_id", false);
		
		getLogger().debug("MONGODB : selector ="+obj.toString());
		
				
		int  nb_metrics =  remoteStatCollection.find(query, obj).count();

		if (nb_metrics > 0){
			getLogger().info("MONGODB : Metrics exist in this period of time");
			return true;
			
		}else{
			getLogger().info("MONGODB : Metrics do not exist in this period of time");
			return false;
			
		}
	}

	
	
	public boolean isThereMetricDetails(ShortRemoteServerId shortRemoteServer,
			String typeOfStat, String typeOfMetric, Date startDate, Date endDate) {

		getLogger().info("MONGODB : get info of type of stats for an specific server");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
		//Create the query
				BasicDBObject query = new BasicDBObject("NameServer", shortRemoteServer.getName())
				.append("ServerIP", shortRemoteServer.getNodeIPAddress())
				.append("StatType", typeOfStat)
				.append("StatDate", new BasicDBObject("$gt", startDate).append("$lt", endDate));
				
				getLogger().debug("MONGODB : query ="+query.toString());
				
				//Create the selector
				BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
				.append("StatDate", true).append("_id", false);
				
				getLogger().debug("MONGODB : selector ="+obj.toString());
				
						
				int  nb_metrics =  remoteStatCollection.find(query, obj).count();

				if (nb_metrics > 0){
					getLogger().info("MONGODB : Metrics exist in this period of time");
					return true;
					
				}else{
					getLogger().info("MONGODB : Metrics do not exist in this period of time");
					return false;
					
				}
		
	}


	public boolean isThereMetricDetails(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId,
			String typeOfStat, String typeOfMetric, Date startDate, Date endDate) {
		
		getLogger().info("MONGODB : get info of type of stats for an specific server");
		getLogger().info("MONGODB : Get a metric in a period of time starting : "+ startDate +" and ending : "+endDate);
		
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
		

		
				
				getLogger().debug("MONGODB : query ="+query.toString());
				
				//Create the selector
				BasicDBObject obj = new BasicDBObject("Metrics."+typeOfMetric, true)
				.append("StatDate", true).append("_id", false);
				
				getLogger().debug("MONGODB : selector ="+obj.toString());
				
						
				int  nb_metrics =  remoteStatCollection.find(query, obj).count();

				if (nb_metrics > 0){
					getLogger().info("MONGODB : Metrics exist in this period of time");
					return true;
					
				}else{
					getLogger().info("MONGODB : Metrics do not exist in this period of time");
					return false;
					
				}
	}

	public void cleanStatsbefore( Date beforeDate) {
		getLogger().info("MONGODB : Drop old statistics from database before than " + beforeDate);
		
		//Create the query
		BasicDBObject query = new BasicDBObject("StatDate", new BasicDBObject("$lt", beforeDate));
		
		long res =  remoteStatCollection.count(query);
		
		if (res > 0){
			
			getLogger().debug("MONGODB : stadistics to drop = " + res);
			remoteStatCollection.remove(query);
		}
		
		long res_1 =  remoteStatCollection.count(query);
		getLogger().debug("MONGODB : stadistics after drop = " + res_1);
		
	}

	public long getNumberStatsBefore(Date beforeDate) {
				
		getLogger().info("MONGODB : Get number of stats before : "+ beforeDate);
		
		//Create the query
		BasicDBObject query = new BasicDBObject("StatDate", new BasicDBObject("$lt", beforeDate));
		
		long res =  remoteStatCollection.count(query);

		
		getLogger().debug("Number of stats = "+ res);
		
		return res;
	}

	public static Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		RemoteBasicStatMongoDAOImpl.logger = logger;
	}

		
}

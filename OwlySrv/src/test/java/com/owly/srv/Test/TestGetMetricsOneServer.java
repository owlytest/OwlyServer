package com.owly.srv.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.owly.srv.BasicStat;
import com.owly.srv.BasicDataMetricInJson;
import com.owly.srv.RemoteBasicStatMongoDAOImpl;
import com.owly.srv.ShortRemoteServerId;
import com.owly.srv.StatsServerMongoDAOImpl;

public class TestGetMetricsOneServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Logger logger = Logger.getLogger("getTypeMetricfromStat.jsp");

		String db = "MCMLOAD";
		//out.print("database is  : " + db);

		String ipserver = "127.0.0.1";
		//out.print("ip is  : " + ipserver);

		Integer portserver = 27017;
		//out.print("portserver is  : " + portserver);


		String dbtype = "MONGODB";
		//out.print("dbtype is  : " + dbtype);
		
		Long repeatInterval = (long) 20000;
		
		
		logger.debug("Environment variables : DBNAME="+db+
				";IPSERVER="+ipserver+
				";PORTSERVER="+portserver+
				";DATABASETYPE="+dbtype+
				";REPEATINTERVAL="+repeatInterval);

		String nameRemServer = "as-adm-b";
		String ipRemServer = "127.0.0.1";
		String type_stat = "WinTypeperf";
		String type_metric = "mem_used";
		Integer offset = -120;
		Integer minutes = 1000;		
		logger.debug("parameters received : nameserver="+nameRemServer+" and ipserver="+ipRemServer +" and type_stat="+type_stat+" and type_metric="+type_metric+" and minutes_to_check="+minutes+" and offset="+offset);

		//Create this object for filtering later
		ShortRemoteServerId shortRemoteServer = new ShortRemoteServerId();
		shortRemoteServer.setName(nameRemServer);
		shortRemoteServer.setNodeIPAddress(ipRemServer);

		MongoClient mongoClient = null;
		
		try {
			mongoClient = new MongoClient(ipserver, portserver);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB statsDB = mongoClient.getDB(db);

		//Get the name of collection where stats are going to be saved.
		StatsServerMongoDAOImpl statsServerDAO = new StatsServerMongoDAOImpl(statsDB);
		String StatsCollection = statsServerDAO.getStatCollectStatsServer();

			
		//Create the DAO for accesing database stats	
		RemoteBasicStatMongoDAOImpl remoteBasicStatMongoDAO = new RemoteBasicStatMongoDAOImpl(statsDB,StatsCollection);
		ArrayList<BasicStat> ListMetric = remoteBasicStatMongoDAO.getMetricDetails(shortRemoteServer, type_stat, type_metric, minutes);
		
		logger.debug("ArrayList =" + ListMetric.toString());
		
		Iterator it = ListMetric.iterator();
		BasicDataMetricInJson flotJsonData = new BasicDataMetricInJson(type_metric);
		flotJsonData.insertDataMetrics(ListMetric, offset);
		
		logger.debug("flotJsonData =" + flotJsonData.toString());
		
		
		flotJsonData.interpolateDataMetrics(repeatInterval,(float)0);
		
		logger.debug("flotJsonData =" + flotJsonData.toString());
				
		mongoClient.close();	

		
	}

}

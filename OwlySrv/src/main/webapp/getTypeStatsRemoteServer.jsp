<%--
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
--%>
<%@page import="com.owly.srv.RemoteServerMongoDAOImpl"%>
<%@page import="com.owly.srv.StatsServerMongoDAOImpl"%>
<%@page import="com.owly.srv.RemoteBasicStatMongoDAOImpl"%>
<%@page import="com.owly.srv.RemoteServer"%>
<%@page import="com.owly.srv.ShortRemoteServerId"%>
<%@page import="com.mongodb.DB"%>
<%@page import="com.mongodb.MongoClient"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ListIterator"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.log4j.Logger;"%>


<%

Logger logger = Logger.getLogger("getTypeMetricsRemoteServer.jsp");

String db = (String)application.getAttribute("DBNAME");
//out.print("database is  : " + db);

String ipserver = (String)application.getAttribute("IPSERVER");
//out.print("ip is  : " + ipserver);

Integer portserver = (Integer) application.getAttribute("PORTSERVER");
//out.print("portserver is  : " + portserver);


String dbtype = (String)application.getAttribute("DATABASETYPE");
//out.print("dbtype is  : " + dbtype);

logger.info("Environment variables : DBNAME="+db+
			";IPSERVER="+ipserver+
			";PORTSERVER="+portserver+
			";DATABASETYPE="+dbtype);

String[] nameRemServer = request.getParameterValues("nameserver");
String[] ipRemServer = request.getParameterValues("ipserver");
Integer minutes = Integer.valueOf(request.getParameter("minutes_to_check"));
logger.info("parameters received : nameserver="+nameRemServer.toString()+" and ipserver="+ipRemServer.toString()+" and minutes_to_check="+minutes);


//Create this object for filtering later
ArrayList<ShortRemoteServerId>  shortRemoteServer = new ArrayList<ShortRemoteServerId>();
for (int i = 0; i < nameRemServer.length; i++){
	ShortRemoteServerId shortRmtSrv = new ShortRemoteServerId();
	shortRmtSrv.setName(nameRemServer[i]);
	shortRmtSrv.setNodeIPAddress(ipRemServer[i]);
	shortRemoteServer.add(shortRmtSrv);
}
	
MongoClient mongoClient = new MongoClient(ipserver, portserver);
DB statsDB = mongoClient.getDB(db);

	//Get list of remote servers
RemoteServer rmtSrv = new RemoteServer();

RemoteServerMongoDAOImpl remsrvDAO = new RemoteServerMongoDAOImpl(statsDB);
	
//Check if the remote servre is in database
Integer count = remsrvDAO.numberRemoteServer(shortRemoteServer);

	
if ( count > 0 ) { 
		
		// Get the name of collection where stats are going to be saved.
		StatsServerMongoDAOImpl statsServerDAO = new StatsServerMongoDAOImpl(statsDB);
		String StatsCollection = statsServerDAO.getStatCollectStatsServer();

		
		// The the stats for this server
		RemoteBasicStatMongoDAOImpl remoteBasicStatMongoDAO = new RemoteBasicStatMongoDAOImpl(statsDB,StatsCollection);
		List<String> typeStat=remoteBasicStatMongoDAO.getTypeStatFromServer(shortRemoteServer,minutes);
				
		Gson gson = new Gson();

		String json = gson.toJson(typeStat);
		logger.info("Json object to send jquery ajax="+json);
			
		out.print(json);				
				
		mongoClient.close();	
	}
	else{
		out.print("Error loading server configuration");
		// Close the conection to database
		mongoClient.close();
	}
%>
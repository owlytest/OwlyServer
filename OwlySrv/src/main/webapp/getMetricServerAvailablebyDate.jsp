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
<%@page import="com.owly.srv.BasicStat"%>
<%@page import="com.owly.srv.BasicDataMetricInJson"%>
<%@page import="com.mongodb.DB"%>
<%@page import="com.mongodb.MongoClient"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Date"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>



<%

Logger logger = Logger.getLogger("getMetricOneServerAvailable.jsp");

String db = (String)application.getAttribute("DBNAME");
//out.print("database is  : " + db);

String ipserver = (String)application.getAttribute("IPSERVER");
//out.print("ip is  : " + ipserver);

Integer portserver = (Integer) application.getAttribute("PORTSERVER");
//out.print("portserver is  : " + portserver);


String dbtype = (String)application.getAttribute("DATABASETYPE");
//out.print("dbtype is  : " + dbtype);

logger.debug("Environment variables : DBNAME="+db+
			";IPSERVER="+ipserver+
			";PORTSERVER="+portserver+
			";DATABASETYPE="+dbtype);

String[] nameRemServer = request.getParameterValues("nameserver");
String[] ipRemServer = request.getParameterValues("ipserver");
String type_stat = request.getParameter("type_stat");
String type_metric = request.getParameter("type_metric");
String start_date = request.getParameter("start_date");
String end_date = request.getParameter("end_date");
Integer offset = Integer.valueOf(request.getParameter("offset_browser"));

logger.debug("parameters received : nameserver="+nameRemServer+" and ipserver="+ipRemServer +" and type_stat="+type_stat+" and type_metric="+type_metric+" and start_date="+start_date+ " and end_date=" + end_date+" and offset="+offset);

SimpleDateFormat dtformat  = new SimpleDateFormat("yyyy-MM-dd HH:mm");
Date dateStart=dtformat.parse(start_date);
Date dateEnd=dtformat.parse(end_date);

logger.debug("start date = "+dateStart+"; End date = "+dateEnd);

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

//Get the name of collection where stats are going to be saved.
StatsServerMongoDAOImpl statsServerDAO = new StatsServerMongoDAOImpl(statsDB);
String StatsCollection = statsServerDAO.getStatCollectStatsServer();

	
//Create the DAO for accesing database stats	
RemoteBasicStatMongoDAOImpl remoteBasicStatMongoDAO = new RemoteBasicStatMongoDAOImpl(statsDB,StatsCollection);
boolean metrics_available = remoteBasicStatMongoDAO.isThereMetricDetails(shortRemoteServer, type_stat, type_metric ,dateStart,dateEnd);	
		
String json = null;
		
if (metrics_available){
	json="{\"isMetrics\":\"OK\"}";
}else
{
	json="{\"isMetrics\":\"NOK\"}";
}
		


logger.debug("Response=" + json);

out.print(json);
		
mongoClient.close();	

%>
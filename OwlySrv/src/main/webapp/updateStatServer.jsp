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

<%@page import="com.owly.srv.StatsServerMongoDAOImpl"%>
<%@page import="com.owly.srv.StatsServer"%>
<%@page import="com.mongodb.DB"%>
<%@page import="com.mongodb.MongoClient"%>
<%@page import="org.apache.log4j.Logger;"%>


<% 

Logger logger = Logger.getLogger("updateStatServer.jsp");

//Read context variables
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


// Save values received into a StatServer Object
StatsServer srv = new StatsServer();
srv.setStatsDatabase(request.getParameter("srvDatabase"));
srv.setStatsDatabaseType(request.getParameter("srvType"));
srv.setStatsIpAddress(request.getParameter("srvIP"));
srv.setStatsPort(Integer.valueOf(request.getParameter("srvPort")));
srv.setName(request.getParameter("srvName"));
srv.setStatsCollect(request.getParameter("srvStatsCollect"));

if (request.getParameter("Status").equals("true")){
	srv.setEnabled(Boolean.valueOf("true"));
}else{
	srv.setEnabled(Boolean.valueOf("false"));
}
	
if (request.getParameter("Monitor").equals("true")){
	srv.setMonitoring(Boolean.valueOf("true"));
}else{
	srv.setMonitoring(Boolean.valueOf("false"));
}

srv.setSavedDays(Integer.valueOf(request.getParameter("srvDays")));

logger.info("Server configuration recived is"+srv.toString());


// Open conection to database
MongoClient mongoClient = new MongoClient(ipserver, portserver);
DB statsDB = mongoClient.getDB(db);

StatsServerMongoDAOImpl srvDAO = new StatsServerMongoDAOImpl(statsDB);
srvDAO.updateStatsServer(srv);

String json="{\"result\":\"OK\"}";
out.print(json);

//Close the conection to database
mongoClient.close();

 %>




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
<%@page import="com.owly.srv.RemoteServer"%>
<%@page import="com.owly.srv.RemoteServerMongoDAOImpl"%>
<%@page import="com.mongodb.DB"%>
<%@page import="com.mongodb.MongoClient"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.log4j.Logger;"%>


<%

Logger logger = Logger.getLogger("getSelectedRemoteServer.jsp");

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

String nameRemServer = request.getParameter("nameserver");
String ipRemServer = request.getParameter("ipserver");
logger.info("parameters received : nameserver="+nameRemServer+" and ipserver="+ipRemServer);

MongoClient mongoClient = new MongoClient(ipserver, portserver);
DB statsDB = mongoClient.getDB(db);

//Get list of remote servers
RemoteServer rmtSrv = new RemoteServer();

RemoteServerMongoDAOImpl remsrvDAO = new RemoteServerMongoDAOImpl(statsDB);

Integer count = remsrvDAO.numberRemoteServer(nameRemServer,ipRemServer);

if ( count > 0 ) { 
	rmtSrv = remsrvDAO.getRemoteServerbyName_IP(nameRemServer,ipRemServer);	
	Gson gson = new Gson();
	
	String json = gson.toJson(rmtSrv);
	logger.info("Json object to send jquery ajax="+json);
		
	out.print(json);
	// Close the conection to database
	mongoClient.close();	
}
else{
	out.print("Error loading server configuration");
	// Close the conection to database
	mongoClient.close();
}


%>
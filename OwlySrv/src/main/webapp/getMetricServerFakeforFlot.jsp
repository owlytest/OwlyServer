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
<%@page import="com.owly.srv.ShortRemoteServerId"%>
<%@page import="com.owly.srv.StatsServerMongoDAOImpl"%>
<%@page import="com.owly.srv.RemoteBasicStatMongoDAOImpl"%>
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
<%@page import="org.apache.log4j.Logger;"%>

<%


Logger logger = Logger.getLogger("getMetricServerFakeforFlot.jsp");

String db = (String)application.getAttribute("DBNAME");
//out.print("database is  : " + db);

String ipserver = (String)application.getAttribute("IPSERVER");
//out.print("ip is  : " + ipserver);

Integer portserver = (Integer) application.getAttribute("PORTSERVER");
//out.print("portserver is  : " + portserver);


String dbtype = (String)application.getAttribute("DATABASETYPE");
//out.print("dbtype is  : " + dbtype);

Long repeatInterval = (Long)application.getAttribute("REPEATINTERVAL");


logger.debug("Environment variables : DBNAME="+db+
	";IPSERVER="+ipserver+
	";PORTSERVER="+portserver+
	";DATABASETYPE="+dbtype+
	";REPEATINTERVAL="+repeatInterval);

String[] nameRemServer = request.getParameterValues("nameserver");
String[] ipRemServer = request.getParameterValues("ipserver");
String type_stat = request.getParameter("type_stat");
String type_metric = request.getParameter("type_metric");
Integer minutes = Integer.valueOf(request.getParameter("minutes_to_check"));
Integer offset = Integer.valueOf(request.getParameter("offset_browser"));
logger.debug("parameters received : nameserver="+nameRemServer+" and ipserver="+ipRemServer +" and type_stat="+type_stat+" and type_metric="+type_metric+" and minutes_to_check="+minutes+" and offset="+offset);


String buffer = new String();
buffer = "[{\"label\":\"cpu_used\", \"data\":[[1372413009559,0],[1372412989548,0],[1372412969553,0]]},{\"label\":\"cpu_used\", \"data\":[[1372413009559,96.25903],[1372412989548,95.30041],[1372412969553,88.43178]]}]";



//String d1 = "[[0, 3], [4, 8], [8, 5], [9, 13]]";
//String d2 = "[[0, 12], [7, 12], null, [7, 2.5], [12, 2.5]]";
//String buffer= "["+d1+","+d2+"]";


logger.debug("JsonData to send the Ajax Jquery : " + buffer);

out.print(buffer);

%>

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


Logger logger = Logger.getLogger("getMetricOneServer.jsp");

String db = (String)application.getAttribute("DBNAME");
//out.print("database is  : " + db);

String ipserver = (String)application.getAttribute("IPSERVER");
//out.print("ip is  : " + ipserver);

Integer portserver = (Integer) application.getAttribute("PORTSERVER");
//out.print("portserver is  : " + portserver);


String dbtype = (String)application.getAttribute("DATABASETYPE");
//out.print("dbtype is  : " + dbtype);

long repeatInterval = (Long)application.getAttribute("REPEATINTERVAL");


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
Integer num_metrics = Integer.valueOf(request.getParameter("num_metrics"));

logger.debug("parameters received : nameserver="+nameRemServer+" and ipserver="+ipRemServer +" and type_stat="+type_stat+" and type_metric="+type_metric+" and minutes_to_check="+minutes+" and offset="+offset+" and num_metrics="+num_metrics);

//Create this object for filtering later
ArrayList<ShortRemoteServerId>  shortRemoteServer = new ArrayList<ShortRemoteServerId>();
for (int i = 0; i < nameRemServer.length; i++){
	ShortRemoteServerId shortRmtSrv = new ShortRemoteServerId();
	shortRmtSrv.setName(nameRemServer[i]);
	shortRmtSrv.setNodeIPAddress(ipRemServer[i]);
	shortRemoteServer.add(shortRmtSrv);
}

ArrayList<BasicDataMetricInJson> resToFlot = new ArrayList<BasicDataMetricInJson>();

// Now we will process server by server the data to show
Iterator<ShortRemoteServerId> iterator = shortRemoteServer.iterator();
while (iterator.hasNext()) {
	
	ShortRemoteServerId shortRmt = iterator.next(); // This the server to process

	MongoClient mongoClient = new MongoClient(ipserver, portserver);
	DB statsDB = mongoClient.getDB(db);

	//Get the name of collection where stats are going to be saved.
	StatsServerMongoDAOImpl statsServerDAO = new StatsServerMongoDAOImpl(statsDB);
	String StatsCollection = statsServerDAO.getStatCollectStatsServer();

	//Create the DAO for accesing database stats	
	RemoteBasicStatMongoDAOImpl remoteBasicStatMongoDAO = new RemoteBasicStatMongoDAOImpl(statsDB,StatsCollection);
	ArrayList<BasicStat> ListMetric = remoteBasicStatMongoDAO.getMetricDetails(shortRmt, type_stat, type_metric, minutes,num_metrics,repeatInterval,(double)0);
			
	Iterator it = ListMetric.iterator();

	//Create the label based on name and IP
	String label=shortRmt.getName()+"("+shortRmt.getNodeIPAddress()+")";
	
	//Create the FLot object to represent in the jquery flot plugin. 
	//BasicDataMetricInJson flotJsonData = new BasicDataMetricInJson(type_metric);
	BasicDataMetricInJson flotJsonData = new BasicDataMetricInJson(label);

	//Process all values from the list of metrics to save in a format valid for flot.
	flotJsonData.insertDataMetrics(ListMetric, offset);

	logger.debug("flotJsonData =" + flotJsonData.toString());
	resToFlot.add(flotJsonData);

	
			
	mongoClient.close();
	
}

// Prepare response to send to Flot
StringBuffer buffer = new StringBuffer();
if (resToFlot.size()==1){	
		buffer.append ("["+resToFlot.get(0).toString()+"]");
	}
else{
	buffer.append ("[");
	Iterator<BasicDataMetricInJson> it2 = resToFlot.iterator();
	while (it2.hasNext()) {		
		BasicDataMetricInJson objJSON = it2.next();
		if ( ! it2.hasNext()) // Last iteration
		{
				buffer.append (objJSON.toString());
				buffer.append ("]");
		}
		else{	// others differrent that last
				buffer.append (objJSON.toString());
				buffer.append (",");
			}
	}
	
}

logger.debug("JsonData to send the Ajax Jquery : " + buffer);

out.print(buffer);

%>

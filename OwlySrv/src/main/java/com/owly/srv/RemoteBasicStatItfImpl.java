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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Implementation of Interface for RemoteBasicStatItf
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class RemoteBasicStatItfImpl extends RemoteBasicStat implements
		RemoteBasicStatItf {

	private static Logger logger = Logger
			.getLogger(RemoteBasicStatItfImpl.class);

	public RemoteBasicStatItfImpl() {
		logger.debug("Start Constructor  Execution");

		logger.debug("Constructor Executed");
	}

	public boolean getStatusRemoteServer(String ipSrv, int clientPort) {

		URL url = null;
		BufferedReader reader = null;
		StringBuilder stringBuilder;
		JSONObject objJSON = new JSONObject();
		JSONParser objParser = new JSONParser();
		String statusServer = "Enable";

		// Check status of remote server with healthCheck
		logger.debug("Check status of remote server with healthCheck");
		// Url to send to remote server
		// for example :
		// http://135.1.128.127:5000/healthCheck

		String urlToSend = "http://" + ipSrv + ":" + clientPort
				+ "/healthCheck";
		logger.debug("URL for HTTP request :  " + urlToSend);

		HttpURLConnection connection;
		try {

			url = new URL(urlToSend);
			connection = (HttpURLConnection) url.openConnection();

			// just want to do an HTTP GET here
			connection.setRequestMethod("GET");

			// uncomment this if you want to write output to this url
			// connection.setDoOutput(true);

			// give it 2 second to respond
			connection.setReadTimeout(1 * 1000);
			connection.connect();

			// read the output from the server
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			stringBuilder = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}

			logger.debug("Response received : " + stringBuilder.toString());
			// map the response into a JSON object
			objJSON = (JSONObject) objParser.parse(stringBuilder.toString());
			// Check the response of the sever with the previous request
			String resServer = (String) objJSON.get("StatusServer");

			// If we don´t receive the correct health checl we will save the
			// disable state in the database
			if (resServer.equals("OK")) {
				statusServer = "Enable";
			} else {
				statusServer = "Disable";
			}

			logger.debug("Status of the Server: " + statusServer);

		} catch (MalformedURLException e1) {
			logger.error("MalformedURLException : " + e1.getMessage());
			logger.error("Exception ::", e1);
			statusServer = "Disable";
			logger.debug("Status of the Server: " + statusServer);

		} catch (IOException e1) {
			logger.error("IOException : " + e1.toString());
			logger.error("Exception ::", e1);
			e1.printStackTrace();
			statusServer = "Disable";
			logger.debug("Status of the Server: " + statusServer);

		} catch (ParseException e1) {
			logger.error("ParseException : " + e1.toString());
			logger.error("Exception ::", e1);
			statusServer = "Disable";
			logger.debug("Status of the Server: " + statusServer);

		} finally {
			logger.debug("Save Status of Server in Database: " + statusServer);

		}

		if (statusServer.equals("Enable")) {
			return true;
		} else {
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.owly.srv.RemoteBasicStatItf#getRemoteStatistic(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public RemoteBasicStat getRemoteStatistic(String nameSrv, String ipSrv,
			String typeSrv, String typeStat, int clientPort) {

		URL url = null;
		BufferedReader reader = null;
		StringBuilder stringBuilder;
		JSONObject objJSON = new JSONObject();
		JSONParser objParser = new JSONParser();
		RemoteBasicStat remoteBasicStat = new RemoteBasicStat();

		HttpURLConnection connection;

		// URL ot execute and get a Basic Stadistic.
		// Url to send to remote server
		// for example :
		// http://135.1.128.127:5000/OwlyClnt/Stats_TopCPU
		String urlToSend = "http://" + ipSrv + ":" + clientPort
				+ "/OwlyClnt/" + typeStat;
		logger.debug("URL for HTTP request :  " + urlToSend);

		try {
			url = new URL(urlToSend);
			connection = (HttpURLConnection) url.openConnection();

			// just want to do an HTTP GET here
			connection.setRequestMethod("GET");

			// uncomment this if you want to write output to this url
			// connection.setDoOutput(true);

			// give it 15 seconds to respond
			connection.setReadTimeout(3 * 1000);
			connection.connect();

			// read the output from the server
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			stringBuilder = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			logger.debug("Response received : " + stringBuilder.toString());
			// Get the response and save into a JSON object, and parse the
			// JSON object
			objJSON = (JSONObject) objParser.parse(stringBuilder.toString());
			logger.debug("JSON received : " + objJSON.toString());

			// Add all info received in a new object of satistics
			remoteBasicStat.setRmtSeverfromJSONObject(objJSON);

			// Add more details for the stadistics.
			remoteBasicStat.setIpServer(ipSrv);
			logger.debug("IP of server : " + remoteBasicStat.getIpServer());

			remoteBasicStat.setNameServer(nameSrv);
			logger.debug("Name of Server : " + remoteBasicStat.getNameServer());

			remoteBasicStat.setTypeServer(typeSrv);
			logger.debug("Type of Server : " + remoteBasicStat.getTypeServer());

		} catch (MalformedURLException e) {
			logger.error("MalformedURLException : " + e.getMessage());
			logger.error("Exception ::", e);
		} catch (IOException e) {
			logger.error("IOException : " + e.toString());
			logger.error("Exception ::", e);
		} catch (ParseException e) {
			logger.error("ParseException : " + e.toString());
			logger.error("Exception ::", e);
		}
		return remoteBasicStat;
		

	}

}

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

package com.owly.clnt;

import static spark.Spark.*;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class starts a web server in spark-java wich will be listen in the port
 * selected with 1st argument and will answer with a JSON object with the metric
 * executed for each mapping.
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 */
public class OwlyClnt {

	/**
	 * @param args
	 *            First argument in the port where webserver is going to start
	 */
	public static void main(String[] args) {

		Logger log = Logger.getLogger(OwlyClnt.class);
		log.debug("Executing OwlyClnt");

		if (args.length == 1) {

			String clientport = args[0];
			setPort(Integer.parseInt(clientport));
			log.debug("Port to run  :" + clientport);

			// URL used for generating the VMstat of the server
			get(new Route("/OwlyClnt/Stats_Vmstat") {
				@Override
				public Object handle(Request request, Response response) {
					
						Logger log = Logger.getLogger(OwlyClnt.class);
						log.debug("Calling Stats_Vmstat");
					
					    OSValidator osValidator = new OSValidator();
					    log.debug("operating System " + osValidator.getOS());
					    
					    JSONObject json = new JSONObject();

					    if (osValidator.isWindows()){

					    	StatsWinTypeperf testStats = new StatsWinTypeperf();

							try {
								json = testStats.getWinTypeperf();
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }else
					    {
					    	if (OSValidator.isUnix()){
								
								StatsVmstat testStats = new StatsVmstat();
								
								try {
									json = testStats.getJSONVmStat();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					    	}else
					    	{
								json.put("StatType", "NOK");
					    		
					    	}
					    	
					    }
					    
						return json.toString();
						
						
				}
						
			});

			// URL used for generating the TopCPU of the server
			get(new Route("/OwlyClnt/Stats_TopCPU") {
				@Override
				public Object handle(Request request, Response response) {

					Logger log = Logger.getLogger(OwlyClnt.class);
					log.debug("Calling Stats_Vmstat");
				
				    OSValidator osValidator = new OSValidator();
				    log.debug("operating System " + osValidator.getOS());
				    
					JSONObject topCpuStatJson = new JSONObject();
				    
				    if (OSValidator.isUnix()){

						StatsTopCpu testStats = new StatsTopCpu();

						try {
							topCpuStatJson = testStats.getJSONTopCpu();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				    }else{
				    	topCpuStatJson.put("StatType", "NOK");
				    	
				    }
				    

					return topCpuStatJson.toString();
				}
			});

			// Url used for executing a healthcheck and see if the server is
			// available
			get(new Route("/healthCheck") {
				@Override
				public Object handle(Request request, Response response) {

					JSONObject objJSON = new JSONObject();
					objJSON.put("StatusServer", "OK");

					return objJSON.toString();
				}
			});

		} else {
			System.out
					.println("ERROR : you need to specify port number -> ./OwlyClnt port ");
		}

	}
}

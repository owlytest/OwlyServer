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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.owly.clnt.StatsTopCpu.stat;

public class StatsWinTypeperf extends StatxBase{
	/**
	 * stadistics : list of mnemonics of metrics to achive, this is generated
	 * dinamically.
	 */
	private final String[] stadistics = new String[] {
			"mem_used", "mem_free", "cpu_used" };

		
	/**
	 * constructor for StatsWinTypeperf
	 */
	public StatsWinTypeperf() {

		super();
		Logger log = Logger.getLogger(StatsWinTypeperf.class);
		
		log.debug("Calling StatsWinTypeperf constructor");
		super.setHeader(AddToHeader(stadistics));

		log.debug("My header is  " + super.getHeader());

		log.debug("StatsWinTypeperf constructor Ended");


	}
	
	public JSONObject getWinTypeperf() {

		String stat_val;

		char ch='"';
	    String st="\\Memory\\Available bytes";
		String cmd =  "typeperf  \"\\Memory\\Committed Bytes\" \"\\Memory\\Available Bytes\" \"\\processor(_total)\\% processor time\"  -sc 1";

		JSONObject genericstat = new JSONObject();
		JSONObject topCpujson = new JSONObject();
		String mydate;
		String myserver;

		Logger log = Logger.getLogger(StatsWinTypeperf.class);

		try {

			log.debug("Initializating JSON object");

			mydate = this.getActualDate();
			myserver = this.getMyhost();

			genericstat.put("Host", myserver);
			genericstat.put("Date", mydate);
			genericstat.put("MetricType", "SystemStat");
			genericstat.put("StatType", "WinTypeperf");

			log.debug("Executing typeperf command");

			Process prc = Runtime.getRuntime().exec(cmd);

			BufferedReader buffread = new BufferedReader(new InputStreamReader(
					prc.getInputStream()));

			// read the child process' output
			String line;

			buffread.readLine();
			line = buffread.readLine();
			log.debug("Line readed = " + line);
			line = buffread.readLine();
			log.debug("Line readed = " + line);
			// Drop comma in the line readed
			String new_line = line.replace(",", " ");
			log.debug("New Line readed = " + new_line);
			
			// split based in the space
			String[] metric = new_line.trim().split("\\s+");

			for (int i = 2; i < metric.length; i++) {
				
				String value=metric[i];
				String value2 = value.replace("\"", " ");
				float fvalue;
				if ((i == 2) || (i==3)){
					fvalue= (Float.parseFloat(value2)/(1024*1024)); 
					log.debug("Value readed = " + fvalue);
					
				}else
				{
					fvalue= (Float.parseFloat(value2)); 
					log.debug("Value readed = " + fvalue);
				}
				topCpujson.put(stadistics[i-2], fvalue);
				log.info("json stat value added :" + stadistics[i-2] + ":"
						+ fvalue);
			}

				buffread.close();
				
			log.debug("Adding metrics to Json Object");
			genericstat.put("Metrics", topCpujson);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return genericstat;

	}

}

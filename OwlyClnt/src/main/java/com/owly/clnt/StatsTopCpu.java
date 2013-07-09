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

/**
 * This class executes the top in the linux system and gets results of CPU from
 * header, command execuetd is : top -n 2 -b -d 0.2 | grep Cpu
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class StatsTopCpu extends StatxBase {

	/**
	 * stadistics : list of mnemonics of metrics to achive, this is generated
	 * dinamically.
	 */
	private final ArrayList<String> stadistics = new ArrayList<String>();

	public enum stat {
		us, sy, ni, id, wa, hi, si, st, novalue;

		public static stat toStat(String str) {
			try {
				return valueOf(str);
			} catch (IllegalArgumentException ex) {
				return novalue;
			}
		}
	}

	/**
	 * constructor for StatsTopCpu
	 */
	public StatsTopCpu() {

		super();
		Logger log = Logger.getLogger(StatsTopCpu.class);
		log.debug("StatsTopCpu constructor Ended");

	}

	/**
	 * This method execute the top : top -n 2 -b -d 0.2 | grep Cpu
	 * 
	 * @return A JSONobject with order : Host, Date, MetricType,StatType and
	 *         Metrics For example :
	 *         {"StatType":"TopCpu","Date":"2013-05-15 14:15:55"
	 *         ,"Host":"centos-63"
	 *         ,"Metrics":{"cpu_idle":95.1,"cpu_steal":0.0,"cpu_user" :2.4},
	 *         "MetricType":"SystemStat"}
	 * 
	 */
	public JSONObject getJSONTopCpu() {

		String stat_val;

		String[] cmd = { "/bin/sh", "-c", "top -n 2 -b -d 0.2 | grep Cpu" };

		JSONObject genericstat = new JSONObject();
		JSONObject topCpujson = new JSONObject();
		String mydate;
		String myserver;

		Logger log = Logger.getLogger(StatsTopCpu.class);

		try {

			log.debug("Initializating JSON object");

			mydate = this.getActualDate();
			myserver = this.getMyhost();

			genericstat.put("Host", myserver);
			genericstat.put("Date", mydate);
			genericstat.put("MetricType", "SystemStat");
			genericstat.put("StatType", "TopCpu");

			log.debug("Executing topcpu command");

			Process prc = Runtime.getRuntime().exec(cmd);

			BufferedReader buffread = new BufferedReader(new InputStreamReader(
					prc.getInputStream()));

			// read the child process' output
			String line;

			// buffread.readLine();
			line = buffread.readLine();
			// log.debug("Line readed = " + line);
			line = buffread.readLine();
			// log.debug("Line readed = " + line);
			// Drop comma in the line readed
			String new_line = line.replace(",", " ");
			// log.debug("New Line readed = " + new_line);

			// split based in the space
			String[] metric = new_line.trim().split("\\s+");

			for (int i = 1; i < metric.length; i++) {
				// log.debug("metric  = " + metric[i]);
				// split the word based in % character
				String[] val = metric[i].trim().split("%");

				// read values from top and save in stadistics type
				switch (stat.toStat(val[1])) {
				case us:
					stat_val = "cpu_user";
					break;
				case id:
					stat_val = "cpu_idle";
					break;
				case ni:
					stat_val = "cpu_low_priority";
					break;
				case hi:
					stat_val = "cpu_hardware_interrupts";
					break;
				case si:
					stat_val = "cpu_software_interrupts";
					break;
				case st:
					stat_val = "cpu_steal";
					break;
				case sy:
					stat_val = "cpu_kernel";
					break;
				case wa:
					stat_val = "cpu_wait";
					break;
				default:
					stat_val = "error";
					break;

				}

				// log.debug("stat_val = " + stat_val + ",value = " + val[0]);
				topCpujson.put(stat_val, Float.parseFloat(val[0]));
				log.info("json topcpu value added :" + stat_val + ":"
						+ Float.parseFloat(val[0]));
				stadistics.add(stat_val);

			}

			buffread.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug("Adding metrics to Json Object");
		genericstat.put("Metrics", topCpujson);

		// Adding the header to the object in case is needed.
		String[] strStadistics = new String[stadistics.size()];
		strStadistics = stadistics.toArray(strStadistics);

		super.setHeader(super.AddToHeader(strStadistics));
		return genericstat;

	}

}

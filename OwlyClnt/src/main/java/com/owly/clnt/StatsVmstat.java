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
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * This class executes the vmstat and returns the values , command execuetd is :
 * /usr/bin/vmstat 1 2
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class StatsVmstat extends StatxBase {

	/**
	 * stadistics : list of mnemonics of metrics to achive with vmstat command
	 */
	private final String[] stadistics = new String[] {
			"proc_waiting_for_run_time", "proc_uninterruptible_sleep",
			"mem_swapped_kB", "mem_free_kB", "mem_buffers_kB", "mem_cache_kB",
			"swapped_in_kB_per_sec", "swapped_out_kB_per_sec",
			"io_blocks_rcv_per_sec", "io_blocks_sent_per_sec",
			"interrupts_per_sec", "context_switches_per_sec", "cpu_user",
			"cpu_system", "cpu_idle", "cpu_waiting_io", "cpu_st" };

	/**
	 * constructor for StatsVmstat
	 */
	public StatsVmstat() {

		super();

		Logger log = Logger.getLogger(StatsVmstat.class);

		log.debug("Calling StatsVmstat constructor");
		super.setHeader(AddToHeader(stadistics));

		log.debug("My header is  " + super.getHeader());

		log.debug("StatsVmstat constructor Ended");

	}

	/**
	 * @return Executes the vmstat comand and retrieves results in a JSON
	 *         objecty with order : Host, Date, MetricType,StatType and Metrics
	 *         For example :
	 *         {"Host":"centos-63","Date":"2013-04-24 08:59:09","MetricType"
	 *         :"SystemStat"
	 *         ,"StatType":"Vmstat","Metrics":{"proc_waiting_for_run_time"
	 *         :0,"proc_uninterruptible_sleep":0}}
	 */
	public JSONObject getJSONVmStat() {

		JSONObject genericstat = new JSONObject();
		JSONObject vmstatjson = new JSONObject();
		String mydate;
		String myserver;

		try {
			Logger log = Logger.getLogger(StatsVmstat.class);

			log.debug("Initializating JSON object");

			mydate = this.getActualDate();
			myserver = this.getMyhost();

			genericstat.put("Host", myserver);
			genericstat.put("Date", mydate);
			genericstat.put("MetricType", "SystemStat");
			genericstat.put("StatType", "Vmstat");

			log.debug("Executing vmstat command");

			Process prc = Runtime.getRuntime().exec("/usr/bin/vmstat 1 2");
			BufferedReader buffread = new BufferedReader(new InputStreamReader(
					prc.getInputStream()));
			String h1 = buffread.readLine();
			String h2 = buffread.readLine();
			String h3 = buffread.readLine();
			String data = buffread.readLine();
			buffread.close();

			String[] metric = data.trim().split("\\s+");

			for (int i = 0; i < metric.length; i++) {
				vmstatjson.put(stadistics[i], Integer.parseInt(metric[i]));
				log.info("json vmstat value added :" + stadistics[i] + ":"
						+ Integer.parseInt(metric[i]));
			}

			log.debug("Adding metrics to Json Object");
			genericstat.put("Metrics", vmstatjson);
		} catch (Exception e) {
		}
		return genericstat;

	}
}

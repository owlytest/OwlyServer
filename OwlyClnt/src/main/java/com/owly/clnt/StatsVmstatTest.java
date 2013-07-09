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

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class StatsVmstatTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Logger log = Logger.getLogger(StatsVmstatTest.class);

		StatsVmstat testStats = new StatsVmstat();
		JSONObject vmstatjson = new JSONObject();

		System.out.println("Date is: " + testStats.getActualDate());
		System.out.println("My Host  is: " + testStats.getMyhost());
		System.out.println("Header: " + testStats.getHeader());
		log.debug("Before VMstat ");
		vmstatjson = testStats.getJSONVmStat();
		log.debug("After VMstat ");
		System.out.println("JSON: " + vmstatjson.toString());

	}

}

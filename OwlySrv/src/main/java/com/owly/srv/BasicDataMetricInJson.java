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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * This class is used for packaging the object received from the platform to a JSON object that will be sent to the Flot pluging of Jquery
 * @author Antonio Menendez Lopez (tonimenen)
 *
 */
public class BasicDataMetricInJson {

	/**
	 * label is used to add the label that will appear in the graph.
	 */
	private String label;
	/**
	 * data with all information to plot in the graph
	 */
	private JSONArray jsonDataArray;
	
	private static Logger logger = Logger
			.getLogger(BasicDataMetricInJson.class);
	
	/**
	 * Constructor used to initialize the object and assign the label. 
	 * @param label label to show in the graph of the statistic
	 *  
	 */
	public BasicDataMetricInJson(String label) {
		super();
		logger.debug("Calling constructor with label = " + label);
		this.label = label;
		JSONArray jsonArray = new JSONArray();
		this.jsonDataArray=jsonArray;
		logger.debug("Constructor Ended");		
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public JSONArray getJsonDataArray() {
		return jsonDataArray;
	}

	public void setJsonDataArray(JSONArray jsonDataArray) {
		this.jsonDataArray = jsonDataArray;
	}
	
	/**
	 * This method will add  a new json array with values the date in epoch mode, and the value capured. This the way flot pluggin in juqery wants the data.
	 * @param dateOfValue is the time where the metrics was captures
	 * @param Value is the value captured
	 */
	public void addJsonDataArray(Date dateOfValue, float Value){
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(dateOfValue.getTime());
		jsonArray.add(Value);
		this.jsonDataArray.add(jsonArray);
		
	}
	
	/**
	 * @return a json object with the label, and the data in the forma Flot plugin requires.
	 */
	public JSONObject getJson() {
		
		JSONObject json = new JSONObject();
		json.put("label", this.getLabel());
		json.put("data", this.getJsonDataArray());
		
		return json;
	}
	
	/**
	 * This method is used to create the data in this object based in the array of Basic Stats captured from database previously. 
	 * The data has two components : the label will be used to showe in the flot plugin, and the array of data with value,time.   
	 * @param ListBasicStats is the list of captured metris from database
	 * @param offset is the offset obtained with timezone in the customer browser
	 */
	public void insertDataMetrics(ArrayList<BasicStat> ListBasicStats,Integer offset){
		
		//Clean the data till now inserted.
		this.jsonDataArray.clear();
	
			
		
			Iterator it = ListBasicStats.iterator();
			
			//Process all values from the list of metrics to save in a format valid for flot.
			while (it.hasNext()) {
				Date d2= new Date();
				BasicStat s =  (BasicStat) it.next();
				Date d = s.getDate();
				//Change offset to milisseconds
				long msecOffset=((long)offset)*60*1000;
				Float v = Float.parseFloat(s.getValue());
				d2.setTime(d.getTime()- msecOffset);
				//logger.debug("date = " + d + ";date_offset = "+d2+";value = "+v );
				this.addJsonDataArray(d2, v);

		}

		
	}
	

	
	
	@Override
	public String toString() {
		return "{\"label\":\"" + label + "\", \"data\":"
				+ jsonDataArray+"}";
	}
}



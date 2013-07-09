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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * Class for RemoteBasicStat object which contains all properties related to a
 * Statistic collected in a remote server.
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class RemoteBasicStat {

	private String nameServer;
	private String ipServer;
	private String typeServer;
	private String typeOfStat;
	private String MetricType;
	private String Hostname;
	private Date rmtServerDate;
	private Date statServerDate;
	private JSONObject metrics;
	private final SimpleDateFormat dateFormat;

	/**
	 * Constructor for RemoteBasicStat
	 */
	public RemoteBasicStat() {
		Logger log = Logger.getLogger(RemoteBasicStat.class);

		log.debug("Calling RemoteBasicStat constructor");

		GregorianCalendar currentDate = new GregorianCalendar();
		statServerDate = currentDate.getTime();

		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.debug("Actual date is " + dateFormat.format(statServerDate));
	}

	public String getNameServer() {
		return nameServer;
	}

	public void setNameServer(String nameServer) {
		this.nameServer = nameServer;
	}

	public String getIpServer() {
		return ipServer;
	}

	public void setIpServer(String ipServer) {
		this.ipServer = ipServer;
	}

	public String getTypeServer() {
		return typeServer;
	}

	public void setTypeServer(String typeServer) {
		this.typeServer = typeServer;
	}

	public String getTypeOfStat() {
		return typeOfStat;
	}

	public void setTypeOfStat(String typeOfStat) {
		this.typeOfStat = typeOfStat;
	}

	public String getHostname() {
		return Hostname;
	}

	public void setHostname(String hostname) {
		Hostname = hostname;
	}

	public JSONObject getMetrics() {
		return metrics;
	}

	public void setMetrics(JSONObject metrics) {
		this.metrics = metrics;
	}

	public Date getDateRmtServerDate() {
		return rmtServerDate;
	}

	public String getStringRmtServerDate() {

		return dateFormat.format(rmtServerDate);
	}

	public void setRmtServerDate(Date rmtServerDate) {
		this.rmtServerDate = rmtServerDate;
	}
	
	public void setstatServerDate(Date statServerDate) {
		this.statServerDate = statServerDate;
	}
	

	public Date getDateStatServerDate() {
		return statServerDate;
	}

	public String getStringStatServerDate() {
		return dateFormat.format(statServerDate);
	}

	/**
	 * Method which adds in to the object all fields related to the JSON object
	 * obtained from the remote Server HTTP response. Here is an example of this
	 * JSON Object :
	 * {"StatType":"TopCpu","Date":"2013-05-16 09:39:17","Host":"centos-63",
	 * "Metrics"
	 * :{"cpu_idle":97.6,"cpu_steal":0.0,"cpu_wait":0.0,"cpu_low_priority"
	 * :0.0,"cpu_software_interrupts"
	 * :0.0,"cpu_hardware_interrupts":0.0,"cpu_kernel":0.0,"cpu_user":2.4},
	 * "MetricType":"SystemStat"}
	 * 
	 * @param jsonObject
	 */
	public void setRmtSeverfromJSONObject(JSONObject jsonObject) {
		Logger log = Logger.getLogger(RemoteBasicStat.class);

		log.debug("Calling setRmtSeverfromJSONObject");

		log.debug("JSON object to analyze : " + jsonObject.toString());

		this.setTypeOfStat((String) jsonObject.get("StatType"));
		log.debug(" Type Stat : " + this.getTypeOfStat());
		
		if (this.getTypeOfStat().equals("NOK")){
			log.error("Metric received is not OK, check in client side");
		}else{
			this.setHostname((String) jsonObject.get("Host"));
			log.debug(" Hostname : " + this.getHostname());

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String strDateRemote = (String) jsonObject.get("Date");

			try {
				Date dateRemote = dateFormat.parse(strDateRemote);
				this.setRmtServerDate(dateRemote);
				log.debug("Remote date is "
						+ dateFormat.format(this.getDateRmtServerDate()));

			} catch (ParseException e) {
				log.error("Parse date  Error : " + e.getMessage());
				log.error("Exception ::", e);
			}

			this.setMetrics((JSONObject) jsonObject.get("Metrics"));
			log.debug(" Metrics : " + this.getMetrics().toString());

			this.setMetricType((String) jsonObject.get("MetricType"));
			log.debug(" Type of Metric : " + this.getMetricType());
			
			
		}

		

	}

	public String getMetricType() {
		return MetricType;
	}

	public void setMetricType(String metricType) {
		MetricType = metricType;
	}

	@Override
	public String toString() {
		return "RemoteBasicStat [nameServer=" + nameServer + ", ipServer="
				+ ipServer + ", typeServer=" + typeServer + ", typeOfStat="
				+ typeOfStat + ", MetricType=" + MetricType + ", Hostname="
				+ Hostname + ", rmtServerDate=" + rmtServerDate
				+ ", statServerDate=" + statServerDate + ", metrics=" + metrics
				+ ", dateFormat=" + dateFormat + "]";
	}
}

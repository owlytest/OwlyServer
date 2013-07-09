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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * This class is the base class which have data shared for rested of classes.
 * When constructor is called timestamp of execution and hostname where is
 * executed are obtained.
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 **/
public class StatxBase {

	/**
	 * ActualDate : Is the date when the class is called.
	 */
	protected Date ActualDate;
	protected SimpleDateFormat dateFormat;

	/**
	 * myhost : Is the hostname of server where metric is going to be captured
	 */
	protected String myhost;

	/**
	 * header : Is the header which has all mnemonics to related to this metric
	 * in order to know what represents each value.
	 */
	protected String header;

	/**
	 * Constructor which gets timestamp of execution and hostname of the server
	 */
	public StatxBase() {

		Logger log = Logger.getLogger(StatxBase.class);
		InetAddress addr;

		log.debug("Calling StatsBase constructor");
		GregorianCalendar currentDate = new GregorianCalendar();
		ActualDate = currentDate.getTime();

		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.debug("Actual date is " + dateFormat.format(ActualDate));

		try {

			addr = InetAddress.getLocalHost();
			myhost = addr.getHostName();
			log.debug("My Host is " + myhost);

		} catch (UnknownHostException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		log.debug("StatsBase constructor Ended");
	}

	/**
	 * @return Actual execution time in the server.
	 */
	public Date getDateActualDate() {
		return ActualDate;
	}

	/**
	 * @return Actual execution time in the server.
	 */
	public String getActualDate() {
		return dateFormat.format(ActualDate);
	}

	/**
	 * @return Server´s hostname where metric is obtained
	 */
	public String getMyhost() {
		return myhost;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @return Returns header with values in the top of the metric.
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @return Sets header with values in the top of the metric.
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @param values
	 *            is an array of strings with all mnemonics to add in the header
	 * 
	 * @return Returns an String with all values sepparated by commas in the
	 *         header
	 */
	public String AddToHeader(String[] values) {

		StringBuffer bf = new StringBuffer();

		for (int i = 0; i < values.length - 1; i++) {
			bf.append(values[i]).append(", ");
		}

		bf.append(values[values.length - 1]);
		;

		return bf.toString();
	}

}

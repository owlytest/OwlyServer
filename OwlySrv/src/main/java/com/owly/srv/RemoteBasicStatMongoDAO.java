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
import java.util.List;
import java.util.Set;

/**
 * DAO interface which defines the methods to access Mongo DB for object
 * RemoteBasicStat
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public interface RemoteBasicStatMongoDAO {

	/**
	 * Methos which defined the insertion of a RemoteBasicStat in the Mongo DB
	 * 
	 * @param remoteBasicStat
	 */
	public void insertRemoteBasicStat(RemoteBasicStat remoteBasicStat);
	
	
	
	/**
	 * Method to get the list of different stats for this server and in this period of time
	 * @param shortRemoteServer is the server we want to check ( defined by IP and Name ) 
	 * @param minutesToCheck Minutes from now to check in the Database
	 * @return A List of all types of stats collected and saved in the database
	 */
	public List<String> getTypeStatFromServer(ShortRemoteServerId shortRemoteServer, Integer minutesToCheck);
	
	/**
	 * Method to get the list of different stats for this server and in this period of time in a group of servers
	 * @param listShortRemoteServerId is a list of servers identified by Ip and name 
	 * @param minutesToCheck Minutes from now to check in the Database
	 * @return A List of all types of stats collected and saved in the database
	 */
	public List<String> getTypeStatFromServer(ArrayList<ShortRemoteServerId> listShortRemoteServerId, Integer minutesToCheck);
	
	
	/**
	 * Method to get the list of different stats for this server and in this period of time
	 * @param shortRemoteServer is the server we want to check ( defined by IP and Name ) 
	 * @param startDate Startime of the period to check
	 * @param endDate Endtime of perio to check
	 * @return A List of all types of stats collected and saved in the database
	 */

	public List<String> getTypeStatFromServer(ShortRemoteServerId shortRemoteServer, Date startDate, Date endDate);
	
	
	
	
	/**
	 * Method to get the list of different stats for this server and in this period of time in a group of servers
	 * @param listShortRemoteServerId is a list of servers identified by Ip and name 
	 * @param startDate startDate Startime of the period to check
	 * @param endDate endDate Endtime of perio to check
	 * @return A List of all types of stats collected and saved in the database
	 */
	public List<String> getTypeStatFromServer(ArrayList<ShortRemoteServerId> listShortRemoteServerId, Date startDate, Date endDate);
	
	
	/**
	 * Method to get the list of metrics  for this server and in this period of time
	 * @param shortRemoteServer is the server we want to check ( defined by IP and Name )
	 * @param typeOfStat type of stats we want to check in the server
	 * @param minutesToCheck minutesToCheck Minutes from now to check in the Database
	 * @return A List of all types of metrcis collected and saved in the database
	 */
	public Set<String> getTypeMetricFromStat(ShortRemoteServerId shortRemoteServer,String typeOfStat, Integer minutesToCheck);
	
	/**
	 * Method to get the list of metrics  for this server and in this period of time in a group of servers
	 * @param listShortRemoteServerId is a list of servers identified by Ip and name 
	 * @param typeOfStat type of stats we want to check in the server
	 * @param minutesToCheck minutesToCheck Minutes from now to check in the Database
	 * @return A List of all types of metrcis collected and saved in the database
	 */
	public Set<String> getTypeMetricFromStat(ArrayList<ShortRemoteServerId> listShortRemoteServerId,String typeOfStat, Integer minutesToCheck);
	
	/**
	 * Method to get the list of metrics  for this server and in this period of time
	 * @param shortRemoteServer is the server we want to check ( defined by IP and Name )
	 * @param typeOfStat type of stats we want to check in the server
	 * @param startDate Startime of the period to check
	 * @param endDate Endtime of perio to check
	 * @return A List of all types of stats collected and saved in the database
	 */
	public Set<String> getTypeMetricFromStat(ShortRemoteServerId shortRemoteServer,String typeOfStat, Date startDate, Date endDate);
	
	/**
	 * Method to get the list of metrics  for this server and in this period of time in a group of servers
	 * @param listShortRemoteServerId is a list of servers identified by Ip and name 
	 * @param typeOfStat type of stats we want to check in the server
	 * @param startDate Startime of the period to check
	 * @param endDate Endtime of perio to check
	 * @return A List of all types of stats collected and saved in the database
	 */
	public Set<String> getTypeMetricFromStat(ArrayList<ShortRemoteServerId> listShortRemoteServerId,String typeOfStat, Date startDate, Date endDate);
	
	
	/**
	 * Method to get a metric from database sine some minutes ago till now
	 * @param shortRemoteServer contains the name and ip of the server
	 * @param typeOfStat contains type of statistic to obtain
	 * @param typeOfMetric contains type of metrics to obtain
	 * @param minutesToCheck contains minutes to get metrics.
	 * @return List of metrics in this period of time with parameters passed.
	 */
	public ArrayList<BasicStat> getMetricDetails (ShortRemoteServerId shortRemoteServer,String typeOfStat,String typeOfMetric, Integer minutesToCheck);

	
	/**
	 * Method to get a metric from database sine some minutes ago till now
	 * @param shortRemoteServer contains the name and ip of the server
	 * @param typeOfStat contains type of statistic to obtain
	 * @param typeOfMetric contains type of metrics to obtain
	 * @param startDate Startime of the period to check
	 * @param endDate Endtime of perio to check
	 * @return List of metrics in this period of time with parameters passed.
	 */
	public ArrayList<BasicStat> getMetricDetails (ShortRemoteServerId shortRemoteServer,String typeOfStat,String typeOfMetric, Date startDate, Date endDate);
	
	
	
	/**
	 * Response with a boolean if there is statistics in this period of time.
	 * @param shortRemoteServer contains the name and ip of the server
	 * @param typeOfStat contains type of statistic to obtain
	 * @param typeOfMetric contains type of metrics to obtain
	 * @param minutesToCheck contains minutes to get metrics.
	 * @return Return a boolean if metrics exists.
	 */
	public boolean isThereMetricDetails (ShortRemoteServerId shortRemoteServer,String typeOfStat,String typeOfMetric, Integer minutesToCheck);
	
	/**
	 * Response with a boolean if there is statistics in this period of time for a group of servers
	 * @param listShortRemoteServerId is a list of servers identified by Ip and name
	 * @param typeOfStat contains type of statistic to obtain
	 * @param typeOfMetric contains type of metrics to obtain
	 * @param minutesToCheck contains minutes to get metrics.
	 * @return Return a boolean if metrics exists.
	 */
	public boolean isThereMetricDetails (ArrayList<ShortRemoteServerId> listShortRemoteServerId,String typeOfStat,String typeOfMetric, Integer minutesToCheck);
	
	/**
	 * Response with a boolean if there is statistics in this perios of time.
	 * @param shortRemoteServer contains the name and ip of the server
	 * @param typeOfStat contains type of statistic to obtain
	 * @param typeOfMetric contains type of metrics to obtain
	 * @param startDate Startime of the period to check
	 * @param endDate Endtime of perio to check
	 * @return Return a boolean if metrics exists.
	 */
	public boolean isThereMetricDetails (ShortRemoteServerId shortRemoteServer,String typeOfStat,String typeOfMetric, Date startDate, Date endDate);
	
	/**
	 * Response with a boolean if there is statistics in this period of time for a group of servers
	 * @param listShortRemoteServerId is a list of servers identified by Ip and name
	 * @param typeOfStat contains type of statistic to obtain
	 * @param typeOfMetric contains type of metrics to obtain
	 * @param startDate Startime of the period to check
	 * @param endDate Endtime of perio to check
	 * @return Return a boolean if metrics exists.
	 */
	public boolean isThereMetricDetails (ArrayList<ShortRemoteServerId> listShortRemoteServerId,String typeOfStat,String typeOfMetric, Date startDate, Date endDate);
	
	/**
	 * Method to clean old stats from database
	 * @param beforeDate is the date to clean stats before it
	 */
	public void cleanStatsbefore( Date beforeDate);
	
	/**
	 * Method to get the number of stats in the database for a  time previous to this date
	 * @param beforeDate is the date to get stats before it
	 * @return the number of Statistics for a  time previous to this date
	 */
	public long getNumberStatsBefore( Date beforeDate);
}

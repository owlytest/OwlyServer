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

/**
 * DAO Interface which defines the methods to access into Mongo Database for the
 * Stats Server configuration object.
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public interface StatsServerMongoDAO {

	/**
	 * Method to know the number of statsServer object saved into the Database
	 * 
	 * @param statsServer
	 * @return number of object statsServer saved into the database
	 */
	public int numberActualStatsServer(StatsServer statsServer);

	/**
	 * Method which return the number of Stats Server in Database
	 * 
	 * @return Number of Stats Server in database
	 */
	public int numberStatsServer();

	/**
	 * Method to delete the statsServer object from database
	 * 
	 * @param statsServer
	 * 
	 */
	public void deleteStatsServer(StatsServer statsServer);

	/**
	 * Method to insert the statsServer object into database
	 * 
	 * @param statsServer
	 */
	public void insertStatsServer(StatsServer statsServer);

	/**
	 * Method to clean the collection
	 * 
	 */
	public void deleteAllStatsServer();

	/**
	 * Method to get the status of Monitoring Flag in Stadistics Server
	 * 
	 * @return
	 */

	public boolean getMonitoringEnabledStatsServer();

	/**
	 * Method to get the name of the collection stat where statistics are saved.
	 * 
	 * @return
	 */
	public String getStatCollectStatsServer();
	
	/**
	 * Method to get the name values of the stat servers saved in database
	 * 
	 * @return
	 */
	public StatsServer getStatsServer(String DatabaseName, String IPserver, Integer Port, String DBtype);
	
	
	/**
	 * Method used for updating the Statserver based in the key parameters 
	 * 
	 * @return
	 */
	public void updateStatsServer(StatsServer statsrv);

}

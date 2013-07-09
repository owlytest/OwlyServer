
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

/**
 * DAO Interface which defines the methods to access into Mongo Database for the
 * Remote Server configuration object.
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public interface RemoteServerMongoDAO {

	/**
	 * Method to delete the remoteServer object from database
	 * 
	 * @param remoteServer
	 */
	public void deleteRemoteServer(String nameServer, String ipSrv);
	
	/**
	 * Method to delete all Remote Servers from database
	 * 
	 */
	public void deleteAllRemoteServer();

	/**
	 * Method to insert the Remote Server into the Database
	 * 
	 * @param remoteServer
	 */
	public void insertRemoteServer(RemoteServer remoteServer);

	/**
	 * Method to get the IP oF all remote serves in the database
	 * 
	 * @return List of IPs
	 */
	public ArrayList<String> listIPRemoteServer();

	/**
	 * Method to get the Full Remote Server Properties based in the IP of the
	 * Server and its name
	 * 
	 * @param IP
	 *            is the IP of the servers to search
	 * @return All properties of the remote Server
	 */
	public RemoteServer getRemoteServerbyName_IP(String NameSrv, String IP);
	
	/**
	 * Method to get the Full Remote Server Properties based in the IP of the
	 * Server
	 * 
	 * @param IP
	 *            is the IP of the servers to search
	 * @return All properties of the remote Server
	 */
	public RemoteServer getRemoteServerbyIP(String IP);

	
	/**
	 * Method to get the Full list of remote servers in database
	 * 
	 * 
	 * @return All Servers in database
	 */
	public ArrayList<RemoteServer> getRemoteServers();
	
	
	/**
	 * Method to get the number of of remote servers in database
	 * 
	 * 
	 * @return number of remote servers in database
	 */
	public Integer numberRemoteServer();
	
	
	/**
	 * Method to get the number of of remote servers in database
	 * 
	 * 
	 * @return number of remote servers in database
	 */
	public Integer numberRemoteServer(String NameSrv, String IP);
	
	/**
	 * Method to get the number of of remote servers in database
	 * @param listShortRemoteServerId is a list of remote servers defined by name and ip  
	 * @return number of remote servers in database
	 */
	public Integer numberRemoteServer(ArrayList<ShortRemoteServerId> listShortRemoteServerId);
	
	/**
	 * Method which updated the status of the server after executing the
	 * healthcheck
	 * 
	 * @param remoteServer
	 */
	
	public void updateStatus(boolean status, RemoteServer remoteServer);

	/**
	 * Method to add a unique index based on IP and Name
	 */
	public void addUniqueIndex(String Field1, String Field2);

}

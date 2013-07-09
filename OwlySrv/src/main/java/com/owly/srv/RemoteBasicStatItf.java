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
 * Interface for defining the Basic methods to get Stadistics from a remote
 * Server using HTTP request
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public interface RemoteBasicStatItf {

	/**
	 * * Method to execute a remote comand to get a Basic Stadistic in the
	 * remote Server.
	 * 
	 * @param nameSrv
	 * @param ipSrv
	 * @param typeSrv
	 * @param typeStat
	 * @param clientPort
	 * @return an object with the Stadistics recollected.
	 */
	RemoteBasicStat getRemoteStatistic(String nameSrv, String ipSrv,
			String typeSrv, String typeStat, int clientPort);

	/**
	 * Method used for executing in the remote Server healthcheck and check if
	 * the system is active or disabled.
	 * 
	 * @param ipSrv
	 * @param clientPort
	 * @return true if client is active, or false if client is deactive.
	 */
	public boolean getStatusRemoteServer(String ipSrv, int clientPort);

}

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
 * Class for defining the RemoteServer object with all properties related to the
 * server where statistics are going to be executed.
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class RemoteServer {
	private String Name;
	private String NodeIPAddress;
	private String SrvType;
	private int ClientPort;
	private ArrayList<String> ListTypeOfStats;
	private boolean ServerStatus;
	private boolean Enabled;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getNodeIPAddress() {
		return NodeIPAddress;
	}

	public void setNodeIPAddress(String nodeIPAddress) {
		NodeIPAddress = nodeIPAddress;
	}

	public String getSrvType() {
		return SrvType;
	}

	public void setSrvType(String srvType) {
		SrvType = srvType;
	}

	public ArrayList<String> getListTypeOfStats() {
		return ListTypeOfStats;
	}

	public void setListTypeOfStats(ArrayList<String> listTypeOfStats) {
		ListTypeOfStats = listTypeOfStats;
	}

	public int getClientPort() {
		return ClientPort;
	}

	public void setClientPort(int clientPort) {
		ClientPort = clientPort;
	}

	public boolean getServerStatus() {
		return ServerStatus;
	}

	public void setServerStatus(boolean serverStatus) {
		ServerStatus = serverStatus;
	}
	

	public boolean isEnabled() {
		return Enabled;
	}

	public void setEnabled(boolean enabled) {
		Enabled = enabled;
	}

	@Override
	public String toString() {
		return "RemoteServer [Name=" + Name + ", NodeIPAddress="
				+ NodeIPAddress + ", SrvType=" + SrvType + ", ClientPort="
				+ ClientPort + ", ListTypeOfStats=" + ListTypeOfStats
				+ ", ServerStatus=" + ServerStatus + ", Enabled=" + Enabled
				+ "]";
	}

	

}

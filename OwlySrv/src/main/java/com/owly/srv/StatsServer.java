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
 * Class for defining the Object StatsServer with all properties related to the
 * Stadistic Server
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class StatsServer {
	private String Name;
	private boolean Enabled;
	private boolean Monitoring;
	private String StatsDatabase;
	private String StatsDatabaseType;
	private String StatsCollect;
	private int SavedDays;
	private String StatsIpAddress;
	private int StatsPort;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public boolean isEnabled() {
		return Enabled;
	}

	public void setEnabled(boolean enabled) {
		Enabled = enabled;
	}

	public String getStatsDatabase() {
		return StatsDatabase;
	}

	public void setStatsDatabase(String statsDatabase) {
		StatsDatabase = statsDatabase;
	}

	public String getStatsCollect() {
		return StatsCollect;
	}

	public void setStatsCollect(String statsCollect) {
		StatsCollect = statsCollect;
	}

	public String getStatsIpAddress() {
		return StatsIpAddress;
	}

	public void setStatsIpAddress(String statsIpAddress) {
		StatsIpAddress = statsIpAddress;
	}

	public int getStatsPort() {
		return StatsPort;
	}

	public void setStatsPort(int statsPort) {
		StatsPort = statsPort;
	}

	public String getStatsDatabaseType() {
		return StatsDatabaseType;
	}

	public void setStatsDatabaseType(String statsDatabaseType) {
		StatsDatabaseType = statsDatabaseType;
	}

	public boolean isMonitoring() {
		return Monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		Monitoring = monitoring;
	}

	public int getSavedDays() {
		return SavedDays;
	}

	public void setSavedDays(int savedDays) {
		SavedDays = savedDays;
	}

	@Override
	public String toString() {
		return "StatsServer [Name=" + Name + ", Enabled=" + Enabled
				+ ", Monitoring=" + Monitoring + ", StatsDatabase="
				+ StatsDatabase + ", StatsDatabaseType=" + StatsDatabaseType
				+ ", StatsCollect=" + StatsCollect + ", SavedDays=" + SavedDays
				+ ", StatsIpAddress=" + StatsIpAddress + ", StatsPort="
				+ StatsPort + "]";
	}

}

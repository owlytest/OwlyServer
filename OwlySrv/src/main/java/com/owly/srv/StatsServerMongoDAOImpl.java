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

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Class to Implement the StatsServerMongoDAO interface
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class StatsServerMongoDAOImpl implements StatsServerMongoDAO {

	final String CollectionDB = "StatsSrvCfg";
	DBCollection statsSrvCfgCollection;
	private static Logger logger = Logger
			.getLogger(StatsServerMongoDAOImpl.class);

	public StatsServerMongoDAOImpl(DB statsDB) {
		statsSrvCfgCollection = statsDB.getCollection(CollectionDB);

	}

	public int numberActualStatsServer(StatsServer statsServer) {

		logger.debug("Check if already exists the configuration based in configuration file");

		BasicDBObject obj = new BasicDBObject("Name", statsServer.getName())
				.append("Enabled", statsServer.isEnabled())
				.append("StatsDatabase", statsServer.getStatsDatabase())
				.append("StatsDatabaseType", statsServer.getStatsDatabaseType())
				.append("StatsCollect", statsServer.getStatsCollect())
				.append("StatsIpAddress", statsServer.getStatsIpAddress())
				.append("StatsPort", statsServer.getStatsPort())
				.append("Monitoring", statsServer.isMonitoring())
				.append("SavedDays", statsServer.getSavedDays());

		// Check if number of tuples in the database are not more that 1, and is
		// the same as the actual config

		return statsSrvCfgCollection.find(obj).count();

	}
	
	public int numberActualStatsServer(String DatabaseName, String IPserver, Integer Port, String DBtype) {

		logger.debug("Check if already exists the configuration based in configuration file");	
		

		BasicDBObject query = new BasicDBObject("StatsDatabase", DatabaseName)
		.append("StatsDatabaseType", DBtype)
		.append("StatsIpAddress", IPserver)
		.append("StatsPort", Port);

		return statsSrvCfgCollection.find(query).count();

	}
	

	public void deleteStatsServer(StatsServer statsServer) {
		logger.debug("Delete Stats Server configuration in DataBase");

		BasicDBObject obj = new BasicDBObject("Name", statsServer.getName())
				.append("Enabled", statsServer.isEnabled())
				.append("StatsDatabase", statsServer.getStatsDatabase())
				.append("StatsDatabaseType", statsServer.getStatsDatabaseType())
				.append("StatsCollect", statsServer.getStatsCollect())
				.append("StatsIpAddress", statsServer.getStatsIpAddress())
				.append("StatsPort", statsServer.getStatsPort())
				.append("Monitoring", statsServer.isMonitoring())
				.append("SavedDays", statsServer.getSavedDays());

		statsSrvCfgCollection.remove(obj);
		logger.debug("Configuration of Stat Server removed");

	}

	public void insertStatsServer(StatsServer statsServer) {

		logger.debug("Insert Stats Server configuration in DataBase");

		BasicDBObject obj = new BasicDBObject("Name", statsServer.getName())
				.append("Enabled", statsServer.isEnabled())
				.append("StatsDatabase", statsServer.getStatsDatabase())
				.append("StatsDatabaseType", statsServer.getStatsDatabaseType())
				.append("StatsCollect", statsServer.getStatsCollect())
				.append("StatsIpAddress", statsServer.getStatsIpAddress())
				.append("StatsPort", statsServer.getStatsPort())
				.append("Monitoring", statsServer.isMonitoring())
				.append("SavedDays", statsServer.getSavedDays());

		statsSrvCfgCollection.insert(obj);
		logger.debug("Configuration of Stat Server inserted");

	}

	public int numberStatsServer() {
		logger.debug("Check Number of rows for " + CollectionDB + "collection");

		return statsSrvCfgCollection.find().count();

	}

	public void deleteAllStatsServer() {
		logger.debug("Clean all data in collection");
		statsSrvCfgCollection.drop();

		logger.debug("All data cleaned  in collection");

	}

	public boolean getMonitoringEnabledStatsServer() {

		logger.debug("get Status of Stat Server");
		// create an empty query
		BasicDBObject query = new BasicDBObject();
		BasicDBObject obj = new BasicDBObject("Monitoring", true).append("_id",
				false);
		//logger.info("MONGODB : FInd this object : " + obj.toString());

		DBObject res = statsSrvCfgCollection.findOne(query, obj);
		Boolean result = (Boolean) res.get("Monitoring");

		logger.info("MONGODB :Status readed  : " + result);

		return result;
	}

	public String getStatCollectStatsServer() {
		logger.debug("get Nmae of collection wherer Stats are saved ");
		// create an empty query
		BasicDBObject query = new BasicDBObject();
		BasicDBObject obj = new BasicDBObject("StatsCollect", true).append(
				"_id", false);
		//logger.info("MONGODB : FInd this object : " + obj.toString());

		DBObject res = statsSrvCfgCollection.findOne(query, obj);
		String result = (String) res.get("StatsCollect");

		logger.info("MONGODB :Status readed  : " + result);

		return result;
	}

	public StatsServer getStatsServer(String DatabaseName, String IPserver, Integer Port, String DBtype) {
		StatsServer statsrv = new StatsServer();
		
		logger.debug("Get characteristics of server saved");

		BasicDBObject query = new BasicDBObject("StatsDatabase", DatabaseName)
		.append("StatsDatabaseType", DBtype)
		.append("StatsIpAddress", IPserver)
		.append("StatsPort", Port);
		
		DBObject res = statsSrvCfgCollection.findOne(query);
		
		statsrv.setName((String) res.get("Name"));
		statsrv.setEnabled((Boolean) res.get("Enabled"));
		statsrv.setStatsDatabase( (String) res.get("StatsDatabase"));
		statsrv.setStatsDatabaseType( (String) res.get("StatsDatabaseType"));
		statsrv.setStatsCollect( (String) res.get("StatsCollect"));
		statsrv.setStatsIpAddress( (String) res.get("StatsIpAddress"));
		statsrv.setStatsPort( (Integer) res.get("StatsPort"));
		statsrv.setMonitoring((Boolean) res.get("Monitoring"));
		statsrv.setSavedDays( (Integer) res.get("SavedDays"));
		
		return statsrv;		
		
	}

	public void updateStatsServer(StatsServer statsServer) {
		logger.debug("Update the Stat Server based in kay parameters");	
		
		BasicDBObject query = new BasicDBObject("Name", statsServer.getName())
		.append("StatsDatabaseType", statsServer.getStatsDatabaseType())
		.append("StatsIpAddress", statsServer.getStatsIpAddress())
		.append("StatsPort", statsServer.getStatsPort());
		
		BasicDBObject obj = new BasicDBObject("Name", statsServer.getName())
		.append("Enabled", statsServer.isEnabled())
		.append("StatsDatabase", statsServer.getStatsDatabase())
		.append("StatsDatabaseType", statsServer.getStatsDatabaseType())
		.append("StatsCollect", statsServer.getStatsCollect())
		.append("StatsIpAddress", statsServer.getStatsIpAddress())
		.append("StatsPort", statsServer.getStatsPort())
		.append("Monitoring", statsServer.isMonitoring())
		.append("SavedDays", statsServer.getSavedDays());

		statsSrvCfgCollection.update(query, obj);
		logger.debug("Configuration of Stat Server updated");
			}

}

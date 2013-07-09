
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
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Class to Implement the RemoteServerMongoDAO interface
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */
public class RemoteServerMongoDAOImpl implements RemoteServerMongoDAO {

	final String CollectionDB = "RemoteSrvCfg";

	DBCollection remoteSrvCfgCollection;
	private static Logger logger = Logger
			.getLogger(RemoteServerMongoDAOImpl.class);

	public RemoteServerMongoDAOImpl(DB statsDB) {
		remoteSrvCfgCollection = statsDB.getCollection(CollectionDB);

	}

	public void deleteRemoteServer(String nameServer, String ipSrv) {
		logger.info("MONGODB : Drop a remote server based in keys NAme, and IP");
	
		
		BasicDBObject obj = new BasicDBObject("Name", nameServer)
				.append("NodeIPAddress", ipSrv);

		Integer numberRecords = remoteSrvCfgCollection.find(obj).count();
		
		if (numberRecords == 1){
			logger.info("MONGODB : Data to drop : " + obj.toString());
			remoteSrvCfgCollection.remove(obj);
			logger.info("MONGODB : Remove Server executed");
		}else{
			logger.info("MONGODB : Nothing to delete");
		}
		
		


	}

	public void deleteAllRemoteServer() {
		logger.info("MONGODB : Drop all Remote Server configuration in DataBase");

		remoteSrvCfgCollection.drop();

		logger.info("MONGODB : Configuration of Remote Server dropped");

	}

	public void insertRemoteServer(RemoteServer remoteServer) {
		logger.info("MONGODB : Insert Remote Server configuration in DataBase");

		BasicDBObject obj = new BasicDBObject("Name", remoteServer.getName())
				.append("NodeIPAddress", remoteServer.getNodeIPAddress())
				.append("SrvType", remoteServer.getSrvType())
				.append("ClientPort", remoteServer.getClientPort())
				.append("ListTypeOfStats", remoteServer.getListTypeOfStats())
				.append("ServerStatus", remoteServer.getServerStatus()).
				append("Enabled", remoteServer.isEnabled());

		logger.info("MONGODB : Data to insert : " + obj.toString());

		remoteSrvCfgCollection.insert(obj);
		logger.info("MONGODB : Configuration of Remote Server inserted");
	}

	public ArrayList<String> listIPRemoteServer() {
		logger.debug("Get the IPs of remote serverssaved in the database");
		ArrayList<String> ipSrv = new ArrayList<String>();

		// create an empty query
		BasicDBObject query = new BasicDBObject();
		BasicDBObject obj = new BasicDBObject("NodeIPAddress", true).append(
				"_id", false);

		//logger.info("MONGODB : FInd this object : " + obj.toString());

		DBCursor cursor = remoteSrvCfgCollection.find(query, obj);

		try {
			while (cursor.hasNext()) {
				DBObject cur = cursor.next();
				logger.debug("NodeIPAddress : " + cur.get("NodeIPAddress"));
				ipSrv.add((String) cur.get("NodeIPAddress"));

			}
		} finally {
			cursor.close();
		}

		logger.info("MONGODB : List obtained");
		return ipSrv;
	}

	public RemoteServer getRemoteServerbyIP(String IP) {

		RemoteServer remoteServer = new RemoteServer();
		BasicDBObject query = new BasicDBObject("NodeIPAddress", IP);
		logger.info("MONGODB : findOne for : " + query.toString());

		DBObject res = remoteSrvCfgCollection.findOne(query);
		
		logger.info("Remote Server obtained in DB : " + res.toString());

		remoteServer.setName((String) res.get("Name"));
		remoteServer.setNodeIPAddress((String) res.get("NodeIPAddress"));
		remoteServer.setSrvType((String) res.get("SrvType"));
		remoteServer.setClientPort((Integer) res.get("ClientPort"));
		remoteServer.setListTypeOfStats((ArrayList<String>) res
				.get("ListTypeOfStats"));
		remoteServer.setServerStatus( (Boolean) res.get("ServerStatus"));
		remoteServer.setEnabled((Boolean) res.get("Enabled"));
		
		logger.info("Remote Server obtained is : " + remoteServer.toString());

		return remoteServer;
	}

	public void updateStatus(boolean status, RemoteServer remoteServer) {

		logger.info("MONGODB : Updating remote server status  with value "
				+ status);
		BasicDBObject query = new BasicDBObject("Name", remoteServer.getName())
				.append("NodeIPAddress", remoteServer.getNodeIPAddress())
				.append("SrvType", remoteServer.getSrvType());

		logger.info("MONGODB : Updating remote server status  for this query "
				+ query.toString());
		if (status) {
			remoteSrvCfgCollection.update(query, new BasicDBObject("$set",
					new BasicDBObject("ServerStatus", true)));
		} else {
			remoteSrvCfgCollection.update(query, new BasicDBObject("$set",
					new BasicDBObject("ServerStatus", false)));
		}

		logger.info("MONGODB : Remote server status updated ");

	}

	public void addUniqueIndex(String Field1, String Field2) {

		BasicDBObject index = new BasicDBObject(Field1, 1).append(Field2, 1);
		BasicDBObject unique = new BasicDBObject("unique", "true");
		logger.info("MONGODB :Add index for  " + index.toString() + " , "
				+ unique.toString());
		remoteSrvCfgCollection.ensureIndex(index, unique);
		logger.info("MONGODB : Index added ");

	}

	public ArrayList<RemoteServer> getRemoteServers() {
		logger.debug("Get the all remote servers in the database");
		ArrayList<RemoteServer> lstSrv = new ArrayList<RemoteServer>();
		

		// create an empty query
		BasicDBObject query = new BasicDBObject();

		logger.info("MONGODB : Get list of all objects in the database");

		DBCursor cursor = remoteSrvCfgCollection.find(query);

		try {
			while (cursor.hasNext()) {
				DBObject cur = cursor.next();
				
				RemoteServer rmtSrv = new RemoteServer();
				
				rmtSrv.setName((String) cur.get("Name"));
				rmtSrv.setNodeIPAddress((String) cur.get("NodeIPAddress"));
				rmtSrv.setSrvType((String) cur.get("SrvType"));
				rmtSrv.setClientPort((Integer) cur.get("ClientPort"));
				rmtSrv.setListTypeOfStats((ArrayList<String>) cur
						.get("ListTypeOfStats"));
				rmtSrv.setServerStatus((Boolean) cur.get("ServerStatus"));
				rmtSrv.setEnabled((Boolean) cur.get("Enabled"));

				// Add this object into the list.
				lstSrv.add(rmtSrv);				

			}
		} finally {
			cursor.close();
		}

		logger.info("MONGODB : List of remoteservers obtained" + lstSrv.toString());		
		return lstSrv;
	}

	public Integer numberRemoteServer() {
		
		logger.debug("Get the nubers remote servers in the database");
		
		// create an empty query
		BasicDBObject query = new BasicDBObject();

		logger.info("MONGODB : Get number  of all objects in the database");

		Integer num = remoteSrvCfgCollection.find(query).count();
		
		logger.info("MONGODB : Number  of all objects in the database obtained");
		
		return num;
		
	}

	public RemoteServer getRemoteServerbyName_IP(String NameSrv, String IP) {
		
		RemoteServer remoteServer = new RemoteServer();
		BasicDBObject query = new BasicDBObject("NodeIPAddress", IP)
		.append("Name", NameSrv);
		
		logger.info("MONGODB : findOne for : " + query.toString());

		DBObject res = remoteSrvCfgCollection.findOne(query);

		remoteServer.setName((String) res.get("Name"));
		remoteServer.setNodeIPAddress((String) res.get("NodeIPAddress"));
		remoteServer.setSrvType((String) res.get("SrvType"));
		remoteServer.setClientPort((Integer) res.get("ClientPort"));
		remoteServer.setListTypeOfStats((ArrayList<String>) res
				.get("ListTypeOfStats"));
		remoteServer.setServerStatus((Boolean) res.get("ServerStatus"));
		remoteServer.setEnabled((Boolean) res.get("Enabled"));

		return remoteServer;
	}

	public Integer numberRemoteServer(String NameSrv, String IP) {
		logger.debug("Get the nubers remote servers in the database with name="+NameSrv+" and ip="+IP);
		
		BasicDBObject query = new BasicDBObject("NodeIPAddress", IP)
		.append("Name", NameSrv);

		logger.info("MONGODB : Get number  of all objects in the database");
		
		logger.info("MONGODB : Get number  of all objects in the database for this clause"+query.toString());
		Integer num = remoteSrvCfgCollection.find(query).count();
		
		logger.info("MONGODB : Number  of all objects in the database obtained");
		
		return num;	}

	public Integer numberRemoteServer(
			ArrayList<ShortRemoteServerId> listShortRemoteServerId) {
		
		logger.debug("Array received"+listShortRemoteServerId.toString());
		
		BasicDBObject query = new BasicDBObject();
		BasicDBList or = new BasicDBList();
			
		Iterator<ShortRemoteServerId> iterator = listShortRemoteServerId.iterator();
		while (iterator.hasNext()) {
				ShortRemoteServerId shortRmt = iterator.next();
				BasicDBObject clause = new BasicDBObject("NodeIPAddress", shortRmt.getNodeIPAddress()).append("Name", shortRmt.getName());
				or.add(clause);
		}
		query = new BasicDBObject("$or", or);

		
		
		logger.info("MONGODB : Get number  of all objects in the database for this clause"+query.toString());

		Integer num = remoteSrvCfgCollection.find(query).count();
		
		logger.info("MONGODB : Number  of all objects in the database obtained");
		
		return num;
	}
	

}

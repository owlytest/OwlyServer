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
package com.owly.srv.config;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.owly.srv.OSValidator;
import com.owly.srv.RemoteServer;
import com.owly.srv.RemoteServerMongoDAOImpl;
import com.owly.srv.StatsServer;
import com.owly.srv.StatsServerMongoDAOImpl;

/**
 * Class to execute when Application Server starts and stops, doing
 * inittialization of all context and creating inittial data in database with
 * objects readed from the cofiguration file.
 * 
 * 
 * @author OwlySrv
 * 
 */

public class ApplicationListener implements ServletContextListener,
		ErrorHandler {

	private static Logger logger = Logger.getLogger(ApplicationListener.class);
	private StatsServer InitStatServer = new StatsServer();
	private ArrayList<RemoteServer> ListInitRemoteServer = new ArrayList<RemoteServer>();
	

	public static final String QUARTZ_FACTORY_KEY = "org.quartz.impl.StdSchedulerFactory.KEY";

	private boolean performShutdown = true;
	private boolean waitOnShutdown = false;

	private Scheduler scheduler = null;

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if (!performShutdown) {
			return;
		}

		try {
			if (scheduler != null) {
				scheduler.shutdown(waitOnShutdown);
			}
		} catch (Exception e) {
			logger.error("Quartz Scheduler failed to shutdown cleanly: "
					+ e.toString());
			logger.error("Exception ::", e);
			e.printStackTrace();
		}

		logger.info("Quartz Scheduler successful shutdown.");
		logger.info("Shutdown : context destroyed");

	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext context = servletContextEvent.getServletContext();
		
		StdSchedulerFactory factory;

		String PropXML = context.getInitParameter("PropertiesXML");
		String PropXSD = context.getInitParameter("PropertiesXSD");
		
		//Format for operating system
		String osNameXML = Format4OSDependent(PropXML);
		String osNameXSD = Format4OSDependent(PropXSD);
		
		//get file component
		String nameXML = fileComponent(osNameXML);
		String nameXSD = fileComponent(osNameXSD);
		
		String catalinaBase=System.getProperty("catalina.base");
		logger.debug("This catalina base : " + catalinaBase);
		
		logger.debug("Files to check in folder properties : " + nameXML +" and " + nameXSD);
		
		String PropertiesXML=catalinaBase+File.separator+"properties"+File.separator+nameXML;
		
		logger.info("XML file to check : " + PropertiesXML);
		
		String PropertiesXSD=catalinaBase+File.separator+"properties"+File.separator+nameXSD;
		
		logger.info("XSD file to check : " + PropertiesXSD);

		logger.info("Checking if properties files exist");

		// Checking if properties files exist
		if (FileExists(PropertiesXML)) {
		} else {
			RuntimeException e = new RuntimeException(
					"Error starting the Application ");
			throw e;
		}

		if (FileExists(PropertiesXSD)) {
		} else {
			RuntimeException e = new RuntimeException(
					"Error starting the Application ");
			throw e;
		}

		// Read values for the Stat Server defined in the properfties file
		logger.info("Check for validation of XML, and read values for the Stats Server defined in the properfties file");
		InitStatServer = getStatsServerConfiguration(PropertiesXML);
		logger.info("Configuration of the Stats Server : "
				+ InitStatServer.toString());
		logger.info("XML confuguration file read correctly");

		// Check and read values for RemoteServers from configuration files
		logger.info("Check and read values for  RemoteServers from configuration files");
		ListInitRemoteServer = getRemoteServerConfiguration(PropertiesXML);
		logger.info("RemoteServers loaded in Memory");

		// Print Remoter Server Variable just for test (if it is needed)
		// for (int i = 0; i < ListInitRemoteServer.size(); i++) {
		// logger.debug("Remote Server in list (" + i + ") : "
		// + ListInitRemoteServer.get(i).toString());
		// }

		// Check if database is UP and running and properly populated.
		logger.info("Check if database is UP and running and properly populated");
		if (checkDatabaseStatus(InitStatServer, ListInitRemoteServer)) {
		} else {
			RuntimeException e = new RuntimeException(
					"Error starting the Application ");
			throw e;
		}
		logger.info("Database Up and Running");
		logger.info("Starting Scheduler");
		try {

			String configFile = context.getInitParameter("quartz:config-file");
			if (configFile == null)
				configFile = context.getInitParameter("config-file"); // older
																		// name,
																		// for
																		// backward
																		// compatibility
			String shutdownPref = context
					.getInitParameter("quartz:shutdown-on-unload");
			if (shutdownPref == null)
				shutdownPref = context.getInitParameter("shutdown-on-unload");
			if (shutdownPref != null) {
				performShutdown = Boolean.valueOf(shutdownPref).booleanValue();
			}
			String shutdownWaitPref = context
					.getInitParameter("quartz:wait-on-shutdown");
			if (shutdownPref != null) {
				waitOnShutdown = Boolean.valueOf(shutdownWaitPref)
						.booleanValue();
			}

			factory = getSchedulerFactory(configFile);

			// Always want to get the scheduler, even if it isn't starting,
			// to make sure it is both initialized and registered.
			scheduler = factory.getScheduler();
			

			// Should the Scheduler being started now or later
			String startOnLoad = context
					.getInitParameter("quartz:start-on-load");
			if (startOnLoad == null)
				startOnLoad = context
						.getInitParameter("start-scheduler-on-load");

			int startDelay = 0;
			String startDelayS = context
					.getInitParameter("quartz:start-delay-seconds");
			if (startDelayS == null)
				startDelayS = context.getInitParameter("start-delay-seconds");
			try {
				if (startDelayS != null && startDelayS.trim().length() > 0)
					startDelay = Integer.parseInt(startDelayS);
			} catch (Exception e) {
				logger.error("Cannot parse value of 'start-delay-seconds' to an integer: "
						+ startDelayS + ", defaulting to 5 seconds.");
				logger.error("Exception ::", e);
				startDelay = 5;
			}

			/*
			 * If the "quartz:start-on-load" init-parameter is not specified,
			 * the scheduler will be started. This is to maintain backwards
			 * compatability.
			 */
			if (startOnLoad == null
					|| (Boolean.valueOf(startOnLoad).booleanValue())) {
				if (startDelay <= 0) {
					// Start now
					scheduler.start();
					logger.info("Scheduler has been started...");
				} else {
					// Start delayed
					scheduler.startDelayed(startDelay);
					logger.info("Scheduler will start in " + startDelay
							+ " seconds.");
				}
			} else {
				logger.info("Scheduler has not been started. Use scheduler.start()");
			}

			String factoryKey = context
					.getInitParameter("quartz:servlet-context-factory-key");
			if (factoryKey == null)
				factoryKey = context
						.getInitParameter("servlet-context-factory-key");
			if (factoryKey == null) {
				factoryKey = QUARTZ_FACTORY_KEY;
			}

			logger.info("Storing the Quartz Scheduler Factory in the servlet context at key: "
					+ factoryKey);
			context.setAttribute(factoryKey, factory);

			String servletCtxtKey = context
					.getInitParameter("quartz:scheduler-context-servlet-context-key");
			if (servletCtxtKey == null)
				servletCtxtKey = context
						.getInitParameter("scheduler-context-servlet-context-key");
			if (servletCtxtKey != null) {
				logger.info("Storing the ServletContext in the scheduler context at key: "
						+ servletCtxtKey);
				scheduler.getContext().put(servletCtxtKey, context);
			}

		} catch (Exception e) {
			logger.error("Quartz Scheduler failed to initialize: "
					+ e.toString());
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		// Save in the Scheduler  and in aaplication context the variables related to Database
		// Startup
		try {
			logger.info("Storing the ServletContext in the scheduler and application context values of Database with key : DBNAME and value : "
					+ InitStatServer.getName());
			scheduler.getContext().put("DBNAME",
					InitStatServer.getStatsDatabase());			
			context.setAttribute("DBNAME",
					InitStatServer.getStatsDatabase());
			
			logger.info("Storing the ServletContext in the scheduler and application context values of Database with key : IPSERVER and value : "
					+ InitStatServer.getStatsIpAddress());
			scheduler.getContext().put("IPSERVER",
					InitStatServer.getStatsIpAddress());
			context.setAttribute("IPSERVER",
					InitStatServer.getStatsIpAddress());
			
			logger.info("Storing the ServletContext in the scheduler and application context values of Database with key : PORTSERVER  and value : "
					+ InitStatServer.getStatsPort());
			scheduler.getContext().put("PORTSERVER",
					InitStatServer.getStatsPort());
			context.setAttribute("PORTSERVER",
					InitStatServer.getStatsPort());
			
			logger.info("Storing the ServletContext in the scheduler and application  context values of Database with key : DATABASETYPE  and value : "
					+ InitStatServer.getStatsPort());
			scheduler.getContext().put("DATABASETYPE",
					InitStatServer.getStatsDatabaseType());
			context.setAttribute("DATABASETYPE",
					InitStatServer.getStatsDatabaseType());
			
			// Get the repeat interval and add as a environment varaible.
			JobKey  mainJob = new JobKey("OwlySrvMainJob","DEFAULT");
			SimpleTriggerImpl trigger = new SimpleTriggerImpl();
			List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(mainJob);
			trigger = (SimpleTriggerImpl) triggers.get(0);
			logger.info("Get repeat interval from Scheduler"+ trigger.getRepeatInterval());
			
			logger.info("Storing the ServletContext in the scheduler and application  context values of Database with key : REPEATINTERVAL  and value : "
					+ trigger.getRepeatInterval());
			scheduler.getContext().put("REPEATINTERVAL",
					trigger.getRepeatInterval());
			context.setAttribute("REPEATINTERVAL",
					trigger.getRepeatInterval());

			
			
			

		} catch (SchedulerException e) {
			logger.error("Quartz Scheduler exception: " + e.toString());
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		
		

		logger.info("Start-Up : context Initialized");

	}

	/**
	 * Checks if a file exists in the system
	 * 
	 * @param FileName
	 *            is the file to check
	 * @return a boolean with the result of the checking
	 */
	public boolean FileExists(String FileName) {
		boolean exists = true;

		logger.debug("Checking if file " + FileName + " Exists");
		

		File file = new File(FileName);
		
		String filePath = file.getAbsolutePath();
		logger.debug("This the path of the file : " + filePath);

		exists = file.exists();

		if (exists) {
			logger.debug("File " + FileName + " Exists");
		} else {
			logger.error("File " + FileName + " Does Not Exists");
		}

		return exists;
	}

	/**
	 * This metod is used for formating filenames from linux to windows and the same in other side
	 * @param Filename Name of the file
	 * @return Filename for spefic OS.
	 */
	String Format4OSDependent(String Filename){
		String newFilename;
		OSValidator osValidator = new OSValidator();
		if (osValidator.isWindows()){
			newFilename = Filename.replace("/", "\\");
		}else
			if (OSValidator.isUnix()){
				newFilename = Filename.replace("\\", "/");
				
			}else
			{
				newFilename = Filename;
			}
		
		return newFilename;
	}
	/**
	   * Remove path information from a filename returning only its file component
	   * 
	   * @param filename
	   *            The filename
	   * @return The filename without path information

	   */
	  public  String fileComponent(String filename) {
		  
	      int i = filename.lastIndexOf(File.separator);
	      return (i > -1) ? filename.substring(i + 1) : filename;
	  }
	
	/**
	 * Method executed for the validation of the configuration XML file with its
	 * XSD file, and parsing the file to get the object of the Stadistics
	 * Server. in case of validation OK returns the Stats Server Configuration.
	 * 
	 * @param FileNameXML
	 *            is the XML file to validate
	 * @return an object with the configuration of the Stats Server
	 */

	StatsServer getStatsServerConfiguration(String FileNameXML) {

		/** Validation feature id (http://xml.org/sax/features/validation). */
		final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

		/**
		 * Schema validation feature id
		 * (http://apache.org/xml/features/validation/schema).
		 */
		final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

		StatsServer statisticsServer = new StatsServer();

		DOMParser parser = new DOMParser();

		try {
			parser.setFeature(VALIDATION_FEATURE_ID, true);
			parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);

		} catch (SAXNotRecognizedException e) {
			logger.error("Feature not recognized: " + e.getMessage());
			logger.error("Exception ::", e);
		} catch (SAXNotSupportedException e) {
			logger.error("Feature not supported: " + e.getMessage());
			logger.error("Exception ::", e);
		}
		// Register Error Handler
		parser.setErrorHandler(this);

		// Parse the XML fie with configuration
		try {
			parser.parse(FileNameXML);
		} catch (SAXException e) {
			logger.error("Parsing XML failed due to a "
					+ e.getClass().getName() + ":");
			logger.error(e.getMessage());
			logger.error("Exception ::", e);
		} catch (IOException e) {
			logger.error("Could not read file: " + e.getMessage());
			logger.error("Exception ::", e);
		}

		// Process the XML an read the configuration for the Stats Server
		Document doc = parser.getDocument();
		logger.debug("Root element :" + doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("StatsServer_Configuration");

		Node nNode = nList.item(0);

		logger.debug("Current Element :" + nNode.getNodeName());

		// Read all properties for the Stats Server object.
		Element eElement = (Element) nNode;

		// Attribute : name
		logger.debug("name : " + eElement.getAttribute("name"));
		statisticsServer.setName(eElement.getAttribute("name"));

		// Attribute : enabled
		logger.debug("enabled : " + eElement.getAttribute("enabled"));
		statisticsServer.setEnabled(Boolean.valueOf(eElement
				.getAttribute("enabled")));

		// Attribute : Monitoring
		logger.debug("Monitoring : " + eElement.getAttribute("Monitoring"));
		statisticsServer.setMonitoring(Boolean.valueOf(eElement
				.getAttribute("Monitoring")));

		// field : StatsDatabase
		logger.debug("StatsDatabase : "
				+ eElement.getElementsByTagName("StatsDatabase").item(0)
						.getTextContent());
		statisticsServer
				.setStatsDatabase(eElement
						.getElementsByTagName("StatsDatabase").item(0)
						.getTextContent());

		// field : StatsDatabaseType
		logger.debug("StatsDatabaseType : "
				+ eElement.getElementsByTagName("StatsDatabaseType").item(0)
						.getTextContent());
		statisticsServer.setStatsDatabaseType(eElement
				.getElementsByTagName("StatsDatabaseType").item(0)
				.getTextContent());

		// field : StatsCollect
		logger.debug("StatsCollect : "
				+ eElement.getElementsByTagName("StatsCollect").item(0)
						.getTextContent());
		statisticsServer.setStatsCollect(eElement
				.getElementsByTagName("StatsCollect").item(0).getTextContent());

		// field : SavedDays
		logger.debug("SavedDays : "
				+ eElement.getElementsByTagName("SavedDays").item(0)
						.getTextContent());
		statisticsServer.setSavedDays(Integer.valueOf(eElement
				.getElementsByTagName("SavedDays").item(0).getTextContent()));

		// field : StatsIpAddress
		logger.debug("StatsIpAddress : "
				+ eElement.getElementsByTagName("StatsIpAddress").item(0)
						.getTextContent());
		statisticsServer.setStatsIpAddress(eElement
				.getElementsByTagName("StatsIpAddress").item(0)
				.getTextContent());

		// field : StatsPort
		logger.debug("StatsPort : "
				+ eElement.getElementsByTagName("StatsPort").item(0)
						.getTextContent());
		statisticsServer.setStatsPort(Integer.valueOf(eElement
				.getElementsByTagName("StatsPort").item(0).getTextContent()));

		return statisticsServer;

	}

	public void error(SAXParseException e) throws SAXException {
		logger.error("Error:  " + e);
		RuntimeException ex = new RuntimeException(
				"Error starting the Application ");
		throw ex;

	}

	public void fatalError(SAXParseException e) throws SAXException {
		logger.error("Fatal Error:  " + e);
		RuntimeException ex = new RuntimeException(
				"Error starting the Application ");
		throw ex;

	}

	public void warning(SAXParseException e) throws SAXException {
		logger.error("Warning:  " + e);
		RuntimeException ex = new RuntimeException(
				"Error starting the Application ");
		throw ex;

	}

	/**
	 * This methond checks status of the stadistics server database
	 * 
	 * @param StadisticsServer
	 * @param listInitRemoteServer
	 * @return a bolean true in case parameters of database are OK, and database
	 *         is up.
	 */
	public boolean checkDatabaseStatus(StatsServer StadisticsServer,
			ArrayList<RemoteServer> listInitRemoteServer) {

		final String CollectionDB = "StatsSrvCfg";

		// logger.debug("Configuration of the Stats Server : " +
		// StadisticsServer.toString());

		if (!StadisticsServer.isEnabled()) {
			logger.error("Statistics server is disabled, check configuration file");
			RuntimeException ex = new RuntimeException(
					"Error starting the Application ");
			throw ex;

		} else {
			if (!StadisticsServer.getStatsDatabaseType().equals("MONGODB")) {
				logger.error("Statistics server database should be MongoDB (others not implemented), check configuration file");
				RuntimeException ex = new RuntimeException(
						"Error starting the Application ");
				throw ex;
			} else {
				if (!StadisticsServer.getStatsIpAddress().equals("127.0.0.1")) {
					logger.info("Statistics server IP is not 127.0.0.1 --> Database is a remote server");					
				} else {
					logger.info("Statistics server IP is  127.0.0.1 --> Database is local server");
				}
				
					logger.debug("Checking if MongoDB is up and running for IP : "
							+ StadisticsServer.getStatsIpAddress()
							+ " and port : " + StadisticsServer.getStatsPort());
					try {

						/**** Connect to MongoDB ****/
						// Since 2.10.0, uses MongoClient
						MongoClient mongoClient = new MongoClient(
								StadisticsServer.getStatsIpAddress(),
								StadisticsServer.getStatsPort());

						try {
							Socket socket = mongoClient.getMongoOptions().socketFactory
									.createSocket();
							socket.connect(mongoClient.getAddress()
									.getSocketAddress());
							socket.close();
						} catch (IOException e) {
							logger.error("mongoDB Error : " + e.getMessage());
							logger.error("Exception ::", e);
							RuntimeException ex = new RuntimeException(
									"Error starting the Application ");
							throw ex;
							
						}

						/**** Get database ****/
						// if database doesn't exists, MongoDB will create it
						// for you
						DB statsDB = mongoClient.getDB(StadisticsServer
								.getStatsDatabase());

						StatsServerMongoDAOImpl statServerCfg = new StatsServerMongoDAOImpl(
								statsDB);

						RemoteServerMongoDAOImpl remoteServerCfg = new RemoteServerMongoDAOImpl(
								statsDB);

						// Drop all previous configuration in the database

						statServerCfg.deleteAllStatsServer();
						//remoteServerCfg.deleteAllRemoteServer();

						// Create new configuration in database
						int numberStats = statServerCfg.numberStatsServer();

						if (numberStats == 0) {
							logger.debug("Actual Server Stats configuration NOT in database");
							logger.debug("Insert configuration in Database");

							// Insert Stats Server in Database
							statServerCfg.insertStatsServer(StadisticsServer);

							// Insert Remote Servers in the Database
							for (int srv = 0; srv < listInitRemoteServer.size(); srv++) {
								logger.debug("Remote Server to Insert in database ("
										+ srv
										+ ") : "
										+ listInitRemoteServer.get(srv)
												.toString());
								RemoteServer remoteServer = listInitRemoteServer.get(srv);
								String nameSrv = remoteServer.getName();
								String ipSrv = remoteServer.getNodeIPAddress();
								
								// Drop the server from database is already exists
								remoteServerCfg.deleteRemoteServer(nameSrv, ipSrv);
								
								// Insert the remote server. 
								remoteServerCfg
										.insertRemoteServer(listInitRemoteServer
												.get(srv));
								logger.debug("Remote Serverinserted");

							}

							// Add a unique index based on IP and name
							remoteServerCfg.addUniqueIndex("NodeIPAddress",
									"Name");

						} else {
							logger.error("Configuration in database "
									+ CollectionDB
									+ " is not OK, manual action is required");
							// Close the conection to database
							logger.info("Closing MongoClient");
							mongoClient.close();
							RuntimeException ex = new RuntimeException(
									"Error starting the Application ");
							throw ex;
						}

						// Close the conection to database
						logger.info("Closing MongoClient");
						mongoClient.close();

					} catch (UnknownHostException e) {
						logger.error("mongoDB Error : " + e.getMessage());
						logger.error("Exception ::", e);
						RuntimeException ex = new RuntimeException(
								"Error starting the Application ");
						throw ex;
					}

				}
			}


		return true;

	}

	/**
	 * This method to get the configuration of remote server in the
	 * configuration database
	 * 
	 * @param FileNameXML
	 * @return an ArraList of RemoteServer with configuration of all remote
	 *         servers.
	 */
	ArrayList<RemoteServer> getRemoteServerConfiguration(String FileNameXML) {

		/** Validation feature id (http://xml.org/sax/features/validation). */
		final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

		/**
		 * Schema validation feature id
		 * (http://apache.org/xml/features/validation/schema).
		 */
		final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

		ArrayList<RemoteServer> ListRemoteServer = new ArrayList<RemoteServer>();
		ArrayList<String> ListTypeOfStats = new ArrayList<String>();

		DOMParser parser = new DOMParser();

		try {
			parser.setFeature(VALIDATION_FEATURE_ID, true);
			parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);

		} catch (SAXNotRecognizedException e) {
			logger.error("Feature not recognized: " + e.getMessage());
			logger.error("Exception ::", e);
		} catch (SAXNotSupportedException e) {
			logger.error("Feature not supported: " + e.getMessage());
			logger.error("Exception ::", e);
		}
		// Register Error Handler
		parser.setErrorHandler(this);

		// Parse the XML fie with configuration
		try {
			parser.parse(FileNameXML);
		} catch (SAXException e) {
			logger.error("Parsing XML failed due to a "
					+ e.getClass().getName() + ":");
			logger.error("Exception ::", e);
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error("Could not read file: " + e.getMessage());
			logger.error("Exception ::", e);
		}

		// Process the XML an read the configuration for the Stats Serve
		Document doc = parser.getDocument();
		logger.debug("Root element :" + doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("node");

		for (int i = 0; i < nList.getLength(); i++) {

			RemoteServer remoteServer = new RemoteServer();
			Node nNode = nList.item(i);

			logger.debug("Current Element :" + nNode.getNodeName());

			// Read all properties for the Remote Server object.
			Element eElement = (Element) nNode;

			// Attribute : name
			String ServerName = eElement.getAttribute("name");
			logger.debug("name : " + ServerName);
			remoteServer.setName(ServerName);
			
			
			// Attribute : enabled
			logger.debug("enabled : " + eElement.getAttribute("enabled"));
			remoteServer.setEnabled(Boolean.valueOf(eElement
					.getAttribute("enabled")));
			

			// field : NodeIPAddress
			logger.debug("NodeIPAddress : "
					+ eElement.getElementsByTagName("NodeIPAddress").item(0)
							.getTextContent());
			remoteServer.setNodeIPAddress(eElement
					.getElementsByTagName("NodeIPAddress").item(0)
					.getTextContent());

			// field : SrvType
			logger.debug("SrvType : "
					+ eElement.getElementsByTagName("SrvType").item(0)
							.getTextContent());
			remoteServer.setSrvType(eElement.getElementsByTagName("SrvType")
					.item(0).getTextContent());

			// field : ClientPort
			logger.debug("ClientPort : "
					+ eElement.getElementsByTagName("ClientPort").item(0)
							.getTextContent());
			remoteServer.setClientPort(Integer.valueOf(eElement
					.getElementsByTagName("ClientPort").item(0)
					.getTextContent()));

			// getting all types of stadistics to execute in this servers
			NodeList nListStType = doc.getElementsByTagName("StatsType");

			// Clear the array with types of Stats that we are going to fill
			ListTypeOfStats.clear();

			for (int j = 0; j < nListStType.getLength(); j++) {
				Node nNodeStType = nListStType.item(j);
				// logger.debug("Current Element :" +
				// nNodeStType.getNodeName());

				// Read all properties for the Type Stats Server object.
				Element stElement = (Element) nNodeStType;

				// Number of elements type StatsNode
				int elementLength = stElement.getElementsByTagName("StatsNode")
						.getLength();

				for (int k = 0; k < elementLength; k++) {
					String ServerElement = stElement
							.getElementsByTagName("StatsNode").item(k)
							.getTextContent();

					// In case the server inside the StatsType tag, is the one
					// we are looping we will add the type of stat
					if (ServerElement.equals(ServerName)) {

						// logger.debug("StatsNode : " + ServerElement);

						// attribute : stat
						logger.debug("stat : " + stElement.getAttribute("stat"));
						ListTypeOfStats.add(stElement.getAttribute("stat"));

						// attribute : type
						// logger.debug("type : " +
						// stElement.getAttribute("type"));

					}
					remoteServer.setListTypeOfStats(ListTypeOfStats);

				}
			}

			// When remote Server is inserted we will save as Enable, later we
			// will check with healthCheck the status.
			remoteServer.setServerStatus(true);
			logger.debug("remote server to add " + remoteServer.toString());
			ListRemoteServer.add(remoteServer);
		}

		return ListRemoteServer;

	}

	protected StdSchedulerFactory getSchedulerFactory(String configFile)
			throws SchedulerException {
		StdSchedulerFactory factory;
		// get Properties
		if (configFile != null) {
			factory = new StdSchedulerFactory(configFile);
		} else {
			factory = new StdSchedulerFactory();
		}
		return factory;
	}

}
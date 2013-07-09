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

/**
 * This class is used to check the operating system running in the application, and depending on this, the application will send different responses.
 * 
 * @author Antonio Menendez Lopez (tonimenen)
 *
 */
public class OSValidator {

	/**
	 * Os is the operating system where application is executed.
	 */
	private static String OS;
	
	/**
	 * Constructor for the class which gets the operating system.
	 */
	public OSValidator() {
		
		super();
		Logger log = Logger.getLogger(OSValidator.class);
		log.debug("Starting constructor for OSValidator");
		OS = System.getProperty("os.name").toLowerCase();
		log.debug("Ending constructor for OSValidator");
	}

	public boolean isWindows() {
		
		 
		return (OS.indexOf("win") >= 0);
		//return (OS.indexOf("winpp") >= 0);
	}
 
	public static boolean isMac() {
 
		return (OS.indexOf("mac") >= 0);
 
	}
 
	public static boolean isUnix() {
 
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
 
	}
 
	public static boolean isSolaris() {
 
		return (OS.indexOf("sunos") >= 0);
 
	}

	public String getOS() {
		return OS;
	}

	public void setOS(String oS) {
		OS = oS;
	}

	
	

}

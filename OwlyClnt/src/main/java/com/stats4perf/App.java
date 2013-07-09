package com.stats4perf;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Logger log = Logger.getLogger(App.class);
    	
    	log.trace("Hello World!");
    	log.debug("How are you today?");
    	log.info("I am fine.");
    	log.error("I am programming.");
    	log.warn("I love programming.");
    	log.fatal("I am now dead. I should have been a movie star.");
    	
        //System.out.println( "Hello World!" );
        
    }
}

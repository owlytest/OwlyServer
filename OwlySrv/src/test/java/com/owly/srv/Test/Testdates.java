package com.owly.srv.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Testdates {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();
	    System.out.println("current: "+c.getTime());

	    TimeZone z = c.getTimeZone();
	    System.out.println("TimeZone: " + z.toString());
	    int offset = z.getRawOffset();
	    System.out.println("offset: " + offset);
	    if(z.inDaylightTime(new Date())){
	        offset = offset + z.getDSTSavings();
	    }
	    System.out.println("offset: " + offset);
	    int offsetHrs = offset / 1000 / 60 / 60;
	    int offsetMins = offset / 1000 / 60 % 60 ;

	    System.out.println("offset for hours: " + offsetHrs);
	    System.out.println("offset for minutes: " + offsetMins);

	    c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
	    c.add(Calendar.MINUTE, (-offsetMins));
	    System.out.println("modified: "+c.toString());

	    System.out.println("GMT Time: "+c.getTime());

	}

}

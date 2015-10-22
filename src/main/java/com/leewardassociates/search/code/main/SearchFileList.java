package com.leewardassociates.search.code.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.services.SearchFileListService;

public class SearchFileList {

	private static Logger log = LoggerFactory.getLogger(SearchEmptyFiles.class);

	public static void main(String[] args) {
		log.info("Process started...");
		long start = System.currentTimeMillis();
		SearchFileListService sflr = new SearchFileListService(args);
		try {
			sflr.execute();
		} catch (Exception e) {
			log.error("Exception in main: "+e.getMessage(), e);
		} finally {
			log.info("Execution complete. Process took " + getTimePeriod((System.currentTimeMillis()-start)) + " to complete.");
			System.exit(0);
		}
	}

	public static String getTimePeriod(long milliseconds) {
		String retTime = "";
		if (milliseconds > 3600000) {
			int hours = getHours(milliseconds);
			int mins = getMinutes(milliseconds%3600000);
			int secs = getSeconds((milliseconds%3600000)%60000);
			retTime = hours + (hours==1?" hour, ":" hours, ") + (mins==1?(mins+" minute, "):(mins + " minutes, ")) + (secs==1?(secs+" second"):(secs+" seconds"));
		} else if (milliseconds > 60000) {
			int mins = getMinutes(milliseconds);
			int secs = getSeconds(milliseconds%60000);
			retTime = (mins==1?(mins+" minute, "):(mins + " minutes, ")) + (secs==1?(secs+" second"):(secs+" seconds"));
		} else if (milliseconds > 1000) {
			int secs = getSeconds(milliseconds);
			retTime = secs==1?(secs+" second"):(secs+" seconds");
		} else {
			retTime = milliseconds + " ms";
		}
		return retTime;
	}
	
	private static int getHours(long milliseconds) {
		return (int)milliseconds/3600000;
	}

	private static int getMinutes(long milliseconds) {
		return (int)milliseconds/60000;
	}
	
	private static int getSeconds(long milliseconds) {
		return (int)milliseconds/1000;
	}

}

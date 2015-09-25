package com.leewardassociates.search.threads;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;

import com.leewardassociates.search.constants.AppConstants;
import com.leewardassociates.search.models.ParamModel;

public abstract class SearchThread implements Runnable {
	
	protected ArrayBlockingQueue<String> inqueue = null;
	protected FileWriterThread fw = null;
	protected ParamModel params = null;
	protected int counter = 0;

	
	protected void process(Logger log) {
		String param;
		while (true) {
			try {
				param = inqueue.poll();
				if (param != null) {
					// If the queue is empty, break from this thread.
					if (AppConstants.END_PROCESSING.equals(param)) {
						inqueue.add(param);
						break;
					}
					search(new File(params.getRoot()).listFiles(), param, fw);
				}
			} catch (Exception e) {
				log.error("IOException processing line: " + e.getMessage(), e);
			}
		}
	}
	
	protected abstract void search(File[] files, String param, FileWriterThread fw)  throws Exception;
}

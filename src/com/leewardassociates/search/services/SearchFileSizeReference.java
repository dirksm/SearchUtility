package com.leewardassociates.search.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.code.util.DateUtil;
import com.leewardassociates.search.constants.AppConstants;
import com.leewardassociates.search.models.ParamModel;
import com.leewardassociates.search.threads.FileSizeProducer;
import com.leewardassociates.search.threads.FileSizeSearchThread;
import com.leewardassociates.search.threads.FileWriterThread;
import com.leewardassociates.search.threads.ReferenceProducer;
import com.leewardassociates.search.threads.ReferenceSearchThread;

public class SearchFileSizeReference {

	private static Logger log = LoggerFactory.getLogger(SearchFileSizeReference.class);
	

	private int threadCount = 0;
	ArrayBlockingQueue<String> inqueue = null;
	OutputStream target = null;
	ParamModel params = null;
	FileWriterThread fw = null;
	Thread t = null;
	
	private SearchFileSizeReference() {
		params = new ParamModel();
		threadCount = 20;
	}
	
	public SearchFileSizeReference(String[] args) {
		this();

		/**
		 * Following arguments:
		 * Case insensitive
		 * Search Project or Workspace
		 * Phase
		 * SearchWorkspace [options [-i -p [-phase1 | -phase2]]]
		 */
		processInputs(args);
	}
	
	public void execute() {

		try {
			
			String fileName = "Empty_Objects_"+(params.isSearchAllCode()?"":params.getRoot().endsWith("PHASE1")?"PHASE1_":params.getRoot().endsWith("PHASE2")?"PHASE2_":"")+DateUtil.format(new Date(), "MM_dd_yyyy_kk_mm_ss_S")+".csv";
			target = new FileOutputStream(new File(params.getOutputFilePath()+"\\\\"+fileName));

			// Set up file writer
			fw = new FileWriterThread(params, target);
			t = new Thread(fw);

			// Populate the queue for java classes
			inqueue = new ArrayBlockingQueue<>(AppConstants.READER_QUEUE_SIZE);
			
			// Submit thread to populate the java classes
			ExecutorService fillES = Executors.newFixedThreadPool(1);
			fillES.submit(new FileSizeProducer(inqueue, params));
			
			ExecutorService es = Executors.newFixedThreadPool(threadCount);
			for (int i = 0; i < threadCount; i++) {
				es.submit(new FileSizeSearchThread(inqueue, fw, params));
			}
			
			t.start();
			
			// Terminate the dao threads
			fillES.shutdown();
			
			// Wait until the threads are complete.
			while (!fillES.isTerminated()) {
				Thread.sleep(2000);
			}
			// Set the end processing flag in the input queue
			inqueue.offer(AppConstants.END_PROCESSING, 365, TimeUnit.DAYS);
						
			// Terminate the search threads
			es.shutdown();

			// Wait until the threads are complete.
			while (!es.isTerminated()) {
				Thread.sleep(2000);
			}
			
			
		} catch (Exception e) {
			log.error("Exception executing SearchFileSizeService: " + e.getMessage(), e);
		} finally {
			try {
				if (fw != null) {
					fw.close();
					while (!fw.isComplete()) {
						Thread.sleep(2000);
					}
				}
				if (target != null) {
					target.close();
				}
			} catch (Exception e2) {
				log.error("Exception closing writers and buffers: " + e2.getMessage(), e2);
			}
		}
		
	}
	
	/**
	 * Process the command line input arguments.
	 * e.g. SearchWorkspace [options [-i -p [-phase1 | -phase2]]]
	 * @param args arguments passed in.
	 */
	private void processInputs(String[] args) {
		List<String> options = new ArrayList<String>();
			
		// Collect args and process
		int index = 0;
		for (String arg : args) {
			if (arg.startsWith("-")) {
				// Collect the options
				options.add(arg);
			} else {
				break;
			}
			index++;
		}
					
		// Set the options
		for (String option : options) {
			if("-i".equals(option)) {
				params.setCaseSensitive(false);
			}
			if ("-p".equals(option)) {
				params.setSearchWorkspace(false);
			}
			if ("-phase1".equals(option)) {
				params.setRootAsPhase1Root();					
			}
			if ("-phase2".equals(option)) {
				params.setRootAsPhase2Root();
			}
		}
		// Set the default root if not specified in the options
		if (StringUtils.isBlank(params.getRoot())) {
			params.setSearchAllCode(true);
			params.setRootAsPhase1Root();
		}
	}

	

}

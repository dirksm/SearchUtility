package com.leewardassociates.search.threads;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.leewardassociates.search.constants.AppConstants;
import com.leewardassociates.search.models.ParamModel;

public abstract class SearchThread implements Runnable {
	
	protected ArrayBlockingQueue<String> inqueue = null;
	protected FileWriterThread fw = null;
	protected ParamModel params = null;
	protected int counter = 0;
	
	/** The file list. */
	protected List<String> fileList;

	/** The project list. */
	protected List<String> projectList = Arrays.asList(new String[]{"\\Benefits","\\Framework","\\Tax"});

	
	/** The ignore list. */
	protected List<String> ignoreList = Arrays.asList(new String[]{".metadata",".svn", "build","lib","bin","target","deploy","config","xdocletmerge","dist","dist-framework","javasource"});

	
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
	
	/**
	 * Determines if the file is one of the file types specified in the fileList.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	protected boolean matchesFileType(String fileName) {
		boolean inFile = false;
		for (String file : fileList) {
			if (StringUtils.isNotBlank(fileName) &&  fileName.endsWith(file)) {
				inFile = true;
				break;
			}
		}
		return inFile;
	}

	/**
	 * Determines if the file is in a project specified.
	 *
	 * @param fileName the file name
	 * @return true, if is in project
	 */
	protected boolean isInProject(String fileName) {
		boolean inProj = params.isSearchWorkspace()?true:false;
		if (!params.isSearchWorkspace()) {
			for (String projectName : projectList) {
				if (StringUtils.isNotBlank(fileName) &&  fileName.startsWith(params.getRoot()+projectName)) {
					inProj = true;
					break;
				}
			}
		}
		return inProj;
	}

	
	protected abstract void search(File[] files, String param, FileWriterThread fw)  throws Exception;
}

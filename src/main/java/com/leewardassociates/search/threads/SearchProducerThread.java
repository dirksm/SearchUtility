package com.leewardassociates.search.threads;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.code.util.FileIO;
import com.leewardassociates.search.constants.AppConstants;
import com.leewardassociates.search.models.ParamModel;

public abstract class SearchProducerThread implements Runnable {

	/** The file list. */
	protected List<String> fileList;

	/** The project list. */
	protected List<String> projectList = Arrays.asList(new String[]{"\\Benefits","\\Framework","\\Tax"});
	
	/** The ignore list. */
	protected List<String> ignoreList = Arrays.asList(new String[]{".metadata",".svn", "build","lib","bin","target","deploy","config","xdocletmerge","dist","dist-framework","javasource"});

	protected ArrayBlockingQueue<String> queue = null;
	protected ParamModel params = null;
	
	public abstract void traverseFiles(File[] files) throws Exception;
	
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
				if (StringUtils.isNotBlank(fileName)) {
					if (params.isSearchAllCode()) {
						 if(fileName.startsWith(params.getRoot()+projectName) ||
							fileName.startsWith(params.getPhase2Root()+projectName)) {
								inProj = true;
								break;
						 }
					} else if (fileName.startsWith(params.getRoot()+projectName)) {
						inProj = true;
						break;
					}
				}
			}
		}
		return inProj;
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


	protected void process(Logger log)  {
		try {
		
			traverseFiles(new File(params.getRoot()).listFiles());
			if (params.isSearchAllCode()) {
				if (log.isInfoEnabled()) {
					log.info("Traversing all files....");
				}
				traverseFiles(new File(params.getPhase2Root()).listFiles());
			}

		} catch (InterruptedException e) {
			log.error("Exception running the SearchProducerThread: " + e.getMessage(), e);
		} catch (Exception e) {
			log.error("Exception running the SearchProducerThread: " + e.getMessage(), e);
		}
	}

}

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

public class SearchProducerThread implements Runnable {

	private static Logger log = LoggerFactory.getLogger(SearchProducerThread.class);
	
	/** The project list. */
	public  List<String> projectList = Arrays.asList(new String[]{"\\Benefits","\\Framework","\\Tax"});
	
	/** The ignore list. */
	public static List<String> ignoreList = Arrays.asList(new String[]{".metadata",".svn", "build","lib","bin","target","deploy","config","xdocletmerge","dist","dist-framework","javasource"});

	private ArrayBlockingQueue<String> queue = null;
	private ParamModel params = null;
	
	private SearchProducerThread() {
		
	}
	
	public SearchProducerThread(ArrayBlockingQueue<String> q, ParamModel params) {
		this();
		this.queue = q;
		this.params = params;
	}
	
	public void traverseFiles(File[] files) throws Exception {
		List<String> list = new ArrayList<String>();
		for (File file : files) {
			if (file.isDirectory()) {
				if (isInProject(file.getCanonicalPath()) && !ignoreList.contains(file.getName())) {
					traverseFiles(file.listFiles());
				}
			} else {
				if (StringUtils.isNotBlank(file.getName()) &&  file.getName().endsWith(".java")) {
					queue.offer(FileIO.getFullyQualifiedName(file), 365, TimeUnit.DAYS);
				}
			}
		}
	}
	
	/**
	 * Determines if the file is in a project specified.
	 *
	 * @param fileName the file name
	 * @return true, if is in project
	 */
	private boolean isInProject(String fileName) {
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

	
	@Override
	public void run() {
		try {
		
			traverseFiles(new File(params.getRoot()).listFiles());
			if (params.isSearchAllCode()) {
				traverseFiles(new File(params.getPhase2Root()).listFiles());
			}

		} catch (InterruptedException e) {
			log.error("Exception running the SearchProducerThread: " + e.getMessage(), e);
		} catch (Exception e) {
			log.error("Exception running the SearchProducerThread: " + e.getMessage(), e);
		}
	}

}

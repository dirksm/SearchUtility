package com.leewardassociates.search.threads;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.code.util.FileIO;
import com.leewardassociates.search.models.ParamModel;

public class ReferenceSearchThread extends SearchThread {

	private static Logger log = LoggerFactory.getLogger(ReferenceSearchThread.class);
	
	/** The project list. */
	public  List<String> projectList = Arrays.asList(new String[]{"\\Benefits","\\Framework","\\Tax"});

	/** The file list. */
	public  List<String> fileList = Arrays.asList(new String[]{".java", ".xml"});
	
	/** The ignore list. */
	public static List<String> ignoreList = Arrays.asList(new String[]{".metadata",".svn", "build","lib","bin","target","deploy","config","xdocletmerge","dist","dist-framework","javasource"});

	
	private ReferenceSearchThread() {
	}
	
	public ReferenceSearchThread(ArrayBlockingQueue<String> q, FileWriterThread fw, ParamModel params) {
		this();
		this.params = params;
		this.fw = fw;
		this.inqueue = q;
	}
	
	@Override
	public void run() {
		process(log);
	}
	
	private boolean showFiles(File[] files, String param) throws Exception {
		boolean found = false;
			for (File file : files) {
				if (!found) {
					if (file.isDirectory()) {
						if (!ignoreList.contains(file.getName())) {
							found = showFiles(file.listFiles(), param);
						}
					} else if (matchesFileType(file.getName()) && FileIO.searchReference(file, param, params.isCaseSensitive())) {
						log.info(param + " was referenced in " + file.getCanonicalPath()+".");
						found = true;
						break;
					}
				} else {
					break;
				}
			}
		return found;
	}

	/**
	 * Determines if the file is one of the file types specified in the fileList.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	private boolean matchesFileType(String fileName) {
		boolean inFile = false;
		for (String file : fileList) {
			if (StringUtils.isNotBlank(fileName) &&  fileName.endsWith(file)) {
				inFile = true;
				break;
			}
		}
		return inFile;
	}

	@Override
	protected void search(File[] files, String param, FileWriterThread fw) throws Exception {
		if (!showFiles(new File(params.getRoot()).listFiles(), param)) {
			if (params.isSearchAllCode()) {
				if (!showFiles(new File(params.getPhase2Root()).listFiles(), param)) {
					log.info(param + " was not referenced anywhere.");
					fw.addBuffer(param);
				}
			} else {
				log.info(param + " was not referenced anywhere.");
				fw.addBuffer(param);
			}
		}
	}

}

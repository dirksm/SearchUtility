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
	
	
	private ReferenceSearchThread() {
		fileList = Arrays.asList(new String[]{".java", ".xml", ".tld", ".jsp"});
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
					} else if (matchesFileType(file.getName()) && !isSameFile(file, param) && FileIO.searchReference(file, FileIO.getJavaFileName(param), params.isCaseSensitive())) {
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

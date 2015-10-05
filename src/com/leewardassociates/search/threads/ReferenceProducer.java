package com.leewardassociates.search.threads;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.code.util.FileIO;
import com.leewardassociates.search.models.ParamModel;

public class ReferenceProducer extends SearchProducerThread {

	private static Logger log = LoggerFactory.getLogger(ReferenceProducer.class);

	private ReferenceProducer() {
		fileList = Arrays.asList(new String[]{".java", ".xml"});
	}
	
	public ReferenceProducer(ArrayBlockingQueue<String> q, ParamModel params) {
		this();
		this.queue = q;
		this.params = params;
	}

	public void traverseFiles(File[] files) throws Exception {
		for (File file : files) {
			if (file.isDirectory()) {
				if (isInProject(file.getCanonicalPath()) && !ignoreList.contains(file.getName())) {
					traverseFiles(file.listFiles());
				}
			} else {
				if (StringUtils.isNotBlank(file.getName()) &&  file.getName().endsWith(".java")) {
					queue.offer(FileIO.getJavaFileName(file), 365, TimeUnit.DAYS);
				}
			}
		}
	}

	@Override
	public void run() {
		process(log);
	}

}

package com.leewardassociates.search.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.models.ParamModel;

public class FileListProducer extends SearchProducerThread {

	Logger log = LoggerFactory.getLogger(FileListProducer.class);
	
	private FileListProducer() {
		fileList = Arrays.asList(new String[]{".java", ".xml", ".jsp"});
	}
	
	public FileListProducer(ArrayBlockingQueue<String> q, ParamModel params) {
		this();
		this.queue = q;
		this.params = params;
	}

	@Override
	public void run() {
		loadFile("C:\\Users\\Public\\Documents\\Main.txt");
	}

	@Override
	public void traverseFiles(File[] files) throws Exception {
	}
	
	private void loadFile(String file) {
		FileInputStream fis = null;
		BufferedReader reader = null;		
		try {
			fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis));
			String line = "";
			while((line=reader.readLine())!=null) {
				queue.offer(line, 365, TimeUnit.DAYS);
			}
		} catch (IOException | InterruptedException e) {
			log.error("Exeption reading input file " + file + ": " + e.getMessage(), e);
		}
	}

}

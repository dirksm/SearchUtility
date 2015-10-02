package com.leewardassociates.search.threads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.models.ParamModel;

public class FileSizeSearchThread extends SearchThread {

	private static Logger log = LoggerFactory.getLogger(FileSizeSearchThread.class);

	private FileSizeSearchThread() {
		fileList = Arrays.asList(new String[]{".java", ".xml", ".jsp"});
	}
	
	public FileSizeSearchThread(ArrayBlockingQueue<String> q, FileWriterThread fw, ParamModel params) {
		this();
		this.params = params;
		this.fw = fw;
		this.inqueue = q;
	}

	@Override
	public void run() {
		process(log);
	}

	
	@Override
	protected void search(File[] files, String param, FileWriterThread fw) throws Exception {
		FileReader fr = null;
		try {
			File f = new File(param);
			fr = new FileReader(f);
			if (f.length() == 0) {
				fw.addBuffer(param);
			}
		} catch (FileNotFoundException e) {
			log.error(param+" was not found.", e);
			fw.addBuffer(param);
		} finally {
			fr.close();
		}
	}

}

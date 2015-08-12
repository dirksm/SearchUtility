package com.leewardassociates.search.code.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class FileIO.
 *
 * @author Michael R Dirks
 */
public class FileIO {
	
	private static Logger log = LoggerFactory.getLogger(FileIO.class);
	
	/**
	 * Parses the file.
	 *
	 * @param file the file
	 * @return the list
	 * @throws Exception the exception
	 */
	public static List<String> parseFile(File file) throws Exception {
		BufferedReader reader = null;
		List<String> list = new ArrayList<String>();
		String line = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine())!=null) {
				list.add(line);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			reader.close();
		}
		return list;
	}
	
	/**
	 * Write file.
	 *
	 * @param file the file
	 * @param text the text
	 * @throws Exception the exception
	 */
	public static void writeFile(File file, String text) throws Exception {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file,true));
			writer.write(text);
		} catch (Exception e) {
			throw e;
		} finally {
			writer.close();
		}
	}
	
	/**
	 * Searches the file for the search param.
	 *
	 * @param file the file
	 * @param param the param
	 */
	public static void searchFile(File file, String param, boolean caseSensitive, TreeSet<String> found)
	{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line=reader.readLine()) != null) {
				sb.append(line);
			}
			String text2search = sb.toString();
			if (caseSensitive) {
				if (text2search.contains(param)) {
					found.add(param);
				}
			} else {
				if (StringUtils.lowerCase(text2search).contains(StringUtils.lowerCase(param))) {
					found.add(param);
				}
			}
		} catch (Exception e) {
			log.error("Exception searching file " + file.getName() +": "+e.getMessage(), e);
		} finally {
			try {
				reader.close();
			} catch (Exception e2) {
				
			}
		}
		
	} 


}

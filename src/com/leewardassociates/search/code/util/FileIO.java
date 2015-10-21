package com.leewardassociates.search.code.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		try {
			String text2search = getContents(file);
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
		}
		
	} 
	
	/**
	 * Returns the contents of a file as a String
	 * @param file
	 * @return {@link String}
	 */
	public static String getContents(File file) {
		
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line=reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			log.error("Exception loading file " + file.getName() +" into String: "+e.getMessage(), e);
		} finally {
			try {
				reader.close();
			} catch (Exception e2) {
				
			}
		}
		return sb.toString();

	}

	/**
	 * Searches the file for the search param.
	 *
	 * @param file the file
	 * @param param the param
	 * @param  caseSensitive Is search case sensitive
	 */
	public static boolean searchReference(File file, String param, boolean caseSensitive)
	{
		BufferedReader reader = null;
		boolean found = false;
		try {
			String path = getFullyQualifiedName(file);
			if (!path.equals(param)) {
				reader = new BufferedReader(new FileReader(file));
				StringBuffer sb = new StringBuffer();
				String line = "";
				while ((line=reader.readLine()) != null) {
					sb.append(line);
				}
				String text2search = sb.toString();
				if (caseSensitive) {
					if (text2search.contains(param)) {
						found = true;
					}
				} else {
					if (StringUtils.lowerCase(text2search).contains(StringUtils.lowerCase(param))) {
						found = true;
					}
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
		return found;
	} 

	public static String getFullyQualifiedName(File file) {
		String name = "";
		if (file.getName().endsWith(".java")) {
			String packageName = getPackage(FileIO.getContents(file));
			name =  packageName + "." + file.getName().substring(0, file.getName().indexOf(".java"));
		}
		return name;
	}
	
	public static String getJavaFileName(File file) {
		String name = "";
		if (file.getName().endsWith(".java")) {
			name = file.getName().substring(0, file.getName().indexOf(".java"));
		}
		return name;
	}
	
	public static String getJavaFileName(String str) {
		String name = "";
		if (StringUtils.isNotBlank(str)) {
			name = str.substring(str.lastIndexOf(".")+1, str.length());
		}
		return name;
	}
	
	
	/**
	 * Retrieves the package name for the file (if it is a java file).
	 *
	 * @param text2search the text2search
	 * @return the package
	 */
	private static String getPackage(String text2search) {
		String text = "";
		Pattern pattern = Pattern.compile("package\\s?(.*?);");
		Matcher m = pattern.matcher(text2search);
		if (m.find()) {
			text = m.group(1);
		}
		return text;
	}
	

}

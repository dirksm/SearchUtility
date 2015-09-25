package com.leewardassociates.search.code.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class FileSearchUtil {
	
	/** The search workspace. */
	private boolean searchWorkspace = true;
	
	/** The case sensitive. */
	private boolean caseSensitive = true;
	
	/** The found. */
	private TreeSet<String> found = new TreeSet<String>();
	
	/** The search list. */
	private List<String> searchList = Arrays.asList();

	/** The ignore list. */
	public  List<String> ignoreList = Arrays.asList();

	/** The root. */
	public  String root = "";

	/** The project list. */
	public  List<String> projectList = Arrays.asList(new String[]{root+"\\Benefits",root+"\\Framework",root+"\\Tax"});

	/** The file list. */
	public  List<String> fileList = Arrays.asList(new String[]{".java",".xml"});
	
	
	private FileSearchUtil() {
		
	}
	
	public FileSearchUtil(String root, boolean searchWorkspace, boolean caseSensitive, TreeSet<String> found, 
			List<String> searchList, List<String> ignoreList, List<String> projectList, List<String> fileList) {
		this();
		this.root = root;
		this.searchWorkspace = searchWorkspace;
		this.caseSensitive = caseSensitive;
		this.found = found;
		this.searchList = searchList;
		this.ignoreList = ignoreList;
		this.projectList = projectList;
		this.fileList = fileList;
	}

	
	/**
	 * Called by the main method.  Builds the search list and grabs the files to traverse.
	 *
	 * @param files the files
	 * @throws Exception the exception
	 */
	public void searchWorkspace(File[] files, String inputFile) throws Exception {
		searchList = FileIO.parseFile(new File(inputFile));
		for (String search : searchList) {
			showFiles(files, search);
		}
	}
	

	/**
	 * Iterates through the list of files and directories.  If it is in the project and the right file type, searches the file for search param.
	 *
	 * @param files the files
	 * @param param the param
	 * @throws Exception the exception
	 */
	public void showFiles(File[] files, String param) throws Exception {
		for (File file : files) {
			if (file.isDirectory()) {
				if (isInProject(file.getCanonicalPath()) && !ignoreList.contains(file.getName())) {
					showFiles(file.listFiles(), param);
				}
			} else {
				if (matchesFileType(file.getName())) {
					FileIO.searchFile(file, param, caseSensitive, found);
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
		boolean inProj = searchWorkspace?true:false;
		if (!searchWorkspace) {
			for (String projectName : projectList) {
				if (StringUtils.isNotBlank(fileName) &&  fileName.startsWith(projectName)) {
					inProj = true;
					break;
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
	
	/**
	 * Retrieves the package name for the file (if it is a java file).
	 *
	 * @param text2search the text2search
	 * @return the package
	 */
	private String getPackage(String text2search) {
		String text = "";
		Pattern pattern = Pattern.compile("^package\\s?(.*?);");
		Matcher m = pattern.matcher(text2search);
		if (m.find()) {
			text = m.group(1);
		}
		return text;
	}

	public TreeSet<String> getFound() {
		return found;
	}


}

package com.leewardassociates.search.code.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ReferenceSearchUtil {

	/** The search workspace. */
	private boolean searchWorkspace = true;
	
	/** The case sensitive. */
	private boolean caseSensitive = true;
	
	/** The found. */
	private TreeSet<String> missing = new TreeSet<String>();

	/** The root. */
	public  String root = "";

	/** The project list. */
	public  List<String> projectList = Arrays.asList(new String[]{"\\Benefits","\\Framework","\\Tax"});

	/** The file list. */
	public  List<String> fileList = Arrays.asList(new String[]{".java", ".xml"});
	
	/** The ignore list. */
	public static List<String> ignoreList = Arrays.asList(new String[]{".metadata",".svn", "build","lib","bin","target","deploy","config","xdocletmerge","dist","dist-framework","javasource"});
	
	private ReferenceSearchUtil() {
		
	}
	
	public ReferenceSearchUtil(String root, boolean searchWorkspace) {
		this();
		this.root = root;
		this.searchWorkspace = searchWorkspace;
	}
	
	public void traverseFiles(File[] files) throws Exception {
		for (File file : files) {
			if (file.isDirectory()) {
				if (isInProject(file.getCanonicalPath()) && !ignoreList.contains(file.getName())) {
					traverseFiles(file.listFiles());
				}
			} else {
				if (StringUtils.isNotBlank(file.getName()) &&  file.getName().endsWith(".java")) {
					searchReferences(file);
				}
			}
		}
	}
	
	public void searchReferences(File file) {
		try {
			String param = FileIO.getFullyQualifiedName(file);
			System.out.println("Search for references of " + param);
			if (!showFiles(new File(root).listFiles(), param)) {
				System.out.println(param + " was not referenced anywhere.");
				missing.add(param);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean showFiles(File[] files, String param) throws Exception {
		boolean found = false;
			for (File file : files) {
				if (!found) {
					if (file.isDirectory()) {
						if (!ignoreList.contains(file.getName())) {
							found = showFiles(file.listFiles(), param);
						}
					} else if (matchesFileType(file.getName()) && FileIO.searchReference(file, param, caseSensitive)) {
						System.out.println(param + " was referenced in " + file.getCanonicalPath()+".");
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
	 * Determines if the file is in a project specified.
	 *
	 * @param fileName the file name
	 * @return true, if is in project
	 */
	private boolean isInProject(String fileName) {
		boolean inProj = searchWorkspace?true:false;
		if (!searchWorkspace) {
			for (String projectName : projectList) {
				if (StringUtils.isNotBlank(fileName) &&  fileName.startsWith(root+projectName)) {
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
	
	public void setRoot(String root) {
		this.root = root;
	}


	public TreeSet<String> getMissing() {
		return missing;
	}

}

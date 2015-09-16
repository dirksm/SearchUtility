package com.leewardassociates.search.code.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.code.util.DateUtil;
import com.leewardassociates.search.code.util.FileIO;



/**
 * The Class SearchWorkspace.
 *
 * @author Michael R Dirks
 */
public class SearchWorkspace {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(SearchWorkspace.class);
	
	/** The search workspace. */
	private static boolean searchWorkspace = true;
	
	/** The case sensitive. */
	private static boolean caseSensitive = true;
	
	/** The search all code. */
	private static boolean searchAllCode = false;
	
	/** The found. */
	private static TreeSet<String> found = new TreeSet<String>();
	
	/** The search list. */
	private static List<String> searchList = Arrays.asList();
	
	/** The root. */
	public static String root = "";
	
	/** The phase1 root. */
	public static String phase1Root = "C:\\Workspaces\\UIM-PHASE1";
	
	/** The phase2 root. */
	public static String phase2Root = "C:\\Workspaces\\UIM-PHASE2";
	
	/** The ignore list. */
	public static List<String> ignoreList = Arrays.asList(new String[]{".metadata",".svn", "build","lib","bin","target","deploy","config","xdocletmerge","uinteract","dist","dist-framework"});
	
	/** The project list. */
	public static List<String> projectList = Arrays.asList(new String[]{root+"\\Benefits",root+"\\Framework",root+"\\Tax"});
	
	/** The file list. */
	public static List<String> fileList = Arrays.asList(new String[]{".java",".xml"});

	/** The input file. */
	public static String inputFile = "";
	
	/** The output file path. */
	public static String outputFilePath = "";

	/**
	 * Main executable method.
	 *
	 * @param args arguments passed in via command line.
	 */
	public static void main(String[] args) {
		/**
		 * Following arguments:
		 * Case insensitive
		 * Search Project or Workspace
		 * Phase
		 * Input file path
		 * Output file path
		 */
		processInputs(args);
		
		try {
			searchWorkspace(new File(root).listFiles());
			if (searchAllCode) {
				root = phase2Root;
				searchWorkspace(new File(root).listFiles());
			}
			String foundFileName = "FoundFileTables_"+DateUtil.format(new Date(), "MM_dd_yyyy_kk_mm_ss_S")+".csv";
			File foundout = new File(outputFilePath+"\\\\"+foundFileName);
			for (String table : found) {
				FileIO.writeFile(foundout, table+"\n");
			}

			List<String> missing = new ArrayList<String>();
			for (String table : searchList) {
				if (!found.contains(table)) {
					missing.add(table);
				}
			}
			
			
			String fileName = "MissingTables_"+(root.endsWith("PHASE1")?"PHASE1_":root.endsWith("PHASE2")?"PHASE2_":"")+DateUtil.format(new Date(), "MM_dd_yyyy_kk_mm_ss_S")+".csv";
			File out = new File(outputFilePath+"\\\\"+fileName);
			for (String table : missing) {
				FileIO.writeFile(out, table+"\n");
			}
			System.out.println("Search complete.  Results can be found in '" + outputFilePath+"\\\\"+fileName + "'");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
	}
	
	/**
	 * Called by the main method.  Builds the search list and grabs the files to traverse.
	 *
	 * @param files the files
	 * @throws Exception the exception
	 */
	public static void searchWorkspace(File[] files) throws Exception {
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
	public static void showFiles(File[] files, String param) throws Exception {
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
	private static boolean isInProject(String fileName) {
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
	private static boolean matchesFileType(String fileName) {
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
	private static String getPackage(String text2search) {
		String text = "";
		Pattern pattern = Pattern.compile("^package\\s?(.*?);");
		Matcher m = pattern.matcher(text2search);
		if (m.find()) {
			text = m.group(1);
		}
		return text;
	}

	/**
	 *  
	 * Process the command line input arguments.
	 *
	 * @param args arguments passed in.
	 */
	private static void processInputs(String[] args) {
		List<String> options = new ArrayList<String>();
		if (args.length < 2) {
			System.out.println("Wrong number of parameters:  SearchWorkspace [options [-i -p [-phase1 | -phase2]]] inputFile outputPath");
			System.exit(0);
		} else {
			
			// Collect args and process
			int index = 0;
			for (String arg : args) {
				if (arg.startsWith("-")) {
					// Collect the options
					options.add(arg);
				} else {
					break;
				}
				index++;
			}
			
			try {
				// Next is the input file path
				inputFile = args[index++];
			} catch (ArrayIndexOutOfBoundsException aioube) {
				System.out.println("No input file specified.  Please execute in the form ' SearchWorkspace [options [-i -p [-phase1 | -phase2]]] inputFile outputPath'");
				System.exit(0);
			}
			try {
				//check for valid file path
				File f = new File(inputFile);
				if (f.isDirectory()) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println(inputFile + " is not a valid file. Please check your file path.");
				System.exit(0);
			}
			try {
				// Then the output file path
				outputFilePath = args[index++];
			} catch (ArrayIndexOutOfBoundsException aioube) {
				System.out.println("No output file specified.  Please execute in the form ' SearchWorkspace [options [-i -p [-phase1 | -phase2]]] inputFile outputPath'");
				System.exit(0);
			}
			try {
				//check for valid file path
				File f = new File(outputFilePath);
				if (!f.isDirectory()) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println(outputFilePath + " is not a valid path. Please check your file path.");
				System.exit(0);
			}
			
			// Set the options
			for (String option : options) {
				if("-i".equals(option)) {
					caseSensitive = false;
				}
				if ("-p".equals(option)) {
					searchWorkspace = false;
				}
				if ("-phase1".equals(option)) {
					root = phase1Root;					
				}
				if ("-phase2".equals(option)) {
					root = phase2Root;
				}
			}
			// Set the default root if not specified in the options
			if (StringUtils.isBlank(root)) {
				searchAllCode = true;
				root = phase1Root;
			}
		}
	}
}

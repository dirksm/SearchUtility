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
import com.leewardassociates.search.code.util.FileSearchUtil;



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
			FileSearchUtil fsu = new FileSearchUtil(root, searchWorkspace, caseSensitive, found, searchList, ignoreList, projectList, fileList);
			fsu.searchWorkspace(new File(root).listFiles(), inputFile);
			if (searchAllCode) {
				root = phase2Root;
				fsu.searchWorkspace(new File(root).listFiles(), inputFile);
			}
			String foundFileName = "FoundFileTables_"+DateUtil.format(new Date(), "MM_dd_yyyy_kk_mm_ss_S")+".csv";
			File foundout = new File(outputFilePath+"\\\\"+foundFileName);
			found = fsu.getFound();
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

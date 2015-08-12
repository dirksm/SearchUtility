package com.leewardassociates.search.code.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leewardassociates.search.code.util.DateUtil;
import com.leewardassociates.search.code.util.FileIO;

public class CompareFiles {

	private static Logger log = LoggerFactory.getLogger(CompareFiles.class);
	
	public static String inputFile = "";
	public static String outputFile = "";

	private static boolean caseSensitive = true;
	private static TreeSet<String> found = new TreeSet<String>();
	private static List<String> paramList = new ArrayList<String>();
	private static List<String> searchList = new ArrayList<String>();

	public static void main(String[] args) {
		processInputs(args);

		try {
			searchFile();			
			
			List<String> missing = new ArrayList<String>();
			for (String table : paramList) {
				if (!found.contains(table)) {
					missing.add(table);
				}
			}

			String fileName = "MissingFileTables_"+DateUtil.format(new Date(), "MM_dd_yyyy_kk_mm_ss_S")+".csv";
			File out = new File(outputFile+"\\\\"+fileName);
			for (String table : missing) {
				FileIO.writeFile(out, table+"\n");
			}
			System.out.println("Search complete.  Results can be found in '" + outputFile+"\\\\"+fileName + "'");

		} catch (Exception e) {
			log.error("Exception in main method: " + e.getMessage(), e);
		}
	}

	private static void searchFile() throws Exception {
		paramList = FileIO.parseFile(new File(inputFile));
		for (String param : paramList) {
			for (String searchFilePath : searchList) {
				File searchFile = new File(searchFilePath);
				FileIO.searchFile(searchFile, param, caseSensitive, found);
			}
		}
	}

	private static void processInputs(String[] args) {
		List<String> options = new ArrayList<String>();
		if (args.length < 3) {
			System.out.println("Wrong number of parameters:  SearchFile [options [-i ]] inputParamFile outputPath [searchFilePath] ");
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
				// Next is the search param file path
				inputFile = args[index++];
			} catch (ArrayIndexOutOfBoundsException aioube) {
				System.out.println("No input file specified.  Please execute in the form 'SearchFile [options [-i ]] inputParamFile outputPath [searchFilePath] '");
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
				outputFile = args[index++];
			} catch (ArrayIndexOutOfBoundsException aioube) {
				System.out.println("No output file specified.  Please execute in the form 'SearchFile [options [-i ]] inputParamFile outputPath [searchFilePath] '");
				System.exit(0);
			}
			try {
				//check for valid file path
				File f = new File(outputFile);
				if (!f.isDirectory()) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println(outputFile + " is not a valid path. Please check your file path.");
				System.exit(0);
			}

			while (index < args.length) {
				String searchFilePath = "";
				try {
					// Then the search file path
					searchFilePath = args[index++];
				} catch (ArrayIndexOutOfBoundsException aioube) {
					System.out.println("No search file specified.  Please execute in the form 'SearchFile [options [-i ]] inputParamFile outputPath [searchFilePath] '");
					System.exit(0);
				}
				try {
					//check for valid file path
					File f = new File(searchFilePath);
					if (f.isDirectory()) {
						throw new Exception();
					}
				} catch (Exception e) {
					System.out.println(searchFilePath + " is not a valid path. Please check your file path.");
					System.exit(0);
				}
				searchList.add(searchFilePath);
			}

			if (searchList.isEmpty()) {
				System.out.println("No search file specified.  Please execute in the form 'SearchFile [options [-i ]] inputParamFile outputPath [searchFilePath] '");
				System.exit(0);
			}
			
			// Set the options
			for (String option : options) {
				if("-i".equals(option)) {
					caseSensitive = false;
				}
			}
		}
	}
}

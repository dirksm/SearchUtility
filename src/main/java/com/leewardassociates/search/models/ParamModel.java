package com.leewardassociates.search.models;

import java.io.Serializable;

public class ParamModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8795348323324753893L;

	/** The search workspace. */
	private boolean searchWorkspace = true;
	
	/** The case sensitive. */
	private boolean caseSensitive = true;
	
	/** The search all code. */
	private boolean searchAllCode = false;

	/** The phase1 root. */
	private String phase1Root = "C:\\Workspaces\\UIM-PHASE1";
	
	/** The phase2 root. */
	private String phase2Root = "C:\\Workspaces\\UIM-PHASE2Code";

	public String outputFilePath = "C:\\Users\\Public\\Documents\\";

	/** The root. */
	public String root = "";

	public boolean isSearchWorkspace() {
		return searchWorkspace;
	}

	public void setSearchWorkspace(boolean searchWorkspace) {
		this.searchWorkspace = searchWorkspace;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isSearchAllCode() {
		return searchAllCode;
	}

	public void setSearchAllCode(boolean searchAllCode) {
		this.searchAllCode = searchAllCode;
	}

	public String getPhase1Root() {
		return phase1Root;
	}

	public void setPhase1Root(String phase1Root) {
		this.phase1Root = phase1Root;
	}

	public String getPhase2Root() {
		return phase2Root;
	}

	public void setPhase2Root(String phase2Root) {
		this.phase2Root = phase2Root;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
	public void setRootAsPhase1Root() {
		this.root = phase1Root;
	}
	
	public void setRootAsPhase2Root() {
		this.root = phase2Root;
	}

}

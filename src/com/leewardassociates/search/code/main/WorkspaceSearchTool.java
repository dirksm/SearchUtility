package com.leewardassociates.search.code.main;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

import com.leewardassociates.search.code.util.WorkspaceSearchRequestor;

public class WorkspaceSearchTool {

	public static void main(String[] args) {
		SearchPattern pattern = SearchPattern.createPattern("gov.state.uim.app.struts.AddEmployerInfoAction", IJavaSearchConstants.TYPE, IJavaSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH);
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		SearchRequestor request = new WorkspaceSearchRequestor();
		SearchEngine engine = new SearchEngine();
		try {
			engine.search(pattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope, request, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}

}

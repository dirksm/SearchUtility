/**
 * 
 */
package com.leewardassociates.search.code.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

/**
 * @author dirksm
 *
 */
public class WorkspaceSearchRequestor extends SearchRequestor {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 */
	@Override
	public void acceptSearchMatch(SearchMatch searchMatch) throws CoreException {
		System.out.println("element: " + searchMatch.getElement());
		System.out.println(searchMatch.toString());
	}

}

package de.philipfrank.gwt.matunus.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import de.philipfrank.gwt.matunus.shared.Util;

public class AccessFilter {
	private String rootDir;
	
	public AccessFilter(ServletContext context){
		rootDir = Util.tailSlash(context.getInitParameter("rootDirectory"));
	}
	
	public void tryAccess(File file) throws IllegalArgumentException {
		if(!canAccess(file)) throw new IllegalArgumentException("not allowed: "+file);
	}
	
	public String getRootDir() {
		return rootDir;
	}
	
	public boolean canAccess(File file) {

		try {
			// TODO: safe???
			if (!Util.tailSlash(file.getCanonicalPath()).startsWith(rootDir)) {
				return false;
			}
			
			
			for(File f = file; f != null; f = f.getParentFile()) {
				if(f.isHidden()) return false;
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}

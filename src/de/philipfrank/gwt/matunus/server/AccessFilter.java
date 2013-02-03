package de.philipfrank.gwt.matunus.server;

import java.io.File;

import javax.servlet.ServletContext;

public class AccessFilter {
	private File rootDir;

	public AccessFilter(ServletContext context) {
		rootDir = new File(context.getInitParameter("rootDirectory"));
	}

	public void tryAccess(File file) throws IllegalArgumentException {
		if (!canAccess(file)) {
			// TODO: don't expose full path
			throw new IllegalArgumentException("not allowed: " + file);
		}
	}

	public File getRootDir() {
		return rootDir;
	}

	public void tryDownloadZip(File dir) throws IllegalArgumentException {
		if (!canDownloadZipped(dir)) {
			// TODO: don't expose full path
			throw new IllegalArgumentException("not allowed: " + dir);
		}
	}

	public boolean canDownloadZipped(File file) {
		return canAccess(file) && file.isDirectory() && !file.equals(rootDir);
	}

	private static boolean isParentOf(File parent, File file) {
		File directParent = file.getParentFile();
		if (directParent == null || parent == null) {
			return false;
		}
		if (directParent.equals(parent)) {
			return true;
		}
		return isParentOf(parent, directParent);
	}

	public boolean canAccess(File file) {
		if (!file.equals(rootDir) && !isParentOf(rootDir, file)) {
			return false;
		}

		for (File f = file; f != null; f = f.getParentFile()) {
			if (f.isHidden()) {
				return false;
			}
		}
		return true;
	}
}

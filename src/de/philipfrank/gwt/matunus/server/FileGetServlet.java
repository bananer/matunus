package de.philipfrank.gwt.matunus.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.philipfrank.gwt.matunus.shared.Util;

/**
 * Servlet for file downloads
 * TODO: maybe use this: http://balusc.blogspot.de/2009/02/fileservlet-supporting-resume-and.html
 * @author Philip Frank
 *
 */
public class FileGetServlet extends HttpServlet {

	private static final long serialVersionUID = -74009257484379937L;
	
	private String rootDir;
	
	@Override
	public void init(){
		// TODO: duplicate code in FileListService
		rootDir = getServletContext().getInitParameter("rootDirectory");
		rootDir = Util.tailSlash(rootDir);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestedPath = req.getPathInfo();

		File file = new File(rootDir + requestedPath);

		// TODO: safe???
		if (!Util.tailSlash(file.getCanonicalPath()).startsWith(rootDir)) {
			throw new IllegalArgumentException("not in serviceRoot: "
					+ file.getCanonicalPath());
		}

		if (file.isDirectory()) {
			throw new IllegalArgumentException("a directory: "+file);
		}
		
		FileInputStream fis = new FileInputStream(file);
		OutputStream os = resp.getOutputStream();
		
		IOUtils.copy(fis, os);
	}
	
}

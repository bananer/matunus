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

/**
 * Servlet for file downloads
 * TODO: maybe use this: http://balusc.blogspot.de/2009/02/fileservlet-supporting-resume-and.html
 * @author Philip Frank
 *
 */
public class FileGetServlet extends HttpServlet {

	private static final long serialVersionUID = -74009257484379937L;
	
	AccessFilter accessFilter;
	
	@Override
	public void init(){
		accessFilter = new AccessFilter(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestedPath = req.getPathInfo();

		File file = new File(accessFilter.getRootDir() + requestedPath);

		accessFilter.tryAccess(file);

		if (file.isDirectory()) {
			throw new IllegalArgumentException("a directory: "+file);
		}
		
		FileInputStream fis = new FileInputStream(file);
		OutputStream os = resp.getOutputStream();
		
		IOUtils.copy(fis, os);
	}
	
}

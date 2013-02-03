package de.philipfrank.gwt.matunus.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Servlet for file downloads TODO: maybe use this:
 * http://balusc.blogspot.de/2009/02/fileservlet-supporting-resume-and.html
 * 
 * @author Philip Frank
 * 
 */
public class DirZipServlet extends HttpServlet {

	private static final long serialVersionUID = -74009257484379937L;

	AccessFilter accessFilter;

	@Override
	public void init() {
		accessFilter = new AccessFilter(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestedPath = req.getPathInfo();

		File file = new File(accessFilter.getRootDir() + requestedPath);

		accessFilter.tryDownloadZip(file);

		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition",
				"attachment; filename=\"" + file.getName() + ".zip\"");

		ZipOutputStream zos = new ZipOutputStream(resp.getOutputStream());
		zipDir(file, file.toURI(), zos);
		zos.close();
	}

	private void zipDir(File dir, URI base, ZipOutputStream os)
			throws IOException {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("not a dir");
		}
		File[] contents = dir.listFiles();
		for (File f : contents) {
			String relName = base.relativize(f.toURI()).getPath();
			try {
				if (f.isDirectory()) {
					os.putNextEntry(new ZipEntry(relName));
					zipDir(f, base, os);
					os.closeEntry();
				} else {
					FileInputStream fis = new FileInputStream(f);
					os.putNextEntry(new ZipEntry(relName));
					IOUtils.copy(fis, os);
					fis.close();
					os.closeEntry();
				}
			} catch (IOException e) {
				System.err.println("Error while reading " + relName);
				e.printStackTrace();
				throw e;
			}
		}
	}
}

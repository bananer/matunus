package de.philipfrank.gwt.matunus.server;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.philipfrank.gwt.matunus.client.FileListService;
import de.philipfrank.gwt.matunus.shared.RemoteDirectory;
import de.philipfrank.gwt.matunus.shared.RemoteFile;
import de.philipfrank.gwt.matunus.shared.Util;

@SuppressWarnings("serial")
public class FileListServiceImpl extends RemoteServiceServlet implements
		FileListService {
	private String urlRoot;
	private AccessFilter accessFilter;
	
	@Override
	public void init(){
		urlRoot = Util.tailSlash(getServletContext().getInitParameter("urlRoot"));
		accessFilter = new AccessFilter(getServletContext());
	}
	
	protected String parentDir(String dir) {
		if(!dir.startsWith(accessFilter.getRootDir())) {
			return "";
		}
		dir = dir.substring(accessFilter.getRootDir().length());
		
		if(dir.lastIndexOf("/") == dir.length()-1) {
			dir = dir.substring(dir.length()-1);
		}
		if(dir.lastIndexOf("/") <= 0) {
			return "";
		}
		return dir.substring(0, dir.lastIndexOf("/"));
	}

	@Override
	public RemoteDirectory read(String requestedDir) {

		File dir = new File(accessFilter.getRootDir() + requestedDir);

		accessFilter.tryAccess(dir);

		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("not a directory: "+dir);
		}
		
		final RemoteDirectory res = new RemoteDirectory(parentDir(dir.getPath()));

		for (File file : dir.listFiles()) {
			if(accessFilter.canAccess(file)) {
				RemoteFile r = new RemoteFile(file.getName(), file.isDirectory());
				r.setDownloadLink(urlRoot + "get/"+requestedDir+file.getName());
				res.add(r);
			}
		}

		return res;
	}

}

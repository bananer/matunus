package de.philipfrank.gwt.matunus.server;

import java.io.File;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.philipfrank.gwt.matunus.client.FileListService;
import de.philipfrank.gwt.matunus.shared.RemoteDirectory;
import de.philipfrank.gwt.matunus.shared.RemoteFile;

@SuppressWarnings("serial")
public class FileListServiceImpl extends RemoteServiceServlet implements
		FileListService {
	private UriUtil uris;
	private AccessFilter accessFilter;
	
	@Override
	public void init(){
		uris = new UriUtil(getServletContext());
		accessFilter = new AccessFilter(getServletContext());
	}
	
	protected String parentDir(String dir) {
		if(dir.isEmpty() || dir.equals("/")) {
			return "";
		}
		if(dir.lastIndexOf("/") == dir.length()-1) {
			dir = dir.substring(0,dir.length()-1);
		}
		if(dir.lastIndexOf("/") <= 0) {
			return "/";
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
		
		String zippedDL = null;
		if(!dir.equals(accessFilter.getRootDir())){
			zippedDL = uris.getServletUri("zip", requestedDir, null).toASCIIString();
		}
		
		final RemoteDirectory res = new RemoteDirectory(requestedDir, parentDir(requestedDir), zippedDL);

		for (File file : dir.listFiles()) {
			if(accessFilter.canAccess(file)) {
				RemoteFile r = new RemoteFile(file.getName(), file.isDirectory());
				r.setDownloadLink(uris.getServletUri("get", requestedDir, r).toASCIIString());
				
				res.add(r);
			}
		}

		return res;
	}

}

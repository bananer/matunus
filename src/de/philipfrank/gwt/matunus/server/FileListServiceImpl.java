package de.philipfrank.gwt.matunus.server;

import java.io.File;
import java.io.IOException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.philipfrank.gwt.matunus.client.FileListService;
import de.philipfrank.gwt.matunus.shared.RemoteDirectory;
import de.philipfrank.gwt.matunus.shared.RemoteFile;

@SuppressWarnings("serial")
public class FileListServiceImpl extends RemoteServiceServlet implements
		FileListService {

	@Override
	public RemoteDirectory read(String requestedDir) {
		final RemoteDirectory res = new RemoteDirectory();

		File dir = new File(ServerSettings.serviceRoot + "/" + requestedDir);

		try {
			// TODO: safe???
			if (!dir.getCanonicalPath().startsWith(ServerSettings.serviceRoot)) {
				throw new IllegalArgumentException("not in serviceRoot: "
						+ dir.getCanonicalPath());
			}

			if (!dir.isDirectory()) {
				throw new IllegalArgumentException("not a directory: "+dir);
			}

			for (File file : dir.listFiles()) {
				RemoteFile r = new RemoteFile(file.getName(), file.isDirectory());
				r.setDownloadLink("/matunus/get/"+requestedDir+file.getName());
				res.add(r);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

}

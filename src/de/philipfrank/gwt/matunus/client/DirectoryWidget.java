package de.philipfrank.gwt.matunus.client;

import com.google.gwt.user.client.ui.Hyperlink;

import de.philipfrank.gwt.matunus.shared.RemoteFile;
import de.philipfrank.gwt.matunus.shared.Util;

class DirectoryWidget extends Hyperlink {

	public DirectoryWidget(String directory, RemoteFile dir) {
		super(Util.tailSlash(dir.getName()), Util.tailSlash(directory) + Util.tailSlash(dir.getName()));
	}
}
package de.philipfrank.gwt.matunus.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.philipfrank.gwt.matunus.shared.RemoteDirectory;

public interface FileListServiceAsync {

	void read(String path, AsyncCallback<RemoteDirectory> callback);
}

package de.philipfrank.gwt.matunus.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.philipfrank.gwt.matunus.shared.RemoteDirectory;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("list")
public interface FileListService extends RemoteService {
	RemoteDirectory read(String path);
}

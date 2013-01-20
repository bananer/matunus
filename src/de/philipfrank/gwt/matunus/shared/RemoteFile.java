package de.philipfrank.gwt.matunus.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteFile implements IsSerializable {
	private String name;
	private String downloadLink;
	private long size;
	private boolean isDir;
	
	public RemoteFile() {}
	
	public RemoteFile(String name, boolean isDir, long size) {
		this.name = name;
		this.isDir = isDir;
		this.size = size;
	}

	public boolean isDirectory() {
		return isDir;
	}

	public String getName() {
		return name;
	}
	
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public String getDownloadLink() {
		return downloadLink;
	}
	
	public long getSize() {
		if(isDirectory()) return 0;
		return size;
	}
}

package de.philipfrank.gwt.matunus.shared;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteDirectory extends LinkedList<RemoteFile> implements
		List<RemoteFile>, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5396599425070946605L;

	private String parentDir;
	private String displayName;
	private String zippedDownload;

	public RemoteDirectory() {
		parentDir = "";
	}

	public RemoteDirectory(String name, String parent, String zippedDowloadURI) {
		parentDir = parent;
		displayName = name.isEmpty() ? "/" : name;
		zippedDownload = zippedDowloadURI;
	}

	public String getParentDir() {
		return parentDir;
	}

	public static void sort(RemoteDirectory list) {
		Collections.sort(list, new Comparator<RemoteFile>() {
			@Override
			public int compare(RemoteFile o1, RemoteFile o2) {
				if (o1.isDirectory() && !o2.isDirectory()) {
					return -1;
				}
				if (!o1.isDirectory() && o2.isDirectory()) {
					return 1;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	public String getZippedDownload() {
		return zippedDownload;
	}

	public String getDisplayName() {
		return displayName;
	}
}

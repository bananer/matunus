package de.philipfrank.gwt.matunus.shared;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;



public class RemoteDirectory extends LinkedList<RemoteFile> implements List<RemoteFile>, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5396599425070946605L;
	
	private String parentDir;
	
	public RemoteDirectory(){
		parentDir = "";
	}

	public RemoteDirectory(String par){
		parentDir = par;
	}
	
	public String getParentDir() {
		return parentDir;
	}
	
}

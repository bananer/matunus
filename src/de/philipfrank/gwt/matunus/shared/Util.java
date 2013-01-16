package de.philipfrank.gwt.matunus.shared;

public class Util {
	public static String tailSlash(String dir) {
		return dir.endsWith("/") ? dir : dir + "/";
	}
}

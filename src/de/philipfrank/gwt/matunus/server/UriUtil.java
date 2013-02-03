package de.philipfrank.gwt.matunus.server;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;

import de.philipfrank.gwt.matunus.shared.RemoteFile;
import de.philipfrank.gwt.matunus.shared.Util;

public class UriUtil {
	private String scheme;
	private String authority;
	private String path;

	public UriUtil(ServletContext context) {
		String urlRoot = context.getInitParameter("urlRoot");
		try {
			URI parsed = new URI(urlRoot);
			scheme = parsed.getScheme();
			authority = parsed.getAuthority();
			path = Util.tailSlash(parsed.getPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public URI getServletUri(String servletEndpoint, String relativeDir,
			RemoteFile file) {
		String relative = path + servletEndpoint + Util.tailSlash(relativeDir);
		if (file != null) {
			relative += file.getName();
		}
		try {
			return new URI(scheme, authority, relative, "", "");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}

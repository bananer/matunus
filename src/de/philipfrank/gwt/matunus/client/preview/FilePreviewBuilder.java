package de.philipfrank.gwt.matunus.client.preview;

import de.philipfrank.gwt.matunus.shared.RemoteFile;

abstract class FilePreviewBuilder extends PreviewBuilder {
	RemoteFile file;

	public FilePreviewBuilder(RemoteFile file) {
		super();
		this.file = file;
	}
}
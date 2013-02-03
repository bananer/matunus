package de.philipfrank.gwt.matunus.client.preview;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import de.philipfrank.gwt.matunus.shared.RemoteFile;

class ImagePreviewBuilder extends FilePreviewBuilder {
	public static final String[] supports = {".jpg", ".jpeg", ".png", ".gif"};
	
	public ImagePreviewBuilder(RemoteFile file) {
		super(file);
	}

	@Override
	public Widget render() {
		return new Image(file.getDownloadLink());
	}
}
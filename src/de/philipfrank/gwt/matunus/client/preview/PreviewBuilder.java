package de.philipfrank.gwt.matunus.client.preview;

import com.google.gwt.user.client.ui.Widget;

import de.philipfrank.gwt.matunus.shared.RemoteFile;

public abstract class PreviewBuilder {
	public abstract Widget render();
	
	public static PreviewBuilder getPreviewBuilder(RemoteFile file) {
		String fileName = file.getName().toLowerCase();
		// TODO: use mimetype

		for (String ext : ImagePreviewBuilder.supports) {
			if (fileName.endsWith(ext)) {
				return new ImagePreviewBuilder(file);
			}
		}
		for (String ext : AudioPreviewBuilder.supports) {
			if (fileName.endsWith(ext)) {
				return new AudioPreviewBuilder(file);
			}
		}
		for (String ext : VideoPreviewBuilder.supports) {
			if (fileName.endsWith(ext)) {
				return new VideoPreviewBuilder(file);
			}
		}
		return null;
	}
}
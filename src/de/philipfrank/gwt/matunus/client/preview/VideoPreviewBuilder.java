package de.philipfrank.gwt.matunus.client.preview;

import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.philipfrank.gwt.matunus.shared.RemoteFile;

public class VideoPreviewBuilder extends FilePreviewBuilder {
	static final String[] supports = {".avi", ".mpg", ".mkv"};
	
	public VideoPreviewBuilder(RemoteFile file) {
		super(file);
	}

	@Override
	public Widget render() {
		Video v = Video.createIfSupported();
		if (v != null) {
			v.setSrc(file.getDownloadLink());
			v.setControls(true);
			v.play();
			v.setTitle(file.getName());
			return v;
		}
		return new Label("Video not supported by your browser.");
	}
}
package de.philipfrank.gwt.matunus.client.preview;

import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.philipfrank.gwt.matunus.shared.RemoteFile;

class AudioPreviewBuilder extends FilePreviewBuilder {
	static final String[] supports = {".mp3", ".ogg", ".wav" };

	public AudioPreviewBuilder(RemoteFile file) {
		super(file);
	}

	@Override
	public Widget render() {
		Audio a = Audio.createIfSupported();
		if(a != null) {
			a.setSrc(file.getDownloadLink());
			a.play();
			a.setControls(true);
			a.setTitle(file.getName());
			return a;
		}
		return new Label("Audio not supported by your browser.");
	}		
}
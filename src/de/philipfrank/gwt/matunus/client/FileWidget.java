package de.philipfrank.gwt.matunus.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.philipfrank.gwt.matunus.shared.RemoteFile;

class FileWidget extends VerticalPanel {
	
	interface PreviewBuilder {
		public Widget render();
	}
	
	abstract static class FilePreviewBuilder implements PreviewBuilder {
		RemoteFile file;

		public FilePreviewBuilder(RemoteFile file) {
			super();
			this.file = file;
		}
	}
	
	static class ImagePreviewBuilder extends FilePreviewBuilder implements PreviewBuilder {
		public static final String[] supports = {".jpg", ".jpeg", ".png", ".gif"};
		
		public ImagePreviewBuilder(RemoteFile file) {
			super(file);
		}

		@Override
		public Widget render() {
			return new Image(file.getDownloadLink());
		}
	}
	
	static class VideoPreviewBuilder extends FilePreviewBuilder implements PreviewBuilder {
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
	
	static class AudioPreviewBuilder extends FilePreviewBuilder implements PreviewBuilder {
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
	
	private PreviewBuilder previewBuilder;
	private Widget preview;
	
	private static PreviewBuilder getPreviewBuilder(RemoteFile file){
		String fileName = file.getName().toLowerCase();
		// TODO: use mimetype
		
		for(String ext : ImagePreviewBuilder.supports) {
			if(fileName.endsWith(ext)) {
				return new ImagePreviewBuilder(file);
			}
		}
		for(String ext : AudioPreviewBuilder.supports) {
			if(fileName.endsWith(ext)) {
				return new AudioPreviewBuilder(file);
			}
		}
		for(String ext : VideoPreviewBuilder.supports) {
			if(fileName.endsWith(ext)) {
				return new VideoPreviewBuilder(file);
			}
		}
		return null;
	}

	
	public FileWidget(RemoteFile file) {
		super();
		HorizontalPanel mainRow = new HorizontalPanel();
		add(mainRow);
		mainRow.add(new Anchor(file.getName(), file.getDownloadLink()));
		previewBuilder = getPreviewBuilder(file);
		if(previewBuilder != null) {
			Button previewButton = new Button("►");
			previewButton.addClickHandler(new ClickHandler() {
				private boolean showing = false;
				@Override
				public void onClick(ClickEvent event) {
					if(!showing) {
						if(preview == null) {
							preview = previewBuilder.render();
						}
						if(preview.getOffsetHeight()>500) {
							preview.setHeight("500px");
						}
						FileWidget.this.add(preview);
					}
					else {
						FileWidget.this.remove(preview);
					}
					showing = !showing;
				}
			});
			mainRow.add(previewButton);
		}
	}
}
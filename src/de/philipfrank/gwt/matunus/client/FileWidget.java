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

class FileWidget extends HorizontalPanel {
	
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
				v.setAutoplay(true);
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
				return a;
			}
			return new Label("Audio not supported by your browser.");
		}		
	}
	
	private PreviewBuilder previewBuilder;
	private DialogBox previewDialog;
	
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
	
	private DialogBox buildDialog() {
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Preview");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		VerticalPanel dialogVPanel = new VerticalPanel();
		Widget preview = previewBuilder.render();
		preview.setHeight("500px");
		preview.setWidth("500px");
		dialogVPanel.add(preview);
		dialogVPanel.add(closeButton);
		dialogBox.setWidth("750px");
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		return dialogBox;
	}
	
	public FileWidget(RemoteFile file) {
		super();
		add(new Anchor(file.getName(), file.getDownloadLink()));
		previewBuilder = getPreviewBuilder(file);
		if(previewBuilder != null) {
			Button previewButton = new Button("►");
			previewButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(previewDialog == null) {
						previewDialog = buildDialog();
					}
					previewDialog.center();
				}
			});
			add(previewButton);
		}
	}
}
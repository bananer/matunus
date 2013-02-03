package de.philipfrank.gwt.matunus.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.philipfrank.gwt.matunus.client.preview.PreviewBuilder;
import de.philipfrank.gwt.matunus.shared.RemoteFile;
import de.philipfrank.gwt.matunus.shared.Util;

class FileWidget extends VerticalPanel {

	private PreviewBuilder previewBuilder;
	private Widget preview;
	private RemoteFile file;
	private HorizontalPanel mainRow;

	private static final SafeUri ICON_PLAY = UriUtils.fromSafeConstant("icons/play.svg");
	private static final SafeUri ICON_STOP = UriUtils.fromSafeConstant("icons/stop.svg");
	private static final SafeUri ICON_FILE = UriUtils.fromSafeConstant("icons/file.svg");
	private static final SafeUri ICON_FOLDER = UriUtils.fromSafeConstant("icons/folder.svg");

	private Image getIcon() {
		SafeUri uri;
		if (file.isDirectory()) {
			uri = ICON_FOLDER;
		} else {
			uri = ICON_FILE;
		}

		Image ret = new Image(uri);
		// ret.setWidth("24px");
		ret.setHeight("24px");
		return ret;
	}
	
	

	private List<Widget> getActions() {
		List<Widget> ret = new LinkedList<Widget>();

		if (file.isDirectory()) {

		} else {
			previewBuilder = PreviewBuilder.getPreviewBuilder(file);
			if (previewBuilder != null) {
				final Image previewButton = new Image(ICON_PLAY);
				previewButton.setWidth("15px");
				previewButton.setHeight("15px");

				previewButton.addClickHandler(new ClickHandler() {
					private boolean showing = false;

					@Override
					public void onClick(ClickEvent event) {
						if (!showing) {
							if (preview == null) {
								preview = previewBuilder.render();
							}
							if(preview.getOffsetWidth() > mainRow.getOffsetWidth())
							{
								preview.setWidth("100%");
							}
							previewButton.setUrl(ICON_STOP);
							FileWidget.this.setHorizontalAlignment(ALIGN_RIGHT);
							FileWidget.this.add(preview);
						} else {
							previewButton.setUrl(ICON_PLAY);
							FileWidget.this.remove(preview);
						}
						showing = !showing;
					}
				});
				ret.add(previewButton);
			}
		}

		return ret;
	}

	public FileWidget(RemoteFile remoteFile, String parentDirectory, int index) {
		super();
		file = remoteFile;
		setWidth("100%");
		mainRow = new HorizontalPanel();
		mainRow.setWidth("100%");
		mainRow.setVerticalAlignment(ALIGN_MIDDLE);
		
		setStylePrimaryName("fileRow");
		if(index %2 == 0) {
			addStyleDependentName("even");
		}
		add(mainRow);
		mainRow.setHorizontalAlignment(ALIGN_LEFT);

		mainRow.add(getIcon());

		Widget link;
		if (file.isDirectory()) {
			link = new Hyperlink(Util.tailSlash(file.getName()),
					Util.tailSlash(parentDirectory)
							+ Util.tailSlash(file.getName()));
			link.addStyleDependentName("directory");
		} else {
			link = new Anchor(file.getName(), file.getDownloadLink());
			link.addStyleDependentName("file");
		}

		link.setStylePrimaryName("fileLink");
		mainRow.add(link);
		mainRow.setCellWidth(link, "100%");
		
		List<Widget> actions = getActions();
		mainRow.setHorizontalAlignment(ALIGN_RIGHT);

		for (Widget a : actions) {
			mainRow.add(a);
		}
	}
}
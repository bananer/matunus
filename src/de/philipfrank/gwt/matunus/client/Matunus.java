package de.philipfrank.gwt.matunus.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.philipfrank.gwt.matunus.shared.RemoteDirectory;
import de.philipfrank.gwt.matunus.shared.RemoteFile;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Matunus implements EntryPoint {

	private final FileListServiceAsync fileListService = GWT
			.create(FileListService.class);
	private String directory;

	// UI elements
	private VerticalPanel fileList = new VerticalPanel(); // TODO: images
	private Label directoryLabel = new Label("/", false);
	private Hyperlink parentLink = new Hyperlink();
	private Widget spinner;
	private Anchor downloadDirLink = new Anchor(".zip");

	private void setDirectory(String newDir) {
		spinner.setVisible(true);

		fileList.clear();
		directory = newDir;
		parentLink.setVisible(false);
		directoryLabel.setVisible(false);
		downloadDirLink.setVisible(false);
		fileListService.read(directory, new AsyncCallback<RemoteDirectory>() {
			@Override
			public void onSuccess(RemoteDirectory result) {
				spinner.setVisible(false);
				directoryLabel.setVisible(true);
				directoryLabel.setText(result.getDisplayName());

				if (result.getParentDir() != null) {
					parentLink.setTargetHistoryToken(result.getParentDir());
					parentLink.setVisible(true);
				}

				if (result.getZippedDownload() != null) {
					downloadDirLink.setHref(result.getZippedDownload());
					downloadDirLink.setVisible(true);
				}

				RemoteDirectory.sort(result);

				int i = 0;
				for (RemoteFile entry : result) {
					fileList.add(new FileWidget(entry, directory, i++));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				connectionError(caught);
			}
		});
	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		directory = History.getToken();

		fileList.setWidth("100%");

		RootPanel container = RootPanel.get("fileListContainer");

		Image parentLinkImage = new Image(
				UriUtils.fromSafeConstant("icons/up.svg"));
		parentLinkImage.setWidth("15px");
		parentLink.getElement().getFirstChild()
				.appendChild(parentLinkImage.getElement());

		Image downloadDirLinkImage = new Image(
				UriUtils.fromSafeConstant("icons/download.svg"));
		downloadDirLinkImage.setWidth("15px");
		downloadDirLink.getElement().appendChild(
				downloadDirLinkImage.getElement());

		HorizontalPanel topRow = new HorizontalPanel();
		topRow.setStylePrimaryName("topRow");

		spinner = RootPanel.get("spinner");
		topRow.add(spinner);
		topRow.add(parentLink);
		topRow.add(directoryLabel);
		topRow.add(downloadDirLink);

		container.add(topRow);

		container.add(fileList);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setDirectory(event.getValue());
			}
		});

		History.fireCurrentHistoryState();

	}

	private void connectionError(Throwable caught) {

		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.add(new Label(caught.getLocalizedMessage()));
		caught.printStackTrace();
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		dialogBox.center();
	}

}

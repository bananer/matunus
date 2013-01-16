package de.philipfrank.gwt.matunus.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	private Tree fileList = new Tree(); // TODO: images
	private Label directoryLabel = new Label("/", false);
	private Hyperlink parentLink = new Hyperlink("..", "");

	private void setDirectory(String newDir) {

		if (newDir.startsWith(directory)) {
			// TODO: Does this make sense?
			parentLink.setTargetHistoryToken(directory);
		}

		directory = newDir;
		directoryLabel.setText(directory.isEmpty() ? "/" : directory);
		fileListService.read(directory, new AsyncCallback<RemoteDirectory>() {
			@Override
			public void onSuccess(RemoteDirectory result) {

				fileList.removeItems();

				for (RemoteFile entry : result) {
					if (entry.isDirectory()) {
						fileList.addItem(new TreeItem(new DirectoryWidget(directory, entry)));
					} else {
						fileList.addItem(new TreeItem(new FileWidget(entry)));
					}
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
	public void onModuleLoad() {
		directory = History.getToken();

		RootPanel container = RootPanel.get("fileListContainer");
		container.add(directoryLabel);
		container.add(parentLink);
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
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		dialogBox.center();
	}
	
}

package de.philipfrank.gwt.matunus.client;

import java.util.Comparator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.util.Strings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import de.philipfrank.gwt.matunus.client.rpc.FileListService;
import de.philipfrank.gwt.matunus.client.rpc.FileListServiceAsync;
import de.philipfrank.gwt.matunus.shared.RemoteDirectory;
import de.philipfrank.gwt.matunus.shared.RemoteFile;
import de.philipfrank.gwt.matunus.shared.Util;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Matunus implements EntryPoint {

	private final FileListServiceAsync fileListService = GWT
			.create(FileListService.class);
	private String directory;
	
	// UI elements
	private Label directoryLabel = new Label("/", false);
	private Hyperlink parentLink = new Hyperlink("..", "");
	private Widget spinner = new Label("Loading...");
	private CellTable<RemoteFile> cellTable = new CellTable<RemoteFile>();
	private ListDataProvider<RemoteFile> dataProvider = new ListDataProvider<RemoteFile>();
	private HandlerRegistration sortHandlerRegistration;
	private TextColumn<RemoteFile> nameCol = new TextColumn<RemoteFile>() {
		@Override
		public String getValue(RemoteFile object) {
			return object.getName();
		}
	};
	private TextColumn<RemoteFile> sizeCol = new TextColumn<RemoteFile>() {
		@Override
		public String getValue(RemoteFile object) {
			if(object.isDirectory()) {
				return "";
			}
			// todo: kB, MB, etc
			return Long.toString(object.getSize());
		}
	};
	
	private Comparator<RemoteFile> nameComparator = new Comparator<RemoteFile>() {
		@Override
		public int compare(RemoteFile o1, RemoteFile o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	private Comparator<RemoteFile> sizeComparator = new Comparator<RemoteFile>() {
		@Override
		public int compare(RemoteFile o1, RemoteFile o2) {
			if(o2.getSize() == o1.getSize()) return 0;
			return o1.getSize() > o2.getSize() ? 1 : -1;
		}
	};

	private void setDirectory(String newDir) {
		spinner.setVisible(true);

		directory = newDir;
		directoryLabel.setText(directory);
		parentLink.setVisible(false);
		
		cellTable.setVisibleRangeAndClearData(new Range(0, 0), true);
		
		fileListService.read(directory, new AsyncCallback<RemoteDirectory>() {

		@Override
		public void onSuccess(RemoteDirectory result) {
			spinner.setVisible(false);
			
			if(!result.getParentDir().isEmpty()) {
				parentLink.setTargetHistoryToken(result.getParentDir());
				parentLink.setVisible(true);
			}
			cellTable.setVisibleRangeAndClearData(new Range(0, result.size()), true);
			dataProvider.setList(result);
			
			if(sortHandlerRegistration != null) {
				sortHandlerRegistration.removeHandler();
			}

			ColumnSortEvent.ListHandler<RemoteFile> listSorter = new ColumnSortEvent.ListHandler<RemoteFile>(dataProvider.getList());
			listSorter.setComparator(nameCol, nameComparator);
			listSorter.setComparator(sizeCol, sizeComparator);
			
			sortHandlerRegistration = cellTable.addColumnSortHandler(listSorter);
			
			/*for (RemoteFile entry : result) {
				if (entry.isDirectory()) {
					cellTable.add
					fileList.addItem(new TreeItem(new DirectoryWidget(directory, entry)));
				} else {
					fileList.addItem(new TreeItem(new FileWidget(entry)));
				}
			}*/
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
		container.add(spinner);
		container.add(parentLink);
		nameCol.setSortable(true);
		cellTable.addColumn(nameCol, "Name");
		sizeCol.setSortable(true);
		cellTable.addColumn(sizeCol, "Size");
		dataProvider.addDataDisplay(cellTable);
		
		cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		final SingleSelectionModel<RemoteFile> selectionModel = new SingleSelectionModel<RemoteFile>();
		cellTable.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				RemoteFile selected = selectionModel.getSelectedObject();
				if(selected.isDirectory()) {
					History.newItem(Util.tailSlash(directory) + Util.tailSlash(selected.getName()));
				}
				else {
					Window.open(selected.getDownloadLink(), "_blank", "");
				}
			}
		});
		
		container.add(cellTable);
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

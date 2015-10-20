package de.brod.tools.picture.curator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import jdk.management.resource.internal.inst.SocketOutputStreamRMHooks;

public class PictureImporter implements Initializable {
	@FXML
	private ListView<String> imageItems;

	@FXML
	private Pane imageBox;

	@FXML
	ListView<String> folderItems;

	@FXML
	Button btnMove;

	@FXML
	Button btnDelete;

	@FXML
	Button btnExit;

	@FXML
	TextField folderInput;

	private ObservableList<String> observedImageItems, observedFolderItems;
	private Map<String, File> mapOfImages = new HashMap<String, File>();
	private Map<String, String> mapOfMonths = new HashMap<String, String>();
	private File importFolder = new File("c:\\Daten\\Bilder\\DCIM");
	private File outputFolder = new File("c:\\Daten\\Bilder\\Alben");
	private File newFolder = new File("c:\\Daten\\Bilder\\AlbenNew");

	private CuratorImage bufferedImage;
	private String lastSize = "";

	private String getMonth(String group) {
		String month = mapOfMonths.get(group);
		if (month == null) {
			try {
				month = new SimpleDateFormat("MMMM").format(new SimpleDateFormat("yyyyddMM").parse("200001" + group));
			} catch (Exception e) {
				month = group + ".";
			}
			mapOfMonths.put(group, month);
		}
		return month;
	}

	private void initFolderItems() {
		MultipleSelectionModel<String> selectionModel = folderItems.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		selectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// Your action here
				folderInput.setText(newValue);
			}
		});
		folderInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				checkStateOfButtons();
			}
		});

		observedFolderItems = FXCollections.observableArrayList();
		folderItems.setItems(observedFolderItems);
	}

	protected void checkStateOfButtons() {
		boolean bImageSelected = imageItems.getSelectionModel().getSelectedItem() != null;
		boolean bFolderItemSelected = folderInput.getText() != null && folderInput.getText().length() > 0;
		boolean bMove = bFolderItemSelected && bImageSelected;
		btnMove.setDisable(!bMove);
		btnDelete.setDisable(!bImageSelected);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initImageItems();
		initFolderItems();
		initImageBox();

		reloadImages();
	}

	private void initImageBox() {
		ChangeListener<Number> sizeListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth) {
				openImageWithinAThread(imageItems.getSelectionModel().getSelectedItem());
			}
		};
		imageBox.widthProperty().addListener(sizeListener);
		imageBox.heightProperty().addListener(sizeListener);
	}

	private void initImageItems() {
		MultipleSelectionModel<String> selectionModel = imageItems.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		selectionModel.selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// Your action here
				checkStateOfButtons();
				openImageWithinAThread(newValue);
			}
		});
		observedImageItems = FXCollections.observableArrayList();
		imageItems.setItems(observedImageItems);

	}

	private void loadFolderList() {
		List<String> lst = new ArrayList<>();
		Pattern compile = Pattern.compile("(.*) \\d{4}");
		addFolderNamesWithoutYear(lst, compile, outputFolder.list());
		for (File subFolder : newFolder.listFiles()) {
			if (subFolder.isDirectory())
				addFolderNamesWithoutYear(lst, compile, subFolder.list());
		}
		Collections.sort(lst);
		observedFolderItems.clear();
		observedFolderItems.addAll(lst);
	}

	private void addFolderNamesWithoutYear(List<String> lst, Pattern compile, String[] list) {
		for (String sFileName : list) {
			Matcher matcher = compile.matcher(sFileName);
			if (matcher.find()) {
				String group = matcher.group(1);
				if (!lst.contains(group)) {
					lst.add(group);
				}
			}
		}
	}

	private void loadImageList() {
		mapOfImages.clear();
		Pattern compile = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})[_](\\d{2})(\\d{2})(\\d{2}).*");
		for (File sFileName : importFolder.listFiles()) {
			String sName = sFileName.getName();
			Matcher matcher = compile.matcher(sName);
			if (matcher.find()) {
				sName = matcher.group(3) + "." + getMonth(matcher.group(2)) + " " + matcher.group(1) + " "
						+ matcher.group(4) + ":" + matcher.group(5) + ":" + matcher.group(6);
				if (mapOfImages.containsKey(sName)) {
					int iCount = 2;
					while (mapOfImages.containsKey(sName + iCount)) {
						iCount++;
					}
					sName += iCount;
				}
			}
			mapOfImages.put(sName, sFileName);
		}
		String[] array = mapOfImages.keySet().toArray(new String[0]);
		Comparator<String> comp = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return mapOfImages.get(o1).compareTo(mapOfImages.get(o2));
			}
		};
		Arrays.sort(array, comp);
		observedImageItems.clear();
		observedImageItems.addAll(array);
	}

	protected void openImageWithinAThread(String sFileName) {
		new Thread() {
			public synchronized void run() {
				openImportImage(sFileName);
			};
		}.start();
	}

	protected void openImportImage(String sFileName) {
		try {
			if (sFileName == null)
				return;
			int scaledWidth = (int) imageBox.getWidth();
			int scaledHeight = (int) imageBox.getHeight();
			String newSize = String.valueOf(scaledWidth + scaledHeight * 10000) + sFileName;
			if (newSize.equals(lastSize)) {
				return;
			}
			lastSize = newSize;
			File file = mapOfImages.get(sFileName);
			if (file == null)
				return;
			bufferedImage = new CuratorImage(file);

			Image image = bufferedImage.resizeBufferedImage(scaledWidth, scaledHeight);
			BackgroundImage images = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
					BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
			Background background = new Background(images);
			imageBox.setBackground(background);

		} catch (Exception e) {
			// could not open image
			showError(e);
		}
	}

	@FXML
	public void buttonMovePressed() {
		String moveToFolder = folderInput.getText();
		List<String> selectedItems = imageItems.getSelectionModel().getSelectedItems();

		TextInputDialog dialog = new TextInputDialog(moveToFolder);
		dialog.setTitle("Move images");
		if (selectedItems.size() != 1) {
			dialog.setHeaderText("Move the " + selectedItems.size() + " selected images");
		} else {
			dialog.setHeaderText("Move the selected image");
		}
		dialog.setGraphic(new ImageView(this.getClass().getResource("Rename.png").toString()));
		dialog.setContentText("Move to");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			// ... user chose OK
			int iNotRenamed = 0;
			for (String string : selectedItems) {
				File file = mapOfImages.get(string);
				String year = file.getName().substring(0, 4);
				String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				File toFolder = new File(new File(newFolder, today), moveToFolder + " " + year);
				if (!toFolder.exists()) {
					toFolder.mkdirs();
				}
				File dest = new File(toFolder, file.getName());
				System.out.println("... rename " + file.getName() + " to " + dest.getAbsolutePath());
				if (!file.renameTo(dest)) {
					iNotRenamed++;
				}
			}
			if (iNotRenamed > 0)
				showError(new IOException("Could not rename " + iNotRenamed + " file" + (iNotRenamed > 1 ? "s" : "")));
			reloadImages();
		} else {
			// ... user chose CANCEL or closed the dialog
		}
	}

	private void showError(Exception ex) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(ex.toString());
		OutputStream out = new ByteArrayOutputStream();
		ex.printStackTrace(new PrintStream(out));
		BufferedReader reader = new BufferedReader(new StringReader(out.toString()));
		String sLine = "";
		StringBuilder sOut = new StringBuilder();
		try {
			while ((sLine = reader.readLine()) != null) {
				if (sLine.replace("\t", " ").contains(" at "))
					if (!sLine.contains("de.brod.")) {
						// ignore
					} else {
						sOut.append(sLine).append("\n");
					}
			}
		} catch (IOException e) {
			// should not happen on stringreader
		}
		alert.setContentText(sOut.toString());
		alert.show();
	}

	@FXML
	public void buttonDeletePressed() {
	}

	@FXML
	public void buttonExitPressed() {
		System.exit(0);
	}

	@FXML
	public void reloadImages() {
		loadImageList();
		loadFolderList();
	}

	@FXML public void openOrganizePage() {
		PictureCurator.openScene(PictureCurator.ORGANIZER);
	}

}

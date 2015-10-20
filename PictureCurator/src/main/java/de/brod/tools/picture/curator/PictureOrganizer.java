package de.brod.tools.picture.curator;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class PictureOrganizer {

	@FXML
	ListView<String> imageItems;
	@FXML
	ListView<String> folderItems;
	@FXML
	TextField folderInput;
	@FXML
	Pane imageBox;

	@FXML
	public void reloadImages() {
	}

	@FXML
	public void buttonMovePressed() {
	}

	@FXML
	public void buttonDeletePressed() {
	}

	@FXML
	public void buttonExitPressed() {
	}

	@FXML
	public void openImportPage() {
		PictureCurator.openScene(PictureCurator.IMPORTER);
	}

}

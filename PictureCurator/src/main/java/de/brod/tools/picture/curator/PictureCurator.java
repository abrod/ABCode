package de.brod.tools.picture.curator;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PictureCurator extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		stage.setTitle("Picture Curator");

		stage.setScene(loadScene());
		stage.setMaximized(true);
		stage.show();
	}

	private Scene loadScene() throws IOException {
		Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("PictureImporter.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("PictureImporter.css").toString());
		return scene;
	}

}

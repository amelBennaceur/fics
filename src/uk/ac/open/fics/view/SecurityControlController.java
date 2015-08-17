package uk.ac.open.fics.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.fics.MainApp;

public class SecurityControlController {

	@FXML
	private TextField featuresField;
	@FXML
	private TextField attributesField;
	@FXML
	private TextField goalField;

	private Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private String goalFilePath;

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleCompose() {
		if (mainApp.compose(true)) {

			
//			dialogStage.close();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText("ERROR Message");

			alert.showAndWait();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleBrowseGoal() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(dialogStage);
		if (selectedFile != null) {
			goalFilePath = selectedFile.getAbsolutePath();
			SecurityControl sc = SecurityControl.loadSecurityControlFromFile(goalFilePath);
			mainApp.setSecurityControl(sc);
			featuresField.setText(sc.getFeatures().toString());
			attributesField.setText(sc.getAttributes() + "");
			goalField.setText(sc.getGoal() + "");
			mainApp.setSecurityControl(sc);
		}
	}

}

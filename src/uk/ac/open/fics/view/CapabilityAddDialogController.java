package uk.ac.open.fics.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.ac.open.fics.MainApp;

public class CapabilityAddDialogController {

	@FXML
	private TextField fmField;
	@FXML
	private TextField behField;

	private Stage dialogStage;
	private boolean okClicked = false;
	private MainApp mainApp;

	private String fmFilePath;
	private String behFilePath;

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
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		System.out.println("Handling OK");

		if (mainApp.addCapability(fmFilePath, behFilePath)) {
			dialogStage.close();
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
	 * Called when the user clicks on the Browse button to select the file
	 * describing the feature model.
	 */
	@FXML
	private void handleBrowseFM() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TVL Files", "*.tvl"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(dialogStage);
		if (selectedFile != null) {
			fmFilePath = selectedFile.getAbsolutePath();
			fmField.setText(fmFilePath);
		}

	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleBrowseBeh() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(dialogStage);
		if (selectedFile != null) {
			behFilePath = selectedFile.getAbsolutePath();
			behField.setText(behFilePath);
		}
	}

}

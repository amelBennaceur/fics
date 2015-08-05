package uk.ac.open.fics.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import uk.ac.open.fics.MainApp;

public class MainLayoutController {
	// Reference to the main application
	private MainApp mainApp;

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void handleManageCapabilities() {
		System.out.println("Handling Manage Capabilities");

		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("CapabilityStore.fxml"));

		Parent root1 = null;

		try {
			root1 = (Parent) loader2.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Stage stage = new Stage();
		stage.setTitle("Capability Store");
		stage.setScene(new Scene(root1));
		stage.show();

		CapabilityStoreController capabilityStoreController = loader2.getController();

		capabilityStoreController.setMainApp(mainApp);
		capabilityStoreController.setDialogStage(stage);
	}

	@FXML
	private void handleCompose() {
		System.out.println("Handling Compose");

		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("SecurityControl.fxml"));

		Parent root1 = null;

		try {
			root1 = (Parent) loader2.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Stage stage = new Stage();
		stage.setTitle("Security Control");
		stage.setScene(new Scene(root1));
		stage.show();

		SecurityControlController scController = loader2.getController();

		scController.setMainApp(mainApp);
		scController.setDialogStage(stage);
	}

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("FICS: Features-driven MedIation for Collaborative Security");
		alert.setHeaderText("About");
		alert.setContentText("Author: Amel Bennaceur\nWebsite: http://amel.me");

		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}

}

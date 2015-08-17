package uk.ac.open.fics.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uk.ac.open.capability.model.Capability;
import uk.ac.open.fics.MainApp;

public class CapabilityStoreController {

	@FXML
	private ListView<Capability> capabilityList;

	private Stage dialogStage;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public CapabilityStoreController() {
	}

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

		// Add observable list data to the table
		capabilityList.setItems(mainApp.getCapabilities());
		capabilityList.setCellFactory(cb -> new CapabilityCell());
	}

	@FXML
	private void handleAddCapability() {
		// Load the fxml file and create a new stage for the popup dialog.
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CapabilityAddDialog.fxml"));

		Parent root1 = null;

		try {
			root1 = (Parent) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create the dialog Stage.
		Stage dialogStage = new Stage();
		dialogStage.setTitle("Add Capability");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		// dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(root1);
		dialogStage.setScene(scene);

		// Set the person into the controller.
		CapabilityAddDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setMainApp(mainApp);

		// Show the dialog and wait until the user closes it
		dialogStage.showAndWait();
	}

	@FXML
	private void handleViewCapability() {
		Capability capa = capabilityList.getSelectionModel().getSelectedItem();
		
		
		// Load the fxml file and create a new stage for the popup dialog.
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CapabilityOverview.fxml"));

		Parent root1 = null;

		try {
			root1 = (Parent) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create the dialog Stage.
		Stage dialogStage = new Stage();
		dialogStage.setTitle("Add Capability");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		// dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(root1);
		dialogStage.setScene(scene);

		// Set the person into the controller.
		CapabilityOverviewController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setFM(capa.getFm().toString());
		controller.setBehaviour(capa.getFts().toString());

		dialogStage.showAndWait();

	}

	@FXML
	private void handleDeleteCapability() {
		mainApp.deleteCapability(capabilityList.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	static class CapabilityCell extends ListCell<Capability> {
		@Override
		public void updateItem(Capability item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(item.getName());
				setAlignment(Pos.CENTER);
			}
		}
	}
}

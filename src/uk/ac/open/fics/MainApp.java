package uk.ac.open.fics;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import solver.Solver;
import uk.ac.open.capability.model.Capability;
import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.capability.selection.CapabilitySelection;
import uk.ac.open.fics.view.CapabilityAddDialogController;
import uk.ac.open.fics.view.CapabilitySelectionResultsController;
import uk.ac.open.fics.view.MainLayoutController;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Capability> capabilities = FXCollections.observableArrayList();

	private ArrayList<Capability> capabilityStore = new ArrayList<Capability>();

	private SecurityControl currentSC;

	private Solver chocoSolver = new Solver("Feature Selection");

	/**
	 * Constructor
	 */
	public MainApp() {
		// Add some sample data

	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("FICS: Features-driven MedIation for Collaborative Security");

		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/MainLayout.fxml"));
		try {
			rootLayout = (BorderPane) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();

		// Give the controller access to the main app.
		MainLayoutController mainLayoutController = loader.getController();
		mainLayoutController.setMainApp(this);

	}

	public static void main(String[] args) {
		launch(args);
	}

	public ObservableList<Capability> getCapabilities() {
		return capabilities;
	}

	public boolean addCapability(String fmFilePath, String behFilePath) {
		Capability capa = new Capability();
		if (capa.loadCapability(fmFilePath, behFilePath, chocoSolver)) {
			capabilities.add(capa);
			capabilityStore.add(capa);
			return true;
		}
		return false;
	}

	public void setSecurityControl(SecurityControl sc) {
		currentSC = sc;
	}

	public void deleteCapability(Capability selectedItem) {
		// TODO must change the solver as well...
		capabilities.remove(selectedItem);
		capabilityStore.remove(selectedItem);
	}

	public boolean compose(String text, String text2, String goalFilePath) {
		boolean done = false;
		CapabilitySelection fs = new CapabilitySelection(capabilityStore, currentSC);
		done = fs.areFeaturesPresentInSecurityControl();
		if (done) {
			done = fs.areAttributesOfSecurityControlPresent();
		}
		if (fs.compose(chocoSolver)) {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("view/CapabilitySelectionResults.fxml"));

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
			CapabilitySelectionResultsController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setSelectedFeatures(fs.getFeatures());
			controller.setSynthesisedMediator(fs.getMediator().toString());

			dialogStage.showAndWait();

			done = true;
		} else {
			done = false;
		}
		return done;
	}
}

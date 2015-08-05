package uk.ac.open.fics.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CapabilitySelectionResultsController {

	@FXML
	private TextArea featuresField;
	@FXML
	private TextArea mediatorField;

	private Stage dialogStage;
	
	
	
	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	
	public void setSelectedFeatures(String selectedFeatures){
		featuresField.setText(selectedFeatures);
	}
	
	public void setSynthesisedMediator(String synthesisedMediator){
		mediatorField.setText(synthesisedMediator);
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		dialogStage.close();
	}

	
}

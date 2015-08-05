package uk.ac.open.fics.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CapabilityOverviewController {

	@FXML
	private TextArea fmField;
	@FXML
	private TextArea behField;

	private Stage dialogStage;
	
	
	
	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	
	public void setFM(String selectedFeatures){
		fmField.setText(selectedFeatures);
	}
	
	public void setBehaviour(String synthesisedMediator){
		behField.setText(synthesisedMediator);
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		dialogStage.close();
	}
}

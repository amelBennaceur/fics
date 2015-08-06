package uk.ac.open.capabilitySelection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.fics.MainApp;

public class MultipleCapabilities {

	// @Test
	// public void loadAndCheckMultipleFeatureModels() {
	// Solver chocoSolver = new Solver("Test Features 1");
	// try {
	// FeatureModel fm = new FeatureModel("examples/tests/capabilities/fm1.tvl",
	// chocoSolver,true);
	// fm = new FeatureModel("examples/tests/capabilities/fm2.tvl",
	// chocoSolver,false);
	//
	// assertEquals(chocoSolver.findAllSolutions(), 4);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
//	 @Test
//	 public void loadSecurityControl() {
//		 MainApp app = new MainApp();
//		 SecurityControl sc = SecurityControl.loadSecurityControlFromFile("examples/tests/capabilities/securityControl.xml");
//		 app.setSecurityControl(sc);
//		 assertEquals(sc.getFeatures().size(),1);
//		 assertEquals(sc.getAttributes().size(),1);
//	 }

	@Test
	public void loadAndCheckMultipleCapabilities() {
		MainApp app = new MainApp();
		app.addCapability("examples/tests/capabilities/fm1.tvl", "examples/tests/capabilities/fts0.xml");
		app.addCapability("examples/tests/capabilities/fm2.tvl", "examples/tests/capabilities/fts0.xml");
		app.setSecurityControl("examples/tests/capabilities/securityControl0.xml");
		app.compose(false);
		assertEquals(app.getFeatures(),"[[A1, B2, FM2, FM1], [A1, A2, FM2, FM1], [B2, FM2, FM1, B1], [A2, FM2, FM1, B1]]");
		System.out.println("Features = "+app.getFeatures());
		
	}

	//
	// @Test
	// public void checkSimpleFeatureSelection() {
	//
	// }
	//
	// @Test
	// public void checkFeatureSelectionWithOneOptimisation() {
	//
	// }
	//
	// @Test
	// public void checkFeatureSelectionWithMultipleOptimisation() {
	//
	// }
	//
	// @Test
	// public void checkFeatureSelectionWithOneOptimisationAndSimpleMediation()
	// {
	//
	// }
}

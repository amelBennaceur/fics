package uk.ac.open.capabilitySelection;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import solver.Solver;
import uk.ac.open.capability.model.FeatureModel;
import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.fics.MainApp;

public class MultipleCapabilities {

	@Test
	public void loadAndCheckMultipleFeatureModels() {
		Solver chocoSolver = new Solver("Test Features 1");
		try {
			FeatureModel fm = new FeatureModel("examples/tests/capabilities/fm1.tvl", chocoSolver, true);
			fm = new FeatureModel("examples/tests/capabilities/fm2.tvl", chocoSolver, false);

			assertEquals(chocoSolver.findAllSolutions(), 9);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void loadSecurityControl() {
		MainApp app = new MainApp();
		app.reinit();
		SecurityControl sc = SecurityControl
				.loadSecurityControlFromFile("examples/tests/capabilities/securityControl0.xml");
		app.setSecurityControl(sc);
		assertEquals(sc.getFeatures(), null);
		assertEquals(sc.getAttributes(), null);
	}

	@Test
	public void loadAndCheckMultipleCapabilities() {
		MainApp app = new MainApp();
		app.reinit();
		app.addCapability("examples/tests/capabilities/fm1.tvl", "examples/tests/capabilities/fts0.xml");
		app.addCapability("examples/tests/capabilities/fm2.tvl", "examples/tests/capabilities/fts0.xml");
		app.setSecurityControl("examples/tests/capabilities/securityControl0.xml");
		app.compose(false);
		assertEquals(app.getNumberOfSolutions(),9);

	}

	@Test
	public void checkFeatureSelectionOneFeatureSecurityControl() {
		MainApp app = new MainApp();
		app.reinit();
		app.addCapability("examples/tests/capabilities/fm1.tvl", "examples/tests/capabilities/fts0.xml");
		app.addCapability("examples/tests/capabilities/fm2.tvl", "examples/tests/capabilities/fts0.xml");
		app.setSecurityControl("examples/tests/capabilities/securityControl1.xml");
		app.compose(false);
		assertEquals(app.getNumberOfSolutions(), 3);
	}

	@Test
	public void checkFeatureSelectionTwoFeatureSecurityControl() {
		MainApp app = new MainApp();
		app.reinit();
		app.addCapability("examples/tests/capabilities/fm1.tvl", "examples/tests/capabilities/fts0.xml");
		app.addCapability("examples/tests/capabilities/fm2.tvl", "examples/tests/capabilities/fts0.xml");
		app.setSecurityControl("examples/tests/capabilities/securityControl2.xml");
		app.compose(false);
		assertEquals(app.getFeatures(), "[[A1, A2, FM2, FM1]]");
	}

	@Test
	public void checkFeatureSelectionWithOneOptimisation() {
		MainApp app = new MainApp();
		app.reinit();
		app.addCapability("examples/tests/capabilities/fm1Opt.tvl", "examples/tests/capabilities/fts0.xml");
		app.addCapability("examples/tests/capabilities/fm2Opt.tvl", "examples/tests/capabilities/fts0.xml");
		app.setSecurityControl("examples/tests/capabilities/securityControl1Optimise.xml");
		app.compose(false);
		assertEquals(app.getFeatures(), "[[A1, FM1.attr == 10, FM1, FM2.attr == 20]]");

	}

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

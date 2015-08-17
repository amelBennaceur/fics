package uk.ac.open.tosemExperiments;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.open.fics.MainApp;

public class CollaborativeRobots {
	
	@Test
	public void basicCollaborativeRobotsExample(){
		MainApp app = new MainApp();
		app.reinit();
		app.addCapability("examples/naoFM.tvl", "examples/naoFTS.xml");
		app.addCapability("examples/createFM.tvl", "examples/createFTS.xml");
		app.setSecurityControl("examples/movePhoneSC.xml");
		app.compose(false);
		System.out.println("sol = " + app.getFeatures());
		assertEquals(app.getNumberOfSolutions(), 16);
	}
	

}
package uk.ac.open.capabilitySelection;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import solver.Solver;
import uk.ac.open.capability.model.FeatureModel;

public class MultipleCapabilities {

	
	@Test
	public void loadAndCheckMultipleFeatureModels() {
		Solver chocoSolver = new Solver("Test Features 1");
		try {
			FeatureModel fm = new FeatureModel("examples/tests/TVLExamples/assert-01.tvl", chocoSolver,true);
			fm = new FeatureModel("examples/tests/TVLExamples/assert-02.tvl", chocoSolver,false);
			
			assertEquals(chocoSolver.findAllSolutions(), 4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

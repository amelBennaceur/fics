package uk.ac.open.capability.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import solver.Solver;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import uk.ac.open.capability.model.Capability;
import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.capability.model.TransitionSystem;
import fr.inria.mics.mediatorSynthesis.SimpleMediatorSynthesisImpl;

public class CapabilitySelection {

	List<Capability> capabilities;
	SecurityControl sc;

	String selectedFeatures;

	TransitionSystem mediator;

	boolean solutionFound = false;

	public CapabilitySelection(ArrayList<Capability> capabilities, SecurityControl sc) {
		this.capabilities = capabilities;
		this.sc = sc;
	}

	public boolean areFeaturesPresentInSecurityControl() {
		List<String> features = sc.getFeatures();
		boolean found;
		for (String f : features) {
			found = false;
			for (Capability capa : capabilities) {
				if (capa.getFm().getTree().findFeatureByShortId(f) != null) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	public boolean areAttributesOfSecurityControlPresent() {
		boolean found = true;
		return found;
	}

	public boolean compose(Solver chocoSolver) {
		solutionFound = false;
		// chocoSolver.findAllSolutions();
		// List<Solution> solutions =
		// chocoSolver.getSolutionRecorder().getSolutions();
		//
		// System.out.println("Number of solutions = " + solutions.size());
		// for (Solution s : solutions) {
		// System.out.println("A solution is s = " + s);

		chocoSolver.findSolution();

		ArrayList<ArrayList<String>> solList = new ArrayList<ArrayList<String>>();
		//TBChanged *********************
		//Collection<IntVar> tmpAll = CPFeatureSolver.getIdVarMap.values();
		Collection<IntVar> tmpAll = new HashSet<IntVar>();
		int i = 0;
		IntVar[] variables = new IntVar[tmpAll.size()];
		for (IntVar var : tmpAll) {
			variables[i] = var;
			i++;
		}
		ArrayList<String> sol;

		do {
			Solution s = chocoSolver.getSolutionRecorder().getLastSolution();
			sol = new ArrayList<String>();
			for (i = 0; i < variables.length; i++) {
				if (s.getIntVal(variables[i]) > 0)
					if (!variables[i].getName().startsWith("ArtificialParent")) {
						if (s.getIntVal(variables[i]) == 1) {
							sol.add(variables[i].getName());
						} else {
							sol.add(variables[i].getName() + " == " + variables[i].getValue());
						}
					}

			}
			solList.add(sol);
		} while (chocoSolver.nextSolution());

		int i1 = 1;
		for (ArrayList<String> sol1 : solList) {
			System.out.println("- Solution " + i1 + ":  " + sol1);
			i1++;
		}
		if (i1 > 1) {
			solutionFound = true;
			selectedFeatures = SampleFeaturesCollaborativeRobotsExample();
			SimpleMediatorSynthesisImpl synthesis = new SimpleMediatorSynthesisImpl();
			mediator = synthesis.synthesiseAbstractMediator(null, null);
		}
		return solutionFound;

	}

	public String getFeatures() {
		if (solutionFound)
			return selectedFeatures;
		return null;
	}

	public TransitionSystem getMediator() {
		if (solutionFound)
			return mediator;
		return null;

	}
	
	private String SampleFeaturesCollaborativeRobotsExample(){
		return "[NAO, NAO.env_light, Core, Behaviour, Connection, WiFi, Vision, DarknessDetection, ObjectRecognition, Sensors, Battery, Sonar, Create, ConnectionC, Bluetooth, MotionC, Moving, Turning]";
	}

}

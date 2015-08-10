package uk.ac.open.capability.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.search.solution.AllSolutionsRecorder;
import solver.search.solution.ParetoSolutionsRecorder;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import uk.ac.open.capability.model.Capability;
import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.capability.model.TransitionSystem;
import fr.inria.mics.mediatorSynthesis.SimpleMediatorSynthesisImpl;

public class CapabilitySelection {

	private List<Capability> capabilities;
	private SecurityControl sc;

	private String selectedFeatures;

	private TransitionSystem mediator;

	private boolean solutionFound = false;

	private int numberSolutions = 0;

	public CapabilitySelection(ArrayList<Capability> capabilities) {
		this.capabilities = capabilities;
	}

	private boolean areFeaturesOfSecurityControlPresent(SecurityControl sc1) {
		List<String> features = sc1.getFeatures();
		if (features == null)
			return true;
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

	private boolean areAttributesOfSecurityControlPresent(SecurityControl sc1) {
		boolean found = true;
		return found;
	}

	public boolean setSecurityControl(SecurityControl sc1) {
		boolean ok = areFeaturesOfSecurityControlPresent(sc1);
		if (!ok)
			return false;
		ok = areAttributesOfSecurityControlPresent(sc1);
		if (!ok)
			return false;
		this.sc = sc1;
		return true;

	}

	public boolean compose(Solver chocoSolver) {
		addSecurityControlToModel(chocoSolver);
		solutionFound = false;

		ArrayList<ArrayList<String>> solList = new ArrayList<ArrayList<String>>();
		Collection<IntVar> tmpAll = CPFeatureSolver.idVarMap.values();
		int i = 0;
		IntVar[] variables = new IntVar[tmpAll.size()];
		for (IntVar var : tmpAll) {
			variables[i] = var;
			i++;
		}
		if ((sc.getAttributes() == null) || (sc.getAttributes().size() == 0)) {

			// no optimisation
			numberSolutions = 0;

			solutionFound = chocoSolver.findSolution();

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
				numberSolutions++;
			} while (chocoSolver.nextSolution());
		} else {

			// with optimisation
			ArrayList<IntVar> paretoVarsList = new ArrayList<IntVar>();
			for (String attribute : sc.getAttributes()) {
				int min = 10, max = 20;

				IntVar var = VariableFactory.bounded(attribute, min, max, chocoSolver);
				paretoVarsList.add(var);

				for (i = 0; i < variables.length; i++) {
					if (variables[i].getName().contains(attribute)) {
						int index = variables[i].getName().indexOf(".");
						String parentFeatureName = variables[i].getName().substring(0, index);
						IntVar parentFeatureVar = CPFeatureSolver.idVarMap.get(parentFeatureName);
						int val = variables[i].getValue();
						chocoSolver.post(LogicalConstraintFactory.ifThen(
								IntConstraintFactory.arithm(parentFeatureVar, ">", 0),
								IntConstraintFactory.arithm(var, "=", val)));
						if (val < min) {
							min = val;
						} else if (val > max) {
							max = val;
						}

					}

				}

			}

			if (paretoVarsList.size() == 1) {
//				chocoSolver.findOptimalSolution(ResolutionPolicy.MINIMIZE, paretoVarsList.get(0));
				chocoSolver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, paretoVarsList.get(0),
				 true);
				
				List<Solution> solutions = chocoSolver.getSolutionRecorder().getSolutions();

				
				
				numberSolutions = 0;
				if ((solutions != null ) && (solutions.size()>0)) { 
					numberSolutions = solutions.size();
					solutionFound = true;
					}

				ArrayList<String> sol;
				
				for (Solution s : solutions) {
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
				}
			} else {
				i = 0;
				IntVar[] paretoVars = new IntVar[paretoVarsList.size()];
				for (IntVar tmpVar : paretoVarsList) {
					paretoVars[i] = tmpVar;
					i++;
				}

				AllSolutionsRecorder rec = new AllSolutionsRecorder(chocoSolver);
				ParetoSolutionsRecorder paretoRecorder = new ParetoSolutionsRecorder(ResolutionPolicy.MINIMIZE,
						paretoVars);
				chocoSolver.findParetoFront(ResolutionPolicy.MINIMIZE, paretoVars);
				List<Solution> paretoFront = chocoSolver.getSolutionRecorder().getSolutions();

				System.out.println("The pareto front has " + paretoFront.size() + " solutions : ");
				// for (Solution s : paretoFront) {
				// System.out.println("The solution is x = " + s.getIntVal(x) +
				// " y = " + s.getIntVal(y) + " z = "
				// + s.getIntVal(z));
				// }
			}

		}

		int i1 = 1;
		for (ArrayList<String> sol1 : solList) {
			System.out.println("- Solution " + i1 + ":  " + sol1);
			i1++;
		}
		selectedFeatures = solList + "";

		// if (i1 > 1) {
		// solutionFound = true;
		// selectedFeatures = SampleFeaturesCollaborativeRobotsExample();
		// SimpleMediatorSynthesisImpl synthesis = new
		// SimpleMediatorSynthesisImpl();
		// mediator = synthesis.synthesiseAbstractMediator(null, null);
		// }
		return solutionFound;

	}

	private void addSecurityControlToModel(Solver chocoSolver) {
		List<String> features = sc.getFeatures();
		if (features == null)
			return;
		for (String f : features) {
			IntVar var = CPFeatureSolver.idVarMap.get(f);
			chocoSolver.post(IntConstraintFactory.arithm(var, "=", 1));
		}
	}

	public int getNumberOfSolutions() {
		return numberSolutions;
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

	private String SampleFeaturesCollaborativeRobotsExample() {
		return "[NAO, NAO.env_light, Core, Behaviour, Connection, WiFi, Vision, DarknessDetection, ObjectRecognition, Sensors, Battery, Sonar, Create, ConnectionC, Bluetooth, MotionC, Moving, Turning]";
	}

}

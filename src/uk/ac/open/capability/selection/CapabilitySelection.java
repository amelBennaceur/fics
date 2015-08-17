package uk.ac.open.capability.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.exception.ContradictionException;
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
	
	private final static Logger LOGGER = Logger.getLogger(CapabilitySelection.class.getName());

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

	public static ArrayList<String> solutionToString(Solution s, IntVar[] variables){
		ArrayList<String> sol = new ArrayList<String>();
		String parentFeatureName = null;
		IntVar parentFeatureVar;
		for (int i = 0; i < variables.length; i++) {
			if (s.getIntVal(variables[i]) > 0)
				if (!variables[i].getName().startsWith("ArtificialParent")) {
					int index = variables[i].getName().indexOf(".");
					if (index > 0) {
						parentFeatureName = variables[i].getName().substring(0, index);
						parentFeatureVar = CPFeatureSolver.idVarMap.get(parentFeatureName);
						if (parentFeatureVar.getValue() > 0) {
							sol.add(variables[i].getName() + " == " + variables[i].getValue());
						}

					} else {

						if (s.getIntVal(variables[i]) == 1) {
							sol.add(variables[i].getName());
						} else {
							sol.add(variables[i].getName() + " == " + variables[i].getValue());
						}
					}
				}

		}
		return sol;
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
				sol = solutionToString(s, variables);
				boolean newSol = true;
				for(ArrayList<String> existing:solList){
					if (areEqual(existing,sol)){
						newSol = false;
						break;
					}
				}
				if (newSol) {
					solList.add(sol);
					numberSolutions++;
				}
			} while (chocoSolver.nextSolution());
			
			
		} else {

			// with optimisation
			ArrayList<IntVar> paretoVarsList = new ArrayList<IntVar>();
			int min = 0, max = 0;
			boolean init = false;
			int count;
			String parentFeatureName = null;
			IntVar parentFeatureVar;
			
			for (String attribute : sc.getAttributes()) {
				init = false;
				
				count = 0;
				
				ArrayList<DataHelper> tmpConstraints = new ArrayList<DataHelper>();

				for (i = 0; i < variables.length; i++) {
					if (variables[i].getName().contains(attribute)) {
						count++;
						int index = variables[i].getName().indexOf(".");
						parentFeatureName = variables[i].getName().substring(0, index);
						parentFeatureVar = CPFeatureSolver.idVarMap.get(parentFeatureName);
						int min_val = variables[i].getLB();
						int max_val = variables[i].getUB();
						tmpConstraints.add(new DataHelper(parentFeatureVar, min_val, max_val));
						if (!init) {
							min = min_val;
							max = max_val;
							init = true;
						} else {
							if (min_val < min) {
								min = min_val;
							}
							if (max_val > max) {
								max = max_val;
							}
						}

					}

				}
				
				IntVar var;
				if (count == 1){
					String s = parentFeatureName+"."+attribute;
					if (!CPFeatureSolver.idVarMap.containsKey(s)) {
						var = VariableFactory.bounded(s, min, max, chocoSolver);
						CPFeatureSolver.idVarMap.put(s, var);
					} else {
						var = CPFeatureSolver.idVarMap.get(s);
					}
					
					
				}else{
					String s = attribute;
					if (!CPFeatureSolver.idVarMap.containsKey(s)) {
						var = VariableFactory.bounded(s, min, max, chocoSolver);
						CPFeatureSolver.idVarMap.put(s, var);
					} else {
						var = CPFeatureSolver.idVarMap.get(s);
					}
				}
				paretoVarsList.add(var);

				for (DataHelper d : tmpConstraints) {
					if (d.min_val==d.max_val){
					chocoSolver.post(LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(d.parent, ">", 0),
							IntConstraintFactory.arithm(var, "=", d.min_val)));
					}
				}

			}

			if (paretoVarsList.size() == 1) {
				chocoSolver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, paretoVarsList.get(0), true);

				List<Solution> solutions = chocoSolver.getSolutionRecorder().getSolutions();

				numberSolutions = 0;
				if ((solutions != null) && (solutions.size() > 0)) {
					numberSolutions = solutions.size();
					solutionFound = true;
				}else{
					solutionFound = false;
				}

				ArrayList<String> sol;

				for (Solution s : solutions) {
					sol = solutionToString(s, variables);
					solList.add(sol);
				}
			} else {
				i = 0;
				IntVar[] paretoVars = new IntVar[paretoVarsList.size()];
				for (IntVar tmpVar : paretoVarsList) {
					paretoVars[i] = tmpVar;
					i++;
				}

				
//				paretoVars[0] = CPFeatureSolver.idVarMap.get("FM.attr1");
//				paretoVars[1] = CPFeatureSolver.idVarMap.get("FM.attr2");
				
				
				AllSolutionsRecorder rec = new AllSolutionsRecorder(chocoSolver);
				ParetoSolutionsRecorder paretoRecorder = new ParetoSolutionsRecorder(ResolutionPolicy.MINIMIZE,
						paretoVars);
				chocoSolver.findParetoFront(ResolutionPolicy.MINIMIZE, paretoVars);
				List<Solution> solutions = chocoSolver.getSolutionRecorder().getSolutions();

				LOGGER.info("The pareto front has " + solutions.size());
				numberSolutions = 0;
				if ((solutions != null) && (solutions.size() > 0)) {
					numberSolutions = solutions.size();
					solutionFound = true;
				}else{
					solutionFound = false;
				}

				ArrayList<String> sol;

				for (Solution s : solutions) {
					sol = solutionToString(s, variables);
					solList.add(sol);
				}
			}

		}

//		int i1 = 1;
//		for (ArrayList<String> sol1 : solList) {
//			LOGGER.info("- Solution " + i1 + ":  " + sol1);
//			i1++;
//		}
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

	private class DataHelper {
		public DataHelper(IntVar var1, int min, int max) {
			parent = var1;
			min_val = min;
			max_val = max;;
		}

		IntVar parent;
		int min_val;
		int max_val;
	}
	
	private boolean areEqual(ArrayList<String> array1, ArrayList<String> array2) {
		if (array1.size() != array2.size())
			return false;
		boolean found;
		for(String s1:array1){
			found = false;
			for(String s2:array2){
				if (s1.equalsIgnoreCase(s2)){
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}
}

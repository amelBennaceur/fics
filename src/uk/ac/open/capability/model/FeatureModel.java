package uk.ac.open.capability.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import solver.Solver;
import solver.exception.ContradictionException;
import uk.ac.open.capability.selection.CPFeatureSolver;
import uk.ac.open.capability.selection.CPTVLParser;
import be.ac.info.fundp.TVLParser.Util.FDElement;
import be.ac.info.fundp.TVLParser.Util.FeatureTree;
import be.ac.info.fundp.TVLParser.symbolTables.ConstraintSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.FeatureSymbol;

/**
 * FeatureModel exposes the operations needed to configure a feature model.
 * 
 * - isSelectable / isUnSelectable - isSelected / isUnselected / isUnassigned -
 * include / exclude / unassign - explain
 * 
 * - getUserChoices - getModel
 * 
 * @author rm
 * 
 */
public class FeatureModel {

	protected CPFeatureSolver solver = null;
	protected FeatureTree tree = new FeatureTree();

	private final List<Integer> fixedIds;
	private final List<Integer> userChoices;

	/**
	 * Builds a new feature model
	 * 
	 * @param input
	 *            Can either be the fullText TVL diagram or the path to a file
	 *            containing the TVL code
	 * @param fullText
	 *            is set to true when input contains the tvl code, false when it
	 *            contains the path to a file
	 * @throws ContradictionException
	 * @throws UnsatisfiableModelException
	 * @throws IOException
	 */
	public FeatureModel(String input, boolean fullText, Solver chocoSolver,boolean reinitialiseVariables) throws IOException {
		CPTVLParser parser = null;
		if (fullText) {
			parser = new CPTVLParser(input, chocoSolver);
		} else {
			parser = new CPTVLParser(new File(input), chocoSolver);
		}
		this.tree.setRoot(parser.getRoot());
		solver = new CPFeatureSolver(tree.getRoot(), chocoSolver,reinitialiseVariables);
		fixedIds = new ArrayList<Integer>();
		userChoices = new ArrayList<Integer>();
	}

	// TODO: Change this to avoid exposure of Parser and Solver objects
	public FeatureModel(FeatureSymbol root, Solver chocoSolver,boolean reinitialiseVariables) {
		this.tree.setRoot(root);
		solver = new CPFeatureSolver(root, chocoSolver,reinitialiseVariables);
		fixedIds = new ArrayList<Integer>();
		userChoices = new ArrayList<Integer>();
	}

	public FeatureModel(String fmFilePath, Solver chocoSolver,boolean reinitialiseVariables) throws IOException {
		this(fmFilePath, false, chocoSolver,reinitialiseVariables);

	}

	public FeatureTree getTree() {
		return tree;
	}

	public void setTree(FeatureTree tree) {
		this.tree = tree;
	}

	// TODO: Avoid exposure of solver exceptions
	public boolean hasAtLeastOneSolution() {
		return isSatisfiable();
	}

	private boolean isSatisfiable() {
		return solver.isSatisfiable();
	}

	private List<List<FDElement>> explain(int id) throws Exception {
		List<List<Integer>> explanation = solver.explain(id, this.fixedIds);

		List<List<FDElement>> solutions = new ArrayList<List<FDElement>>();
		for (List<Integer> constr : explanation) {
			List<FDElement> constrExpl = new ArrayList<FDElement>();
			for (Integer dimacsId : constr) {
				constrExpl.add(makeFDElement(tree.findFeatureByDimacsId(Math.abs(dimacsId))));
			}
			solutions.add(constrExpl);
		}
		return solutions;
	}

	/*
	 * ********************************************************************************************
	 * isXXX ?
	 * 
	 * **************************************************************************
	 * ****************
	 */

	/**
	 * Returns true if the feature corresponding to the given name can be
	 * selected or is already selected, false otherwise
	 * 
	 * @param featureName
	 *            The name of the feature
	 * @param longName
	 *            indicates whether the name is fully qualified or not
	 * @throws Exception
	 */
	public boolean isSelectable(String featureName, boolean longName) throws Exception {
		return isSelectable(findFeature(featureName, longName));
	}

	/**
	 * Returns true if the given feature can be selected or is already selected,
	 * false otherwise
	 * 
	 * @param feature
	 * @return
	 * @throws TimeoutException
	 * @throws ContradictionException
	 */
	public boolean isSelectable(FeatureSymbol feature) {
		if (feature != null) {
			final int id = feature.getDIMACS_ID();
			if (fixedIds.contains(Integer.valueOf(id))) {
				return true;
			} else {
				return isSatisfiable();
			}
		}
		return false;
	}

	public boolean isUnSelectable(String featureName, boolean longName) throws Exception {
		return isUnSelectable(findFeature(featureName, longName));
	}

	public boolean isUnSelectable(FeatureSymbol feature) {
		if (feature != null) {
			final int id = feature.getDIMACS_ID();
			// Value is already excluded
			if (fixedIds.contains(Integer.valueOf(-id)))
				return true;

			// Value was included as the result of a propagation
			if (fixedIds.contains(Integer.valueOf(id)) && !userChoices.contains(Integer.valueOf(id)))
				return false;

			// return isSatisfiable(-feature.getDIMACS_ID());
			return isSatisfiable();
		}
		return false;
	}

	public boolean isIncluded(String featureName, boolean longName) throws Exception {
		return isIncluded(longName ? tree.findFeatureByLongId(featureName) : tree.findFeatureByShortId(featureName));
	}

	public boolean isIncluded(FeatureSymbol feature) {
		return fixedIds.contains(feature.getDIMACS_ID());
	}

	public boolean isExcluded(String featureName, boolean longName) throws Exception {
		return isExcluded(longName ? tree.findFeatureByLongId(featureName) : tree.findFeatureByShortId(featureName));
	}

	public boolean isExcluded(FeatureSymbol feature) {
		return fixedIds.contains(-feature.getDIMACS_ID());
	}

	public boolean isUnassigned(String featureName, boolean longName) throws Exception {
		return isUnassigned(longName ? tree.findFeatureByLongId(featureName) : tree.findFeatureByShortId(featureName));
	}

	public boolean isUnassigned(FeatureSymbol feature) {
		return (!isIncluded(feature) && !isExcluded(feature));
	}

	/*
	 * ********************************************************************************************
	 * Actions (include, exclude, unassign, explain)
	 * 
	 * **************************************************************************
	 * ****************
	 */

	/**
	 * Includes a feature in the configuration
	 * 
	 * @param featureName
	 *            the name of the feature
	 * @param longName
	 *            is set to true when the full dotted notation is used to
	 *            specify the name of the feature
	 * @return the list of features of which the state has changed
	 * @throws Exception
	 */
	public List<FDElement> include(String featureName, boolean longName) throws Exception {
		final FeatureSymbol fs = findFeature(featureName, longName);
		if (isSelectable(fs)) {
			resetFixedIds();
			this.userChoices.add(fs.getDIMACS_ID());

		} else
			System.out.println("This feature cannot be included");
		return makeAllFDElements(include(fs));
	}

	private List<FeatureSymbol> include(FeatureSymbol feature) throws Exception {
		return setState(feature, true);
	}

	/**
	 * Excludes a feature from the configuration
	 * 
	 * @param featureName
	 *            the name of the feature
	 * @param longName
	 *            is set to true when the full dotted notation is used to
	 *            specify the name of the feature
	 * @return the list of features of which the state has changed
	 * @throws Exception
	 */
	public List<FDElement> exclude(String featureName, boolean longName) throws Exception {
		final FeatureSymbol fs = findFeature(featureName, longName);
		if (isUnSelectable(fs)) {
			resetFixedIds();
			this.userChoices.add(-fs.getDIMACS_ID());
		} else
			System.out.println("This feature cannot be excluded");
		return makeAllFDElements(exclude(fs));
	}

	private List<FeatureSymbol> exclude(FeatureSymbol feature) throws Exception {
		return setState(feature, false);
	}

	/**
	 * Unassigns a feature
	 * 
	 * @param featureName
	 *            the name of the feature as defined in the diagram
	 * @param longName
	 *            is set to true when the full dotted notation is used to
	 *            specify the name of the feature
	 * @return the list of features of which the state has changed
	 * @throws Exception
	 */
	public List<FDElement> unassign(String featureName, boolean longName) throws Exception {
		final FeatureSymbol fs = findFeature(featureName, longName);
		return makeAllFDElements(unassign(fs));
	}

	private List<FeatureSymbol> unassign(FeatureSymbol feature) throws Exception {
		int id = feature.getDIMACS_ID();

		if (!isUnassigned(feature)) {
			return unassign(isIncluded(feature) ? id : -id);
		} else {
			System.out.println("This feature is not assigned and cannot be unassigned");
			return null;
		}

	}

	private List<FeatureSymbol> unassign(int id) throws Exception {
		final Integer theId = Integer.valueOf(id);
		if (userChoices.contains(theId)) {
			this.userChoices.remove(theId);
			final ArrayList<Integer> before = new ArrayList<Integer>();
			before.addAll(fixedIds);
			resetFixedIds();
			final List<FeatureSymbol> modified = fullPropagate(tree.getRoot(), new ArrayList<FeatureSymbol>());
			FeatureSymbol tmp;
			List<FeatureSymbol> modTmp;
			for (Integer i : before) {
				modTmp = null;
				if ((i > 0) && (!i.equals(Integer.valueOf(theId))) && !fixedIds.contains(i)) {
					tmp = tree.findFeatureByDimacsId(i);

					if (isSelectable(tmp)) {
						userChoices.add(i);
						modTmp = include(tmp);

						for (FeatureSymbol fs : modTmp) {
							if (!modified.contains(fs)) {
								modified.add(fs);
							}
						}
					}
				}
			}
			return mergeUnassigned(before, modified);
		} else {
			System.out.println("This feature has been automatically set and cannot be unassigned");
			return null;
		}

	}

	private List<FeatureSymbol> setState(FeatureSymbol feature, boolean state) throws Exception {
		if (feature != null) {
			int id = feature.getDIMACS_ID();
			id = (state ? id : -id);
			final boolean operationAllowed = (state ? isSelectable(feature) : isUnSelectable(feature));

			if (isUnassigned(feature) && operationAllowed) {
				// Assign new state
				fixedIds.add(id);
				return fullPropagate(tree.getRoot(), new ArrayList<FeatureSymbol>());
			}
		}
		return null;
	}

	private void resetFixedIds() {
		fixedIds.clear();
		fixedIds.addAll(userChoices);
	}

	/**
	 * This method propagates a choice, i.e. an inclusion / exclusion. It
	 * automatically includes features that could never be excluded given the
	 * current state of the configuration and excludes the ones that could never
	 * be included. For each of this changes, a propagation is made again.
	 * 
	 * This ensures the selection process is "backtrack-free". In other words
	 * the user will never face a "dead end".
	 * 
	 * @param feature
	 * @param modified
	 * @return
	 * @throws Exception
	 */
	private List<FeatureSymbol> fullPropagate(FeatureSymbol feature, List<FeatureSymbol> modified) throws Exception {

		final boolean selectable = isSelectable(feature);
		final boolean unSelectable = isUnSelectable(feature);
		List<FeatureSymbol> sub = null;

		if (selectable && !unSelectable && !isIncluded(feature)) {
			// feature will not be unselectable, including it automatically
			sub = include(feature);
			modified.add(feature);
			if (sub != null)
				modified.addAll(sub);
		}

		if (unSelectable && !selectable && !isExcluded(feature)) {
			// feature will not be selectable, excluding it automatically
			sub = exclude(feature);
			modified.add(feature);
			if (sub != null)
				modified.addAll(sub);
		}

		final Map<String, FeatureSymbol> children = feature.getChildrenFeatures();
		if (children != null) {
			for (FeatureSymbol child : children.values()) {
				fullPropagate(child, modified);
			}
		}

		return modified;
	}

	/**
	 * Returns a list of elements that are involved in constraints preventing to
	 * change the state of the given feature
	 * 
	 * @param featureName
	 * @param longName
	 * @param dontRetry
	 *            : a list of element that should not be explained anymore (used
	 *            for recursion, start with an empty List)
	 * @return
	 * @throws Exception
	 */
	public List<FDElement> explain(String featureName, boolean longName, List<FDElement> dontRetry) throws Exception {

		final FeatureSymbol fs = findFeature(featureName, longName);
		List<List<FDElement>> explanation = null;
		int goal = 0;
		if (isIncluded(fs))
			goal = -fs.getDIMACS_ID();

		if (isExcluded(fs))
			goal = fs.getDIMACS_ID();
		List<FDElement> result = null;
		if (goal != 0) {
			result = new ArrayList<FDElement>();
			explanation = explain(goal);

			explain(explanation, dontRetry);

		}
		return result;
	}

	private List<FDElement> explain(List<List<FDElement>> explanation, List<FDElement> dontRetry) throws Exception {
		List<FDElement> uChoices = this.getUserChoices();
		List<FDElement> result = new ArrayList<FDElement>();
		List<FDElement> toExplainLater = new ArrayList<FDElement>();
		if (explanation != null) {
			for (List<FDElement> constr : explanation) {

				for (FDElement elem : constr) {
					if (dontRetry.indexOf(elem) < 0) {
						if (uChoices.indexOf(elem) >= 0) {
							if (result.indexOf(elem) < 0) {
								result.add(elem);
								dontRetry.add(elem);
							}
						} else {
							if (elem.isSelected() || elem.isUnselected()) {
								dontRetry.add(elem);
								toExplainLater.add(elem);
							}
						}
					}
				}

			}
			if (result.size() == 0) {
				for (FDElement elem : toExplainLater) {
					for (FDElement sol : explain(elem.getName(), false, dontRetry)) {
						if (result.indexOf(sol) < 0)
							result.add(sol);
					}
				}
			}
			return result;

		} else {
			return null;
		}
	}

	private List<FDElement> makeAllFDElements(List<FeatureSymbol> list) {
		final List<FDElement> result = new ArrayList<FDElement>();
		for (FeatureSymbol feature : list) {
			result.add(makeFDElement(feature));
		}
		return result;
	}

	private List<FeatureSymbol> mergeUnassigned(List<Integer> before, List<FeatureSymbol> after) throws Exception {
		FeatureSymbol elem;
		final List<FeatureSymbol> result = new ArrayList<FeatureSymbol>();
		result.addAll(after);
		for (Integer i : before) {
			elem = tree.findFeatureByDimacsId((i > 0) ? i : -i);

			boolean found = false;
			for (FeatureSymbol changed : after) {
				if (changed.getID().equals(elem.getID())) {
					found = true;
					break;
				}
			}

			if (!found) {
				result.add(elem);
			}
		}
		return result;
	}

	private FDElement makeFDElement(FeatureSymbol symbol) {
		return new FDElement(symbol.getID(), isIncluded(symbol), isExcluded(symbol));
	}

	private FeatureSymbol findFeature(String featureName, boolean longName) throws Exception {
		return (longName ? tree.findFeatureByLongId(featureName) : tree.findFeatureByShortId(featureName));
	}

	public String flattenFeature(FeatureSymbol feature, int tabs) {
		String s = "";
		for (int i = 0; i < tabs; i++)
			s += "\t";
		s += feature.getID() + "(";
		if (!isUnassigned(feature))
			s += isIncluded(feature);
		else
			s += "?";
		s += ")";
		s += "[" + feature.getMinCardinality() + ".." + feature.getMaxCardinality() + "]";
		s += "\n";
		Map<String, FeatureSymbol> children = feature.getChildrenFeatures();
		if (children != null) {
			for (FeatureSymbol child : children.values()) {
				s += flattenFeature(child, tabs + 1);
			}
		}
		if (feature.hasConstraintSymbols()) {
			for (ConstraintSymbol cs : feature.getConstraintSymbols()) {
				s += "\n**" + cs.getExpression().toString();
			}
		}
		return s;
	}

	public List<FDElement> getUserChoices() throws Exception {
		final List<FDElement> result = new ArrayList<FDElement>();
		for (Integer did : this.userChoices) {
			FeatureSymbol fs = tree.findFeatureByDimacsId(did);
			result.add(makeFDElement(fs));
		}
		return result;
	}

	public List<FDElement> getModel() {
		return getModel(tree.getRoot());
	}

	public void setModel(List<FDElement> model) throws Exception {
		for (FDElement feature : model) {
			if (feature.isSelected())
				include(feature.getName(), false);

			if (feature.isUnselected())
				exclude(feature.getName(), false);
		}
	}

	private List<FDElement> getModel(FeatureSymbol feature) {

		final List<FDElement> result = new ArrayList<FDElement>();

		result.add(makeFDElement(feature));
		final Map<String, FeatureSymbol> children = feature.getChildrenFeatures();
		if (children != null) {
			for (FeatureSymbol child : feature.getChildrenFeatures().values()) {
				result.addAll(getModel(child));
			}
		}

		return result;
	}

	public String getRootName() {
		return tree.getRoot().getID();
	}

	@Override
	public String toString() {
		return flattenFeature(tree.getRoot(), 0);
	}

}

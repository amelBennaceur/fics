package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * This class allows to create "FeatureSymbol" objects. Each feature symbol is
 * used to represent a feature of the FM into the features symbol table. So,
 * each feature symbol must correspond to a feature of the FM.
 * 
 */
public class FeatureSymbol extends Symbol {

	private final static Logger LOGGER = Logger.getLogger(FeatureSymbol.class.getName());
	
	
	boolean intoSyntaxTree = false;

	/**
	 * If the feature has children features, these parameters will contain the
	 * maximum and the minimum cardinality of the group.
	 */
	private int minCardinality, maxCardinality;

	/**
	 * Shows if the feature is the root, shared or optional
	 */
	private boolean shared, optional, root;

	/**
	 * This parameter is used to detect cycles in the FM. If this feature has
	 * been visited by the algorithm (method "detectCycle()" in the class
	 * "FeaturesSymbolTable"), the parameter is set to true.
	 */
	private boolean visited = false;

	/**
	 * This parameter is also used to detect cycles in the graph.
	 */
	private boolean childrenFeaturesVisited = false;

	/**
	 * The feature's ID.
	 */
	private String id;

	/**
	 * A dictionary between the ID and the symbols of all the parents features.
	 */
	private Map<String, FeatureSymbol> parentsFeatures;

	/**
	 * A dictionary between the ID and the symbols of all the children features.
	 */
	private Map<String, FeatureSymbol> childrenFeatures;

	/**
	 * A dictionary between the ID and the symbols of all the attributes.
	 */
	private Map<String, AttributeSymbol> attributes;

	/**
	 * A vector which contains all the constraints declared in the different
	 * features bodies of the feature ( represented by the feature symbol ).
	 */
	private Vector<ConstraintSymbol> constraintSymbols;

	// Constructors :
	// --------------

	/**
	 * The single class constructor.
	 */
	public FeatureSymbol(String featureID, boolean isRoot, boolean isOptional) {
		this.optional = isOptional;
		this.shared = false;
		this.id = featureID;
		this.childrenFeatures = null;
		this.attributes = null;
		this.root = isRoot;
		this.constraintSymbols = null;
	}

	// Parents methods :
	// -----------------

	/**
	 * Return all the parents features of the feature symbol
	 */
	public Map<String, FeatureSymbol> getParentsFeature() {
		return this.parentsFeatures;
	}

	/**
	 * Return the first parent feature (into the order of "parentsFeature"). If
	 * the feature symbol is the root, it returns null.
	 * 
	 * @return The first parent feature (into the order of "parentsFeature").
	 */
	public FeatureSymbol getFirstParentFeature() {
		if (this.isRoot()) {
			return null;
		} else {
			Object[] parents = this.parentsFeatures.keySet().toArray();
			return this.parentsFeatures.get(parents[0].toString());
		}
	}

	/**
	 * Add a feature to the list of the parents features of the feature symbol.
	 * 
	 * @param parentFeatureSymbole
	 *            The feature which will become a parent feature of the feature
	 *            symbol.
	 */
	public void addParentFeatureSymbol(FeatureSymbol parentFeatureSymbol) {
		if (this.parentsFeatures == null)
			this.parentsFeatures = new HashMap<String, FeatureSymbol>();
		this.parentsFeatures.put(parentFeatureSymbol.getID(), parentFeatureSymbol);
	}

	/**
	 * Return the parent feature which has "parentID" as ID. If the feature
	 * symbol has no parent feature named "parentID", the method returns "null".
	 * 
	 * @param parentID
	 * 
	 * @return The parent feature which has "parentID" has ID.
	 */
	public FeatureSymbol getParentFeatureSymbol(String parentID) {
		if (this.parentsFeatures == null)
			return null;
		else
			return this.parentsFeatures.get(parentID);
	}

	// Children features methods :
	// ---------------------------

	/**
	 * Allows to add a feature to the children features of the feature symbol.
	 * Make sure that the feature symbol doesn't have a child feature with the
	 * same ID than "featureSymbol" or it will be erased by "featureSymbol".
	 * 
	 * @param featureSymbol
	 *            The feature which will become a new child feature of the
	 *            feature symbol.
	 * 
	 */
	public void addChildrenFeature(FeatureSymbol featureSymbol) {
		if (this.childrenFeatures == null) {
			this.childrenFeatures = new HashMap<String, FeatureSymbol>();
			this.childrenFeatures.put(featureSymbol.getID(), featureSymbol);
		} else {
			this.childrenFeatures.put(featureSymbol.getID(), featureSymbol);
		}
	}

	/**
	 * Return the child feature which has "featureID" as ID. If the feature
	 * symbol has no child feature named "featureID", the method returns "null".
	 * 
	 * @param featureID
	 *            The ID of a child feature of the feature symbol.
	 * 
	 * @return The child feature which has "featureID" as ID.
	 */
	public FeatureSymbol getChildrenFeature(String featureID) {
		if (this.childrenFeatures == null) {
			return null;
		} else
			return this.childrenFeatures.get(featureID);
	}

	/**
	 * Set the children features to "childrenFeatures".
	 * 
	 * @param childrenFeatures
	 *            The new children features. (A dictionary between the ID and
	 *            the symbols of all the children features.)
	 */
	public void setChildrenFeatures(Map<String, FeatureSymbol> childrenFeatures) {
		this.childrenFeatures = childrenFeatures;
	}

	/**
	 * Return all the children features of the feature symbol
	 * 
	 * @return All the children features of the feature symbol
	 */
	public Map<String, FeatureSymbol> getChildrenFeatures() {
		return this.childrenFeatures;
	}

	/**
	 * Shows if the feature symbol has a child feature named "childFeatureID".
	 * 
	 * @param chilFeatureID
	 *            The ID of a feature of which could be a child feature of the
	 *            feature symbol.
	 * 
	 * @return true if the feature symbol has a child feature named
	 *         "childFeatureID"
	 */
	public boolean hasChildrenFeature(String chilFeatureID) {
		if (this.childrenFeatures == null) {
			return false;
		} else {
			return this.childrenFeatures.containsKey(chilFeatureID);
		}
	}

	/**
	 * Shows if the feature symbol has at least one child feature.
	 * 
	 * @return true if the feature symbol has at least one child feature.
	 */
	public boolean hasChildrenFeatures() {
		if (this.childrenFeatures != null)
			return true;
		else
			return false;
	}

	/**
	 * Shows if the feature symbol has a child feature named
	 * "childrenFeatureID".
	 * 
	 * @param childrenFeatureID
	 *            The ID of a feature which could be a child feature of the
	 *            feature symbol.
	 * 
	 * @return true if the feature symbol has a child feature named
	 *         "childrenFeatureID".
	 */
	public boolean containsChildrenFeature(String childrenFeatureID) {
		if (this.childrenFeatures == null)
			return false;
		else
			return this.childrenFeatures.containsKey(childrenFeatureID);
	}

	/**
	 * Return true if the all the children features of the feature symbol has
	 * been visited by the method "detectCycle()" of the class
	 * featuresSymbolTable.
	 */
	public boolean hasChildrenFeaturesVisited() {
		return childrenFeaturesVisited;
	}

	/**
	 * Set the visited state of the children features of the feature symbol to
	 * true.
	 */
	public void setChildrenFeaturesVisited() {
		this.childrenFeaturesVisited = true;
	}

	// Attributes methods :
	// --------------------

	/**
	 * Add an attribute to the attributes of the feature symbol. Make sure that
	 * the feature symbol doesn't have an attribute with the same ID than
	 * "attributeSymbol" or it will be erased by "attributeSymbol".
	 * 
	 * @param attributeSymbol
	 *            The attribute which will be added to the attributes of the
	 *            feature symbol.
	 */
	public void addAttribute(AttributeSymbol attributeSymbol) {
		if (this.attributes == null)
			this.attributes = new HashMap<String, AttributeSymbol>();
		this.attributes.put(attributeSymbol.getID(), attributeSymbol);
	}

	/**
	 * Show if the feature symbol possess an attribute named
	 * "attributeSymbolID".
	 * 
	 * @param attributeSymbolID
	 *            The ID of an attribute that the feature symbol could have.
	 * 
	 * @return true if the feature symbol possess an attribute named
	 *         "attributeSymbolID".
	 */
	public boolean containsAttributeSymbol(String attributeSymbolID) {
		if (this.attributes == null)
			return false;
		else
			return this.attributes.containsKey(attributeSymbolID);
	}

	/**
	 * Return the attribute symbol named "attributeSymbolID". If the feature
	 * symbol doesn't have this attribute, return null.
	 * 
	 * @param attributeSymbolID
	 *            The ID of the attribute symbol which will be returned.
	 * 
	 * @return The attribute symbol corresponding to "attributeSymbolID".
	 */
	public AttributeSymbol getAttributeSymbol(String attributeSymbolID) {
		return this.attributes.get(attributeSymbolID);
	}

	/**
	 * Return all the attributes that the feature symbol have.
	 * 
	 * @return All the attributes that the feature symbol have.
	 */
	public Map<String, AttributeSymbol> getAttributesSymbols() {
		return this.attributes;
	}

	/**
	 * Shows if the feature symbol has at least one attribute.
	 * 
	 * @return true if the feature symbol has at least one attribute.
	 */
	public boolean hasAttributesSymbols() {
		if (this.attributes == null)
			return false;
		else
			return true;
	}

	// Constraints Methods :
	// ---------------------

	/**
	 * Add a constraint to the constraints of the feature symbol.
	 * 
	 * @param constraintSymbol
	 *            The constraint which will be added to the constraints of the
	 *            feature symbol.
	 * 
	 */
	public void addConstraintSymbol(ConstraintSymbol constraintSymbol) {
		if (this.constraintSymbols == null)
			this.constraintSymbols = new Vector<ConstraintSymbol>();
		this.constraintSymbols.add(constraintSymbol);
	}

	/**
	 * Return all the constraints of the feature symbol.
	 * 
	 * @return All the constraints of the feature symbol.
	 */
	public Vector<ConstraintSymbol> getConstraintSymbols() {
		return this.constraintSymbols;
	}

	/**
	 * Shows if the feature symbol has a least one constraint symbol.
	 * 
	 * @return true if the feature symbol has a least one constraint symbol.
	 */
	public boolean hasConstraintSymbols() {
		if (this.constraintSymbols == null)
			return false;
		else
			return true;
	}

	// Others methods :
	// ----------------

	/**
	 * Return true if the featureSymbol has been visited by the method
	 * "detectCycle()" of the class featuresSymbolTable.
	 */
	public boolean hasBeenVisited() {
		return visited;
	}

	/**
	 * Set the visited state of the feature symbol to true.
	 */
	public void setVisited() {
		this.visited = true;
	}

	/**
	 * Shows if the feature symbol is the root feature.
	 * 
	 * @return true if the feature symbol is the root feature.
	 */
	public boolean isRoot() {
		return this.root;
	}

	/**
	 * Set the shared state of the feature symbol to true.
	 */
	public void setShared() {
		this.shared = true;
	}

	/**
	 * Shows if the feature symbol is shared.
	 * 
	 * @return true if the feature symbol is shared.
	 */
	public boolean isShared() {
		return this.shared;
	}

	/**
	 * Shows if the feature symbol is optional.
	 * 
	 * @return true if the feature symbol is optional.
	 */
	public boolean isOptionnal() {
		return this.optional;
	}

	/**
	 * Return the ID of the feature symbol.
	 */
	@Override
	public String getID() {
		return this.id;
	}

	/**
	 * Display information about the feature symbol.
	 * 
	 * @param espace
	 *            Used for make a lag between the displayed information and
	 *            between the edge of the window.
	 */
	public void printFeature(String espace) {
		if (this.isOptionnal()) {
			if (this.isShared())
				LOGGER.info(espace + "  optional " + id + " (shared) { ");
			else
				LOGGER.info(espace + "  optional " + id + " { ");
		} else {
			if (this.isShared())
				LOGGER.info(espace + "  " + id + " (shared) { ");
			else
				LOGGER.info(espace + "  " + id + " { ");
		}

		int i;
		if (this.attributes == null) {
			LOGGER.info(espace + "  |  Attributes : No attributes");
		} else {
			LOGGER.info(espace + "  |  Attributes {");
			Object[] attributesArray = this.attributes.keySet().toArray();
			i = 0;
			while (i < attributesArray.length) {
				this.attributes.get(attributesArray[i]).printAttribute(espace);
				i++;
			}
			LOGGER.info(espace + "  |  }");
		}
		if (this.constraintSymbols == null) {
			LOGGER.info(espace + "  |  Constraints : No constraints");
		} else {
			LOGGER.info(espace + "  |  Constraints {");
			i = this.constraintSymbols.size() - 1;
			while (i >= 0) {
				LOGGER.info(espace + "  |      " + this.constraintSymbols.get(i).toString());
				i--;
			}
			LOGGER.info(espace + "  |  }");
		}
		if (this.childrenFeatures == null) {
			LOGGER.info(espace + "  |  Children Features : No features");
		} else {
			LOGGER.info(espace + "  |  Children Features {");
			Object[] childrenFeaturesArray = this.childrenFeatures.keySet().toArray();
			i = 0;
			while (i < childrenFeaturesArray.length) {
				this.childrenFeatures.get(childrenFeaturesArray[i]).printFeature(espace.concat("    "));
				i++;
			}
			LOGGER.info(espace + "  |  }");
		}
		LOGGER.info(espace + "  }");

	}

	@Override
	public String toString() {
		String st = "";
		if (this.isOptionnal()) {
			if (this.isShared())
				st += "  optional " + id + " (shared) { \n";
			else
				st += "  optional " + id + " { \n";
		} else {
			if (this.isShared())
				st += "  " + id + " (shared) { \n";
			else
				st += "  " + id + " { \n";
		}

		int i;
		if (this.attributes == null) {
			st += "  |  Attributes : No attributes \n";
		} else {
			st += "  |  Attributes {";
			Object[] attributesArray = this.attributes.keySet().toArray();
			i = 0;
			while (i < attributesArray.length) {
				st += this.attributes.get(attributesArray[i]).toString();
				i++;
			}
			st += "}\n";
		}
		if (this.constraintSymbols == null) {
			st += "  |  Constraints : No constraints \n";
		} else {
			st += "  |  Constraints { \n";
			i = this.constraintSymbols.size() - 1;
			while (i >= 0) {
				st += "    |      " + this.constraintSymbols.get(i).toString() + " \n";
				i--;
			}
			st += "  } \n";
		}
		if (this.childrenFeatures == null) {
			st += "  |  Children Features : No features \n";
		} else {
			st += "  |  Children Features { \n";
			Object[] childrenFeaturesArray = this.childrenFeatures.keySet().toArray();
			i = 0;
			while (i < childrenFeaturesArray.length) {
				// st +=
				// this.childrenFeatures.get(childrenFeaturesArray[i]).toString();
				st += "         " + childrenFeaturesArray[i] + "\n";
				i++;
			}
			st += "     } \n";
		}
		st += "  } \n";
		return st;
	}

	/**
	 * Return the minimum cardinality of the children features. If the feature
	 * symbol has no children features, it will return null.
	 * 
	 * @return The minimum cardinality of the children features.
	 */
	public int getMinCardinality() {
		return minCardinality;
	}

	/**
	 * Set the minimum cardinality to "minCardinality"
	 * 
	 * @param minCardinality
	 *            The new minimum cardinality
	 */
	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}

	/**
	 * Return the maximum cardinality of the children features. If the feature
	 * symbol has no children features, it will return null.
	 * 
	 * @return The maximum cardinality of the children features.
	 */
	public int getMaxCardinality() {
		return maxCardinality;
	}

	/**
	 * Set the maximum cardinality to "maxCardinality"
	 * 
	 * @param maxCardinality
	 *            The new maximum cardinality
	 */
	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	/**
	 * This method is not yet implemented and returns false by default.
	 * 
	 * @return false.
	 */
	public boolean hasData() {
		// TODO
		return false;
	}

	public boolean isIntoSyntaxTree() {
		return this.intoSyntaxTree;
	}

	public void setIntoSyntaxTree() {
		this.intoSyntaxTree = true;
	}
}

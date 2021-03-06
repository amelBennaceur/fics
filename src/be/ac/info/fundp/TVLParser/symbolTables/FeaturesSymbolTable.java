package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import be.ac.info.fundp.TVLParser.Parser.FeaturesStack;
import be.ac.info.fundp.TVLParser.SyntaxTree.Attribute;
import be.ac.info.fundp.TVLParser.SyntaxTree.AttributeConditionnal;
import be.ac.info.fundp.TVLParser.SyntaxTree.Constraint;
import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.SyntaxTree.Feature;
import be.ac.info.fundp.TVLParser.SyntaxTree.SubAttribute;
import be.ac.info.fundp.TVLParser.Util.Util;

/**
 * The class allows to create the feature symbol table of a syntax tree. The
 * construction of the table is divided in 3 main phases :
 * 
 * First phase : ------------- During the first phase, all the features and
 * their attributes (only the attribute ID and it's values domain) are
 * transformed into symbols and saved in the table.
 * 
 * Second phase : -------------- During the second phase, the algorithm looks
 * for shared features. Each time that it finds one, it creates the link between
 * the shared feature and its new parent. This phase is necessary because a
 * feature may be declared shared everywhere in the FD. For example, before the
 * declaration of its feature body :
 * 
 * ... Kitchen { group oneof { shared Door } LivingRoom { group oneof { Door {
 * enum color in { White, Black }; } } } ...
 * 
 * 
 * Third phase : ------------- During the last phase, all the attributes bodies
 * ("ifin:", "ifout:" and "is") and the constraints are checked. The third phase
 * is necessary because the constraints and the attributes bodies may contain
 * references to shared feature. For example, the constraints :
 * 
 * ... Kitchen.Door == White; ...
 * 
 * The table is represented by a dictionary between the features ID and their
 * features symbols. If a feature ID is ambiguous (a least two have the same
 * ID), it's symbols in the dictionary is set to null. For example, the FM :
 * 
 * root Vehicle { group oneof { Car { group oneof { Ford, Ferrari } }, Truck {
 * group oneof { Ford, Peugeot } } } }
 * 
 * has this features symbol table :
 * 
 * ID Feature Symbol
 * -----------------------------------------------------------------------------
 * Car --> Car { | Attributes : No attributes | Constraints : No constraints |
 * Children Features { Ford { | Attributes : No attributes | Constraints : No
 * constraints | Children Features : No features } Ferrari { | Attributes : No
 * attributes | Constraints : No constraints | Children Features : No features }
 * | } }
 * -----------------------------------------------------------------------------
 * Truck --> Truck { | Attributes : No attributes | Constraints : No constraints
 * | Children Features { Ford { | Attributes : No attributes | Constraints : No
 * constraints | Children Features : No features } Peugeot { | Attributes : No
 * attributes | Constraints : No constraints | Children Features : No features }
 * | } }
 * -----------------------------------------------------------------------------
 * Ford --> null
 * -----------------------------------------------------------------------------
 * Ferrari --> Ferrari { | Attributes : No attributes | Constraints : No
 * constraints | Children Features : No features }
 * -----------------------------------------------------------------------------
 * Peugeot --> Peugeot { | Attributes : No attributes | Constraints : No
 * constraints | Children Features : No features }
 * -----------------------------------------------------------------------------
 * Vehicle --> Vehicle { | Attributes : No attributes | Constraints : No
 * constraints | Children Features { Car { | Attributes : No attributes |
 * Constraints : No constraints | Children Features { Ford { | Attributes : No
 * attributes | Constraints : No constraints | Children Features : No features }
 * Ferrari { | Attributes : No attributes | Constraints : No constraints |
 * Children Features : No features } | } } Truck { | Attributes : No attributes
 * | Constraints : No constraints | Children Features { Ford { | Attributes : No
 * attributes | Constraints : No constraints | Children Features : No features }
 * Peugeot { | Attributes : No attributes | Constraints : No constraints |
 * Children Features : No features } | } } | } }
 * -----------------------------------------------------------------------------
 * 
 * 
 * 
 */
public class FeaturesSymbolTable {

	private final static Logger LOGGER = Logger.getLogger(FeaturesSymbolTable.class.getName());
	
	// A dictionary which allows to easily retrieve the feature symbol
	// corresponding to a feature ID.
	private Map<String, FeatureSymbol> table;

	// At each moment of the construction of the table, the stack allows to know
	// in which feature body you are.
	private FeaturesStack stack;

	// The root feature ID
	private String rootFeatureID;

	// The types symbol table
	private TypesSymbolTable typesSymbolTable;

	// The constants symbol table
	private ConstantsSymbolTable constantsSymbolTable;

	// If the FM described in the input syntax tree is valid, shows if this FM
	// contains at least one int attribute
	private boolean hasInt = false;

	// If the FM described in the input syntax tree is valid, shows if this FM
	// contains at least one real attribute
	private boolean hasReal = false;

	// If the FM described in the input syntax tree is valid, shows if this FM
	// contains at least one bool attribute
	private boolean hasBool = false;

	// If the FM described in the input syntax tree is valid, shows if this FM
	// contains at least one enum attribute
	private boolean hasEnum = false;

	// If the FM described in the input syntax tree is valid, shows if this FM
	// contains at least one struct attribute
	private boolean hasStruct = false;

	// If the FM described in the input syntax tree is valid, shows if this FM
	// contains at least one attribute
	private boolean hasAttributes = false;

	// If the FM described in the input syntax tree is valid, shows the number
	// of features that this FM contains.
	private int nbFeatures = 0;

	private int nbIntAttributes = 0;

	private int nbRealAttributes = 0;

	private int nbBoolAttributes = 0;

	private int nbEnumAttributes = 0;

	private int nbStructAttributes = 0;

	private int nbEnumValues = 0;;

	// Shows if the table is empty or not. The table is declared empty if the
	// first phase of construction of
	// the table hasn't been launched.
	private boolean empty = true;

	// Constructor :
	// -------------

	/**
	 * The first and single constructor of the class. Nothing is initialized, it
	 * creates an empty object "FeaturesSymbolTable". This constructor is used
	 * during the parsing where the table is transmitted to the expressions
	 * which will need it. For example, during the typeChecking, it will allow
	 * to a LongIDExpression to retrieve the symbol that it represents.
	 */
	public FeaturesSymbolTable() {
	}

	// Method of construction of the table :
	// -----------------------------------

	/**
	 * This method allows to launch the construction of the table.
	 * 
	 * @param features
	 *            A vector which contains only the features (grammar format) of
	 *            first level of the syntax tree.
	 * 
	 * @param typesSymbolTable
	 *            The types symbol table of the FM described in the syntax tree.
	 * 
	 * @param constantsSymbolTable
	 *            The constants symbol table of the FM described in the syntax
	 *            tree.
	 * 
	 * @exception see
	 *                below
	 */
	public void lauchConstruction(Vector<Feature> features, TypesSymbolTable typesSymbolTable,
			ConstantsSymbolTable constantsSymbolTable) {
		this.table = new HashMap<String, FeatureSymbol>();
		this.stack = new FeaturesStack();
		this.typesSymbolTable = typesSymbolTable;
		this.constantsSymbolTable = constantsSymbolTable;
		this.constructTableFirstPhase(features);
		this.empty = false;
		this.constructTableSecondPhase(features);
		this.constructTableThirdPhase(features);
	}

	// First Phase Methods :
	// ---------------------

	/**
	 * This method realizes the first phase of construction of the table. During
	 * this phase, all the features and their are attributes are converted into
	 * symbols and saved in the table. If an attribute has a values domain (in
	 * its attribute body or in its type), this domain is checked and attached
	 * to the attribute.
	 * 
	 * @param features
	 *            A vector which contains only the features (grammar format) of
	 *            first level of the syntax tree.
	 * 
	 * @exception see
	 *                below
	 */
	private void constructTableFirstPhase(Vector<Feature> features) {
		int i = features.size() - 1;
		FeatureSymbol featureSymbol = null;
		while (i >= 0) {
			Feature feature = features.get(i);
			String[] idArray = feature.getID().split("\\.");
			// Checks about the root feature
			if (i == features.size() - 1) {
				if (!feature.isRoot()) {
					System.out
							.println("Type error : The first feature must be the root feature and it must have a children group. Currently, the first feature is "
									+ feature.getID() + ".");
				}
			}

			if (idArray.length > 1) {
				// If the feature ID is composed of many words (ID.ID.ID)
				if (i == features.size() - 1)
					LOGGER.info("Type error : the root feature ID " + feature.getID()
							+ " is composed of many ID.");
				if (idArray[0].equals("root")) {
					featureSymbol = this.table.get(this.rootFeatureID);
					this.stack.push(rootFeatureID);
				} else {
					if (!(Util.isAFeatureID(idArray[0])))
						LOGGER.info("Type error : the feature ID path " + feature.getID()
								+ " doesn't begin by an upper letter. A feature ID must begin by an upper letter.");
					if (this.table.containsKey(idArray[0])) {
						featureSymbol = this.table.get(idArray[0]);
						if (featureSymbol == null)
							LOGGER.info("Type error : the feature ID path " + feature.getID()
									+ " is invalid. The first feature ID " + idArray[0] + " is ambiguous.");
						this.stack.push(idArray[0]);
					} else {
						LOGGER.info("Type error : the feature corresponding to the path " + feature.getID()
								+ " cannot be found");
					}
				}
				int j = 1;
				while (j <= idArray.length - 1) {
					if (!(featureSymbol.containsChildrenFeature(idArray[j])))
						LOGGER.info("Type error : the path " + feature.getID() + " is invalid. The feature "
								+ featureSymbol.getID() + " doesn't have a children feature named " + idArray[j] + ".");
					featureSymbol = featureSymbol.getChildrenFeature(idArray[j]);
					this.stack.push(idArray[j]);
					j++;
				}
				if (feature.hasAttributes()) {
					this.AttributesProcessingFirstPhase(feature.getAttributes(), featureSymbol);
				}
				if (feature.hasChildrenFeatures()) {
					if (featureSymbol.hasChildrenFeatures()) {
						LOGGER.info("Type error : the feature " + feature.getID()
								+ " has many children features groups declared in different features bodies.");
					} else {
						Map<String, FeatureSymbol> childrenFeatures = this.childrenFeaturesGroupProcessingFirstPhase(
								feature.getChildrenFeatures(), feature.getMinCardinality(),
								feature.getMaxCardinality(), featureSymbol);
						featureSymbol.setChildrenFeatures(childrenFeatures);
					}
				}
				if (feature.hasData()) {
					// TODO Data management
				}
				j = 0;
				while (j <= (idArray.length - 1)) {
					stack.pop();
					j++;
				}
			} else {
				// If the feature ID is composed of a single word.
				if (i == features.size() - 1) {
					Util.checkUseOfReservedWord(feature.getID());
					if (!(Util.isAFeatureID(feature.getID())))
						LOGGER.info("Type error : the root feature ID " + feature.getID()
								+ " doesn't begin by an upper letter. A feature ID must begin by an upper letter.");
					this.rootFeatureID = feature.getID();
					featureSymbol = new FeatureSymbol(feature.getID(), true, false);
					this.table.put(feature.getID(), featureSymbol);
					this.stack.push(feature.getID());
					this.nbFeatures += 1;
				} else {
					if (feature.getID().equals("root")) {
						featureSymbol = this.table.get(rootFeatureID);
						this.stack.push(rootFeatureID);
					} else {
						if (!(Util.isAFeatureID(feature.getID())))
							LOGGER.info("Type error : the feature ID " + feature.getID()
									+ " doesn't begin by an upper letter. A feature ID must begin by an upper letter.");
						if (this.table.containsKey(feature.getID())) {
							featureSymbol = this.table.get(feature.getID());
							if (featureSymbol == null)
								LOGGER.info("Type error : the feature ID " + feature.getID()
										+ " is ambiguous. Try to use to use an other path to access the feature.");
							this.stack.push(featureSymbol.getID());
						} else {
							LOGGER.info("Type error : the feature corresponding to the ID " + feature.getID()
									+ " cannot be found");
						}
					}
				}
				if (feature.hasAttributes()) {
					this.AttributesProcessingFirstPhase(feature.getAttributes(), featureSymbol);
				}
				if (feature.hasChildrenFeatures()) {
					if (featureSymbol.hasChildrenFeatures()) {
						LOGGER.info("Type error : the feature " + feature.getID()
								+ " has many children features groups declared in different features bodies.");
					} else {
						Map<String, FeatureSymbol> childrenFeatures = this.childrenFeaturesGroupProcessingFirstPhase(
								feature.getChildrenFeatures(), feature.getMinCardinality(),
								feature.getMaxCardinality(), featureSymbol);
						featureSymbol.setChildrenFeatures(childrenFeatures);
					}
				}
				if (feature.hasData()) {
					// TODO Data management
				}
				this.stack.pop();
			}
			i--;
		}
		this.checkIDEnumValuesConflict();
	}

	/**
	 * For a feature which has a children features group, this method allows to
	 * : 1) Save all the children features into the table. 2) Check the validity
	 * of the cardinality of the group.
	 * 
	 * @param childrenFeatures
	 *            A vector which contains all the children features (syntax tree
	 *            format) of "parentFeatureSymbol."
	 * 
	 * @param minCardinality
	 *            The minimum cardinality of the group.
	 * 
	 * @param maxCardinality
	 *            The maximum cardinality of the group.
	 * 
	 * @param parentFeatureSymbol
	 *            The parent feature of the features contained in
	 *            "childrenFeatures".
	 * 
	 * @return A dictionary between the children features ID and the children
	 *         features symbols.
	 * 
	 * @throws see
	 *             below
	 */
	public Map<String, FeatureSymbol> childrenFeaturesGroupProcessingFirstPhase(Vector<Feature> childrenFeatures,
			String minCardinality, String maxCardinality, FeatureSymbol parentFeatureSymbol) {
		int i = childrenFeatures.size() - 1;
		Map<String, FeatureSymbol> childrenFeaturesMap = new HashMap<String, FeatureSymbol>();
		FeatureSymbol featureSymbol;
		Feature feature;
		int countMax = 0;
		while (i >= 0) {
			feature = childrenFeatures.get(i);
			if (feature.isShared()) {
				// See the second phase.
				countMax = countMax + 1;
			} else {

				// Checks
				if (feature.getID().contains("."))
					LOGGER.info("Type error : the feature ID " + feature.getID()
							+ " is not valid. You cannot use a long ID inside a children features group.");
				if (feature.getID().equals(rootFeatureID))
					LOGGER.info("Type error : many features have the same ID than the root feature.");
				Util.checkUseOfReservedWord(feature.getID());
				if (!(Util.isAFeatureID(feature.getID())))
					LOGGER.info("Type error : the feature ID " + feature.getID()
							+ " is invalid, it must begin by en upper letter.");
				if (childrenFeaturesMap.containsKey(feature.getID()))
					LOGGER.info("Type error : the feature " + parentFeatureSymbol.getID()
							+ " has many children features with an identical ID.");

				// Save the children feature
				this.stack.push(feature.getID());
				featureSymbol = this.HierarchicalFeaturesProcessingFirstPhase(feature);
				this.nbFeatures += 1;
				countMax = countMax + 1;
				childrenFeaturesMap.put(feature.getID(), featureSymbol);
				featureSymbol.addParentFeatureSymbol(parentFeatureSymbol);
				if (this.table.containsKey(feature.getID()))
					// The children feature ID is amiguous
					this.table.put(feature.getID(), null);
				else
					this.table.put(feature.getID(), featureSymbol);
				this.stack.pop();
			}
			i--;
		}
		// Checks cardinality
		int min = 0, max = 0;
		try {
			min = Integer.parseInt(minCardinality);
		} catch (NumberFormatException e) {
			if (minCardinality.equals("*")) {
				min = childrenFeatures.size();
			} else {
				if (this.containsConstant(minCardinality)) {
					if (this.getConstantType(minCardinality) == Expression.INT) {
						min = Integer.parseInt(this.getConstantValue(minCardinality));
					} else {
						LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
								+ " is invalid. The constant of the inferior bound " + minCardinality
								+ " is not an integer.");
					}
				} else {
					LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
							+ " is invalid. There is no constant named " + minCardinality + ".");
				}
			}

		}
		try {
			max = Integer.parseInt(maxCardinality);
		} catch (NumberFormatException e) {
			if (maxCardinality.equals("*")) {
				max = childrenFeatures.size();
			} else {
				if (this.containsConstant(maxCardinality)) {
					if (this.getConstantType(maxCardinality) == Expression.INT) {
						max = Integer.parseInt(this.getConstantValue(maxCardinality));
					} else {
						LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
								+ " is invalid. The constant of the upper bound " + maxCardinality
								+ " is not an integer.");
					}
				} else {
					LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
							+ " is invalid. There is no constant named " + maxCardinality + ".");
				}
			}

		}
		if (min < 0) {
			LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
					+ " is invalid. The inferior bound ( " + min + " ) is negative.");
		}
		if (max < 0) {
			LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
					+ " is invalid. The upper bound ( " + max + " ) is negative.");
		}
		if (max < min) {
			LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
					+ " is invalid. The upper bound ( " + max + " ) is smaller than the inferior bound ( " + min
					+ " ).");
		}
		if (countMax < min) {
			LOGGER.info("Type error : the cardinality of the feature " + parentFeatureSymbol.getID()
					+ " is invalid. The maximum number of selectable children features " + countMax
					+ " is smaller than the cardinality inferior bound " + min + ".");
		}
		if (countMax < max)
			System.out
					.println("++ WARNING : In the children fatures group of "
							+ parentFeatureSymbol.getID()
							+ ", the cardinality upper bound is bigger than the maximum number of selectable children features ++");
		parentFeatureSymbol.setMinCardinality(min);
		parentFeatureSymbol.setMaxCardinality(max);
		return childrenFeaturesMap;
	}

	/**
	 * This method allows to transform a feature contained in a features group
	 * into a feature symbol. All the attributes and their values domain are
	 * also checked and attached to the feature symbol. Be careful, this method
	 * doesn't save the symbol into the table, it only converts the feature.
	 * 
	 * @param feature
	 *            The feature which will saved in the table.
	 * 
	 * @return The feature symbol corresponding to "feature".
	 * @throws see
	 *             below
	 */
	public FeatureSymbol HierarchicalFeaturesProcessingFirstPhase(Feature feature) throws NumberFormatException {
		FeatureSymbol featureSymbol = new FeatureSymbol(feature.getID(), false, feature.isOptionnal());
		if (feature.hasAttributes()) {
			this.AttributesProcessingFirstPhase(feature.getAttributes(), featureSymbol);
		}
		if (feature.hasConstraints()) {

		}
		if (feature.hasChildrenFeatures()) {
			if (featureSymbol.hasChildrenFeatures()) {
				System.out
						.println("Type error : the feature "
								+ featureSymbol.getID()
								+ " has many children features group specified. A feature may have one and only one children features group specified.");
			} else {
				Map<String, FeatureSymbol> childrenFeatures = this.childrenFeaturesGroupProcessingFirstPhase(
						feature.getChildrenFeatures(), feature.getMinCardinality(), feature.getMaxCardinality(),
						featureSymbol);
				featureSymbol.setChildrenFeatures(childrenFeatures);
			}
		}
		if (feature.hasData()) {
			// TODO data processing
		}
		return featureSymbol;
	}

	/**
	 * This method allows to check the validity of a set attributes and to
	 * transform theses attributes into attributes symbols. Each attribute and
	 * its values domain is checked, converted into an attribute symbol and
	 * attached to the feature symbol which possess it.
	 * 
	 * @param attributes
	 *            A vector which contains all the attributes (syntax tree
	 *            format) of "featureSymbol".
	 * 
	 * @param featureSymbol
	 *            The feature symbol which possess the attributes contained in
	 *            "attributes".
	 * 
	 * @throws see
	 *             below
	 */
	public void AttributesProcessingFirstPhase(Vector<Attribute> attributes, FeatureSymbol featureSymbol)
			throws NumberFormatException {
		int i = 0;
		while (i <= attributes.size() - 1) {
			// Checks
			Util.checkUseOfReservedWord(attributes.get(i).getID());
			if (Util.isAFeatureID(attributes.get(i).getID()))
				LOGGER.info("Type error : the attribute " + attributes.get(i).getID() + " of the feature "
						+ featureSymbol.getID() + " begin by an upper letter, it must begin by a lower letter.");
			if (this.typesSymbolTable.containsTypes(attributes.get(i).getID()))
				LOGGER.info("Type error : a type has already the same ID than the attribute "
						+ attributes.get(i).getID() + " of the feature " + featureSymbol.getID() + ".");
			if (this.constantsSymbolTable.containsConstant(attributes.get(i).getID()))
				LOGGER.info("Type error : a constant has already the same ID than the attribute "
						+ attributes.get(i).getID() + " of the feature " + featureSymbol.getID() + ".");
			if (featureSymbol.containsAttributeSymbol(attributes.get(i).getID()))
				LOGGER.info("Type error : the feature " + featureSymbol.getID()
						+ " has many attributes with an identical ID ( " + attributes.get(i).getID() + " ).");

			if (attributes.get(i).getType() == Expression.STRUCT) {

				// The attribute is a struct attribute
				this.nbStructAttributes = this.nbStructAttributes + 1;
				Attribute attribute = attributes.get(i);
				RecordSymbol recordSymbol = null;
				// Checks
				if (this.typesSymbolTable.containsTypes(attribute.getUserType())) {
					if (!(this.typesSymbolTable.getTypeSymbol(attribute.getUserType()).getType() == Expression.STRUCT)) {
						LOGGER.info("Type error : the attribute " + attribute.getID() + " of the feature "
								+ featureSymbol.getID() + " seems to be a struct attribute but its type "
								+ attribute.getUserType() + " doesn't correpond to a struct type.");
					} else {
						recordSymbol = (RecordSymbol) this.typesSymbolTable.getTypeSymbol(attribute.getUserType());
					}
				} else {
					LOGGER.info("Type error : the type " + attribute.getUserType() + " of the attribute "
							+ attribute.getID() + " of the feature " + featureSymbol.getID() + " is not defined.");
				}

				Map<String, AttributeSymbol> subAttributes = new HashMap<String, AttributeSymbol>();
				int j = 0;
				while (j <= attribute.getSubAttributes().size() - 1) {
					// Checks
					Attribute subAttribute = attribute.getSubAttributes().get(j);
					if (!(recordSymbol.containsRecordField(subAttribute.getID())))
						LOGGER.info("Type error : In the struct attribute " + attribute.getID()
								+ " of the feature " + featureSymbol.getID() + ", the sub-attribute "
								+ subAttribute.getID() + " hasn't been defined in the struct type "
								+ attribute.getUserType() + ".");
					if (subAttributes.containsKey(subAttribute.getID()))
						LOGGER.info("Type error : the struct attribute " + attribute.getID()
								+ " of the feature " + featureSymbol.getID()
								+ " has many sub-attributes with an identical ID ( " + subAttribute.getID() + " ).");

					AttributeSymbol subAttributeSymbol = recordSymbol.getAttributeSymbol(subAttribute.getID());
					if (subAttributeSymbol.getType() == Expression.USER_CREATED) {

						// The type of the sub-attribute has been created by the
						// user
						if (this.typesSymbolTable.getTypeSymbol(subAttributeSymbol.getUserType())
								.hasASetExpressionSymbol()) {
							// The type of the sub-attribute has a setExpression
							SetExpressionSymbol setExpressionSymbol = this.typesSymbolTable
									.getTypeSymbol(subAttributeSymbol.getUserType()).getSetExpressionSymbol().copy();
							setExpressionSymbol.setAttributeID(subAttribute.getID());
							subAttributes.put(
									subAttribute.getID(),
									new AttributeSymbol(subAttributeSymbol.getUserType(), this.typesSymbolTable
											.getTypeSymbol(subAttributeSymbol.getUserType()).getType(), subAttribute
											.getID(), setExpressionSymbol));
							this.saveInformationAboutAttribute(this.typesSymbolTable.getTypeSymbol(
									subAttributeSymbol.getUserType()).getType());
						} else if (subAttribute.hasAnAttributeBody()) {
							if (subAttribute.getAttributeBody().hasAnInSetExpression()) {
								// The sub-attribute has a set expression
								// declared into its feature body.
								if (subAttribute.getAttributeBody().getInSetExpression().hasAnExpressionList()) {
									EnumSetExpressionSymbol enumSetExpressionSymbol = new EnumSetExpressionSymbol(
											subAttributeSymbol.getTrueType(), subAttribute.getAttributeBody()
													.getInSetExpression().getExpressionList(), subAttribute.getID(),
											this);
									subAttributes.put(
											subAttribute.getID(),
											new AttributeSymbol(subAttribute.getUserType(), subAttributeSymbol
													.getTrueType(), subAttribute.getID(), enumSetExpressionSymbol));
									this.saveInformationAboutAttribute(subAttributeSymbol.getTrueType());
								} else {
									IntervalSetExpressionSymbol intervalSetExpressionSymbol = new IntervalSetExpressionSymbol(
											subAttributeSymbol.getTrueType(), subAttribute.getAttributeBody()
													.getInSetExpression().getMinExpression(), subAttribute
													.getAttributeBody().getInSetExpression().getMaxExpression(),
											subAttribute.getID());
									subAttributes.put(
											subAttribute.getID(),
											new AttributeSymbol(subAttribute.getUserType(), subAttributeSymbol
													.getTrueType(), subAttribute.getID(), intervalSetExpressionSymbol));
									this.saveInformationAboutAttribute(subAttributeSymbol.getTrueType());
								}
							} else {
								// The sub-attribute has an attribute body but
								// no set expression
								subAttributes.put(subAttribute.getID(), new AttributeSymbol(subAttribute.getUserType(),
										subAttributeSymbol.getTrueType(), subAttribute.getID()));
								this.saveInformationAboutAttribute(subAttributeSymbol.getTrueType());
							}
						} else {
							// The sub-attribute has no values domain (nor in
							// its attribute body or in its type )
							subAttributes.put(subAttribute.getID(), new AttributeSymbol(subAttribute.getUserType(),
									subAttributeSymbol.getTrueType(), subAttribute.getID()));
							this.saveInformationAboutAttribute(subAttributeSymbol.getTrueType());
						}
					} else {
						// The type of the sub-attribute is a base type (int,
						// real, bool or enum)
						if (subAttributeSymbol.hasASetExpressionSymbol()) {
							// In "recordSymbol", the sub-attribute which
							// represents "subAttribute" has a set expression
							SetExpressionSymbol setExpressionSymbol = subAttributeSymbol.getSetExpressionSymbol()
									.copy();
							subAttributes.put(subAttribute.getID(), new AttributeSymbol(subAttributeSymbol.getType(),
									subAttribute.getID(), setExpressionSymbol));
							this.saveInformationAboutAttribute(subAttributeSymbol.getTrueType());
						} else {
							// In "recordSymbol", the sub-attribute which
							// represents "subAttribute" has no set expression
							if (subAttribute.hasAnAttributeBody()) {
								if (subAttribute.getAttributeBody().hasAnInSetExpression()) {
									// The sub-attribute has a set expression
									// declared into its attribute body
									if (subAttribute.getAttributeBody().getInSetExpression().hasAnExpressionList()) {
										EnumSetExpressionSymbol enumSetExpressionSymbol = new EnumSetExpressionSymbol(
												recordSymbol.getAttributeSymbol(subAttribute.getID()).getType(),
												subAttribute.getAttributeBody().getInSetExpression()
														.getExpressionList(), subAttribute.getID(), this);
										subAttributes.put(
												subAttribute.getID(),
												new AttributeSymbol(recordSymbol.getAttributeSymbol(
														subAttribute.getID()).getType(), subAttribute.getID(),
														enumSetExpressionSymbol));
										this.saveInformationAboutAttribute(recordSymbol.getAttributeSymbol(
												subAttribute.getID()).getTrueType());
									} else {
										IntervalSetExpressionSymbol intervalSetExpressionSymbol = new IntervalSetExpressionSymbol(
												recordSymbol.getAttributeSymbol(subAttribute.getID()).getType(),
												subAttribute.getAttributeBody().getInSetExpression().getMinExpression(),
												subAttribute.getAttributeBody().getInSetExpression().getMaxExpression(),
												subAttribute.getID());
										subAttributes.put(
												subAttribute.getID(),
												new AttributeSymbol(recordSymbol.getAttributeSymbol(
														subAttribute.getID()).getType(), subAttribute.getID(),
														intervalSetExpressionSymbol));
										this.saveInformationAboutAttribute(recordSymbol.getAttributeSymbol(
												subAttribute.getID()).getTrueType());
									}
								} else {
									// The sub-attribute has an attribute body
									// but no set expression
									subAttributes.put(subAttribute.getID(), new AttributeSymbol(recordSymbol
											.getAttributeSymbol(subAttribute.getID()).getType(), subAttribute.getID()));
									this.saveInformationAboutAttribute(recordSymbol.getAttributeSymbol(
											subAttribute.getID()).getType());
								}
							} else {
								// The sub-attribute has no values domain
								subAttributes.put(subAttribute.getID(), new AttributeSymbol(recordSymbol
										.getAttributeSymbol(subAttribute.getID()).getType(), subAttribute.getID()));
								this.saveInformationAboutAttribute(recordSymbol
										.getAttributeSymbol(subAttribute.getID()).getType());
							}
						}
					}
					j++;
				}
				featureSymbol.addAttribute(new RecordSymbol(attribute.getUserType(), attribute.getType(), attribute
						.getID(), subAttributes));
				this.hasStruct = true;
			} else {
				Attribute baseAttribute = attributes.get(i);
				if (baseAttribute.getType() == Expression.USER_CREATED) {

					// The type of the attribute has been created by the user
					if (this.typesSymbolTable.containsTypes(baseAttribute.getUserType())) {
						AttributeSymbol typeAttributeSymbol = (AttributeSymbol) this.typesSymbolTable
								.getTypeSymbol(baseAttribute.getUserType());
						if (typeAttributeSymbol.getType() == Expression.STRUCT) {
							// The attribute is a struct attribute
							// For example : size carSize; where size is a
							// struct attribute
							this.nbStructAttributes = this.nbStructAttributes + 1;
							int j = 0;
							Map<String, AttributeSymbol> subAttributes = new HashMap<String, AttributeSymbol>();
							RecordSymbol recordSymbol = (RecordSymbol) typeAttributeSymbol;
							Object[] subAttributesSymbols = recordSymbol.getAttributeSymbols().keySet().toArray();
							while (j <= subAttributesSymbols.length - 1) {
								AttributeSymbol subAttributeSymbol = recordSymbol
										.getAttributeSymbol(subAttributesSymbols[j].toString());
								if (subAttributeSymbol.getType() == Expression.USER_CREATED) {
									// The type of the sub-attribute has been
									// created by the user
									if (this.typesSymbolTable.getTypeSymbol(subAttributeSymbol.getUserType())
											.hasASetExpressionSymbol()) {
										// The type of the sub-attribute has a
										// set-expression
										SetExpressionSymbol setExpressionSymbol = this.typesSymbolTable
												.getTypeSymbol(subAttributeSymbol.getUserType())
												.getSetExpressionSymbol().copy();
										setExpressionSymbol.setAttributeID(subAttributeSymbol.getID());
										subAttributes.put(subAttributeSymbol.getID(), new AttributeSymbol(
												subAttributeSymbol.getUserType(), subAttributeSymbol.getTrueType(),
												subAttributeSymbol.getID(), setExpressionSymbol));
										if (this.typesSymbolTable.getTypeSymbol(subAttributeSymbol.getUserType())
												.getType() == Expression.ENUM) {
											EnumSetExpressionSymbol EnumSubAttributeValues = (EnumSetExpressionSymbol) setExpressionSymbol;
											this.nbEnumValues = this.nbEnumValues
													+ EnumSubAttributeValues.getContainedValues().size();
										}
									} else {
										// The type of the sub-attribute has no
										// set expression.
										subAttributes.put(subAttributeSymbol.getID(), new AttributeSymbol(
												subAttributeSymbol.getUserType(), subAttributeSymbol.getType(),
												subAttributeSymbol.getID()));
									}
									this.saveInformationAboutAttribute(subAttributeSymbol.getTrueType());
								} else {
									// The type of the sub-attribute is a simple
									// type (int, real, bool or enum)
									if (subAttributeSymbol.hasASetExpressionSymbol()) {
										// The sub-attribute has a set
										// expression
										SetExpressionSymbol setExpressionSymbol = subAttributeSymbol
												.getSetExpressionSymbol().copy();
										setExpressionSymbol.setAttributeID(subAttributeSymbol.getID());
										subAttributes.put(subAttributeSymbol.getID(), new AttributeSymbol(
												subAttributeSymbol.getType(), subAttributeSymbol.getID(),
												setExpressionSymbol));
										if (subAttributeSymbol.getType() == Expression.ENUM) {
											EnumSetExpressionSymbol EnumSubAttributeValues = (EnumSetExpressionSymbol) setExpressionSymbol;
											this.nbEnumValues = this.nbEnumValues
													+ EnumSubAttributeValues.getContainedValues().size();
										}
									} else {
										// The sub-attribute has no set
										// expression
										subAttributes.put(subAttributeSymbol.getID(), subAttributeSymbol.clone());
									}
									this.saveInformationAboutAttribute(subAttributeSymbol.getType());
								}
								j++;
							}
							featureSymbol.addAttribute(new RecordSymbol(baseAttribute.getUserType(), Expression.STRUCT,
									baseAttribute.getID(), subAttributes));
							this.hasStruct = true;
						} else {
							// The type of attribute has been created by the
							// user and it's not a struct attribute.
							if (typeAttributeSymbol.hasASetExpressionSymbol()) {
								// The type of the attribute has a set
								// expression
								SetExpressionSymbol setExpressionSymbol = typeAttributeSymbol.getSetExpressionSymbol()
										.copy();
								setExpressionSymbol.setAttributeID(baseAttribute.getID());
								featureSymbol.addAttribute(new AttributeSymbol(baseAttribute.getUserType(),
										typeAttributeSymbol.getType(), baseAttribute.getID(), setExpressionSymbol));
								if (typeAttributeSymbol.getType() == Expression.ENUM) {
									EnumSetExpressionSymbol EnumSubAttributeValues = (EnumSetExpressionSymbol) setExpressionSymbol;
									this.nbEnumValues = this.nbEnumValues
											+ EnumSubAttributeValues.getContainedValues().size();
								}
								this.saveInformationAboutAttribute(typeAttributeSymbol.getType());
								this.countAttribute(typeAttributeSymbol.getType());
							} else {
								if (baseAttribute.hasAnAttributeBody()) {
									// The attribute has an attribute body
									if (baseAttribute.getAttributeBody().hasAnInSetExpression()) {
										// The attribute has a set expression
										// into its attribute body.
										if (baseAttribute.getAttributeBody().getInSetExpression().hasAnExpressionList()) {
											EnumSetExpressionSymbol enumSetExpressionSymbol = new EnumSetExpressionSymbol(
													typeAttributeSymbol.getType(), baseAttribute.getAttributeBody()
															.getInSetExpression().getExpressionList(),
													baseAttribute.getID(), this);
											featureSymbol.addAttribute(new AttributeSymbol(typeAttributeSymbol
													.getUserType(), typeAttributeSymbol.getType(), baseAttribute
													.getID(), enumSetExpressionSymbol));
											if (typeAttributeSymbol.getType() == Expression.ENUM) {
												this.nbEnumValues = this.nbEnumValues
														+ enumSetExpressionSymbol.getContainedValues().size();
											}
										} else {
											IntervalSetExpressionSymbol intervalSetExpressionSymbol = new IntervalSetExpressionSymbol(
													typeAttributeSymbol.getType(), baseAttribute.getAttributeBody()
															.getInSetExpression().getMinExpression(),
													baseAttribute.getAttributeBody().getInSetExpression()
															.getMaxExpression(), baseAttribute.getID());
											featureSymbol.addAttribute(new AttributeSymbol(typeAttributeSymbol
													.getUserType(), typeAttributeSymbol.getType(), baseAttribute
													.getID(), intervalSetExpressionSymbol));
										}
									} else {
										// The attribute has no set expression
										// into its feature body
										featureSymbol.addAttribute(new AttributeSymbol(typeAttributeSymbol
												.getUserType(), typeAttributeSymbol.getType(), baseAttribute.getID()));
									}
								} else {
									// The attribute has no feature body
									featureSymbol.addAttribute(new AttributeSymbol(typeAttributeSymbol.getUserType(),
											typeAttributeSymbol.getType(), baseAttribute.getID()));
								}
								this.saveInformationAboutAttribute(typeAttributeSymbol.getType());
								this.countAttribute(typeAttributeSymbol.getType());
							}
						}
					} else {
						LOGGER.info("Type error : the type " + baseAttribute.getUserType()
								+ " of the attribute " + baseAttribute.getID()
								+ " cannot be found in the type symbol table.");
					}
				} else {

					// The attribute is not a struct attribute and its type is a
					// base type (int, real, bool or enum).
					AttributeSymbol attributeSymbol = new AttributeSymbol(baseAttribute.getType(),
							baseAttribute.getID());
					if (baseAttribute.hasAnAttributeBody()) {
						if (baseAttribute.getAttributeBody().hasAnInSetExpression()) {
							// Its values domain is saved
							if (baseAttribute.getAttributeBody().getInSetExpression().hasAnExpressionList()) {
								attributeSymbol.setSetExpressionSymbol(new EnumSetExpressionSymbol(attributeSymbol
										.getType(), baseAttribute.getAttributeBody().getInSetExpression()
										.getExpressionList(), baseAttribute.getID(), this));
								if (attributeSymbol.getType() == Expression.ENUM) {
									EnumSetExpressionSymbol EnumAttributeSymbolValues = (EnumSetExpressionSymbol) attributeSymbol
											.getSetExpressionSymbol();
									this.nbEnumValues = this.nbEnumValues
											+ EnumAttributeSymbolValues.getContainedValues().size();
								}
							} else {
								attributeSymbol.setSetExpressionSymbol(new IntervalSetExpressionSymbol(attributeSymbol
										.getType(), baseAttribute.getAttributeBody().getInSetExpression()
										.getMinExpression(), baseAttribute.getAttributeBody().getInSetExpression()
										.getMaxExpression(), baseAttribute.getID()));
							}
						}
					}
					featureSymbol.addAttribute(attributeSymbol);
					this.saveInformationAboutAttribute(baseAttribute.getType());
					this.countAttribute(baseAttribute.getType());
				}
			}
			i++;
		}
	}

	// Second Phase Methods :
	// ----------------------

	/**
	 * This method realizes the second phase of construction of the table.
	 * During this phase, each time a feature is declared shared, it is attached
	 * to its new parent feature. At the end of this phase, this method also
	 * check if the FM doesn't contain a cycle.
	 * 
	 * @param features
	 *            A vector which contains only the features (grammar format) of
	 *            first level of the syntax tree.
	 * 
	 * @exception see
	 *                below
	 */
	private void constructTableSecondPhase(Vector<Feature> features) {
		int i = features.size() - 1;
		FeatureSymbol featureSymbol;
		while (i >= 0) {
			Feature feature = features.get(i);
			String[] idArray = feature.getID().split("\\.");
			// ///////////////////////////////////////////////////////////////////////////
			if (idArray.length > 1) {
				if (idArray[0].equals("root")) {
					featureSymbol = this.table.get(this.rootFeatureID);
					this.stack.push(rootFeatureID);
				} else {
					featureSymbol = this.table.get(idArray[0]);
					this.stack.push(idArray[0]);
				}
				int j = 1;
				while (j <= idArray.length - 1) {
					featureSymbol = featureSymbol.getChildrenFeature(idArray[j]);
					this.stack.push(idArray[j]);
					j++;
				}
				if (feature.hasChildrenFeatures()) {
					this.childrenFeaturesGroupProcessingSecondPhase(feature.getChildrenFeatures(), featureSymbol);
				}
				j = 0;
				while (j <= (idArray.length - 1)) {
					stack.pop();
					j++;
				}
			} else {
				featureSymbol = this.table.get(feature.getID());
				this.stack.push(featureSymbol.getID());
				if (feature.hasChildrenFeatures()) {
					this.childrenFeaturesGroupProcessingSecondPhase(feature.getChildrenFeatures(), featureSymbol);
				}
				this.stack.pop();
			}
			i--;
		}
		this.detectCycle(this.table.get(rootFeatureID), new FeaturesStack());
	}

	/**
	 * For the features of children features group, this method allows to attach
	 * each feature declared shared to its new parent.
	 * 
	 * @param childrenFeatures
	 *            A vector containing all the features (syntax tree format) of a
	 *            children features group.
	 * 
	 * @param parentFeatureSymbol
	 *            The feature symbol which is the parent of the features
	 *            contained in "features".
	 * 
	 * @throws see
	 *             below
	 */
	public void childrenFeaturesGroupProcessingSecondPhase(Vector<Feature> childrenFeatures,
			FeatureSymbol parentFeatureSymbol) {
		int i = childrenFeatures.size() - 1;
		FeatureSymbol featureSymbol;
		Feature feature;
		while (i >= 0) {
			feature = childrenFeatures.get(i);
			if (feature.isShared()) {
				featureSymbol = this.getFeatureSymbol(feature.getID());
				featureSymbol.setShared();
				if (!(this.getFeatureSymbol(this.stack.toPath()).containsChildrenFeature(featureSymbol.getID()))) {
					this.getFeatureSymbol(this.stack.toPath()).addChildrenFeature(featureSymbol);
					featureSymbol.addParentFeatureSymbol(parentFeatureSymbol);
				} else
					LOGGER.info("Type error : the feature " + parentFeatureSymbol.getID()
							+ " has many attributes with an identical ID ( " + featureSymbol.getID() + " ).");
			} else {
				featureSymbol = this.table.get(feature.getID());
				if (featureSymbol == null) {
					featureSymbol = this.getFeatureSymbol(this.stack.toPath() + "." + feature.getID());
				}
			}
			this.stack.push(featureSymbol.getID());
			if (feature.hasChildrenFeatures()) {
				this.childrenFeaturesGroupProcessingSecondPhase(feature.getChildrenFeatures(), featureSymbol);
			}
			this.stack.pop();
			i--;
		}
	}

	// Third phase methods :
	// ---------------------

	/**
	 * This method realizes the third and last phase of construction of the
	 * table. During this phase, all the attributes bodies ("ifin:", "ifout:"
	 * and "is") and the constraints are checked.
	 * 
	 * @param features
	 *            A vector which contains only the features (grammar format) of
	 *            first level of the syntax tree.
	 * 
	 * @exception see
	 *                below
	 */
	private void constructTableThirdPhase(Vector<Feature> features) {
		int i = features.size() - 1;
		FeatureSymbol featureSymbol;
		while (i >= 0) {
			Feature feature = features.get(i);
			String[] idArray = feature.getID().split("\\.");
			// ///////////////////////////////////////////////////////////////////////////
			if (idArray.length > 1) {
				if (idArray[0].equals("root")) {
					featureSymbol = this.table.get(this.rootFeatureID);
					this.stack.push(rootFeatureID);
				} else {
					featureSymbol = this.table.get(idArray[0]);
					this.stack.push(idArray[0]);
				}
				int j = 1;
				while (j <= idArray.length - 1) {
					featureSymbol = featureSymbol.getChildrenFeature(idArray[j]);
					this.stack.push(idArray[j]);
					j++;
				}
				if (feature.hasAttributes()) {
					this.attributesProcessingThirdPhase(feature.getAttributes(), featureSymbol);
				}
				if (feature.hasConstraints()) {
					this.constraintsProcessing(feature.getConstraints(), featureSymbol);
				}
				if (feature.hasChildrenFeatures()) {
					this.childrenFeaturesGroupProcessingThirdPhase(feature.getChildrenFeatures());
				}
				if (feature.hasData()) {
					// TODO Gestion des datas
				}
				j = 0;
				while (j <= (idArray.length - 1)) {
					stack.pop();
					j++;
				}
			} else {
				featureSymbol = this.table.get(feature.getID());
				this.stack.push(featureSymbol.getID());
				if (feature.hasAttributes()) {
					this.attributesProcessingThirdPhase(feature.getAttributes(), featureSymbol);
				}
				if (feature.hasConstraints()) {
					this.constraintsProcessing(feature.getConstraints(), featureSymbol);
				}
				if (feature.hasChildrenFeatures()) {
					this.childrenFeaturesGroupProcessingThirdPhase(feature.getChildrenFeatures());
				}
				if (feature.hasData()) {
					// TODO Gestion des datas
				}
				this.stack.pop();
			}
			i--;
		}
	}

	/**
	 * For each feature of a set of features, this method allows to check the
	 * validity of : 1) The constraints of the feature 2) The declarations
	 * (ifin:, ifout:, is) of the attributes of the feature.
	 * 
	 * @param childrenFeatures
	 *            A vector of features (syntax tree format). The features
	 *            symbols corresponding to theses features and the attributes
	 *            symbols corresponding to their attributes must exist in the
	 *            table.
	 * 
	 * @throws See
	 *             above
	 */
	public void childrenFeaturesGroupProcessingThirdPhase(Vector<Feature> childrenFeatures) {
		int i = childrenFeatures.size() - 1;
		FeatureSymbol featureSymbol;
		Feature feature;
		while (i >= 0) {
			feature = childrenFeatures.get(i);
			if (!feature.isShared()) {
				this.stack.push(feature.getID());
				featureSymbol = this.table.get(feature.getID());
				if (featureSymbol == null) {
					String[] stackArray = this.stack.toPath().split("\\.");
					featureSymbol = this.table.get(stackArray[0]);
					int j = 1;
					while (j <= stackArray.length - 2) {
						featureSymbol = featureSymbol.getChildrenFeature(stackArray[j]);
						j++;
					}
					featureSymbol = featureSymbol.getChildrenFeature(stackArray[j]);
				}
				this.hierarchicalFeaturesProcessingThirdPhase(feature, featureSymbol);
				this.stack.pop();
			}
			i--;
		}
	}

	/**
	 * For a feature contained in a children features group, this method allows
	 * to check the validity of : 1) Its constraints. 2) The declarations
	 * (ifin:, ifout:, is) of its attributes.
	 * 
	 * @param feature
	 *            The feature for which the attributes and the constraints will
	 *            be checked.
	 * 
	 * @param featureSymbol
	 *            The feature symbol corresponding to "feature".
	 * 
	 * @throws See
	 *             above
	 */
	public void hierarchicalFeaturesProcessingThirdPhase(Feature feature, FeatureSymbol featureSymbol) {
		if (feature.hasAttributes()) {
			this.attributesProcessingThirdPhase(feature.getAttributes(), featureSymbol);
		}
		if (feature.hasConstraints()) {
			this.constraintsProcessing(feature.getConstraints(), featureSymbol);
		}
		if (feature.hasChildrenFeatures()) {
			this.childrenFeaturesGroupProcessingThirdPhase(feature.getChildrenFeatures());
		}
		if (feature.hasData()) {

		}
	}

	/**
	 * This method allows to check the validity of a set of attribute. For each
	 * attribute, it will check the validity of its declaration (ifin:, ifout:
	 * and is) and save the information contained in this declaration into the
	 * attribute symbol which corresponds to attribute.
	 * 
	 * @param attributes
	 *            A vector of attributes (syntax tree format)
	 * 
	 * @param featureSymbol
	 *            The feature symbol which possess the attributes of
	 *            "attributes".
	 * 
	 * @throws see
	 *             above
	 */
	public void attributesProcessingThirdPhase(Vector<Attribute> attributes, FeatureSymbol featureSymbol) {
		int i = 0;
		while (i <= attributes.size() - 1) {
			Attribute attribute = attributes.get(i);
			if (attribute.getType() == Expression.STRUCT) {
				RecordSymbol recordSymbol = (RecordSymbol) featureSymbol.getAttributeSymbol(attribute.getID());
				int j = 0;
				while (j <= attribute.getSubAttributes().size() - 1) {
					SubAttribute subAttribute = attribute.getSubAttributes().get(j);
					AttributeSymbol attributeSymbol = recordSymbol.getAttributeSymbol(subAttribute.getID());
					this.attributeProcessingThirdPhase(subAttribute, featureSymbol, attributeSymbol);
					j++;
				}
			} else {
				AttributeSymbol attributeSymbol = featureSymbol.getAttributeSymbol(attribute.getID());
				this.attributeProcessingThirdPhase(attribute, featureSymbol, attributeSymbol);
			}
			i++;
		}
	}

	/**
	 * This method allows to check all the declaration of an attribute. Each
	 * clause (ifin:, ifout:, and is ) is checked and attached to the attribute
	 * symbol corresponding to the attribute. This attribute symbol has been
	 * created during the first phase.
	 * 
	 * @param attribute
	 *            The attribute which will be checked.
	 * 
	 * @param featureSymbol
	 *            The feature symbol which possess "attributeSymbol"
	 * 
	 * @param attributeSymbol
	 *            The attribute symbol which corresponds to "attribute".
	 * 
	 * @throws see
	 *             above
	 */
	public void attributeProcessingThirdPhase(Attribute attribute, FeatureSymbol featureSymbol,
			AttributeSymbol attributeSymbol) {
		if (attribute.hasAnAttributeBody()) {
			int attributeSymbolType;
			if (attribute.getType() == Expression.USER_CREATED)
				attributeSymbolType = attributeSymbol.getTrueType();
			else
				attributeSymbolType = attributeSymbol.getType();

			if (attribute.getAttributeBody().hasAnIsExpression()) {
				if (attributeSymbol.hasExpression())
					LOGGER.info("Type error : in the feature " + featureSymbol.getID()
							+ ", the attribute declaration " + attribute.toString() + " is not valid. "
							+ attribute.getID() + " has already a valid domain defined.");
				if (attributeSymbol.hasASetExpressionSymbol()) {
					if (attributeSymbol.getSetExpressionSymbol().containsExpression(
							attribute.getAttributeBody().getIsExpression())) {
						attributeSymbol.setExpression(attribute.getAttributeBody().getIsExpression());
					} else {
						LOGGER.info("Type error : the expression "
								+ attribute.getAttributeBody().getIsExpression().toString()
								+ " is out of bound of the domain of the attribute " + attributeSymbol.getID()
								+ " from the feature " + featureSymbol.getID() + ".");
					}
				} else {
					if ((attribute.getAttributeBody().getIsExpression().getType() == attributeSymbolType)
							|| ((attribute.getAttributeBody().getIsExpression().getType() == Expression.INT) && (attributeSymbolType == Expression.REAL))) {
						attributeSymbol.setExpression(attribute.getAttributeBody().getIsExpression());
					} else {
						LOGGER.info("Type error : the type "
								+ Util.toStringExpressionType(attribute.getAttributeBody().getIsExpression().getType())
								+ " of the domain " + attribute.getAttributeBody().getIsExpression().toString()
								+ " doesn't correspond to the type " + Util.toStringExpressionType(attributeSymbolType)
								+ " of the attribute " + attributeSymbol.getID() + " from the feature "
								+ featureSymbol.getID() + ".");
					}
				}
			}
			if (attribute.getAttributeBody().hasAnAttributeConditionnal()) {
				AttributeConditionnal attributeConditionnal = attribute.getAttributeBody().getAttributeConditionnal();
				if (attributeConditionnal.isIfIn()) {
					if (attributeConditionnal.getExpression1() == null) {
						if (attributeSymbol.hasASetExpressionSymbol()) {
							// The attribute has a values domain
							if (attributeSymbol.getSetExpressionSymbol().containsSetExpression(
									attributeConditionnal.getSetExpression1())) {
								attributeSymbol.setIfInSetExpression(attributeConditionnal.getSetExpression1());
							} else {
								System.out
										.println("Type error : In a ifin: of an attribute declaration, the domain of the attribute "
												+ attributeSymbol.getID()
												+ " from the feature "
												+ featureSymbol.getID()
												+ " doesn't include the set expression "
												+ attributeConditionnal.getSetExpression1().toString() + ".");
							}
						} else {
							// The attribute doesn't have a values domain
							if ((attributeSymbolType == attributeConditionnal.getSetExpression1().getType())
									|| ((attributeConditionnal.getExpression1().getType() == Expression.INT) && (attributeSymbolType == Expression.REAL))) {
								attributeSymbol.setIfInSetExpression(attributeConditionnal.getSetExpression1());
							} else {
								LOGGER.info("Type error : In a ifin: of an attribute declaration, the type "
										+ Util.toStringExpressionType(attributeConditionnal.getSetExpression1()
												.getType()) + " of the set expression "
										+ attributeConditionnal.getSetExpression1().toString()
										+ " doesn't correspond to the type of the attribute " + attributeSymbol.getID()
										+ " from the feature " + featureSymbol.getID() + ".");
							}
						}
					} else {
						if (attributeSymbol.hasASetExpressionSymbol()) {
							// The attribute has a values domain
							if (attributeSymbol.getSetExpressionSymbol().containsExpression(
									attributeConditionnal.getExpression1()))
								attributeSymbol.setIfInExpression(attributeConditionnal.getExpression1());
							else
								System.out
										.println("Type error : In a ifin: of an attribute declaration, the domain of the attribute "
												+ attributeSymbol.getID()
												+ " from the feature "
												+ featureSymbol.getID()
												+ " doesn't include the expression "
												+ attributeConditionnal.getExpression1() + ".");
						} else {
							// The attribute doesn't have a values domain
							if ((attributeSymbolType == attributeConditionnal.getExpression1().getType())
									|| ((attributeConditionnal.getExpression1().getType() == Expression.INT) && (attributeSymbolType == Expression.REAL))) {
								attributeSymbol.setIfInExpression(attributeConditionnal.getExpression1());
							} else {
								LOGGER.info("Type error : In a ifin: of an attribute declaration, the type "
										+ Util.toStringExpressionType(attributeConditionnal.getExpression1().getType())
										+ " of the expression " + attributeConditionnal.getExpression1().toString()
										+ " doesn't correpond to the type "
										+ Util.toStringExpressionType(attributeSymbolType) + " of the attribute "
										+ attributeSymbol.getID() + " from the feature " + featureSymbol.getID() + ".");
							}
						}
					}
				}
				if (attributeConditionnal.isIfOut()) {
					if (attributeConditionnal.getExpression2() == null) {
						if (attributeSymbol.hasASetExpressionSymbol()) {
							if (attributeSymbol.getSetExpressionSymbol().containsSetExpression(
									attributeConditionnal.getSetExpression2())) {
								attributeSymbol.setIfOutSetExpression(attributeConditionnal.getSetExpression2());
							} else {
								System.out
										.println("Type error : In a ifout: of an attribute declaration, the domain of the attribute "
												+ attributeSymbol.getID()
												+ " from the feature "
												+ featureSymbol.getID()
												+ " doesn't include the set expression "
												+ attributeConditionnal.getSetExpression2().toString() + ".");
							}
						} else {
							if ((attributeSymbolType == attributeConditionnal.getSetExpression2().getType())
									|| ((attributeConditionnal.getExpression2().getType() == Expression.INT) && (attributeSymbolType == Expression.REAL))) {
								attributeSymbol.setIfOutSetExpression(attributeConditionnal.getSetExpression2());
							} else {
								LOGGER.info("Type error : In a ifout: of an attribute declaration, the type "
										+ Util.toStringExpressionType(attributeConditionnal.getSetExpression2()
												.getType()) + " of the set expression "
										+ attributeConditionnal.getSetExpression2().toString()
										+ " doesn't correspond to the type of the attribute " + attributeSymbol.getID()
										+ " from the feature " + featureSymbol.getID() + ".");
							}
						}
					} else {
						if (attributeSymbol.hasASetExpressionSymbol()) {
							if (attributeSymbol.getSetExpressionSymbol().containsExpression(
									attributeConditionnal.getExpression2()))
								attributeSymbol.setIfOutExpression(attributeConditionnal.getExpression2());
							else
								System.out
										.println("Type error : In a ifout: of an attribute declaration, the domain of the attribute "
												+ attributeSymbol.getID()
												+ " from the feature "
												+ featureSymbol.getID()
												+ " doesn't include the expression "
												+ attributeConditionnal.getExpression2() + ".");
						} else {
							if ((attributeSymbolType == attributeConditionnal.getExpression2().getType())
									|| ((attributeConditionnal.getExpression2().getType() == Expression.INT) && (attributeSymbolType == Expression.REAL))) {
								attributeSymbol.setIfOutExpression(attributeConditionnal.getExpression2());
							} else {
								LOGGER.info("Type error : In a ifout: of an attribute declaration, the type "
										+ Util.toStringExpressionType(attributeConditionnal.getExpression2().getType())
										+ " of the expression " + attributeConditionnal.getExpression2().toString()
										+ " doesn't correpond to the type "
										+ Util.toStringExpressionType(attributeSymbolType) + " of the attribute "
										+ attributeSymbol.getID() + " from the feature " + featureSymbol.getID() + ".");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method allows to : 1) Check the validity of a set of constraints. 2)
	 * Transform these constraints into constraints symbols. 3) Attach these
	 * constraints symbols to the feature symbol which possess them.
	 * 
	 * @param constraints
	 *            A vector of constraints (syntax tree format)
	 * 
	 * @param featureSymbol
	 *            The feature symbol which possess the constraints of
	 *            "constraints".
	 * 
	 * @throws See
	 *             above.
	 */
	public void constraintsProcessing(Vector<Constraint> constraints, FeatureSymbol featureSymbol) {
		int i = 0;
		while (i <= constraints.size() - 1) {

			int type = constraints.get(i).getExpression().getType();
			if (type != Expression.BOOL)
				LOGGER.info("Type error : the type of the constraint " + constraints.get(i).toString()
						+ " of the feature " + featureSymbol.getID() + " is "
						+ Util.toStringExpressionType(constraints.get(i).getExpression().getType()) + " and not bool.");
			featureSymbol.addConstraintSymbol(new ConstraintSymbol(constraints.get(i).isIfIn(), constraints.get(i)
					.isIfOut(), constraints.get(i).getExpression()));
			i++;

		}
	}

	// Utility methods :
	// -----------------

	/**
	 * Display informations about the features contained in the table.
	 */
	public void printTable() {
		LOGGER.info("-----------------------------------------------------------------------------");
		LOGGER.info("--------------------------- Feature Symbol Table ----------------------------");
		LOGGER.info("-----------------------------------------------------------------------------");
		int i = 0;
		Object[] keys = this.table.keySet().toArray();
		while (i <= this.table.size() - 1) {
			if (this.table.get(keys[i].toString()) != null)
				this.table.get(keys[i].toString()).printFeature("    ");
			else
				LOGGER.info("  " + keys[i].toString() + "  : AMBIGUOUS Feature ID");
			LOGGER.info("-----------------------------------------------------------------------------");
			i++;
		}
	}

	/**
	 * Check if there are conflicts between the enum values contained in
	 * "enumSetExpressionSymboland" and between the symbols ID ( feature,
	 * attribute, constant or type).
	 * 
	 * @param enumSetExpressionSymbol
	 *            A enum set which contains enum values (String).
	 * 
	 * @throws IDEnumValuesConflictException
	 *             Thrown if the there is a conflict between an enum value and
	 *             between a symbol ID.
	 */
	public void checkIDEnumValuesConflict(EnumSetExpressionSymbol enumSetExpressionSymbol) {
		Vector<Expression> enumValues = enumSetExpressionSymbol.getContainedValues();
		if (enumValues != null) {
			int i = 0;
			while (i <= enumValues.size() - 1) {
				if ((this.table.containsKey(enumValues.get(i).toString()))
						|| (this.constantsSymbolTable.containsConstant(enumValues.get(i).toString()))
						|| (this.typesSymbolTable.containsTypes(enumValues.get(i).toString())))
					LOGGER.info("Type error : the enum value " + enumValues.get(i) + " of the set Expression "
							+ enumSetExpressionSymbol.toString()
							+ " corresponds to a feature ID, a constant ID or type ID.");
				i++;
			}
		}
	}

	/**
	 * Check if there are conflicts between the enum values contained in the set
	 * expressions of the types symbol table and between the symbols ID (
	 * feature, attribute, constant or type).
	 * 
	 * @throws IDEnumValuesConflictException
	 *             Thrown if the there is a conflict between an enum value and
	 *             between a symbol ID.
	 */
	public void checkIDEnumValuesConflict() {
		Vector<String> enumValues = this.typesSymbolTable.getEnumValues();
		if (enumValues != null) {
			int i = 0;
			while (i <= enumValues.size() - 1) {
				if ((this.table.containsKey(enumValues.get(i)))
						|| (this.constantsSymbolTable.containsConstant(enumValues.get(i)))
						|| (this.typesSymbolTable.containsTypes(enumValues.get(i))))
					LOGGER.info("Type error : the enum value " + enumValues.get(i)
							+ " corresponds to a feature ID, a constant ID or a type ID.");
				i++;
			}
		}
	}

	/**
	 * Return the feature symbol corresponding to the path "path". Make sure
	 * that this path corresponds to an existing feature symbol.
	 * 
	 * @param path
	 *            A path which leads to a feature.
	 * 
	 * @return The feature symbol corresponding to the path "path".
	 * 
	 * @throws See
	 *             above
	 */
	public FeatureSymbol getFeatureSymbol(String path) {
		String[] pathArray = path.split("\\.");
		FeatureSymbol featureSymbol = null;
		if (pathArray[0].equals("root")) {
			pathArray[0] = this.rootFeatureID;
		}
		if (!(this.table.containsKey(pathArray[0]))) {
			LOGGER.info("Type error : the feature corresponding to the path " + path + " cannot be found.");
		} else {
			featureSymbol = this.table.get(pathArray[0]);
			if (featureSymbol == null) {
				LOGGER.info("Type error : the ID path " + path + " is invalid, the first feature ID "
						+ pathArray[0] + " is ambiguous.");
			}
		}
		int i = 1;
		while (i <= pathArray.length - 1) {
			if (featureSymbol.containsChildrenFeature(pathArray[i]))
				featureSymbol = featureSymbol.getChildrenFeature(pathArray[i]);
			else
				LOGGER.info("Type error : the path " + path + " is invalid, the feature "
						+ featureSymbol.getID() + " has no children feature named " + pathArray[i] + ".");
			i++;
		}
		return featureSymbol;
	}

	/**
	 * Return the root feature ID.
	 * 
	 * @return The root feature ID
	 */
	public String getRootFeatureID() {
		return this.rootFeatureID;
	}

	/**
	 * Shows if the table is empty or not.The table is declared empty if the
	 * first phase of construction of the table hasn't been launched.
	 */
	public boolean isEmpty() {
		return this.empty;
	}

	/**
	 * Shows if a path leads to a symbol contained in the table.
	 * 
	 * @param path
	 *            A path which could lead to a symbol.
	 * 
	 * @return true if the path "path" leads to an existing symbol ( feature or
	 *         attribute ).
	 */
	public Boolean containsSymbol(String path) {
		String[] pathArray = path.split("\\.");
		FeatureSymbol featureSymbol = null;
		AttributeSymbol attributeSymbol = null;
		if (pathArray.length > 1) {
			if (pathArray[0].equals("parent")) {
				featureSymbol = this.getFeatureSymbol(this.getStack().toPath());
				if (featureSymbol.isShared()) {
					return false;
				} else {
					featureSymbol = featureSymbol.getFirstParentFeature();
					if (featureSymbol == null) {
						return false;
					}
				}
			} else {
				if (pathArray[0].equals("this")) {
					featureSymbol = this.getFeatureSymbol(this.stack.toPath());
				} else {
					if (pathArray[0].equals("root")) {
						featureSymbol = this.getFeatureSymbol(this.rootFeatureID);
					} else {
						if (Util.isAFeatureID(pathArray[0])) {
							featureSymbol = this.getFeatureSymbol(pathArray[0]);
						} else {
							featureSymbol = this.getFeatureSymbol(this.stack.toPath());
							if (!(featureSymbol.containsAttributeSymbol(pathArray[0]))) {
								return false;
							} else {
								attributeSymbol = featureSymbol.getAttributeSymbol(pathArray[0]);
							}
						}
					}
				}
			}
			int i = 1;
			while (i <= pathArray.length - 1) {
				if (pathArray[i].equals("parent")) {
					if (featureSymbol.isShared()) {
						return false;
					} else {
						featureSymbol = featureSymbol.getFirstParentFeature();
						if (featureSymbol == null)
							return false;
					}
				} else {
					if (Util.isAFeatureID(pathArray[i])) {
						if (!(featureSymbol.containsChildrenFeature(pathArray[i]))) {
							return false;
						} else {
							featureSymbol = featureSymbol.getChildrenFeature(pathArray[i]);
						}
					} else {
						if (attributeSymbol == null) {
							if (!(featureSymbol.containsAttributeSymbol(pathArray[i]))) {
								return false;
							} else {
								attributeSymbol = featureSymbol.getAttributeSymbol(pathArray[i]);
							}
						} else {
							if (!attributeSymbol.isARecordSymbol()) {
								return false;
							} else {
								RecordSymbol recordSymbol = (RecordSymbol) attributeSymbol;
								if (!(recordSymbol.containsRecordField(pathArray[i]))) {
									return false;
								} else {
									attributeSymbol = recordSymbol.getAttributeSymbol(pathArray[i]);
									if (!(i == pathArray.length - 1))
										return false;
								}
							}
						}
					}
				}
				i++;
			}
			return true;
		} else {
			if (pathArray[0].equals("parent")) {
				featureSymbol = this.getFeatureSymbol(this.getStack().toPath());
				if (featureSymbol.isShared())
					return false;
				else
					featureSymbol = featureSymbol.getFirstParentFeature();
				if (featureSymbol != null)
					return true;
				else
					return false;
			} else {
				if (pathArray[0].equals("this")) {
					featureSymbol = this.getFeatureSymbol(this.stack.toPath());
					return true;
				} else {
					if (pathArray[0].equals("root")) {
						featureSymbol = this.getFeatureSymbol(this.rootFeatureID);
						return true;
					} else {
						if (Util.isAFeatureID(pathArray[0])) {
							if (this.table.containsKey(pathArray[0])) {
								featureSymbol = this.getFeatureSymbol(pathArray[0]);
								if (featureSymbol == null) {
									return false;
								} else {
									return true;
								}
							} else {
								return false;
							}

						} else {
							if (this.stack.toPath() == null)
								return false;
							featureSymbol = this.getFeatureSymbol(this.stack.toPath());
							if (!(featureSymbol.containsAttributeSymbol(pathArray[0]))) {
								return false;
							} else {
								attributeSymbol = featureSymbol.getAttributeSymbol(pathArray[0]);
								return true;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method allows to find a non ambiguous path to access to a feature
	 * symbol.
	 * 
	 * @param featureSymbol
	 *            The feature symbol for which a non ambiguous path will be
	 *            found.
	 * 
	 * @return A non ambiguous path which will allow to access to
	 *         "featureSymbol".
	 */
	public String getNonAmbiguousPath(FeatureSymbol featureSymbol) {
		String path = featureSymbol.getID();
		FeatureSymbol tempFeatureSymbol = featureSymbol;
		while (this.isAmbiguousID(tempFeatureSymbol.getID())) {
			tempFeatureSymbol = tempFeatureSymbol.getFirstParentFeature();
			path = tempFeatureSymbol.getID() + "." + path;
		}
		return path;
	}

	/**
	 * This method allows to : 1) Get the symbol corresponding to the path
	 * "path". 2) Get a path which leads to the symbol and which could be
	 * shorter than "path". This new path is used during the normalization
	 * process.
	 * 
	 * @param path
	 *            A path which leads to a symbol (feature or attribute). Be sure
	 *            that the path leads to an existing symbol or an exception will
	 *            be thrown.
	 * 
	 * @return A vector which will contain two elements : 1) The symbol
	 *         corresponding to the path "path". 2) A path which leads to the
	 *         symbol and which could be shorter than "path".
	 * 
	 * @throws see
	 *             above
	 */
	public Vector<Object> getSymbol(String path) {
		Vector<Object> pair = new Vector<Object>();
		String normalLongID;
		String[] pathArray = path.split("\\.");
		FeatureSymbol featureSymbol = null;
		AttributeSymbol attributeSymbol = null;
		if (pathArray.length > 1) {
			if (pathArray[0].equals("parent")) {
				featureSymbol = this.getFeatureSymbol(this.getStack().toPath());
				if (featureSymbol.isShared()) {
					System.out
							.println("Type error : the feature "
									+ featureSymbol.getID()
									+ " is shared and the key word \"parent\" cannot be used in its attributes or constraints.");
				} else {
					featureSymbol = featureSymbol.getFirstParentFeature();
				}
			} else {
				if (pathArray[0].equals("this")) {
					featureSymbol = this.getFeatureSymbol(this.stack.toPath());
				} else {
					if (pathArray[0].equals("root")) {
						featureSymbol = this.getFeatureSymbol(this.rootFeatureID);
					} else {
						if (Util.isAFeatureID(pathArray[0])) {
							featureSymbol = this.getFeatureSymbol(pathArray[0]);
						} else {
							featureSymbol = this.getFeatureSymbol(this.stack.toPath());
							if (!(featureSymbol.containsAttributeSymbol(pathArray[0]))) {
								LOGGER.info("Type error : the feature " + featureSymbol.getID()
										+ " has no attributes named " + pathArray[0] + ". The path " + path
										+ " is incorrect.");
							} else {
								attributeSymbol = featureSymbol.getAttributeSymbol(pathArray[0]);
							}
						}
					}
				}
			}
			normalLongID = this.getNonAmbiguousPath(featureSymbol);
			int i = 1;
			while (i <= pathArray.length - 1) {
				if (pathArray[i].equals("parent")) {
					if (featureSymbol.isShared()) {
						LOGGER.info("Type error : the feature " + featureSymbol.getID()
								+ " is shared and cannot be used with the key word \"parent\".");
					} else {
						featureSymbol = featureSymbol.getFirstParentFeature();
						normalLongID = this.getNonAmbiguousPath(featureSymbol);
						pair.removeAllElements();
						pair.add(featureSymbol);
						pair.add(normalLongID);
					}
				} else {
					if (Util.isAFeatureID(pathArray[i])) {
						if (!(featureSymbol.containsChildrenFeature(pathArray[i]))) {
							LOGGER.info("Type error : the symbol corresponding to the path " + path
									+ " cannot be found.");
						} else {
							featureSymbol = featureSymbol.getChildrenFeature(pathArray[i]);
							normalLongID = normalLongID.concat("." + featureSymbol.getID());
							pair.removeAllElements();
							pair.add(featureSymbol);
							pair.add(normalLongID);
						}
					} else {
						if (attributeSymbol == null) {
							if (!(featureSymbol.containsAttributeSymbol(pathArray[i]))) {
								LOGGER.info("Type error : the feature " + featureSymbol.getID()
										+ " has no attributes named " + pathArray[i] + ". The path " + path
										+ " is incorrect.");
							} else {
								attributeSymbol = featureSymbol.getAttributeSymbol(pathArray[i]);
								// Pour permettre la regénération des ID dans
								// les structs (Voir
								// longIDExpression.toNormalForm)
								// normalLongID =
								// normalLongID.concat("."+attributeSymbol.getID());
								pair.removeAllElements();
								pair.add(attributeSymbol);
								pair.add(normalLongID);
							}
						} else {
							if (!attributeSymbol.isARecordSymbol()) {
								LOGGER.info("Type error : the symbol corresponding to the path " + path
										+ " cannot be found.");
							} else {
								RecordSymbol recordSymbol = (RecordSymbol) attributeSymbol;
								if (!(recordSymbol.containsRecordField(pathArray[i]))) {
									LOGGER.info("Type error : the struct attribute " + recordSymbol.getId()
											+ " has no attributes names " + pathArray[i] + ". The path " + path
											+ " is incorrect.");
								} else {
									attributeSymbol = recordSymbol.getAttributeSymbol(pathArray[i]);
									// Pour permettre la regénération des ID
									// dans les structs (Voir
									// longIDExpression.toNormalForm)
									// normalLongID =
									// normalLongID.concat("_"+attributeSymbol.getID());
									pair.removeAllElements();
									pair.add(attributeSymbol);
									pair.add(normalLongID);
									if (!(i == pathArray.length - 1))
										LOGGER.info("Type error : the symbol corresponding to the path " + path
												+ " cannot be found.");
								}
							}
						}
					}
				}
				i++;
			}
			return pair;
		} else {
			if (pathArray[0].equals("parent")) {
				featureSymbol = this.getFeatureSymbol(this.getStack().toPath()).getFirstParentFeature();
				normalLongID = this.getNonAmbiguousPath(featureSymbol);
				pair.add(featureSymbol);
				pair.add(normalLongID);
				return pair;
			} else {
				if (pathArray[0].equals("this")) {
					featureSymbol = this.getFeatureSymbol(this.stack.toPath());
					normalLongID = this.getNonAmbiguousPath(featureSymbol);
					pair.add(featureSymbol);
					pair.add(normalLongID);
					return pair;
				} else {
					if (pathArray[0].equals("root")) {
						featureSymbol = this.getFeatureSymbol(this.rootFeatureID);
						pair.add(featureSymbol);
						pair.add(featureSymbol.getID());
						return pair;
					} else {
						if (Util.isAFeatureID(pathArray[0])) {
							featureSymbol = this.getFeatureSymbol(pathArray[0]);
							normalLongID = this.getNonAmbiguousPath(featureSymbol);
							pair.add(featureSymbol);
							pair.add(normalLongID);
							return pair;
						} else {
							featureSymbol = this.getFeatureSymbol(this.stack.toPath());
							if (!(featureSymbol.containsAttributeSymbol(pathArray[0]))) {
								LOGGER.info("Type error : the fearure " + featureSymbol.getID()
										+ " contains no attributes named " + pathArray[0] + ". The ID " + path
										+ " is incorrect.");
							} else {
								attributeSymbol = featureSymbol.getAttributeSymbol(pathArray[0]);
								normalLongID = this.getNonAmbiguousPath(featureSymbol);
								pair.add(attributeSymbol);
								pair.add(normalLongID);
								return pair;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Shows if a feature ID is ambiguous.
	 * 
	 * @param ID
	 *            A feature ID which could be ambiguous.
	 * 
	 * @return true if "ID" is ambiguous.
	 */
	public boolean isAmbiguousID(String ID) {
		if (this.table.get(ID) == null)
			return true;
		else
			return false;
	}

	/**
	 * Allow to detect if the FM described in the syntax tree contains a cycle.
	 * 
	 * @param featureSymbol
	 *            The root feature symbol of the FM.
	 * 
	 * @param featuresStack
	 *            An empty features stack.
	 * 
	 * @throws CycleFoundException
	 *             Thrown if the FM contains a cycle.
	 */
	public void detectCycle(FeatureSymbol featureSymbol, FeaturesStack featuresStack) {
		if (featureSymbol.hasBeenVisited()) {
			if (!(featureSymbol.hasChildrenFeaturesVisited())) {
				featuresStack.push(featureSymbol.getID());
				LOGGER.info("Type error : there is a cycle in the DAG => " + featuresStack.toArrowPath());
			} else {
				return;
			}
		} else {
			featureSymbol.setVisited();
			if (!(featureSymbol.hasChildrenFeatures())) {
				featureSymbol.setChildrenFeaturesVisited();
			} else {
				featuresStack.push(featureSymbol.getID());
				Object[] childrenFeaturesArray = featureSymbol.getChildrenFeatures().keySet().toArray();
				int i = 0;
				while (i <= childrenFeaturesArray.length - 1) {
					detectCycle(featureSymbol.getChildrenFeature(childrenFeaturesArray[i].toString()), featuresStack);
					i++;
				}
				featureSymbol.setChildrenFeaturesVisited();
				featuresStack.pop();
			}
		}
	}

	/**
	 * Return the features stack
	 * 
	 * @return the features stack.
	 */
	public FeaturesStack getStack() {
		return this.stack;
	}

	/**
	 * Allow to know if the constants symbol table contains a constant named
	 * "constantID".
	 * 
	 * @param constantID
	 *            The ID of the constant which could be contained in the
	 *            constants symbol table.
	 * 
	 * @return true if the constants symbol table contains a constant named
	 *         "constantID".
	 */
	public boolean containsConstant(String constantID) {
		return constantsSymbolTable.containsConstant(constantID);
	}

	/**
	 * Allow to know if the types symbol table contains a type named "typeID".
	 * 
	 * @param typeID
	 *            The ID of the type which could be contained in the types
	 *            symbol table.
	 * 
	 * @return true if the types symbol table contains a type named "typeID".
	 */
	public boolean containsType(String typeID) {
		return typesSymbolTable.containsTypes(typeID);
	}

	/**
	 * Return the type of the constant named "constantID". Be sure that the
	 * constants symbol table contains "constantID".
	 * 
	 * @param constantID
	 *            The ID of the constant for which the type will be returned.
	 * 
	 * @return The type of the constant named "constantID".
	 */
	public int getConstantType(String constantID) {
		return this.constantsSymbolTable.table.get(constantID).getType();
	}

	/**
	 * Return the value of the constant named "constantID". Be sure that the
	 * constants symbol table contains "constantID".
	 * 
	 * @param constantID
	 *            The ID of the constant for which the value will be returned.
	 * 
	 * @return The value of the constant named "constantID".
	 */
	public String getConstantValue(String constantID) {
		return this.constantsSymbolTable.table.get(constantID).getValue();
	}

	/**
	 * Shows if the table contains at least one feature with at least one
	 * attribute.
	 * 
	 * @return true if the table contains at least one feature with at least one
	 *         attribute
	 */
	public boolean hasAttributes() {
		return this.hasAttributes;
	}

	/**
	 * Shows if the table contains at least one feature with at least one int
	 * attribute.
	 * 
	 * @return true if the table contains at least one feature with at least one
	 *         int attribute
	 */
	public boolean hasInt() {
		return this.hasInt;
	}

	/**
	 * Shows if the table contains at least one feature with at least one real
	 * attribute.
	 * 
	 * @return true if the table contains at least one feature with at least one
	 *         real attribute
	 */
	public boolean hasReal() {
		return this.hasReal;
	}

	/**
	 * Shows if the table contains at least one feature with at least one bool
	 * attribute.
	 * 
	 * @return true if the table contains at least one feature with at least one
	 *         bool attribute
	 */
	public boolean hasBool() {
		return this.hasBool;
	}

	/**
	 * Shows if the table contains at least one feature with at least one enum
	 * attribute.
	 * 
	 * @return true if the table contains at least one feature with at least one
	 *         enum attribute
	 */
	public boolean hasEnum() {
		return this.hasEnum;
	}

	/**
	 * Shows if the table contains at least one feature with at least one enum
	 * attribute.
	 * 
	 * @return true if the table contains at least one feature with at least one
	 *         enum attribute
	 */
	public boolean hasStruct() {
		return this.hasStruct;
	}

	/**
	 * 
	 * @return The number of features that the table contains. This number
	 *         doesn't correspond to the number of lines of the table. Each
	 *         feature, even if its ID is ambiguous, count for one feature.
	 */
	public int getNBFeatures() {
		return this.nbFeatures;
	}

	/**
	 * @return The constants symbol table.
	 */
	public ConstantsSymbolTable getConstantsSymbolTable() {
		return this.constantsSymbolTable;
	}

	/**
	 * 
	 * @return The types symbol table.
	 */
	public TypesSymbolTable getTypesSymbolTable() {
		return this.typesSymbolTable;
	}

	/**
	 * This method allows to save information about an attribute contained in
	 * the FM. It will allow to save that the FM contains a least one attribute
	 * and that the type of this attribute is "type". If you use this method,
	 * make sure that the FM contains the attribute whose type is "type".
	 * 
	 * @param type
	 *            The type of the attribute.
	 */
	private void saveInformationAboutAttribute(int type) {
		switch (type) {
		case Expression.INT:
			this.hasInt = true;
			break;
		case Expression.BOOL:
			this.hasBool = true;
			break;
		case Expression.REAL:
			this.hasReal = true;
			break;
		case Expression.ENUM:
			this.hasEnum = true;
			break;
		}
		this.hasAttributes = true;
	}

	private void countAttribute(int type) {
		switch (type) {
		case Expression.INT:
			this.nbIntAttributes = this.nbIntAttributes + 1;
			break;
		case Expression.BOOL:
			this.nbBoolAttributes = this.nbBoolAttributes + 1;
			break;
		case Expression.REAL:
			this.nbRealAttributes = this.nbRealAttributes + 1;
			break;
		case Expression.ENUM:
			this.nbEnumAttributes = this.nbEnumAttributes + 1;
			break;
		}
	}

	public int getNbIntAttributes() {
		return nbIntAttributes;
	}

	public int getNbRealAttributes() {
		return nbRealAttributes;
	}

	public int getNbBoolAttributes() {
		return nbBoolAttributes;
	}

	public int getNbEnumAttributes() {
		return nbEnumAttributes;
	}

	public int getNbStructAttributes() {
		return nbStructAttributes;
	}

	public int getNbEnumValues() {
		return this.nbEnumValues;
	}

	@Override
	public String toString() {
		String st = "";
		st += "-----------------------------------------------------------------------------\n";
		st += "--------------------------- Feature Symbol Table ----------------------------\n";
		st += "-----------------------------------------------------------------------------\n";
		int i = 0;
		Object[] keys = this.table.keySet().toArray();
		while (i <= this.table.size() - 1) {
			if (this.table.get(keys[i].toString()) != null)
				// st += this.table.get(keys[i].toString());
				st += keys[i].toString() + "->" + this.table.get(keys[i].toString()).getID() + "\n";
			else
				st += "  " + keys[i].toString() + "  : AMBIGUOUS Feature ID\n";
			i++;
		}
		return st;
	}

}

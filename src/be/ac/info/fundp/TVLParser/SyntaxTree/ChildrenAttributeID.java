package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;
import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.symbolTables.AttributeSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.FeatureSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.FeaturesSymbolTable;
import be.ac.info.fundp.TVLParser.symbolTables.RecordSymbol;

public class ChildrenAttributeID {
	
	private final static Logger LOGGER = Logger.getLogger(ChildrenAttributeID.class.getName());
	
	public static int SELECTED_CHILDREN = 1;
	public static int CHILDREN = 2;
	int selectionType;
	String longID;
	public Vector<Object> childrenAttributesPath; // Used for the normalization

	public FeaturesSymbolTable featuresSymbolTable;

	public ChildrenAttributeID(int selectionType, String longID, FeaturesSymbolTable featuresSymbolTable) {
		this.selectionType = selectionType;
		this.longID = longID;
		this.featuresSymbolTable = featuresSymbolTable;
	}

	@Override
	public String toString() {
		if (this.selectionType == ChildrenAttributeID.SELECTED_CHILDREN)
			return "selectedchildren." + this.longID;
		else
			return "children." + this.longID;
	}

	public Vector<Object> getChildrenAttributesPath() {
		return this.childrenAttributesPath;
	}

	public int getType() {
		int currentType = 0;
		FeatureSymbol currentFeatureSymbol = this.featuresSymbolTable.getFeatureSymbol(this.featuresSymbolTable
				.getStack().toPath());
		String[] attributeIDArray = this.longID.split("\\.");
		this.childrenAttributesPath = new Vector<Object>();
		if (attributeIDArray.length > 2) {
			LOGGER.info("SymbolNotFoundException");
		} else {
			FeatureSymbol childrenFeatureSymbol;
			// Struct attribute
			if (attributeIDArray.length == 2) {
				RecordSymbol recordSymbol;
				Object[] featuresArray = currentFeatureSymbol.getChildrenFeatures().keySet().toArray();
				int i = 0;
				while (i <= featuresArray.length - 1) {
					childrenFeatureSymbol = currentFeatureSymbol.getChildrenFeature(featuresArray[i].toString());
					// Used for the normalization
					this.childrenAttributesPath
							.add(this.featuresSymbolTable.getNonAmbiguousPath(childrenFeatureSymbol));
					if (childrenFeatureSymbol.containsAttributeSymbol(attributeIDArray[0])) {
						if (childrenFeatureSymbol.getAttributeSymbol(attributeIDArray[0]).getType() != Expression.STRUCT)
							LOGGER.info("Illegal Expression");
						recordSymbol = (RecordSymbol) childrenFeatureSymbol.getAttributeSymbol(attributeIDArray[0]);
						if (!(recordSymbol.containsRecordField(attributeIDArray[1]))) {
							LOGGER.info("SymbolNotFoundException");
						} else {
							if (i == 0) {
								if (recordSymbol.getAttributeSymbol(attributeIDArray[1]).getType() == Expression.USER_CREATED) {
									currentType = recordSymbol.getAttributeSymbol(attributeIDArray[1]).getTrueType();
								} else {
									currentType = recordSymbol.getAttributeSymbol(attributeIDArray[1]).getType();
								}
							} else {
								if (recordSymbol.getAttributeSymbol(attributeIDArray[1]).getType() == Expression.USER_CREATED) {
									if (currentType != recordSymbol.getAttributeSymbol(attributeIDArray[1])
											.getTrueType()) {
										if ((currentType == Expression.REAL)
												&& (recordSymbol.getAttributeSymbol(attributeIDArray[1]).getTrueType() == Expression.INT)) {
											currentType = Expression.REAL;
										} else {
											if ((currentType == Expression.INT)
													&& (recordSymbol.getAttributeSymbol(attributeIDArray[1])
															.getTrueType() == Expression.REAL)) {
												currentType = Expression.REAL;
											} else {
												LOGGER.info("Illegal Expression");
											}
										}
									}
								} else {
									if (currentType != recordSymbol.getAttributeSymbol(attributeIDArray[1]).getType()) {
										if ((currentType == Expression.REAL)
												&& (recordSymbol.getAttributeSymbol(attributeIDArray[1]).getType() == Expression.INT)) {
											currentType = Expression.REAL;
										} else {
											if ((currentType == Expression.INT)
													&& (recordSymbol.getAttributeSymbol(attributeIDArray[1]).getType() == Expression.REAL)) {
												currentType = Expression.REAL;
											} else {
												LOGGER.info("Illegal Expression");
											}
										}
									}
								}
							}
						}
						// Used for the normalization
						this.childrenAttributesPath.add(recordSymbol.getAttributeSymbol(attributeIDArray[1]));
					} else {
						LOGGER.info("SymbolNotFoundException");
					}

					i++;
				}
				return currentType;
			} else {
				AttributeSymbol attributeSymbol;
				Object[] featuresArray = currentFeatureSymbol.getChildrenFeatures().keySet().toArray();
				int i = 0;
				while (i <= featuresArray.length - 1) {
					childrenFeatureSymbol = currentFeatureSymbol.getChildrenFeature(featuresArray[i].toString());
					// Used for the normalization
					this.childrenAttributesPath.add(featuresSymbolTable.getNonAmbiguousPath(childrenFeatureSymbol));
					if (childrenFeatureSymbol.containsAttributeSymbol(attributeIDArray[0])) {
						attributeSymbol = childrenFeatureSymbol.getAttributeSymbol(attributeIDArray[0]);
						if (i == 0) {
							if (attributeSymbol.getType() == Expression.USER_CREATED) {
								currentType = attributeSymbol.getTrueType();
							} else {
								currentType = attributeSymbol.getType();
							}
						} else {
							if (attributeSymbol.getType() == Expression.USER_CREATED) {
								if (currentType != attributeSymbol.getTrueType()) {
									if ((currentType == Expression.REAL)
											&& (attributeSymbol.getTrueType() == Expression.INT)) {
										currentType = Expression.REAL;
									} else {
										if ((currentType == Expression.INT)
												&& (attributeSymbol.getTrueType() == Expression.REAL)) {
											currentType = Expression.REAL;
										} else {
											LOGGER.info("Illegal Expression");
										}
									}
								}
							} else {
								if (currentType != attributeSymbol.getType()) {
									if ((currentType == Expression.REAL)
											&& (attributeSymbol.getType() == Expression.INT)) {
										currentType = Expression.REAL;
									} else {
										if ((currentType == Expression.INT)
												&& (attributeSymbol.getType() == Expression.REAL)) {
											currentType = Expression.REAL;
										} else {
											LOGGER.info("Illegal Expression");
										}
									}
								}
							}
						}
						this.childrenAttributesPath.add(attributeSymbol);
					} else {
						LOGGER.info("Type error : the expression " + this.toString()
								+ " is not valid. The child feature "
								+ currentFeatureSymbol.getChildrenFeature(featuresArray[i].toString()).getID()
								+ " of the parent feature " + currentFeatureSymbol.getID() + " has no attribute named "
								+ attributeIDArray[0] + ".");
					}
					i++;
				}
				return currentType;
			}
		}
		return Expression.UNKNOWN;
	}
}

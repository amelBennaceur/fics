package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.symbolTables.FeatureSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.FeaturesSymbolTable;

public class CountAggExpression implements Expression {

	private final static Logger LOGGER = Logger.getLogger(CountAggExpression.class.getName());
	
	int childrenAttributeID;
	FeatureSymbol currentFeatureSymbol;

	FeaturesSymbolTable featuresSymbolTable;

	public CountAggExpression(int childrenAttributeID, FeaturesSymbolTable featuresSymbolTable) {
		this.childrenAttributeID = childrenAttributeID;
		this.featuresSymbolTable = featuresSymbolTable;
	}

	/**
	 * @return the expressionList
	 */

	/**
	 * @return the childrenAttributeID
	 */
	public int getChildrenAttributeID() {
		return childrenAttributeID;
	}

	@Override
	public String toString() {
		if (this.childrenAttributeID == ChildrenAttributeID.SELECTED_CHILDREN) {
			return "count( selectedchildren )";
		} else
			return "count( children )";
	}

	@Override
	public int getType() {
		currentFeatureSymbol = featuresSymbolTable.getFeatureSymbol(featuresSymbolTable.getStack().toPath());
		if (currentFeatureSymbol.hasChildrenFeatures())
			return Expression.INT;
		else
			LOGGER.info("Type error : the expression " + this.toString() + " is not valid. The feature "
					+ currentFeatureSymbol.getID() + " has no children features.");
		return Expression.UNKNOWN;
	}

	@Override
	public Expression toNormalForm() {
		if (this.childrenAttributeID == ChildrenAttributeID.SELECTED_CHILDREN) {
			Object[] currentFeatureChildrenArray = this.currentFeatureSymbol.getChildrenFeatures().keySet().toArray();
			FeatureSymbol childFeature = this.currentFeatureSymbol.getChildrenFeature(currentFeatureChildrenArray[0]
					.toString());
			String childFeatureNonAmbiguousPath = featuresSymbolTable.getNonAmbiguousPath(childFeature);
			ExpressionList expressionList = new ExpressionList(new QuestExpression(new LongIDExpression(
					childFeatureNonAmbiguousPath, null), new IntExpression("1"), new IntExpression("0")));
			int i = 1;
			while (i <= currentFeatureChildrenArray.length - 1) {
				childFeature = this.currentFeatureSymbol.getChildrenFeature(currentFeatureChildrenArray[0].toString());
				childFeatureNonAmbiguousPath = featuresSymbolTable.getNonAmbiguousPath(childFeature);
				expressionList = new ExpressionList(new QuestExpression(new LongIDExpression(
						childFeatureNonAmbiguousPath, null), new IntExpression("1"), new IntExpression("0")),
						expressionList);
				i++;
			}
			return new SumAggExpression(expressionList);
		} else {
			return new IntExpression(String.valueOf(this.currentFeatureSymbol.getChildrenFeatures().size()));
		}
	}

}

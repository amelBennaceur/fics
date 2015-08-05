package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;

import be.ac.info.fundp.TVLParser.Util.Util;
import be.ac.info.fundp.TVLParser.symbolTables.AttributeSymbol;

public class MinAggExpression implements Expression {

	ExpressionList expressionList;
	ChildrenAttributeID childrenAttributeID;

	public MinAggExpression(ExpressionList e1) {
		this.expressionList = e1;
	}

	public MinAggExpression(ChildrenAttributeID e1) {
		this.childrenAttributeID = e1;
	}

	/**
	 * @return the expressionList
	 */
	public ExpressionList getExpressionList() {
		return expressionList;
	}

	/**
	 * @return the childrenAttributeID
	 */
	public ChildrenAttributeID getChildrenAttributeID() {
		return childrenAttributeID;
	}

	@Override
	public String toString() {
		if (this.expressionList != null) {
			return "min( " + this.expressionList.toString() + " )";
		} else
			return "min( " + this.childrenAttributeID.toString() + " )";
	}

	@Override
	public int getType() throws NumberFormatException {
		if (this.expressionList == null) {
			if (this.childrenAttributeID.getType() == Expression.REAL) {
				return Expression.REAL;
			} else {
				if (this.childrenAttributeID.getType() == Expression.INT) {
					return Expression.INT;
				} else {
					System.out
							.println("Type error : the expression "
									+ this.toString()
									+ " is invalid. In an aggregate min expression, the type of the parameter must be int or real. Currently, The type of the children attribute is "
									+ Util.toStringExpressionType(this.childrenAttributeID.getType())
									+ " and not int or real.");
				}
			}
		} else {
			if (this.expressionList.getType() == Expression.REAL) {
				return Expression.REAL;
			} else {
				if (this.expressionList.getType() == Expression.INT) {
					return Expression.INT;
				} else {
					System.out
							.println("Type error : the expression "
									+ this.toString()
									+ " is invalid. In an aggregate min expression, the type of the parameter must be int or real. Currently, The type of the expression list is "
									+ Util.toStringExpressionType(this.childrenAttributeID.getType())
									+ " and not int or real.");
				}
			}
		}
		return Expression.UNKNOWN;
	}

	@Override
	public Expression toNormalForm() {
		if (this.childrenAttributeID != null) {
			Vector<Object> attributesIDPath = this.childrenAttributeID.getChildrenAttributesPath();
			LongIDExpression longIDExpression = new LongIDExpression(attributesIDPath.get(0).toString() + "."
					+ ((AttributeSymbol) attributesIDPath.get(1)).getID(), null);
			ExpressionList expressionList = new ExpressionList(longIDExpression);
			int i = 2;
			while (i <= attributesIDPath.size() - 1) {
				longIDExpression = new LongIDExpression(attributesIDPath.get(i).toString() + "."
						+ ((AttributeSymbol) attributesIDPath.get(i + 1)).getID(), null);
				expressionList = new ExpressionList(longIDExpression, expressionList);
				i = i + 2;
			}
			return new MinAggExpression(expressionList);
		} else {
			int i = 1;
			ExpressionList normalizedExpressionList = new ExpressionList(this.expressionList.getExpressions().get(i));
			while (i <= this.expressionList.expressions.size() - 1) {
				normalizedExpressionList = new ExpressionList(this.expressionList.getExpressions().get(i)
						.toNormalForm(), normalizedExpressionList);
				i++;
			}
			return new MinAggExpression(expressionList);
		}
	}

}

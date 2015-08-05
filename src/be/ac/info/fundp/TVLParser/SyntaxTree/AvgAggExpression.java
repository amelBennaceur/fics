package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;

import be.ac.info.fundp.TVLParser.Util.Util;

public class AvgAggExpression implements Expression {

	ExpressionList expressionList;
	ChildrenAttributeID childrenAttributeID;

	public AvgAggExpression(ExpressionList e1) {
		this.expressionList = e1;
	}

	public AvgAggExpression(ChildrenAttributeID e1) {
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
	public int getType() {
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
									+ " is invalid. In an aggregate avg expression, the type of the parameter must be int or real. Currently, The type of the children attribute is "
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
									+ " is invalid. In an aggregate avg expression, the type of the parameter must be int or real. Currently, The type of the expression list is "
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
			if (this.childrenAttributeID.selectionType == ChildrenAttributeID.SELECTED_CHILDREN) {
				Vector<Object> childrenAttributesPath = this.childrenAttributeID.getChildrenAttributesPath();
				ExpressionList expressionList = new ExpressionList(
						new QuestExpression(new LongIDExpression(childrenAttributesPath.get(0).toString(), null),
								new IntExpression("1"), new IntExpression("0")));
				int i = 2;
				while (i <= childrenAttributesPath.size() - 1) {
					expressionList = new ExpressionList(new QuestExpression(new LongIDExpression(childrenAttributesPath
							.get(i).toString(), null), new IntExpression("1"), new IntExpression("0")), expressionList);
					i = i + 2;
				}
				return new DivideExpression((new SumAggExpression(this.childrenAttributeID)).toNormalForm(),
						(new SumAggExpression(expressionList)));
			} else {
				return new DivideExpression((new SumAggExpression(this.childrenAttributeID)).toNormalForm(),
						new IntExpression(
								new Integer(this.childrenAttributeID.childrenAttributesPath.size() / 2).toString()));
			}
		} else {
			int i = 1;
			ExpressionList normalizedExpressionList = new ExpressionList(this.expressionList.getExpressions().get(i));
			while (i <= this.expressionList.expressions.size() - 1) {
				normalizedExpressionList = new ExpressionList(this.expressionList.getExpressions().get(i)
						.toNormalForm(), normalizedExpressionList);
				i++;
			}
			return new AvgAggExpression(expressionList);
		}
	}

	@Override
	public String toString() {
		if (this.expressionList != null) {
			return "avg( " + this.expressionList.toString() + " )";
		} else
			return "avg( " + this.childrenAttributeID.toString() + " )";
	}

}

package be.ac.info.fundp.TVLParser.SyntaxTree;

import be.ac.info.fundp.TVLParser.Util.Util;

public class AbsExpression implements Expression {

	Expression expression;

	public AbsExpression(Expression e1) {
		this.expression = Util.removeParentheses(e1);
	}

	/**
	 * @return the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	@Override
	public int getType() {
		if (this.expression.getType() == Expression.REAL)
			return Expression.REAL;
		if (this.expression.getType() == Expression.INT)
			return Expression.INT;
		System.out.println("Type error : the expression " + this.toString()
				+ " is invalid. The type of the paramater ( " + this.expression.toString()
				+ " ) of an abs expression must be int or real. Currently,  its type is "
				+ Util.toStringExpressionType(this.expression.getType()) + ".");

		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return "abs( " + this.expression.toString() + " )";
	}

	@Override
	public AbsExpression toNormalForm() {
		AbsExpression normalizedExpression = new AbsExpression(this.expression.toNormalForm());
		return normalizedExpression;
	}
}

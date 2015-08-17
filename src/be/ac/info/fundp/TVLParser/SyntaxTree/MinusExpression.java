package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Util.Util;

public class MinusExpression implements Expression {

	private final static Logger LOGGER = Logger.getLogger(MinusExpression.class.getName());
	
	Expression expression1, expression2;

	public MinusExpression(Expression e1, Expression e2) {
		this.expression1 = Util.removeParentheses(e1);
		this.expression2 = Util.removeParentheses(e2);
	}

	public MinusExpression(Expression e1) {
		this.expression1 = Util.removeParentheses(e1);
		this.expression2 = null;
	}

	/**
	 * @return the expression1
	 */
	public Expression getExpression1() {
		return expression1;
	}

	/**
	 * @return the expression2
	 */
	public Expression getExpression2() {
		return expression2;
	}

	@Override
	public int getType() {
		if (this.expression2 == null)
			return this.getTypeUMinus();
		else
			return this.getTypeMinus();
	}

	public int getTypeMinus() {
		if (this.expression1.getType() == Expression.INT) {
			if (this.expression2.getType() == Expression.INT) {
				return Expression.INT;
			} else {
				if (this.getExpression2().getType() == Expression.REAL) {
					return Expression.REAL;
				} else {
					LOGGER.info("Type error : the expression " + this.toString()
							+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
							+ " ) of a minus expression must be real or int. Currently,  its type is "
							+ Util.toStringExpressionType(this.expression2.getType()) + ".");
				}
			}
		} else {
			if (this.expression1.getType() == Expression.REAL) {
				if (this.expression2.getType() == Expression.INT) {
					return Expression.REAL;
				} else {
					if (this.getExpression2().getType() == Expression.REAL) {
						return Expression.REAL;
					} else {
						LOGGER.info("Type error : the expression " + this.toString()
								+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
								+ " ) of a minus expression must be real or int. Currently,  its type is "
								+ Util.toStringExpressionType(this.expression2.getType()) + ".");
					}
				}
			} else {
				LOGGER.info("Type error : the expression " + this.toString()
						+ " is invalid. The type of the left paramater ( " + this.expression1.toString()
						+ " ) of a minus expression must be real or int. Currently,  its type is "
						+ Util.toStringExpressionType(this.expression1.getType()) + ".");
			}
		}
		return Expression.UNKNOWN;
	}

	public int getTypeUMinus() {
		if (this.expression1.getType() == Expression.REAL)
			return Expression.REAL;
		if (this.expression1.getType() == Expression.INT)
			return Expression.INT;
		LOGGER.info("Type error : the expression " + this.toString()
				+ " is invalid. The type of the paramater ( " + this.expression1.toString()
				+ " ) of an unary minus expression must be int or real. Currently,  its type is "
				+ Util.toStringExpressionType(this.expression1.getType()) + ".");

		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		if (this.expression2 == null)
			return "- " + this.expression1.toString();
		else
			return this.expression1.toString() + " - " + this.expression2.toString();
	}

	@Override
	public Expression toNormalForm() {
		if (this.expression2 == null)
			return new MinusExpression(this.expression1.toNormalForm());
		else
			return new MinusExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
	}

}

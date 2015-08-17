package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Util.Util;

public class DivideExpression implements Expression {

	private final static Logger LOGGER = Logger.getLogger(DivideExpression.class.getName());
	
	Expression expression1, expression2;

	public DivideExpression(Expression e1, Expression e2) {
		this.expression1 = Util.removeParentheses(e1);
		this.expression2 = Util.removeParentheses(e2);
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
		if (this.expression1.getType() == Expression.INT) {
			// L'expression de gauche est de type entier
			if (this.expression2.getType() == Expression.INT) {
				// L'expression de droite est aussi un entier
				// L'expression tout entière est un entier ==> C'est correcte !
				// Vu que c'est une division, même avec deux expressions
				// entières, on retourne un réel !
				return Expression.REAL;
			} else {
				if (this.getExpression2().getType() == Expression.REAL) {
					// L'expression de droite est un réel ==> tute l'expression
					// devient un réel
					return Expression.REAL;
				} else {
					LOGGER.info("Type error : the expression " + this.toString()
							+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
							+ " ) of a divide expression must be real or int. Currently,  its type is "
							+ Util.toStringExpressionType(this.expression2.getType()) + ".");
				}
			}
		} else {
			if (this.expression1.getType() == Expression.REAL) {
				// L'expression de gauche est un réel, toute l'expression
				// devient un réel mais on vérifié quand même le type de
				// l'expressiond de droite
				if (this.expression2.getType() == Expression.INT) {
					// L'expression de droite est aussi un entier
					// L'expression tout entière est un entier ==> C'est
					// correcte !
					return Expression.REAL;
				} else {
					if (this.getExpression2().getType() == Expression.REAL) {
						// L'expression de droite est un réel ==> tute
						// l'expression devient un réel
						return Expression.REAL;
					} else {
						LOGGER.info("Type error : the expression " + this.toString()
								+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
								+ " ) of a divide expression must be real or int. Currently,  its type is "
								+ Util.toStringExpressionType(this.expression2.getType()) + ".");
					}
				}
			} else {
				LOGGER.info("Type error : the expression " + this.toString()
						+ " is invalid. The type of the left paramater ( " + this.expression1.toString()
						+ " ) of a divide expression must be real or int. Currently,  its type is "
						+ Util.toStringExpressionType(this.expression1.getType()) + ".");
			}
		}
		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return this.expression1.toString() + " / " + this.expression2.toString();
	}

	@Override
	public Expression toNormalForm() {
		return new DivideExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
	}

}

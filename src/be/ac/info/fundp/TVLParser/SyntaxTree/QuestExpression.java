package be.ac.info.fundp.TVLParser.SyntaxTree;

import be.ac.info.fundp.TVLParser.Util.Util;

public class QuestExpression implements BooleanExpression {

	Expression expression1, expression2, expression3;

	public QuestExpression(Expression e1, Expression e2, Expression e3) {
		this.expression1 = Util.removeParentheses(e1);
		this.expression2 = Util.removeParentheses(e2);
		this.expression3 = Util.removeParentheses(e3);
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

	/**
	 * @return the expression3
	 */
	public Expression getExpression3() {
		return expression3;
	}

	@Override
	public int getType() {
		if (!(this.expression1.getType() == Expression.BOOL))
			System.out.println("Type error : The expression " + this.toString()
					+ " isn't valid. The type of the first parameter ( " + this.expression1.toString() + " ) is "
					+ Util.toStringExpressionType(this.expression1.getType()) + " and not bool.");
		if (this.expression2.getType() == Expression.BOOL) {
			if (this.expression3.getType() == Expression.BOOL)
				return Expression.BOOL;
			else
				System.out.println("Type error : The expression " + this.toString() + " is invalid. The type "
						+ Util.toStringExpressionType(this.expression2.getType()) + " of the second parameter ( "
						+ this.expression2.toString() + " ) is different from the type "
						+ Util.toStringExpressionType(this.expression3.getType()) + " of the second parameter ( "
						+ this.expression3.toString() + " ).");
		} else {
			if (this.expression2.getType() == Expression.INT) {
				if (this.expression3.getType() == Expression.INT)
					return Expression.INT;
				else
					System.out.println("Type error : The expression " + this.toString() + " is invalid. The type "
							+ Util.toStringExpressionType(this.expression2.getType()) + " of the second parameter ( "
							+ this.expression2.toString() + " ) is different from the type "
							+ Util.toStringExpressionType(this.expression3.getType()) + " of the second parameter ( "
							+ this.expression3.toString() + " ).");
			} else {
				if (this.expression2.getType() == Expression.REAL) {
					if ((this.expression3.getType() == Expression.REAL)
							|| (this.expression3.getType() == Expression.INT))
						return Expression.REAL;
					else
						System.out.println("Type error : The expression " + this.toString() + " is invalid. The type "
								+ Util.toStringExpressionType(this.expression2.getType())
								+ " of the second parameter ( " + this.expression2.toString()
								+ " ) is different from the type "
								+ Util.toStringExpressionType(this.expression3.getType())
								+ " of the second parameter ( " + this.expression3.toString() + " ).");
				} else {
					if (this.expression2.getType() == Expression.ENUM) {
						if (this.expression3.getType() == Expression.ENUM)
							return Expression.ENUM;
						else
							System.out.println("Type error : The expression " + this.toString()
									+ " is invalid. The type "
									+ Util.toStringExpressionType(this.expression2.getType())
									+ " of the second parameter ( " + this.expression2.toString()
									+ " ) is different from the type "
									+ Util.toStringExpressionType(this.expression3.getType())
									+ " of the second parameter ( " + this.expression3.toString() + " ).");
					} else {
						System.out.println("Type error : The expression " + this.toString() + " is invalid. The type "
								+ Util.toStringExpressionType(this.expression2.getType())
								+ " of the second parameter ( " + this.expression2.toString()
								+ " ) is different from the type "
								+ Util.toStringExpressionType(this.expression3.getType())
								+ " of the second parameter ( " + this.expression3.toString() + " ).");
					}
				}
			}
		}
		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return this.expression1.toString() + " ? " + this.expression2.toString() + " : " + this.expression3.toString();
	}

	@Override
	public Expression toNormalForm() {
		return new QuestExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm(),
				this.expression3.toNormalForm());
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		return new OrExpression(new AndExpression(((BooleanExpression) this.expression1).toSimplifiedForm(),
				((BooleanExpression) this.expression2).toSimplifiedForm()), new AndExpression(new NotExpression(
				((BooleanExpression) this.expression1).toSimplifiedForm()),
				((BooleanExpression) this.expression3).toSimplifiedForm()));
		// return new OrExpression(new ParenthesesExpression(new
		// AndExpression(this.expression1, this.expression2)), new
		// ParenthesesExpression(new AndExpression(new
		// NotExpression(this.expression1), this.expression3)));
	}

	/**
	 * This method is normally never used because the toSimplifiedForm() method
	 * removes each XorAggExpression from the diagram.
	 * 
	 * @return
	 */
	@Override
	public BooleanExpression removeArrows() {
		return this;
	}

	/**
	 * This method is normally never used because the toSimplifiedForm() method
	 * removes each XorAggExpression from the diagram.
	 * 
	 * @return
	 */
	@Override
	public BooleanExpression distributeDisjunctions() {
		return this;
	}

	/**
	 * This method is normally never used because the toSimplifiedForm() method
	 * removes each XorAggExpression from the diagram.
	 * 
	 * @return
	 */
	@Override
	public BooleanExpression distributeNegations() {
		return this;
	}
}
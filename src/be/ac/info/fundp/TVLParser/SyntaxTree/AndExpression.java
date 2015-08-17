package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Util.Util;

public class AndExpression implements BooleanExpression {

	private final static Logger LOGGER = Logger.getLogger(AndExpression.class.getName());
	
	Expression expression1, expression2;

	public AndExpression(Expression e1, Expression e2) {
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
		if (this.expression1.getType() == Expression.BOOL) {
			if (this.expression2.getType() == Expression.BOOL) {
				return Expression.BOOL;
			} else {
				LOGGER.info("Type error : the expression " + this.toString()
						+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
						+ " ) of an and expression must be bool. Currently,  its type is "
						+ Util.toStringExpressionType(this.expression2.getType()) + ".");
			}
		} else {
			LOGGER.info("Type error : the expression " + this.toString()
					+ " is invalid. The type of the left paramater ( " + this.expression1.toString()
					+ " ) of an and expression must be bool. Currently,  its type is "
					+ Util.toStringExpressionType(this.expression1.getType()) + ".");
		}
		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return this.expression1.toString() + " && " + this.expression2.toString();
	}

	@Override
	public BooleanExpression toNormalForm() {
		return new AndExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
	}

	@Override
	public BooleanExpression removeArrows() {
		return new AndExpression(((BooleanExpression) this.expression1).removeArrows(),
				((BooleanExpression) this.expression2).removeArrows());
	}

	@Override
	public AndExpression toSimplifiedForm() {
		return new AndExpression(((BooleanExpression) this.expression1).toSimplifiedForm(),
				((BooleanExpression) this.expression2).toSimplifiedForm());
	}

	@Override
	public BooleanExpression distributeDisjunctions() {
		return new AndExpression(((BooleanExpression) this.expression1).distributeDisjunctions(),
				((BooleanExpression) this.expression2).distributeDisjunctions());
	}

	@Override
	public BooleanExpression distributeNegations() {
		return new AndExpression(((BooleanExpression) this.expression1).distributeNegations(),
				((BooleanExpression) this.expression2).distributeNegations());
	}

	public void setExpression1(Expression expression) {
		this.expression1 = expression;
	}

	public void setExpression2(Expression expression) {
		this.expression2 = expression;
	}

}

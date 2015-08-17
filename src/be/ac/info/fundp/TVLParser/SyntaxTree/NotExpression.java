package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Util.Util;

public class NotExpression implements BooleanExpression {

	private final static Logger LOGGER = Logger.getLogger(NotExpression.class.getName());
	
	Expression expression;

	public NotExpression(Expression e1) {
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
		if (this.expression.getType() == Expression.BOOL) {
			return Expression.BOOL;
		}
		LOGGER.info("Type error : the expression " + this.toString()
				+ " is invalid. The type of the paramater ( " + this.expression.toString()
				+ " ) of a not expression must be bool. Currently,  its type is "
				+ Util.toStringExpressionType(this.expression.getType()) + ".");
		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return "! " + this.expression.toString();
	}

	@Override
	public Expression toNormalForm() {
		return new NotExpression(this.expression.toNormalForm());
	}

	@Override
	public BooleanExpression removeArrows() {
		return new NotExpression(((BooleanExpression) this.expression).removeArrows());
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		return new NotExpression(((BooleanExpression) this.expression).toSimplifiedForm());
	}

	@Override
	public BooleanExpression distributeNegations() {
		while (this.expression instanceof ParenthesesExpression) {
			this.expression = ((ParenthesesExpression) this.expression).getExpression();
		}
		if (this.expression instanceof LongIDExpression) {
			return new NotExpression(this.expression);
		} else {
			if (this.expression instanceof NotExpression) {
				return ((BooleanExpression) ((NotExpression) this.expression).getExpression()).distributeNegations();
			} else {
				BooleanExpression expression1;
				BooleanExpression expression2;
				if (this.expression instanceof OrExpression) {
					expression1 = (BooleanExpression) ((OrExpression) expression).getExpression1();
					expression2 = (BooleanExpression) ((OrExpression) expression).getExpression2();
					return new AndExpression((new NotExpression(expression1)).distributeNegations(),
							(new NotExpression(expression2)).distributeNegations());
				} else {
					if (this.expression instanceof AndExpression) {
						expression1 = (BooleanExpression) ((AndExpression) expression).getExpression1();
						expression2 = (BooleanExpression) ((AndExpression) expression).getExpression2();
						return new OrExpression((new NotExpression(expression1)).distributeNegations(),
								(new NotExpression(expression2)).distributeNegations());
					} else {
						if (this.expression instanceof TrueExpression) {
							return new FalseExpression();
						} else {
							return new TrueExpression();
						}
					}
				}
			}
		}
	}

	@Override
	public BooleanExpression distributeDisjunctions() {
		// return new
		// NotExpression(((BooleanExpression)this.expression).distributeDisjunctions());
		return this;
	}

}

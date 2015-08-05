package be.ac.info.fundp.TVLParser.SyntaxTree;

import be.ac.info.fundp.TVLParser.Util.Util;

public class InverseImpliesExpression implements BooleanExpression {

	Expression expression1, expression2;

	public InverseImpliesExpression(Expression e1, Expression e2) {
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
				System.out.println("Type error : the expression " + this.toString()
						+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
						+ " ) of an and expression must be bool. Currently,  its type is "
						+ Util.toStringExpressionType(this.expression2.getType()) + ".");
			}
		} else {
			System.out.println("Type error : the expression " + this.toString()
					+ " is invalid. The type of the left paramater ( " + this.expression1.toString()
					+ " ) of an and expression must be bool. Currently,  its type is "
					+ Util.toStringExpressionType(this.expression1.getType()) + ".");
		}
		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return this.expression1.toString() + " <- " + this.expression2.toString();
	}

	@Override
	public Expression toNormalForm() {
		return new InverseImpliesExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		return new OrExpression(new NotExpression(((BooleanExpression) this.expression2).toSimplifiedForm()),
				((BooleanExpression) this.expression1).toSimplifiedForm());
	}

	@Override
	public BooleanExpression removeArrows() {
		return new OrExpression(new NotExpression(((BooleanExpression) this.expression2).removeArrows()),
				((BooleanExpression) this.expression1).removeArrows());
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

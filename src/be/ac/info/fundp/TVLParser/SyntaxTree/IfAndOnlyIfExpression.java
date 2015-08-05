package be.ac.info.fundp.TVLParser.SyntaxTree;

import be.ac.info.fundp.TVLParser.Util.Util;

public class IfAndOnlyIfExpression implements BooleanExpression {

	Expression expression1, expression2;

	public IfAndOnlyIfExpression(Expression e1, Expression e2) {
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

	public boolean check() {
		if (this.getType() == Expression.BOOL)
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return this.expression1.toString() + " <-> " + this.expression2.toString();
	}

	@Override
	public Expression toNormalForm() {
		return new IfAndOnlyIfExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		// BooleanExpression simplifiedExpression1 =
		// ((BooleanExpression)this.expression1).toSimplifiedForm();
		// BooleanExpression simplifiedExpression2 =
		// ((BooleanExpression)this.expression2).toSimplifiedForm();
		return new AndExpression(new ImpliesExpression(this.expression1, this.expression2).toSimplifiedForm(),
				new ImpliesExpression(this.expression2, this.expression1).toSimplifiedForm());
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

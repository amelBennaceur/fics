package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Util.Util;

public class OrExpression implements BooleanExpression {

	private final static Logger LOGGER = Logger.getLogger(OrExpression.class.getName());
	
	Expression expression1, expression2;

	public OrExpression(Expression e1, Expression e2) {
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
		return this.expression1.toString() + " || " + this.expression2.toString();
	}

	@Override
	public Expression toNormalForm() {
		return new OrExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
	}

	@Override
	public BooleanExpression removeArrows() {
		return new OrExpression(((BooleanExpression) this.expression1).removeArrows(),
				((BooleanExpression) this.expression2).removeArrows());
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		return new OrExpression(((BooleanExpression) this.expression1).toSimplifiedForm(),
				((BooleanExpression) this.expression2).toSimplifiedForm());
	}

	@Override
	public BooleanExpression distributeDisjunctions() {
		while (this.expression2 instanceof ParenthesesExpression) {
			this.expression2 = ((ParenthesesExpression) this.expression2).getExpression();
		}
		while (this.expression1 instanceof ParenthesesExpression) {
			this.expression1 = ((ParenthesesExpression) this.expression1).getExpression();
		}
		/*
		 * this.expression1 = ((BooleanExpression)
		 * this.expression1).distributeDisjunctions(); this.expression2 =
		 * ((BooleanExpression) this.expression2).distributeDisjunctions(); if
		 * (this.expression2 instanceof AndExpression) { // ( a || ( b && c ) )
		 * AndExpression andExpression = (AndExpression) this.expression2;
		 * return new AndExpression( new OrExpression(this.expression1,
		 * andExpression.getExpression1()), new OrExpression(this.expression1,
		 * andExpression.getExpression2())).distributeDisjunctions(); } else {
		 * // ( ( a && b ) || c ) if (this.expression1 instanceof AndExpression)
		 * { AndExpression andExpression = (AndExpression) this.expression1;
		 * return new AndExpression( new OrExpression(this.expression2,
		 * andExpression.getExpression1()), new OrExpression(this.expression2,
		 * andExpression.getExpression2())).distributeDisjunctions(); } else {
		 * return new OrExpression(((BooleanExpression) this.expression1),
		 * ((BooleanExpression) this.expression2)); } }
		 * 
		 * while
		 * (this.expression1.getClass().toString().contains("ParenthesesExpression"
		 * )) { this.expression1 = ((ParenthesesExpression)
		 * this.expression1).getExpression(); } while
		 * (this.expression2.getClass(
		 * ).toString().contains("ParenthesesExpression")) { this.expression2 =
		 * ((ParenthesesExpression) this.expression2).getExpression(); }
		 * 
		 * this.expression1 = ((BooleanExpression)
		 * this.expression1).distributeDisjunctions(); this.expression2 =
		 * ((BooleanExpression) this.expression2).distributeDisjunctions();
		 * 
		 * if ((this.expression1 instanceof OrExpression) && (this.expression2
		 * instanceof OrExpression)) return new OrExpression(this.expression1,
		 * this.expression2); else if ((this.expression1 instanceof
		 * AndExpression) && (this.expression2 instanceof OrExpression)) { // (A
		 * ∧ B) ∨ C ∨ D <=> (A ∨ C ∨ D) ∧ (B ∨ C ∨ D) AndExpression
		 * andExpression = (AndExpression) this.expression1; OrExpression
		 * orExpression = (OrExpression) this.expression2; return new
		 * AndExpression( new OrExpression(new
		 * OrExpression(andExpression.getExpression1
		 * (),orExpression.getExpression1()),orExpression.getExpression2()), new
		 * OrExpression(new
		 * OrExpression(andExpression.getExpression2(),orExpression
		 * .getExpression1()),orExpression.getExpression2()) ); } else if
		 * ((this.expression1 instanceof OrExpression) && (this.expression2
		 * instanceof AndExpression)) { // C ∨ D ∨(A ∧ B) ∨ <=> (A ∨ C ∨ D) ∧ (B
		 * ∨ C ∨ D) AndExpression andExpression = (AndExpression)
		 * this.expression2; OrExpression orExpression = (OrExpression)
		 * this.expression1; return new AndExpression( new OrExpression(new
		 * OrExpression
		 * (andExpression.getExpression1(),orExpression.getExpression1
		 * ()),orExpression.getExpression2()), new OrExpression(new
		 * OrExpression(
		 * andExpression.getExpression2(),orExpression.getExpression1
		 * ()),orExpression.getExpression2()) ); } else if ((this.expression1
		 * instanceof AndExpression) && (this.expression2 instanceof
		 * AndExpression)) { // (A ∧ B) ∨ (C ∧ D) <=> (A ∨ C) ∧ (B ∨ C) ∧ (A ∨
		 * D) ∧ (B ∨ D) AndExpression leftAndExpression = (AndExpression)
		 * this.expression1; AndExpression rightAndExpression = (AndExpression)
		 * this.expression2; OrExpression orExpression1 = new
		 * OrExpression(leftAndExpression.getExpression1(),
		 * rightAndExpression.getExpression1()); OrExpression orExpression2 =
		 * new OrExpression(leftAndExpression.getExpression2(),
		 * rightAndExpression.getExpression1()); OrExpression orExpression3 =
		 * new OrExpression(leftAndExpression.getExpression1(),
		 * rightAndExpression.getExpression2()); OrExpression orExpression4 =
		 * new OrExpression(leftAndExpression.getExpression2(),
		 * rightAndExpression.getExpression2()); return new AndExpression(new
		 * AndExpression(new AndExpression(orExpression1, orExpression2),
		 * orExpression3), orExpression4); } else if (this.expression2
		 * instanceof AndExpression) { // Case A || ( B && C )
		 * //LOGGER.info("1 : "+this.toString()); AndExpression
		 * andExpression = (AndExpression) this.expression2; return new
		 * AndExpression( new OrExpression( this.expression1,
		 * andExpression.getExpression1()).distributeDisjunctions(), new
		 * OrExpression( this.expression1,
		 * andExpression.getExpression2()).distributeDisjunctions()); } else if
		 * (this.expression1 instanceof AndExpression) { // Case ( A && B ) || C
		 * //LOGGER.info("2 : "+this.toString()); AndExpression
		 * andExpression = (AndExpression) this.expression1; return new
		 * AndExpression( new OrExpression( this.expression2,
		 * andExpression.getExpression1()).distributeDisjunctions(), new
		 * OrExpression( this.expression2,
		 * andExpression.getExpression2()).distributeDisjunctions()); } else
		 * return new OrExpression(this.expression1, this.expression2);
		 */

		if (this.expression2.getClass().toString().contains("AndExpression")) {
			// Case A || ( B && C )
			AndExpression andExpression = (AndExpression) this.expression2;
			return new AndExpression(
					new OrExpression(this.expression1, andExpression.getExpression1()).distributeDisjunctions(),
					new OrExpression(this.expression1, andExpression.getExpression2()).distributeDisjunctions());
		} else {
			if (this.expression1.getClass().toString().contains("AndExpression")) {
				// Case ( A && B ) || C
				AndExpression andExpression = (AndExpression) this.expression1;
				return new AndExpression(
						new OrExpression(this.expression2, andExpression.getExpression1()).distributeDisjunctions(),
						new OrExpression(this.expression2, andExpression.getExpression2()).distributeDisjunctions());
			} else {
				BooleanExpression booleanExp1 = ((BooleanExpression) this.expression1).distributeDisjunctions();
				BooleanExpression booleanExp2 = ((BooleanExpression) this.expression2).distributeDisjunctions();
				if ((booleanExp1.getClass().toString().contains("AndExpression"))
						|| (booleanExp2.getClass().toString().contains("AndExpression"))
						|| (booleanExp1.getClass().toString().contains("ParenthesesExpression"))
						|| (booleanExp2.getClass().toString().contains("ParenthesesExpression"))) {
					return new OrExpression(booleanExp1, booleanExp2).distributeDisjunctions();
				} else {
					return new OrExpression(this.expression1, this.expression2);
				}
			}
		}
	}

	@Override
	public BooleanExpression distributeNegations() {
		return new OrExpression(((BooleanExpression) this.expression1).distributeNegations(),
				((BooleanExpression) this.expression2).distributeNegations());
	}
}

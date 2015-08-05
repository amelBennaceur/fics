package be.ac.info.fundp.TVLParser.SyntaxTree;

import be.ac.info.fundp.TVLParser.Util.Util;

public class ParenthesesExpression implements BooleanExpression {

	public Expression expression;

	public ParenthesesExpression(Expression expression) {
		this.expression = Util.removeParentheses(expression);
	}

	@Override
	public String toString() {
		return "( " + this.expression.toString() + " )";
	}

	@Override
	public int getType() {
		return this.expression.getType();
	}

	@Override
	public Expression toNormalForm() {
		return new ParenthesesExpression(this.expression.toNormalForm());
	}

	@Override
	public BooleanExpression removeArrows() {
		return new ParenthesesExpression(((BooleanExpression) this.expression).removeArrows());
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		return new ParenthesesExpression(((BooleanExpression) this.expression).toSimplifiedForm());
	}

	public Expression getExpression() {
		return this.expression;
	}

	@Override
	public BooleanExpression distributeNegations() {
		return new ParenthesesExpression(((BooleanExpression) this.expression).distributeNegations());
	}

	@Override
	public BooleanExpression distributeDisjunctions() {
		return ((BooleanExpression) this.expression).distributeDisjunctions();
	}
}

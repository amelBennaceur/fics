package be.ac.info.fundp.TVLParser.SyntaxTree;

public class ZeroExpression implements Expression {

	public ZeroExpression() {

	}

	@Override
	public int getType() {
		return Expression.INT;
	}

	@Override
	public String toString() {
		return "0";
	}

	@Override
	public Expression toNormalForm() {
		return new ZeroExpression();
	}
}

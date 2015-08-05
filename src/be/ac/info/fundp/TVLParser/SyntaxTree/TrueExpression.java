package be.ac.info.fundp.TVLParser.SyntaxTree;

public class TrueExpression implements BooleanExpression {

	public TrueExpression() {

	}

	@Override
	public int getType() {
		return Expression.BOOL;
	}

	public boolean check() {
		return true;
	}

	@Override
	public String toString() {
		return "true";
	}

	@Override
	public Expression toNormalForm() {
		return new TrueExpression();
	}

	@Override
	public BooleanExpression removeArrows() {
		return this;
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		return this;
	}

	@Override
	public BooleanExpression distributeDisjunctions() {
		return this;
	}

	@Override
	public BooleanExpression distributeNegations() {
		return this;
	}

}

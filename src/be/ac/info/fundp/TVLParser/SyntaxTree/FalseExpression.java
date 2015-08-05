package be.ac.info.fundp.TVLParser.SyntaxTree;

public class FalseExpression implements BooleanExpression {

	public FalseExpression() {

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
		return "false";
	}

	@Override
	public Expression toNormalForm() {
		return new FalseExpression();
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

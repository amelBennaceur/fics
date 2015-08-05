package be.ac.info.fundp.TVLParser.SyntaxTree;

public class RealExpression implements Expression {

	float value;

	public RealExpression(String value) {
		this.value = Float.parseFloat(value);
	}

	public float getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		Float f = new Float(this.value);
		return f.toString();
	}

	@Override
	public int getType() {
		return Expression.REAL;
	}

	public boolean check() {
		return true;
	}

	@Override
	public Expression toNormalForm() {
		return new RealExpression(String.valueOf(this.value));
	}

}

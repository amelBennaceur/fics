package be.ac.info.fundp.TVLParser.SyntaxTree;

public class IntExpression implements Expression {

	int value;

	public IntExpression(String value) {
		this.value = Integer.parseInt(value);
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		Integer integer = new Integer(this.value);
		return integer.toString();
	}

	@Override
	public int getType() {
		return Expression.INT;
	}

	public boolean check() {
		return true;
	}

	@Override
	public Expression toNormalForm() {
		return new IntExpression(String.valueOf(this.value));
	}

}

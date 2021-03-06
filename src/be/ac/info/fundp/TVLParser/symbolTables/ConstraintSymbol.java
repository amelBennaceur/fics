package be.ac.info.fundp.TVLParser.symbolTables;

import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;

public class ConstraintSymbol {

	Expression expression;
	boolean ifIn;
	boolean ifOut;

	public ConstraintSymbol(boolean ifIn, boolean ifOut, Expression expression) {
		this.ifIn = ifIn;
		this.ifOut = ifOut;
		this.expression = expression;
	}

	@Override
	public String toString() {
		if (ifIn && ifOut) {
			return this.expression.toString();
		} else {
			if (ifIn) {
				return "ifin: " + this.expression.toString();
			} else {
				return "ifout: " + this.expression.toString();
			}
		}
	}

	public boolean isIfIn() {
		return this.ifIn;
	}

	public boolean isIfOut() {
		return this.ifOut;
	}

	public Expression getExpression() {
		return this.expression;
	}
}

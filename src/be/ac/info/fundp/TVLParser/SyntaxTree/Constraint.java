package be.ac.info.fundp.TVLParser.SyntaxTree;

public class Constraint implements FeatureBodyItem {

	Expression expression;
	boolean ifIn;
	boolean ifOut;

	public Constraint(boolean ifIn, Expression expression) {
		if (ifIn) {
			this.ifIn = true;
			this.ifOut = false;
		} else {
			this.ifOut = true;
			this.ifIn = false;
		}
		this.expression = expression;
	}

	@Override
	public String toString() {
		if (ifIn && ifOut) {
			return this.expression.toString() + ";";
		} else {
			if (ifIn) {
				return "ifin: " + this.expression.toString() + ";";
			} else {
				return "ifout: " + this.expression.toString() + ";";
			}
		}
	}

	public Constraint(Expression expression) {
		this.ifIn = true;
		this.ifOut = true;
		this.expression = expression;
	}

	/**
	 * @return the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * @return the ifIn
	 */
	public boolean isIfIn() {
		return ifIn;
	}

	public boolean isIfOut() {
		return this.ifOut;
	}

	@Override
	public boolean isAData() {
		return false;
	}

	@Override
	public boolean isAnAttribute() {
		return false;
	}

	@Override
	public boolean isAConstraint() {
		return true;
	}

	@Override
	public boolean isAFeatureGroup() {
		return false;
	}

}

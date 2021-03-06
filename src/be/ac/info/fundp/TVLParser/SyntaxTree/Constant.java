package be.ac.info.fundp.TVLParser.SyntaxTree;

public class Constant implements ModelItem {

	String ID, value;
	int type;

	public Constant(int type, String ID, String value) {
		this.type = type;
		this.ID = ID;
		this.value = value;
	}

	@Override
	public String toString() {
		return this.type + " " + this.ID + " " + this.value + ";\n";
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the id
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	// Utilisé lors de la construction du modèle
	@Override
	public boolean isAFeature() {
		return false;
	}

	@Override
	public boolean isAType() {
		return false;
	}

	@Override
	public boolean isAconstant() {
		return true;
	}
}

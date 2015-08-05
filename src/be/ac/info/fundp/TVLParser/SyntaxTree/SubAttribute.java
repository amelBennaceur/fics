package be.ac.info.fundp.TVLParser.SyntaxTree;

public class SubAttribute extends Attribute {

	String ID;
	int type;
	AttributeBody attributeBody;

	public SubAttribute(String ID, AttributeBody attributeBody) {
		this.ID = ID;
		this.attributeBody = attributeBody;
		this.type = Expression.STRUCT_FIELD;
	}

	public String toString(String whiteSpace) {
		return whiteSpace + this.ID + " " + this.attributeBody.toString() + ";\n";
	}

	/**
	 * @return the iD
	 */
	@Override
	public String getID() {
		return ID;
	}

	/**
	 * @return the type
	 */
	@Override
	public int getType() {
		return type;
	}

	/**
	 * @return the attributeBody
	 */
	@Override
	public AttributeBody getAttributeBody() {
		return attributeBody;
	}

	@Override
	public boolean hasAnAttributeBody() {
		if (this.attributeBody != null)
			return true;
		else
			return false;
	}
}

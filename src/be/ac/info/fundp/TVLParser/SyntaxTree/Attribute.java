package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;

public class Attribute implements FeatureBodyItem {

	String userType;
	int type;
	String ID;
	Vector<SubAttribute> subAttributes;

	@Override
	public String toString() {
		String text = this.type + " " + this.ID + " {\n";
		int i = 0;
		while (i <= this.subAttributes.size() - 1) {
			text = text.concat("    " + this.subAttributes.get(i).toString() + "\n");
			i++;
		}
		return text = text.concat("}");
	}

	public String getUserType() {
		return userType;
	}

	public int getType() {
		return type;
	}

	public String getID() {
		return ID;
	}

	public Vector<SubAttribute> getSubAttributes() {
		return subAttributes;
	}

	public Attribute() {

	}

	public Attribute(String type, String ID, StructAttributeBody structAttributeBody) {
		this.userType = type;
		this.type = Expression.STRUCT;
		this.ID = ID;
		this.subAttributes = structAttributeBody.getSubAttributes();
	}

	public boolean isABaseAttribute() {
		return false;
	}

	// Utilisé pour la construction d'une feature
	@Override
	public boolean isAData() {
		return false;
	}

	@Override
	public boolean isAnAttribute() {
		return true;
	}

	@Override
	public boolean isAConstraint() {
		return false;
	}

	@Override
	public boolean isAFeatureGroup() {
		return false;
	}

	public boolean hasAnAttributeBody() {
		return false;
	}

	public AttributeBody getAttributeBody() {
		return null;
	}

}

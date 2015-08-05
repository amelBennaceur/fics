package be.ac.info.fundp.TVLParser.SyntaxTree;

import be.ac.info.fundp.TVLParser.Util.Util;

public class BaseAttribute extends Attribute {
	int type;
	String ID;
	String userType = null;
	AttributeBody attributeBody;

	public BaseAttribute(int type, String ID, AttributeBody attributeBody) {
		this.type = type;
		this.ID = ID;
		this.attributeBody = attributeBody;
	}

	public BaseAttribute(String type, String ID, AttributeBody attributeBody) {
		this.type = Expression.USER_CREATED;
		this.ID = ID;
		this.userType = type;
		this.attributeBody = attributeBody;
	}

	@Override
	public String toString() {
		if (this.type == Expression.USER_CREATED) {
			if (this.attributeBody != null) {
				return this.userType + " " + this.ID + " " + this.attributeBody.toString() + ";";
			} else {
				return this.userType + " " + this.ID + ";";
			}
		} else {
			if (this.attributeBody != null) {
				return Util.toStringExpressionType(type) + " " + this.ID + " " + this.attributeBody.toString() + ";";
			} else {
				return Util.toStringExpressionType(type) + " " + this.ID + ";";
			}
		}
	}

	@Override
	public boolean isABaseAttribute() {
		return true;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getUserType() {
		return userType;
	}

	@Override
	public AttributeBody getAttributeBody() {
		return attributeBody;
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

	@Override
	public boolean hasAnAttributeBody() {
		if (this.attributeBody != null)
			return true;
		else
			return false;
	}

}

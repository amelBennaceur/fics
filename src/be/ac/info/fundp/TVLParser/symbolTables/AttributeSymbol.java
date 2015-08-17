package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Parser.lexer;
import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.SyntaxTree.SetExpression;

public class AttributeSymbol extends Symbol implements TypeSymbol, Cloneable {

	private final static Logger LOGGER = Logger.getLogger(AttributeSymbol.class.getName());
	
	int type, trueType;
	String id, userType;

	Map<String, AttributeSymbol> enumValueToBooleanAttribute; // Only used with
																// enum
																// attributes
																// for the
																// boolean form

	String nonNormalizedID; // Only used during the normalization process

	Expression ifInExpression, ifOutExpression;
	SetExpression ifInSetExpression, ifOutSetExpression;

	SetExpressionSymbol setExpressionSymbol;
	Expression expression;

	public Map<String, AttributeSymbol> getBooleanAttributes() {
		return this.enumValueToBooleanAttribute;
	}

	public void initializeEnumValueToBooleanAttribute() {
		this.enumValueToBooleanAttribute = new HashMap<String, AttributeSymbol>();
	}

	public int getNumberOfBooleanAttributes() {
		return this.enumValueToBooleanAttribute.size();
	}

	public void addBooleanAttribute(String enumValue, AttributeSymbol booleanAttribute) {
		this.enumValueToBooleanAttribute.put(enumValue, booleanAttribute);
	}

	public AttributeSymbol getBooleanAttribute(String enumValue) {
		return this.enumValueToBooleanAttribute.get(enumValue);
	}

	@Override
	public AttributeSymbol clone() {
		AttributeSymbol clonedAttributeSymbol = new AttributeSymbol(this.userType, this.trueType, this.id);
		clonedAttributeSymbol.nonNormalizedID = this.nonNormalizedID;
		clonedAttributeSymbol.type = this.type;
		clonedAttributeSymbol.ifInExpression = this.ifInExpression;
		clonedAttributeSymbol.ifOutExpression = this.ifOutExpression;
		clonedAttributeSymbol.ifInSetExpression = this.ifInSetExpression;
		clonedAttributeSymbol.ifOutSetExpression = this.ifOutSetExpression;
		clonedAttributeSymbol.expression = this.expression;
		if (this.hasASetExpressionSymbol()) {
			clonedAttributeSymbol.setExpressionSymbol = this.setExpressionSymbol.copy();
		} else {
			clonedAttributeSymbol.setExpressionSymbol = null;
		}
		return clonedAttributeSymbol;
	}

	// Utilisé pour les champs des records dans les types
	public AttributeSymbol(String userType, int trueType, String id) {
		this.id = id;
		this.nonNormalizedID = id;
		this.type = Expression.USER_CREATED;
		this.userType = userType;
		this.ifInExpression = null;
		this.ifOutExpression = null;
		this.ifInSetExpression = null;
		this.ifOutSetExpression = null;
		this.setExpressionSymbol = null;
		this.expression = null;
		this.trueType = trueType;
	}

	public AttributeSymbol(String userType, int trueType, String id, SetExpressionSymbol setExpressionSymbol) {
		this.id = id;
		this.nonNormalizedID = id;
		this.type = Expression.USER_CREATED;
		this.userType = userType;
		this.ifInExpression = null;
		this.ifOutExpression = null;
		this.ifInSetExpression = null;
		this.ifOutSetExpression = null;
		this.setExpressionSymbol = setExpressionSymbol;
		this.expression = null;
		this.trueType = trueType;
	}

	public AttributeSymbol(int Type, String ID, SetExpressionSymbol setExpressionSymbol) {
		this.id = ID;
		this.nonNormalizedID = ID;
		this.type = Type;
		this.setExpressionSymbol = setExpressionSymbol;
		this.userType = null;
		this.ifInExpression = null;
		this.ifOutExpression = null;
		this.ifInSetExpression = null;
		this.ifOutSetExpression = null;
		this.expression = null;
		this.trueType = type;
	}

	public AttributeSymbol(int type, String ID) {
		this.id = ID;
		this.nonNormalizedID = ID;
		this.type = type;
		this.setExpressionSymbol = null;
		this.userType = null;
		this.ifInExpression = null;
		this.ifOutExpression = null;
		this.ifInSetExpression = null;
		this.ifOutSetExpression = null;
		this.expression = null;
		this.trueType = type;
	}

	public String getNonNormalizedID() {
		return this.nonNormalizedID;
	}

	/**
	 * @return the ifInSetExpression
	 */
	public SetExpression getIfInSetExpression() {
		return ifInSetExpression;
	}

	/**
	 * @param ifInSetExpression
	 *            the ifInSetExpression to set
	 */
	public void setIfInSetExpression(SetExpression ifInSetExpression) {
		this.ifInSetExpression = ifInSetExpression;
	}

	/**
	 * @return the ifOutSetExpression
	 */
	public SetExpression getIfOutSetExpression() {
		return ifOutSetExpression;
	}

	/**
	 * @param ifOutSetExpression
	 *            the ifOutSetExpression to set
	 */
	public void setIfOutSetExpression(SetExpression ifOutSetExpression) {
		this.ifOutSetExpression = ifOutSetExpression;
	}

	public int getTrueType() {
		return trueType;
	}

	public String getId() {
		return id;
	}

	public void setID(String newID) {
		this.id = newID;
	}

	// Utilisé pours les attibuts des features
	/*
	 * public AttributeSymbol(String type, String id, SetExpressionSymbol
	 * setExpressionSymbol) { this.id = id; this.type = type;
	 * this.ifInExpression = null; this.ifOutExpression = null;
	 * this.setExpressionSymbol = setExpressionSymbol; }
	 */
	public AttributeSymbol() {
	}

	@Override
	public boolean isARecordSymbol() {
		return false;
	}

	/**
	 * @return the ifInExpression
	 */
	public Expression getIfInExpression() {
		return ifInExpression;
	}

	/**
	 * @param ifInExpression
	 *            the ifInExpression to set
	 */
	public void setIfInExpression(Expression ifInExpression) {
		this.ifInExpression = ifInExpression;
	}

	/**
	 * @return the ifOutExpression
	 */
	public Expression getIfOutExpression() {
		return ifOutExpression;
	}

	/**
	 * @param ifOutExpression
	 *            the ifOutExpression to set
	 */
	public void setIfOutExpression(Expression ifOutExpression) {
		this.ifOutExpression = ifOutExpression;
	}

	/**
	 * @return the setExpressionSymbol
	 */
	@Override
	public SetExpressionSymbol getSetExpressionSymbol() {
		return setExpressionSymbol;
	}

	/**
	 * @param setExpressionSymbol
	 *            the setExpressionSymbol to set
	 */
	public void setSetExpressionSymbol(SetExpressionSymbol setExpressionSymbol) {
		this.setExpressionSymbol = setExpressionSymbol;
	}

	/**
	 * @return the id
	 */
	@Override
	public String getID() {
		return id;
	}

	/**
	 * @return the type
	 */
	@Override
	public int getType() {
		return type;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @return the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public void printAttribute(String espace) {
		String s = "";
		if (this.type == Expression.INT) {
			if (this.hasASetExpressionSymbol()) {
				s = espace + "  |      int " + this.id + " in " + this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = espace + "  |      int " + this.id + " is " + this.expression;
				} else {
					s = espace + "  |      int " + this.id;
				}
			}
		}
		if (this.type == Expression.REAL) {
			if (this.hasASetExpressionSymbol()) {
				s = espace + "  |      real " + this.id + " in " + this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = espace + "  |      real " + this.id + " is " + this.expression;
				} else {
					s = espace + "  |      real " + this.id;
				}
			}
		}
		if (this.type == Expression.BOOL) {
			s = espace + "  |      bool " + this.id;
		}
		if (this.type == Expression.ENUM) {
			if (this.hasASetExpressionSymbol()) {
				s = espace + "  |      enum " + this.id + " in " + this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = espace + "  |      enum " + this.id + " is " + this.expression;
				} else {
					s = espace + "  |      enum " + this.id;
				}
			}
		}
		if (this.type == Expression.USER_CREATED) {
			String trueType = "default";
			switch (this.trueType) {
			case Expression.INT:
				trueType = "int";
				break;
			case Expression.REAL:
				trueType = "real";
				break;
			case Expression.ENUM:
				trueType = "enum";
				break;
			case Expression.STRUCT:
				trueType = "struct";
				break;
			case Expression.BOOL:
				trueType = "bool";
				break;
			}
			if (this.hasASetExpressionSymbol()) {
				s = espace + "  |      " + this.userType + " " + trueType + " " + this.id + " in "
						+ this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = espace + "  |      " + this.userType + " " + trueType + " " + this.id + " is "
							+ this.expression;
				} else {
					s = espace + "  |      " + this.userType + " " + trueType + " " + this.id;
				}
			}
		}
		if (this.ifInExpression != null) {
			s = s.concat(", ifin: " + this.ifInExpression);
		}
		if (this.ifOutExpression != null) {
			s = s.concat(", ifout: " + this.ifOutExpression);
		}
		LOGGER.info(s);
	}

	@Override
	public String toString() {
		String s = "";
		if (this.type == Expression.INT) {
			if (this.hasASetExpressionSymbol()) {
				s = "int " + this.id + " in " + this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = "int " + this.id + " is " + this.expression;
				} else {
					s = "int " + this.id;
				}
			}
		}
		if (this.type == Expression.REAL) {
			if (this.hasASetExpressionSymbol()) {
				s = "real " + this.id + " in " + this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = "real " + this.id + " is " + this.expression;
				} else {
					s = "real " + this.id;
				}
			}
		}
		if (this.type == Expression.BOOL) {
			s = "bool " + this.id;
		}
		if (this.type == Expression.ENUM) {
			if (this.hasASetExpressionSymbol()) {
				s = "enum " + this.id + " in " + this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = "enum " + this.id + " is " + this.expression;
				} else {
					s = "enum " + this.id;
				}
			}
		}
		if (this.type == Expression.USER_CREATED) {
			String trueType = "default";
			switch (this.trueType) {
			case Expression.INT:
				trueType = "int";
				break;
			case Expression.REAL:
				trueType = "real";
				break;
			case Expression.ENUM:
				trueType = "enum";
				break;
			case Expression.STRUCT:
				trueType = "struct";
				break;
			case Expression.BOOL:
				trueType = "bool";
				break;
			}
			if (this.hasASetExpressionSymbol()) {
				s = this.userType + " " + trueType + " " + this.id + " in " + this.setExpressionSymbol.toString();
			} else {
				if (this.expression != null) {
					s = this.userType + " " + trueType + " " + this.id + " is " + this.expression;
				} else {
					s = this.userType + " " + trueType + " " + this.id;
				}
			}
		}
		if (this.ifInExpression != null) {
			s = s.concat(", ifin: " + this.ifInExpression);
		}
		if (this.ifOutExpression != null) {
			s = s.concat(", ifout: " + this.ifOutExpression);
		}
		return s;
	}

	public void printAttribute() {
		LOGGER.info("      " + this.type + " " + this.id);
	}

	@Override
	public void print() {
		String typeName = "";
		switch (this.type) {
		case Expression.INT:
			typeName = "int";
			break;
		case Expression.REAL:
			typeName = "real";
			break;
		case Expression.BOOL:
			typeName = "bool";
			break;
		case Expression.ENUM:
			typeName = "enum";
			break;
		}
		if (!(this.setExpressionSymbol == null))
			LOGGER.info("  " + id + " " + typeName + " in " + this.setExpressionSymbol.toString());
		else
			LOGGER.info("  " + id + " " + typeName);
	}

	@Override
	public boolean hasASetExpressionSymbol() {
		if (this.setExpressionSymbol == null)
			return false;
		else
			return true;
	}

	public boolean hasIfInDeclaration() {
		if ((this.ifInExpression != null) || (this.ifInSetExpression != null))
			return true;
		else
			return false;
	}

	public boolean hasIfOutDeclaration() {
		if ((this.ifOutExpression != null) || (this.ifOutSetExpression != null))
			return true;
		else
			return false;
	}

	public boolean hasExpression() {
		if (this.expression != null)
			return true;
		else
			return false;
	}

}

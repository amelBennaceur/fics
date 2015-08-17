package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;

public class ConstantSymbol {

	private final static Logger LOGGER = Logger.getLogger(ConstantSymbol.class.getName());
	
	String id, value;
	int type;

	public ConstantSymbol(int type, String id, String value) {
		this.type = type;
		this.id = id;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public String getID() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void printConstant() {
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
		}
		LOGGER.info("  " + id + "  " + typeName + "  " + value);
	}

}

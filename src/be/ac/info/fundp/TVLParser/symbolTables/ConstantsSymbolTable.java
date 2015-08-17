package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.SyntaxTree.Constant;
import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.Util.Util;

public class ConstantsSymbolTable {
	
	private final static Logger LOGGER = Logger.getLogger(ConstantsSymbolTable.class.getName());

	boolean isEmpty = true;

	Map<String, ConstantSymbol> table;

	TypesSymbolTable typesSymbolTable;

	public ConstantsSymbolTable(Vector<Constant> constants, TypesSymbolTable typesSymbolTable) {
		this.table = new HashMap<String, ConstantSymbol>();
		this.typesSymbolTable = typesSymbolTable;
		this.constructTable(constants);
	}

	private void constructTable(Vector<Constant> constants) {
		if (constants != null) {
			this.isEmpty = false;
			int i = 0;
			while (i <= constants.size() - 1) {
				Util.checkUseOfReservedWord(constants.get(i).getID());
				if (Util.isAFeatureID(constants.get(i).getID()))
					LOGGER.info("Type error : the constant ID " + constants.get(i).getID()
							+ " begin by an upper letter, a constant ID must begin by a lower letter");
				if (this.typesSymbolTable.containsTypes(constants.get(i).getID()))
					LOGGER.info("Type error : a type and a constant have a identical ID ( "
							+ constants.get(i).getID() + " ).");
				if (this.table.containsKey(constants.get(i).getID()))
					LOGGER.info("Type error : it exists many constants with an identical ID ( "
							+ constants.get(i).getID() + " ).");
				Constant constant = constants.get(i);
				if (constant.getType() == Expression.INT) {
					Integer.parseInt(constant.getValue());
					this.table.put(constant.getID(),
							new ConstantSymbol(constant.getType(), constant.getID(), constant.getValue()));
				} else {
					if (constant.getType() == Expression.REAL) {
						Float.parseFloat(constant.getValue());
						this.table.put(constant.getID(), new ConstantSymbol(constant.getType(), constant.getID(),
								constant.getValue()));
					} else {
						Boolean.parseBoolean(constant.getValue());
						this.table.put(constant.getID(), new ConstantSymbol(constant.getType(), constant.getID(),
								constant.getValue()));
					}
				}
				i++;
			}
		}
	}

	public boolean containsConstant(String constantID) {
		return this.table.containsKey(constantID);
	}

	public int getConstantType(String id) {
		return this.table.get(id).getType();
	}

	public void printTable() {
		LOGGER.fine("-----------------------------------------------------------------------------");
		LOGGER.fine("--------------------------- Constant Symbol Table ---------------------------");
		LOGGER.fine("-----------------------------------------------------------------------------");
		if (this.isEmpty) {
			LOGGER.fine("                                    Empty                                    ");
		} else {
			int i = 0;
			Object[] keys = table.keySet().toArray();
			while (i <= table.size() - 1) {
				table.get(keys[i].toString()).printConstant();
				LOGGER.info("-----------------------------------------------------------------------------");
				i++;
			}
		}
		LOGGER.fine("");
		LOGGER.fine("");
	}

	public boolean isEmpty() {
		return this.isEmpty;
	}

	public Map<String, ConstantSymbol> getTable() {
		return this.table;
	}
}

package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.SyntaxTree.Record;
import be.ac.info.fundp.TVLParser.SyntaxTree.RecordField;
import be.ac.info.fundp.TVLParser.SyntaxTree.SimpleType;
import be.ac.info.fundp.TVLParser.SyntaxTree.Type;
import be.ac.info.fundp.TVLParser.Util.Util;

public class TypesSymbolTable {

	private final static Logger LOGGER = Logger.getLogger(TypesSymbolTable.class.getName());
	
	Map<String, TypeSymbol> table;

	Vector<String> enumValues;

	boolean isEmpty = true;

	public TypesSymbolTable(Vector<Type> types) {
		this.table = new HashMap<String, TypeSymbol>();
		this.constructTable(types);
	}

	private void constructTable(Vector<Type> types) {
		if (types != null) {
			this.isEmpty = false;
			int i = types.size() - 1;
			while (i >= 0) {
				Util.checkUseOfReservedWord(types.get(i).getID());
				if (Util.isAFeatureID(types.get(i).getID()))
					LOGGER.info("Type error : the type ID " + types.get(i).getID()
							+ " begin by an upper letter, a type ID must begin by a lower letter");
				// Vérifie que la table ne contient pas déjà cette ID
				if (this.table.containsKey(types.get(i).getID()))
					LOGGER.info("Type error : it exists many types with an identical ID ( "
							+ types.get(i).getID() + " ).");
				// Vérifie quel est la structure du type (simple type ou record)
				if (types.get(i).isARecord()) {
					Record record = (Record) types.get(i);
					Map<String, AttributeSymbol> recordFields = new HashMap<String, AttributeSymbol>();
					int j = 0;
					while (j <= record.getRecordFields().size() - 1) {
						if (recordFields.containsKey(record.getRecordFields().get(j)))
							LOGGER.info("Type error : the stuct type " + record.getID()
									+ " has many sub-attributes with an identical ID ( "
									+ record.getRecordFields().get(j).getID() + " ).");
						RecordField recordField = record.getRecordFields().get(j);
						if (recordField.hasASetExpression()) {
							if (recordField.getSetExpression().hasAnExpressionList()) {
								EnumSetExpressionSymbol enumSetExpressionSymbol = new EnumSetExpressionSymbol(
										recordField.getType(), recordField.getSetExpression().getExpressionList());
								recordFields.put(recordField.getID(), new AttributeSymbol(recordField.getType(),
										recordField.getID(), enumSetExpressionSymbol));
								if (recordField.getType() == Expression.ENUM) {
									if (this.enumValues == null)
										this.enumValues = new Vector<String>();
									int k = 0;
									while (k <= enumSetExpressionSymbol.getContainedValues().size() - 1) {
										if (!(this.enumValues.contains(enumSetExpressionSymbol.getContainedValues()
												.get(k).toString())))
											this.enumValues.add(enumSetExpressionSymbol.getContainedValues().get(k)
													.toString());
										k++;
									}
								}
							} else {
								recordFields.put(recordField.getID(), new AttributeSymbol(recordField.getType(),
										recordField.getID(), new IntervalSetExpressionSymbol(recordField.getType(),
												recordField.getSetExpression().getMinExpression(), recordField
														.getSetExpression().getMaxExpression(), recordField.getID())));
							}
						} else {
							if (recordField.getType() == Expression.USER_CREATED) {
								if (!(this.table.containsKey(recordField.getUserType()))) {
									LOGGER.info("Type error : the type " + recordField.getUserType()
											+ " of the sub-attribute " + recordField.getID() + " of the struct type "
											+ record.getID() + " hasn't been defined.");
								} else {
									if (this.table.get(recordField.getUserType()).isARecordSymbol())
										System.out
												.println("Type error : the sub-attribute "
														+ recordField.getID()
														+ " of the struct type "
														+ record.getID()
														+ " is not valid. You cannot use a struct type inside another struct type.");
								}
								if (this.table.get(recordField.getUserType()).hasASetExpressionSymbol()) {
									SetExpressionSymbol setExpressionSymbol = this.table.get(recordField.getUserType())
											.getSetExpressionSymbol();
									setExpressionSymbol.setAttributeID(recordField.getID());
									recordFields.put(recordField.getID(), new AttributeSymbol(
											recordField.getUserType(), this.table.get(recordField.getUserType())
													.getType(), recordField.getID(), setExpressionSymbol));
									if (this.table.get(recordField.getUserType()).getType() == Expression.ENUM) {
										if (setExpressionSymbol.isAnEnumSetExpressionSymbol()) {
											if (this.enumValues == null)
												this.enumValues = new Vector<String>();
											int k = 0;
											EnumSetExpressionSymbol enumSetExpressionSymbol = (EnumSetExpressionSymbol) setExpressionSymbol;
											while (k <= enumSetExpressionSymbol.getContainedValues().size() - 1) {
												if (!(this.enumValues.contains(enumSetExpressionSymbol
														.getContainedValues().get(k).toString())))
													this.enumValues.add(enumSetExpressionSymbol.getContainedValues()
															.get(k).toString());
												k++;
											}
										}
									}
								} else {
									recordFields.put(recordField.getID(), new AttributeSymbol(
											recordField.getUserType(), this.table.get(recordField.getUserType())
													.getType(), recordField.getID()));
								}
							} else {
								recordFields.put(recordField.getID(), new AttributeSymbol(recordField.getType(),
										recordField.getID()));
							}
						}
						j++;
					}
					RecordSymbol recordSymbol = new RecordSymbol(Expression.STRUCT, record.getID(), recordFields);
					this.table.put(record.getID(), recordSymbol);
				} else {
					SimpleType simpleType = (SimpleType) types.get(i);
					if (simpleType.hasASetExpression()) {
						if (simpleType.getSetExpression().hasAnExpressionList()) {
							EnumSetExpressionSymbol enumSetExpressionSymbol = new EnumSetExpressionSymbol(
									simpleType.getType(), simpleType.getSetExpression().getExpressionList());
							enumSetExpressionSymbol.setAttributeID(simpleType.getID());
							;
							this.table.put(simpleType.getID(),
									new AttributeSymbol(simpleType.getType(), simpleType.getID(),
											enumSetExpressionSymbol));
							if (simpleType.getType() == Expression.ENUM) {
								if (this.enumValues == null)
									this.enumValues = new Vector<String>();
								int k = 0;
								while (k <= enumSetExpressionSymbol.getContainedValues().size() - 1) {
									if (!(this.enumValues.contains(enumSetExpressionSymbol.getContainedValues().get(k)
											.toString())))
										this.enumValues.add(enumSetExpressionSymbol.getContainedValues().get(k)
												.toString());
									k++;
								}
							}
						} else {
							this.table.put(simpleType.getID(),
									new AttributeSymbol(simpleType.getType(), simpleType.getID(),
											new IntervalSetExpressionSymbol(simpleType.getType(), simpleType
													.getSetExpression().getMinExpression(), simpleType
													.getSetExpression().getMaxExpression(), simpleType.getID())));
						}
					} else {
						this.table.put(simpleType.getID(),
								new AttributeSymbol(simpleType.getType(), simpleType.getID()));
					}
				}
				i--;
			}
		}
	}

	public Map<String, TypeSymbol> getTable() {
		return table;
	}

	public Vector<String> getEnumValues() {
		return enumValues;
	}

	public boolean containsTypes(String typeID) {
		return this.table.containsKey(typeID);
	}

	public TypeSymbol getTypeSymbol(String typeSymbolID) {
		return this.table.get(typeSymbolID);
	}

	public void printTable() {
		LOGGER.fine("-----------------------------------------------------------------------------");
		LOGGER.fine("----------------------------- Type Symbol Table -----------------------------");
		LOGGER.fine("-----------------------------------------------------------------------------");
		if (this.isEmpty) {
			LOGGER.fine("\n                                    Empty                                    ");
		} else {
			int i = 0;
			Object[] keys = table.keySet().toArray();
			while (i <= table.size() - 1) {
				table.get(keys[i].toString()).print();
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

}

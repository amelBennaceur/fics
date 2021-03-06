package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Util.Util;
import be.ac.info.fundp.TVLParser.symbolTables.AttributeSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.EnumSetExpressionSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.RecordSymbol;

public class EqualsExpression implements BooleanExpression {

	private final static Logger LOGGER = Logger.getLogger(EqualsExpression.class.getName());
	
	Expression expression1, expression2;
	int expression1Type, expression2Type;

	public EqualsExpression(Expression e1, Expression e2) {
		this.expression1 = Util.removeParentheses(e1);
		this.expression2 = Util.removeParentheses(e2);
	}

	/**
	 * @return the expression1
	 */
	public Expression getExpression1() {
		return expression1;
	}

	/**
	 * @return the expression2
	 */
	public Expression getExpression2() {
		return expression2;
	}

	public void checkNumericalComparison() throws NumberFormatException {
		if ((this.expression1 instanceof LongIDExpression) && (this.expression2 instanceof LongIDExpression)) {
			LongIDExpression longIDExpression1 = (LongIDExpression) this.expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) this.expression2;
			AttributeSymbol numericalAttribute1 = (AttributeSymbol) longIDExpression1.getSymbol();
			AttributeSymbol numericalAttribute2 = (AttributeSymbol) longIDExpression2.getSymbol();
			if ((numericalAttribute1.hasASetExpressionSymbol()) && (numericalAttribute2.hasASetExpressionSymbol())) {
				numericalAttribute1.getSetExpressionSymbol().print();
				numericalAttribute2.getSetExpressionSymbol().print();
				if (!((numericalAttribute1.getSetExpressionSymbol().containsSetExpressionSymbol(numericalAttribute2
						.getSetExpressionSymbol())) && (numericalAttribute2.getSetExpressionSymbol()
						.containsSetExpressionSymbol(numericalAttribute1.getSetExpressionSymbol())))) {
					LOGGER.info("Type error : the expression " + this.toString() + " is not valid. "
							+ numericalAttribute1.getID() + " and " + numericalAttribute2.getID()
							+ " haven't the same values domain");
				}
			}
		} else if (this.expression1 instanceof LongIDExpression) {
			LongIDExpression longIDExpression1 = (LongIDExpression) this.expression1;
			AttributeSymbol numericalAttribute1 = (AttributeSymbol) longIDExpression1.getSymbol();
			if (numericalAttribute1.hasASetExpressionSymbol()) {
				if (!(numericalAttribute1.getSetExpressionSymbol().containsExpression(expression2))) {
					LOGGER.info("Type error : the expression " + this.toString()
							+ " is not valid. The values domain of " + numericalAttribute1.getID()
							+ " doesn't include " + this.expression2.toString());
				}
			}
		} else if (this.expression2 instanceof LongIDExpression) {
			LongIDExpression longIDExpression2 = (LongIDExpression) this.expression2;
			AttributeSymbol numericalAttribute2 = (AttributeSymbol) longIDExpression2.getSymbol();
			if (numericalAttribute2.hasASetExpressionSymbol()) {
				if (!(numericalAttribute2.getSetExpressionSymbol().containsExpression(expression1))) {
					LOGGER.info("Type error : the expression " + this.toString()
							+ " is not valid. The values domain of " + numericalAttribute2.getID()
							+ " doesn't include " + this.expression1.toString());
				}
			}
		} else
			LOGGER.info("Type error : the expression " + this.toString()
					+ " is not valid, you cannot compare two numerical values.");
	}

	@Override
	public int getType() {
		this.expression1Type = this.expression1.getType();
		this.expression2Type = this.expression2.getType();
		if (this.expression1Type == Expression.INT) {
			if (this.expression2Type == Expression.INT) {
				this.checkNumericalComparison();
				return Expression.BOOL;
			} else {
				if (this.getExpression2().getType() == Expression.REAL) {
					this.checkNumericalComparison();
					return Expression.BOOL;
				} else {
					LOGGER.info("Type error : the expression " + this.toString() + " is invalid. The type "
							+ Util.toStringExpressionType(this.expression1Type) + " of the left paramater ( "
							+ this.expression1.toString() + " ) is different from the type "
							+ Util.toStringExpressionType(this.expression2Type) + " of the right parameter ( "
							+ this.expression2.toString() + " ).");
				}
			}
		} else {
			if (this.expression1Type == Expression.REAL) {
				if (this.expression2Type == Expression.INT) {
					this.checkNumericalComparison();
					return Expression.BOOL;
				} else {
					if (this.getExpression2().getType() == Expression.REAL) {
						this.checkNumericalComparison();
						return Expression.BOOL;
					} else {
						LOGGER.info("Type error : the expression " + this.toString() + " is invalid. The type "
								+ Util.toStringExpressionType(this.expression1Type) + " of the left paramater ( "
								+ this.expression1.toString() + " ) is different from the type "
								+ Util.toStringExpressionType(this.expression2Type) + " of the right parameter ( "
								+ this.expression2.toString() + " ).");
					}
				}
			} else {
				if ((this.expression1Type == Expression.BOOL) && (this.expression2Type == Expression.BOOL)) {
					return Expression.BOOL;
				} else {

					if ((this.getExpression1().getType() == Expression.ENUM)
							&& (this.getExpression2().getType() == Expression.ENUM)) {
						LongIDExpression longIDExpression1 = (LongIDExpression) this.expression1;
						LongIDExpression longIDExpression2 = (LongIDExpression) this.expression2;
						if (longIDExpression1.getSymbol() != null) {
							if (longIDExpression2.getSymbol() != null) {
								AttributeSymbol enumAttribute1 = (AttributeSymbol) longIDExpression1.getSymbol();
								AttributeSymbol enumAttribute2 = (AttributeSymbol) longIDExpression2.getSymbol();
								if ((enumAttribute1.getType() == Expression.USER_CREATED)
										&& (enumAttribute2.getType() == Expression.USER_CREATED)) {
									if (enumAttribute1.getUserType().equals(enumAttribute2.getUserType()))
										return Expression.BOOL;
									else {
										if ((((EnumSetExpressionSymbol) enumAttribute1.getSetExpressionSymbol())
												.hasTheSameEnumValuesDomain(((EnumSetExpressionSymbol) enumAttribute2
														.getSetExpressionSymbol()).getContainedValues()))) {
											return Expression.BOOL;
										} else {
											System.out
													.println("Type error : the expression "
															+ this.toString()
															+ " is invalid. The values domain of the left paramater ( "
															+ this.expression1.toString()
															+ " ) is different from the values domain of the right parameter ( "
															+ this.expression2.toString() + " ).");
										}
									}
								} else {
									if ((((EnumSetExpressionSymbol) enumAttribute1.getSetExpressionSymbol())
											.hasTheSameEnumValuesDomain(((EnumSetExpressionSymbol) enumAttribute2
													.getSetExpressionSymbol()).getContainedValues()))) {
										return Expression.BOOL;
									} else
										LOGGER.info("Type error : the expression " + this.toString()
												+ " is invalid. The values domain of the left paramater ( "
												+ this.expression1.toString()
												+ " ) is different from the values domain of the right parameter ( "
												+ this.expression2.toString() + " ).");
								}
							} else {
								AttributeSymbol enumAttribute1 = (AttributeSymbol) longIDExpression1.getSymbol();
								if (enumAttribute1.getSetExpressionSymbol().containsExpression(longIDExpression2)) {
									return Expression.BOOL;
								} else {
									LOGGER.info("Type error : the expression " + this.toString()
											+ " is invalid. The values domain of the attribute ( "
											+ this.expression1.toString() + " ) doesn't contain the enum value ( "
											+ this.expression2.toString() + " ).");
								}
							}
						} else {
							if (longIDExpression2.getSymbol() != null) {
								AttributeSymbol enumAttribute2 = (AttributeSymbol) longIDExpression2.getSymbol();
								if (enumAttribute2.getSetExpressionSymbol().containsExpression(longIDExpression1)) {
									return Expression.BOOL;
								} else {
									LOGGER.info("Type error : the expression " + this.toString()
											+ " is invalid. The values domain of the attribute ( "
											+ this.expression1.toString() + " ) doesn't contain the enum value ( "
											+ this.expression2.toString() + " ).");
								}
							} else {
								LOGGER.info("Type error : the expression " + this.toString()
										+ " is invalid. You cannot compare two enum values ("
										+ this.expression1.toString() + ", " + this.expression2.toString() + " ).");
							}
						}
					} else {
						if ((this.expression1Type == Expression.STRUCT) && (this.expression2Type == Expression.STRUCT)) {
							LongIDExpression longIDExpression1 = (LongIDExpression) this.expression1;
							LongIDExpression longIDExpression2 = (LongIDExpression) this.expression2;
							RecordSymbol recordSymbol1 = (RecordSymbol) longIDExpression1.getSymbol();
							RecordSymbol recordSymbol2 = (RecordSymbol) longIDExpression2.getSymbol();
							if (recordSymbol1.getUserType().equals(recordSymbol2.getUserType()))
								return Expression.BOOL;
							else
								LOGGER.info("Type error : the type \"" + recordSymbol1.getUserType()
										+ "\" of the left paramater ( " + this.expression1.toString()
										+ " ) is different from the type \"" + recordSymbol2.getUserType()
										+ "\" of the right parameter ( " + this.expression2.toString() + " ).");
						} else {
							LOGGER.info("Type error : the expression " + this.toString()
									+ " is invalid. The type " + Util.toStringExpressionType(this.expression1Type)
									+ " of the left paramater ( " + this.expression1.toString()
									+ " ) is different from the type "
									+ Util.toStringExpressionType(this.expression2Type) + " of the right parameter ( "
									+ this.expression2.toString() + " ).");
						}
					}
				}
			}
		}
		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return this.expression1.toString() + " == " + this.expression2.toString();
	}

	@Override
	public Expression toNormalForm() {
		if ((this.expression1Type == Expression.STRUCT) && (this.expression2Type == Expression.STRUCT)) {
			Expression expression = null;
			LongIDExpression longIDExpression1 = (LongIDExpression) this.expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) this.expression2;
			RecordSymbol recordSymbol1 = (RecordSymbol) longIDExpression1.getSymbol();
			RecordSymbol recordSymbol2 = (RecordSymbol) longIDExpression2.getSymbol();
			int i = 0;
			Object[] recordSymbol1AttributesArray = recordSymbol1.getAttributeSymbols().keySet().toArray();
			Object[] recordSymbol2AttributesArray = recordSymbol2.getAttributeSymbols().keySet().toArray();
			AttributeSymbol recorsSymbol1Attribute;
			AttributeSymbol recorsSymbol2Attribute;
			while (i <= recordSymbol1.getAttributeSymbols().size() - 1) {
				recorsSymbol1Attribute = recordSymbol1.getAttributeSymbol(recordSymbol1AttributesArray[i].toString());
				recorsSymbol2Attribute = recordSymbol2.getAttributeSymbol(recordSymbol2AttributesArray[0].toString());
				int j = 1;
				while (!recorsSymbol1Attribute.getNonNormalizedID().equals(recorsSymbol2Attribute.getNonNormalizedID())) {
					recorsSymbol2Attribute = recordSymbol2.getAttributeSymbol(recordSymbol2AttributesArray[j]
							.toString());
					j++;
				}
				if (expression == null) {
					expression = new EqualsExpression(new LongIDExpression(longIDExpression1.getNormalizedLongID()
							+ "." + recorsSymbol1Attribute.getID(), null), new LongIDExpression(
							longIDExpression2.getNormalizedLongID() + "." + recorsSymbol2Attribute.getID(), null));
				} else {
					expression = new AndExpression(expression, new EqualsExpression(new LongIDExpression(
							longIDExpression1.getNormalizedLongID() + "." + recorsSymbol1Attribute.getID(), null),
							new LongIDExpression(longIDExpression2.getNormalizedLongID() + "."
									+ recorsSymbol2Attribute.getID(), null)));
				}
				i++;
			}
			expression = new ParenthesesExpression(expression);
			return expression;
		} else {
			return new EqualsExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
		}
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		if ((this.expression1Type == Expression.ENUM) && (this.expression2Type == Expression.ENUM)) {
			LongIDExpression longIDExpression1 = (LongIDExpression) this.expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) this.expression2;
			if (longIDExpression1.getSymbol() != null) {
				if (longIDExpression2.getSymbol() != null) {
					AttributeSymbol enumAttribute1 = (AttributeSymbol) longIDExpression1.getSymbol();
					AttributeSymbol enumAttribute2 = (AttributeSymbol) longIDExpression2.getSymbol();
					Object[] enumValues = enumAttribute1.getBooleanAttributes().keySet().toArray();
					if (enumAttribute1.getNumberOfBooleanAttributes() == 1) {
						return new AndExpression(new LongIDExpression(longIDExpression1.getNormalizedLongID() + "."
								+ enumAttribute1.getBooleanAttribute(enumValues[0].toString()).getID(), null),
								new LongIDExpression(longIDExpression2.getNormalizedLongID() + "."
										+ enumAttribute2.getBooleanAttribute(enumValues[0].toString()).getID(), null));
					} else {
						OrExpression simplifiedExpression = new OrExpression(new AndExpression(new LongIDExpression(
								longIDExpression1.getNormalizedLongID() + "."
										+ enumAttribute1.getBooleanAttribute(enumValues[0].toString()).getID(), null),
								new LongIDExpression(longIDExpression2.getNormalizedLongID() + "."
										+ enumAttribute2.getBooleanAttribute(enumValues[0].toString()).getID(), null)),
								new AndExpression(new LongIDExpression(longIDExpression1.getNormalizedLongID() + "."
										+ enumAttribute1.getBooleanAttribute(enumValues[1].toString()).getID(), null),
										new LongIDExpression(longIDExpression2.getNormalizedLongID() + "."
												+ enumAttribute2.getBooleanAttribute(enumValues[1].toString()).getID(),
												null)));
						int i = 2;
						while (i <= enumAttribute1.getNumberOfBooleanAttributes() - 1) {
							simplifiedExpression = new OrExpression(simplifiedExpression, new AndExpression(
									new LongIDExpression(longIDExpression1.getNormalizedLongID() + "."
											+ enumAttribute1.getBooleanAttribute(enumValues[i].toString()).getID(),
											null), new LongIDExpression(longIDExpression2.getNormalizedLongID() + "."
											+ enumAttribute2.getBooleanAttribute(enumValues[i].toString()).getID(),
											null)));
							i++;
						}
						return simplifiedExpression;
					}
				} else {
					AttributeSymbol enumAttribute1 = (AttributeSymbol) longIDExpression1.getSymbol();
					return new LongIDExpression(longIDExpression1.getNormalizedLongID() + "."
							+ enumAttribute1.getBooleanAttribute(longIDExpression2.getLongID()).getID(), null);
				}
			} else {
				AttributeSymbol enumAttribute2 = (AttributeSymbol) longIDExpression2.getSymbol();
				return new LongIDExpression(longIDExpression2.getNormalizedLongID() + "."
						+ enumAttribute2.getBooleanAttribute(longIDExpression1.getLongID()).getID(), null);

			}
		} else if ((this.expression1Type == Expression.INT) && (this.expression2Type == Expression.INT)) {
			return this;
		} else {
			return new IfAndOnlyIfExpression(this.expression1, this.expression2).toSimplifiedForm();
		}
	}

	/**
	 * This method is normally never used because the toSimplifiedForm() method
	 * removes each XorAggExpression from the diagram.
	 * 
	 * @return
	 */
	@Override
	public BooleanExpression removeArrows() {
		return this;
	}

	/**
	 * This method is normally never used because the toSimplifiedForm() method
	 * removes each XorAggExpression from the diagram.
	 * 
	 * @return
	 */
	@Override
	public BooleanExpression distributeDisjunctions() {
		return this;
	}

	/**
	 * This method is normally never used because the toSimplifiedForm() method
	 * removes each XorAggExpression from the diagram.
	 * 
	 * @return
	 */
	@Override
	public BooleanExpression distributeNegations() {
		return this;
	}
}

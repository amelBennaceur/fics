package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;
import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.Util.Util;
import be.ac.info.fundp.TVLParser.symbolTables.AttributeSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.EnumSetExpressionSymbol;

public class GEQExpression implements BooleanExpression {

	private final static Logger LOGGER = Logger.getLogger(GEQExpression.class.getName());
	
	Expression expression1, expression2;

	public GEQExpression(Expression e1, Expression e2) {
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
		if (this.expression1.getType() == Expression.INT) {
			if (this.expression2.getType() == Expression.INT) {
				this.checkNumericalComparison();
				return Expression.BOOL;
			} else {
				if (this.getExpression2().getType() == Expression.REAL) {
					this.checkNumericalComparison();
					return Expression.BOOL;
				} else {
					LOGGER.info("Type error : the expression " + this.toString()
							+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
							+ " ) of a greater or equal expression must be real or int. Currently,  its type is "
							+ Util.toStringExpressionType(this.expression2.getType()) + ".");
				}
			}
		} else {
			if (this.expression1.getType() == Expression.REAL) {
				if (this.expression2.getType() == Expression.INT) {
					this.checkNumericalComparison();
					return Expression.BOOL;
				} else {
					if (this.getExpression2().getType() == Expression.REAL) {
						this.checkNumericalComparison();
						return Expression.BOOL;
					} else {
						LOGGER.info("Type error : the expression " + this.toString()
								+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
								+ " ) of a greater or equal expression must be real or int. Currently,  its type is "
								+ Util.toStringExpressionType(this.expression2.getType()) + ".");
					}
				}
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
										LOGGER.info("Type error : the expression " + this.toString()
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
									+ " is invalid. You cannot compare two enum values (" + this.expression1.toString()
									+ ", " + this.expression2.toString() + " ).");
						}
					}
				} else {
					LOGGER.info("Type error : the expression " + this.toString()
							+ " is invalid. The type of the left paramater ( " + this.expression1.toString()
							+ " ) of a greataer or equal expression must be real or int. Currently,  its type is "
							+ Util.toStringExpressionType(this.expression1.getType()) + ".");
				}
			}
		}
		return Expression.UNKNOWN;
	}

	@Override
	public String toString() {
		return this.expression1.toString() + " >= " + this.expression2.toString();
	}

	@Override
	public Expression toNormalForm() {
		return new GEQExpression(this.expression1.toNormalForm(), this.expression2.toNormalForm());
	}

	@Override
	public BooleanExpression distributeDisjunctions() {
		return this;
	}

	@Override
	public BooleanExpression distributeNegations() {
		return this;
	}

	@Override
	public BooleanExpression removeArrows() {
		return this;
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		BooleanExpression boolExpr = null;
		if (this.expression1.getType() == Expression.INT) {
			if (this.expression2.getType() == Expression.INT) {
				this.checkNumericalComparison();
				boolExpr = this;
			} else {
				if (this.getExpression2().getType() == Expression.REAL) {
					this.checkNumericalComparison();
					boolExpr = this;
				} else {
					LOGGER.info("Type error : the expression " + this.toString()
							+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
							+ " ) of a greater or equal expression must be real or int. Currently,  its type is "
							+ Util.toStringExpressionType(this.expression2.getType()) + ".");
				}
			}
		} else {
			if (this.expression1.getType() == Expression.REAL) {
				if (this.expression2.getType() == Expression.INT) {
					this.checkNumericalComparison();
					boolExpr = this;
				} else {
					if (this.getExpression2().getType() == Expression.REAL) {
						this.checkNumericalComparison();
						boolExpr = this;
					} else {
						LOGGER.info("Type error : the expression " + this.toString()
								+ " is invalid. The type of the right paramater ( " + this.expression2.toString()
								+ " ) of a greater or equal expression must be real or int. Currently,  its type is "
								+ Util.toStringExpressionType(this.expression2.getType()) + ".");
					}
				}
			} else {
				LongIDExpression londIDExpression1 = ((LongIDExpression) this.expression1);
				LongIDExpression londIDExpression2 = ((LongIDExpression) this.expression2);
				if ((londIDExpression1.getSymbol() != null) && (londIDExpression2.getSymbol() != null)) {
					AttributeSymbol enumAttribute1 = (AttributeSymbol) londIDExpression1.getSymbol();
					AttributeSymbol enumAttribute2 = (AttributeSymbol) londIDExpression2.getSymbol();
					Vector<Expression> enumExpressions1 = ((EnumSetExpressionSymbol) enumAttribute1
							.getSetExpressionSymbol()).getContainedValues();
					Vector<Expression> enumExpressions2 = ((EnumSetExpressionSymbol) enumAttribute1
							.getSetExpressionSymbol()).getContainedValues();
					Vector<Expression> tempExpressions1 = new Vector<Expression>();
					Vector<Expression> tempExpressions2 = new Vector<Expression>();
					int i = enumExpressions1.size() - 1;
					while (i >= 0) {
						tempExpressions1.add(enumExpressions1.get(i));
						tempExpressions2.add(enumExpressions2.get(i));
						i--;
					}
					((EnumSetExpressionSymbol) enumAttribute1.getSetExpressionSymbol())
							.setExpressions(tempExpressions1);
					((EnumSetExpressionSymbol) enumAttribute2.getSetExpressionSymbol())
							.setExpressions(tempExpressions2);
					BooleanExpression simplifiedExpression = new LEQExpression(this.expression1, this.expression2)
							.toSimplifiedForm();
					((EnumSetExpressionSymbol) enumAttribute1.getSetExpressionSymbol())
							.setExpressions(enumExpressions1);
					((EnumSetExpressionSymbol) enumAttribute2.getSetExpressionSymbol())
							.setExpressions(enumExpressions2);
					boolExpr = simplifiedExpression;
				} else {
					if (londIDExpression1.getSymbol() != null) {
						AttributeSymbol enumAttribute1 = (AttributeSymbol) londIDExpression1.getSymbol();
						Vector<Expression> enumExpressions1 = ((EnumSetExpressionSymbol) enumAttribute1
								.getSetExpressionSymbol()).getContainedValues();
						Vector<Expression> tempExpressions1 = new Vector<Expression>();
						int i = enumExpressions1.size() - 1;
						while (i >= 0) {
							tempExpressions1.add(enumExpressions1.get(i));
							i--;
						}
						((EnumSetExpressionSymbol) enumAttribute1.getSetExpressionSymbol())
								.setExpressions(tempExpressions1);
						BooleanExpression simplifiedExpression = new LEQExpression(this.expression1, this.expression2)
								.toSimplifiedForm();
						((EnumSetExpressionSymbol) enumAttribute1.getSetExpressionSymbol())
								.setExpressions(enumExpressions1);
						boolExpr = simplifiedExpression;
					} else {
						AttributeSymbol enumAttribute2 = (AttributeSymbol) londIDExpression2.getSymbol();
						Vector<Expression> enumExpressions2 = ((EnumSetExpressionSymbol) enumAttribute2
								.getSetExpressionSymbol()).getContainedValues();
						Vector<Expression> tempExpressions2 = new Vector<Expression>();
						int i = enumExpressions2.size() - 1;
						while (i >= 0) {
							tempExpressions2.add(enumExpressions2.get(i));
							i--;
						}
						((EnumSetExpressionSymbol) enumAttribute2.getSetExpressionSymbol())
								.setExpressions(tempExpressions2);
						BooleanExpression simplifiedExpression = new LEQExpression(this.expression1, this.expression2)
								.toSimplifiedForm();
						((EnumSetExpressionSymbol) enumAttribute2.getSetExpressionSymbol())
								.setExpressions(enumExpressions2);
						boolExpr = simplifiedExpression;
					}
				}
			}

		}
		return boolExpr;
	}

}

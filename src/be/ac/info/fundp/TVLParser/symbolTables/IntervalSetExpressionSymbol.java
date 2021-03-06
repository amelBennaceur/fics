package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.Vector;
import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.SyntaxTree.IntExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.LongIDExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.RealExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.SetExpression;
import be.ac.info.fundp.TVLParser.Util.Util;

public class IntervalSetExpressionSymbol implements SetExpressionSymbol {
	
	private final static Logger LOGGER = Logger.getLogger(IntervalSetExpressionSymbol.class.getName());
	
	private String max, min, attributeID;
	private int type;

	@Override
	public IntervalSetExpressionSymbol copy() {
		IntervalSetExpressionSymbol clonedIntervalSetExpressionSymbol = new IntervalSetExpressionSymbol();
		clonedIntervalSetExpressionSymbol.min = this.min;
		clonedIntervalSetExpressionSymbol.max = this.max;
		clonedIntervalSetExpressionSymbol.type = this.type;
		return clonedIntervalSetExpressionSymbol;
	}

	public IntervalSetExpressionSymbol() {

	}

	public IntervalSetExpressionSymbol(int type, String min, String max, String attributeID) {
		if ((!(type == Expression.REAL)) && (!(type == Expression.INT)))
			LOGGER.info("Type error : In the (sub-)attribute/type " + attributeID
					+ ", the type of the interval [ " + min + ".." + max + " ] is " + Util.toStringExpressionType(type)
					+ " and not int or real.");

		this.type = type;
		this.attributeID = attributeID;

		if (min.equals("*")) {
			if (this.type == Expression.REAL) {
				// Solution temporaire
				// Float minFloat = new Float(Float.MIN_VALUE);
				// this.min = minFloat.toString();
				Integer minInteger = new Integer(Integer.MIN_VALUE);
				this.min = minInteger.toString();
			} else {
				Integer minInteger = new Integer(Integer.MIN_VALUE);
				this.min = minInteger.toString();
			}
		} else {
			this.min = min;
		}
		if (max.equals("*")) {
			if (this.type == Expression.REAL) {
				// Solution temporaire
				// Float maxFloat = new Float(Float.MAX_VALUE);
				// this.max = maxFloat.toString();
				Integer maxInteger = new Integer(Integer.MAX_VALUE);
				this.max = maxInteger.toString();
			} else {
				Integer maxInteger = new Integer(Integer.MAX_VALUE);
				this.max = maxInteger.toString();
			}
		} else {
			this.max = max;
		}
		this.check();
	}

	public String getMax() {
		return max;
	}

	public String getMin() {
		return min;
	}

	@Override
	public void print() {
		LOGGER.info("	[ " + this.min + ".." + this.max + " ]");
	}

	@Override
	public String toString() {
		return "[ " + this.min + ".." + this.max + " ]";
	}

	public boolean check() {
		try {
			if (this.type == Expression.REAL) {
				Float minFloat = Float.parseFloat(min);
				Float maxFloat = Float.parseFloat(max);
				if (minFloat > maxFloat)
					LOGGER.info("Type error : In the (sub-)attribute/type " + attributeID + ", the interval [ "
							+ minFloat.toString() + ".." + maxFloat.toString()
							+ " ] is not valid, the lower bound must be smaller or equals to the upper bound.");
				else
					return true;
			} else {
				int minInteger = Integer.parseInt(min);
				int maxInteger = Integer.parseInt(max);
				if (minInteger > maxInteger) {
					LOGGER.info("Type error : In the (sub-)attribute/type " + attributeID + ", the interval [ "
							+ minInteger + ".." + maxInteger
							+ " ] is not valid, the lower bound must be smaller or equals to the upper bound.");
				} else
					return true;
			}
		} catch (NumberFormatException e) {
			LOGGER.info("Type error : In the (sub-)attribute/type " + attributeID + ", the interval [ "
					+ this.min + ".." + this.max
					+ " ] is not valid. It contains a string or the interval type is int and it contains a real.");
		}
		return false;
	}

	@Override
	public boolean containsExpression(Expression expression) {
		try {
			if (this.type == Expression.REAL) {
				Float minFloat = Float.parseFloat(min);
				Float maxFloat = Float.parseFloat(max);
				if ((!(expression.getType() == Expression.REAL)) && (!(expression.getType() == Expression.INT))) {
					return false;
				} else {
					if ((expression instanceof RealExpression) || (expression instanceof IntExpression)) {
						float floatExpr = Float.parseFloat(expression.toString());
						if ((floatExpr < minFloat) || (floatExpr > maxFloat))
							return false;
						else
							return true;
					} else
						return true;
				}
			} else {
				Integer minInteger = Integer.parseInt(min);
				Integer maxInteger = Integer.parseInt(max);
				if (!(expression.getType() == Expression.INT)) {
					return false;
				} else {
					if (expression instanceof IntExpression) {
						Integer integerExpr = Integer.parseInt(expression.toString());
						if ((integerExpr < minInteger) || (integerExpr > maxInteger))
							return false;
						else
							return true;
					} else
						return true;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean containsSetExpression(SetExpression setExpression) {
		int setExpressionType = setExpression.getType();
		if (setExpressionType == Expression.ENUM)
			return false;
		if (setExpression.hasAnExpressionList()) {
			Vector<Expression> expressions = setExpression.getExpressionList().getExpressions();
			if (this.type == Expression.REAL) {
				int i = 0;
				while (i <= expressions.size() - 1) {
					if (!this.containsExpression(expressions.get(i)))
						return false;
				}
				return true;
			} else {
				int i = 0;
				while (i <= expressions.size() - 1) {
					if (!this.containsExpression(expressions.get(i)))
						return false;
					i++;
				}
				return true;
			}
		} else {
			if ((this.containsExpression(new LongIDExpression(setExpression.getMinExpression(), null)))
					&& (this.containsExpression(new LongIDExpression(setExpression.getMaxExpression(), null))))
				return true;
			else
				return false;
		}
	}

	@Override
	public boolean isAnEnumSetExpressionSymbol() {
		return false;
	}

	@Override
	public void setAttributeID(String attributeID) {
		this.attributeID = attributeID;
	}

	@Override
	public boolean containsSetExpressionSymbol(SetExpressionSymbol setExpressionSymbol) {
		int setExpressionType = setExpressionSymbol.getType();
		if (setExpressionType == Expression.ENUM)
			return false;
		if (setExpressionSymbol.isAnEnumSetExpressionSymbol()) {
			Vector<Expression> expressions = ((EnumSetExpressionSymbol) setExpressionSymbol).getContainedValues();
			if (this.type == Expression.REAL) {
				int i = 0;
				while (i <= expressions.size() - 1) {
					if (!this.containsExpression(expressions.get(i)))
						return false;
				}
				return true;
			} else {
				int i = 0;
				while (i <= expressions.size() - 1) {
					if (!this.containsExpression(expressions.get(i)))
						return false;
					i++;
				}
				return true;
			}
		} else {
			if (setExpressionType == Expression.INT) {
				if ((this.containsExpression(new IntExpression(((IntervalSetExpressionSymbol) setExpressionSymbol)
						.getMin())))
						&& (this.containsExpression(new IntExpression(
								((IntervalSetExpressionSymbol) setExpressionSymbol).getMax()))))
					return true;
				else
					return false;
			} else {
				if ((this.containsExpression(new RealExpression(((IntervalSetExpressionSymbol) setExpressionSymbol)
						.getMin())))
						&& (this.containsExpression(new RealExpression(
								((IntervalSetExpressionSymbol) setExpressionSymbol).getMax()))))
					return true;
				else
					return false;
			}

		}
	}

	@Override
	public int getType() {
		return this.type;
	}
}

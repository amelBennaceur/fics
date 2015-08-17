package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;
import java.util.logging.Logger;

public class ExpressionList {

	private final static Logger LOGGER = Logger.getLogger(ExpressionList.class.getName());
	
	Vector<Expression> expressions;

	public ExpressionList(Expression e1) {
		this.expressions = new Vector<Expression>();
		this.expressions.add(e1);
	}

	public ExpressionList(Expression e1, ExpressionList e2) {
		this.expressions = e2.getExpressions();
		this.expressions.add(e1);
		// this.expressions.add(0, e1);
	}

	/**
	 * @return the expressionList
	 */
	public Vector<Expression> getExpressions() {
		return expressions;
	}

	@Override
	public String toString() {
		String list = this.expressions.get(0).toString();
		int i = 1;
		while (i <= this.expressions.size() - 1) {
			list = list.concat(", " + this.expressions.get(i).toString());
			i++;
		}
		return list;
	}

	public int getType() {
		int currentType = 0;
		int i = 0;
		while (i <= this.expressions.size() - 1) {
			if (i == 0) {
				currentType = expressions.get(i).getType();
			} else {
				if (currentType != expressions.get(i).getType())
					if ((currentType == Expression.REAL) && (expressions.get(i).getType() == Expression.INT)) {
						currentType = Expression.REAL;
					} else {
						if ((currentType == Expression.INT) && (expressions.get(i).getType() == Expression.REAL)) {
							currentType = Expression.REAL;
						} else {
							LOGGER.info("Illegal Expression");
						}
					}
			}
			i++;
		}
		return currentType;
	}
}

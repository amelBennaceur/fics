package be.ac.info.fundp.TVLParser.Util;

import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.SyntaxTree.ParenthesesExpression;

/**
 * This a utility class which contains only static methods.
 * 
 */
public class Util {

	/**
	 * Check if ID is not a reserved word.
	 * 
	 * @param ID
	 *            The ID which could be a reserved word.
	 * 
	 * @throws BadUseOfReservedWordException
	 *             Throw id ID is a reserved word.
	 */
	public static void checkUseOfReservedWord(String ID) {
		if ((ID.equals("root")) || (ID.equals("this")) || (ID.equals("parent")) || (ID.equals("children"))
				|| ID.equals("selectedchildren") || (ID.equals("int")) || (ID.equals("real")) || (ID.equals("bool"))
				|| (ID.equals("enum")) || (ID.equals("struct")) || (ID.equals("const")) || (ID.equals("requires"))
				|| (ID.equals("excludes")) || (ID.equals("is")) || (ID.equals("in")) || (ID.equals("true"))
				|| (ID.equals("false")) || (ID.equals("and")) || (ID.equals("or")) || (ID.equals("xor"))
				|| (ID.equals("min")) || (ID.equals("max")) || (ID.equals("mul")) || (ID.equals("count"))
				|| (ID.equals("sum")) || (ID.equals("avg")) || (ID.equals("abs")) || (ID.equals("data"))
				|| (ID.equals("ifin")) || (ID.equals("ifout")) || (ID.equals("shared")) || (ID.equals("opt")))
			System.out.println("Type error : the ID " + ID + " uses a reserved word.");
	}

	/**
	 * Show if "id" is a feature ID, if it starts by an upper letter.
	 * 
	 * @param id
	 *            The id which could be a feature ID.
	 * 
	 * @return true if "id" is a feature ID.
	 */
	public static boolean isAFeatureID(String id) {
		char beginChar = id.charAt(0);
		if ((beginChar >= 'A') && (beginChar <= 'Z'))
			return true;
		else
			return false;
	}

	/**
	 * Return the type name which corresponds to "type".
	 * 
	 * @param type
	 *            The int for which the type name will be returned.
	 * 
	 * @return the type name which corresponds to "type". If "type" doesn't
	 *         correspond to any type names, it returns "unknown".
	 */
	public static String toStringExpressionType(int type) {
		switch (type) {
		case Expression.BOOL:
			return "bool";
		case Expression.ENUM:
			return "enum";
		case Expression.INT:
			return "int";
		case Expression.REAL:
			return "real";
		case Expression.STRUCT:
			return "struct";
		case Expression.USER_CREATED:
			return "user specified";
		default:
			return "unknown";
		}
	}

	/**
	 * This method removes some or all the parentheses around "expression". For
	 * example : ((( LongIDExpression ))) --> LongIDExpression (((
	 * !LongIDExpression ))) --> ( !LongIDExpression )
	 * 
	 * @param expression
	 *            An expression which could have parentheses around it.
	 * 
	 * @return "expression" without some or all the parentheses around it.
	 */
	public static Expression removeParentheses(Expression expression) {
		while (expression.getClass().toString().contains("ParenthesesExpression")) {
			if (!((ParenthesesExpression) expression).getExpression().getClass().toString()
					.contains("ParenthesesExpression")) {
				expression = ((ParenthesesExpression) expression).getExpression();
				if (expression.getClass().toString().contains("LongIDExpression"))
					return expression;
				else
					return new ParenthesesExpression(expression);
			} else {
				expression = ((ParenthesesExpression) expression).getExpression();
			}
		}
		return expression;
	}

}
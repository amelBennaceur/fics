package be.ac.info.fundp.TVLParser.symbolTables;

import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.SyntaxTree.SetExpression;

public interface SetExpressionSymbol {

	public void print();

	public boolean containsExpression(Expression expression);

	public boolean containsSetExpression(SetExpression setExpression);

	public boolean isAnEnumSetExpressionSymbol();

	public void setAttributeID(String attributeID);

	public SetExpressionSymbol copy();

	public boolean containsSetExpressionSymbol(SetExpressionSymbol setExpressionSymbol);

	public int getType();
}
package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.ArrayList;

import solver.variables.IntVar;

public interface NumericalExpression extends Expression {

	public void getScalar(ArrayList<IntVar> vars, ArrayList<Integer> coeff);
}

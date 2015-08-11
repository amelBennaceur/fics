package uk.ac.open.capabilitySelection;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import solver.Solver;
import uk.ac.open.capability.selection.CPTVLParser;

public class AddingAttributes {

	Solver chocoSolver = new Solver("Test Features with Attributes");
	
	@Test
	public void andConstraint() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root A {" + "B -> C && D;" + "group[2..3]{" + "B," + "C," + "D" + "}" + "}",chocoSolver);
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 3);
	}

	@Test
	public void trueExpression() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "bool attrib;" + "A -> attrib==true;" + "group allof {" + "A" + "}"
				+ "}",chocoSolver);
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void falseExpression() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "bool attrib;" + "A -> attrib==false;" + "A requires B;"
				+ "group someof {" + "A," + "B" + "}" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 4);
	}

	@Test
	public void GEQExpressionBetweenAttributes() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "int attrib1 in [2..2];" + "int attrib2 in [2..4];" + "attrib1 >= attrib2;"
				+ "A requires B;" + "group someof {" + "A," + "B" + "}" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void intIntervalsforAttributes() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "int attrib1;" + "attrib1 in [2..4];" + "group someof {" + "A" + "}"
				+ "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 3);
	}

	@Test
	public void multipleConstraintWithOneAttribute() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "int attrib1;" + "attrib1 in [2..4];" + "attrib1 >= 3;" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void compareIntAttributes() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "	int attrib1;" + " int attrib2;" + "attrib1 in [2..4];"
				+ "attrib2 in [4..5];" + "attrib1 >= attrib2;" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 1);
	}

	@Test
	public void compareIntAttributesGExpression() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "	int attrib1;" + " int attrib2;" + "attrib1 in [2..4];"
				+ "attrib2 in [3..5];" + "attrib1 > attrib2;" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 1);
	}

	@Test
	public void compareIntAttributesEExpression() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "	int attrib1;" + " int attrib2;" + "attrib1 in [2..4];"
				+ "attrib2 in [4..5];" + "attrib1 == attrib2;" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 1);
	}

	@Test
	public void compareIntAttributesLEQExpression() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "	int attrib1;" + " int attrib2;" + "attrib1 in [2..4];"
				+ "attrib2 in [3..5];" + "attrib1 <= attrib2;" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 8);
	}

	@Test
	public void compareIntAttributesLExpression() {
		CPTVLParser parser = null;
		parser = new CPTVLParser("root Test1 {" + "	int attrib1;" + " int attrib2;" + "attrib1 in [2..4];"
				+ "attrib2 in [3..5];" + "attrib1 < attrib2;" + "}",chocoSolver);

		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 6);
	}
}

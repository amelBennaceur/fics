package uk.ac.open.capabilitySelection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import solver.Solver;
import uk.ac.open.capability.selection.CPTVLParser;

public class TVLExamples {

//	Solver chocoSolver = new Solver("Test Features");
	
	@Test
	public void test1() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 1");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/assert-01.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		System.out.println("Solutions:"+"\n"+ solutions);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test2() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 2");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/assert-02.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		System.out.println("STEP####");
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		System.out.println("Solutions:"+"\n"+ solutions);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test3() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 3");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/cfdp-FULL.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 46);
	}

	@Test
	public void test4() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 4");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/cfdp-r591.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 30);
	}

	@Test
	public void test5() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 5");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/cfdp-r591.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 30);
	}

	@Test
	public void test6() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 6");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/complex.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 16);
	}

	@Test
	public void test7() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 7");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/deadlock-01.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test8() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 8");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/deadlock-02.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test9() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 9");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/deadlock-03.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 3);
	}

	@Test
	public void test10() {
		Solver chocoSolver = new Solver("Test Features 10");
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/elevator.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 256);
	}

	@Test
	public void test11() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 11");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/eratosthenes.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 1);
	}

	@Test
	public void test12() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 12");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/minepump.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 128);
	}

	@Test
	public void test13() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 13");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/mutex.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 6);
	}

	@Test
	public void test14() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 14");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/reachability.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test15() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 15");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/testNoNumAttribute.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 36);
	}

	@Test
	public void test16() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 16");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/website-bad.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 4);
	}

	@Test
	public void test17() {
		CPTVLParser parser = null;
		Solver chocoSolver = new Solver("Test Features 17");
		try {
			parser = new CPTVLParser(new File("examples/tests/TVLExamples/website-good.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
		assertEquals(solutions.size(), 2);
	}
}

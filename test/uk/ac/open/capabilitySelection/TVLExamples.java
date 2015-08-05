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

	Solver chocoSolver = new Solver("Test Features");
	
	@Test
	public void test1() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/assert-01.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test2() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/test.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test3() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/cfdp-FULL.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 46);
	}

	@Test
	public void test4() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/cfdp-r591.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 30);
	}

	@Test
	public void test5() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/cfdp-r591.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 30);
	}

	@Test
	public void test6() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/complex.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 16);
	}

	@Test
	public void test7() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/deadlock-01.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test8() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/deadlock-02.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test9() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/deadlock-03.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 3);
	}

	@Test
	public void test10() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/elevator.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 256);
	}

	@Test
	public void test11() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/eratosthenes.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 1);
	}

	@Test
	public void test12() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/minepump.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 128);
	}

	@Test
	public void test13() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/mutex.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 6);
	}

	@Test
	public void test14() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/reachability.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 2);
	}

	@Test
	public void test15() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/testNoNumAttribute.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 36);
	}

	@Test
	public void test16() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/website-bad.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 4);
	}

	@Test
	public void test17() {
		CPTVLParser parser = null;
		try {
			parser = new CPTVLParser(new File("examples/tests/website-good.tvl"),chocoSolver);
		} catch (FileNotFoundException e) {
			fail("Verify file path for tests");

		}
		parser.run();
		ArrayList<ArrayList<String>> solutions = parser.getSolutions();
		assertEquals(solutions.size(), 2);
	}
}

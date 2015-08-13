package uk.ac.open.capability.selection;

import java.util.List;

import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.IntConstraintFactory;
import solver.search.solution.AllSolutionsRecorder;
import solver.search.solution.ParetoSolutionsRecorder;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import solver.variables.view.ScaleView;

public class TestsChoco {

	public static void main(String[] args) {
		// 1. Create a Solver
		Solver solver = new Solver("Feature Selection");

		// 2. Create variables through the variable factory
		IntVar x = VariableFactory.bounded("X", 1, 5, solver);
		IntVar y = VariableFactory.bounded("Y", 1, 5, solver);
		IntVar z = VariableFactory.bounded("Z", 1, 5, solver);

		// 3. Create and post constraints by using constraint factories

		/******************* Simple constraint *******************/
		 solver.post(IntConstraintFactory.arithm(x, "+", y, "<", 5));

		/******************* Ifthen constraint *******************/
		// Constraint a = IntConstraintFactory.arithm(x, ">", 1);
		// Constraint b = IntConstraintFactory.arithm(y, "=", x);
		// solver.post(LogicalConstraintFactory.ifThen(a, b));

		/******************* Sum constraint *******************/
		// IntVar[] tabSum = { x, y };
		// IntVar minSum = VariableFactory.bounded("Minimum Sum of variables",
		// 4, 4, solver);
		// IntVar maxSum = VariableFactory.bounded("Maximum Sum of variables",
		// 5, 5, solver);
		// solver.post(IntConstraintFactory.sum(tabSum, ">=", minSum));
		// solver.post(IntConstraintFactory.sum(tabSum, "<=", maxSum));

		/******************* assert-01.tvl *******************/
		// IntVar a = VariableFactory.bounded("A", 1, 1, solver);
		// IntVar b = VariableFactory.bounded("B", 0, 1, solver);
		// IntVar c = VariableFactory.bounded("C", 0, 1, solver);
		//
		// IntVar[] tabSum = { b, c };
		// IntVar minSum = VariableFactory.bounded("Minimum Sum of variables",
		// 1, 1, solver);
		// IntVar maxSum = VariableFactory.bounded("Maximum Sum of variables",
		// 1, 1, solver);
		//
		// Constraint groupCstIf =
		// LogicalConstraintFactory.and(IntConstraintFactory.sum(tabSum, ">=",
		// minSum), IntConstraintFactory.sum(tabSum, "<=", maxSum));
		//
		// Constraint[] allChildrenNill = { IntConstraintFactory.arithm(b, "=",
		// 0), IntConstraintFactory.arithm(c, "=", 0) };
		// Constraint groupCstElse =
		// LogicalConstraintFactory.and(allChildrenNill);
		// solver.post(LogicalConstraintFactory.ifThenElse(IntConstraintFactory.arithm(a,
		// ">", 0), groupCstIf, groupCstElse));
		//
		// // mandatory
		// solver.post(LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(b,
		// ">", 0), IntConstraintFactory.arithm(a, ">", 0)));
		// solver.post(LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(c,
		// ">", 0), IntConstraintFactory.arithm(a, ">", 0)));

		/******************* Numerical Expression *******************/
//		IntVar[] vars = { new ScaleView(x, 1, solver), new ScaleView(y, 1, solver) };
//
//		IntVar val = VariableFactory.fixed(4, solver);
//
//		solver.post(IntConstraintFactory.sum(vars, "<", val));

		// 4. Define the search strategy
		// solver.set(IntStrategyFactory.lexico_LB(new IntVar[] { x, y }));

//		 5. Launch the resolution process
//		 solver.post(IntConstraintFactory.arithm(x, "=", 1));
		 
		 
//		 if (solver.findSolution()) {
//		 do {
//		 Solution s = solver.getSolutionRecorder().getLastSolution();
//		 // System.out.println("A solution is A = " + s.getIntVal(a) +
//		 // " B = " + s.getIntVal(b) + " C = " + s.getIntVal(c));
//		 System.out.println("A solution is x = " + s.getIntVal(x)
//		 + " y = " + s.getIntVal(y));
//		 } while (solver.nextSolution());
//		 }

		// 6. Maximise
		// solver.findOptimalSolution(ResolutionPolicy.MAXIMIZE, x);
		// solver.findAllOptimalSolutions(ResolutionPolicy.MAXIMIZE, x, true);
		//
		// List<Solution> solutions =
		// solver.getSolutionRecorder().getSolutions();
		//
		// System.out.println("Number of solutions = " + solutions.size());
		// for (Solution s : solutions) {
		// System.out.println("A solution is x = " + s.getIntVal(x) + " y = "
		// + s.getIntVal(y));
		// }

		// 7. Multi-objective Optimisation
		IntVar[] paretoVars = { x, y };
		AllSolutionsRecorder rec = new AllSolutionsRecorder(solver);
		ParetoSolutionsRecorder paretoRecorder = new ParetoSolutionsRecorder(ResolutionPolicy.MAXIMIZE, paretoVars);
		solver.findParetoFront(ResolutionPolicy.MAXIMIZE, x, y);
		List<Solution> paretoFront = solver.getSolutionRecorder().getSolutions();
		System.out.println("The pareto front has " + paretoFront.size() + " solutions : ");
		for (Solution s : paretoFront) {
			System.out.println("The solution is x = " + s.getIntVal(x) + " y = " + s.getIntVal(y)+ " z = " + s.getIntVal(z));
		}

//		// Testing other recoders
//		List<Solution> solutions = rec.getSolutions();
//		for (Solution s : solutions) {
//			System.out.println("A solution is x = " + s.getIntVal(x) + " y = " + s.getIntVal(y));
//		}

	}

}

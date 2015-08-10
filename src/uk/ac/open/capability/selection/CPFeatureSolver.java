package uk.ac.open.capability.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import be.ac.info.fundp.TVLParser.SyntaxTree.AndExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.BooleanExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.EqualsExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.Expression;
import be.ac.info.fundp.TVLParser.SyntaxTree.FalseExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.GEQExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.GreaterExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.InExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.IntExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.LEQExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.LongIDExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.LowerExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.NotExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.OrExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.PlusExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.SetExpression;
import be.ac.info.fundp.TVLParser.SyntaxTree.TrueExpression;
import be.ac.info.fundp.TVLParser.symbolTables.AttributeSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.ConstraintSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.FeatureSymbol;

/**
 * An object from this class allows to compute different operations about a
 * feature model. This feature model has to be normalized and converted into its
 * boolean form.
 * 
 */
public class CPFeatureSolver {

	// The solver
	private Solver solver;

	// The keys are feature IDs and the values are the associated CSP variables
	public static HashMap<String, IntVar> idVarMap = new HashMap<String, IntVar>();

	HashSet<String> visitedFeatures = new HashSet<String>();

	boolean isSatisfiable = false;

	boolean isSolverLaunched = false;

	ArrayList<ArrayList<String>> solList = null;

	// Constructor :
	// -------------

	/**
	 * This constructor builds a new solver. The different constraints of
	 * "rootFeatureSymbol" are converted into the DIMACS format and added to
	 * "chocoSolver". The constraints of the cardinalities and the features
	 * justification rules are also generated into the DIMACS format and added
	 * to "sat4JSolver".
	 * 
	 * @param rootFeatureSymbol
	 *            The root feature of the feature model on which the solver will
	 *            work. If this feature model has'nt been normalized and convert
	 *            into it's boolean form, this constructor will crash.
	 * 
	 * @exception UnsatisfiableModelException
	 *                If the feature model is unsatisfiable.
	 * 
	 */
	public CPFeatureSolver(FeatureSymbol rootFeatureSymbol, Solver newSolver,boolean reinitialiseVariables) {
		// solver = new Solver("Feature Selection");
		if (reinitialiseVariables)
			idVarMap = new HashMap<String, IntVar>();
		solver = newSolver;
		constructCPModel(rootFeatureSymbol);
	}

	private void constructCPModel(FeatureSymbol f) {
		if (!visitedFeatures.contains(f.getID())) {
			visitedFeatures.add(f.getID());
			IntVar x = null;

			if (!f.isRoot()) {
				if (!idVarMap.containsKey(f.getID())) {
					x = VariableFactory.bounded(f.getID(), 0, 1, solver);
					idVarMap.put(f.getID(), x);
				} else {
					x = idVarMap.get(f.getID());
				}
				IntVar y = null;
				if (!f.isOptionnal()) {
					// constraint related to mandatory features: parent = child
					Set<String> parents = f.getParentsFeature().keySet();
					for (String parent_id : parents) {
						y = idVarMap.get(parent_id);
						solver.post(LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(x, ">", 0),
								IntConstraintFactory.arithm(y, ">", 0)));
					}

				} else {
					// constraint related to optional features: if (parent = 0)
					// then child = 0
					Constraint childNil, parentNil;
					Set<String> parents = f.getParentsFeature().keySet();
					childNil = IntConstraintFactory.arithm(x, "=", 0);
					for (String parent_id : parents) {
						y = idVarMap.get(parent_id);
						parentNil = IntConstraintFactory.arithm(y, "=", 0);
						solver.post(LogicalConstraintFactory.ifThen(parentNil, childNil));
					}
				}
			} else {
				if (!idVarMap.containsKey(f.getID())) {
					x = VariableFactory.bounded(f.getID(), 0, 1, solver);
					idVarMap.put(f.getID(), x);
				} else {
					x = idVarMap.get(f.getID());
				}
			}
			// Group feature if (p>0) then MinCardinality <= Sum[Children] <=
			// MaxCardinality else children = 0;
			Map<String, FeatureSymbol> childrenFeatures = f.getChildrenFeatures();
			if (childrenFeatures != null) {
				Collection<FeatureSymbol> children1 = childrenFeatures.values();
				IntVar[] tabSum = new IntVar[children1.size()];
				Constraint[] allChildrenNill = new Constraint[children1.size()];
				int i = 0;
				for (FeatureSymbol child_i : children1) {
					if (!idVarMap.containsKey(child_i.getID())) {
						tabSum[i] = VariableFactory.bounded(child_i.getID(), 0, 1, solver);
						idVarMap.put(child_i.getID(), tabSum[i]);
					} else {
						tabSum[i] = idVarMap.get(child_i.getID());
					}
					allChildrenNill[i] = IntConstraintFactory.arithm(tabSum[i], "=", 0);
					i++;
				}
				IntVar minSum = VariableFactory.bounded("Minimum Sum of variables", f.getMinCardinality(),
						f.getMinCardinality(), solver);
				IntVar maxSum = VariableFactory.bounded("Maximum Sum of variables", f.getMaxCardinality(),
						f.getMaxCardinality(), solver);

				Constraint groupCstIf = LogicalConstraintFactory.and(IntConstraintFactory.sum(tabSum, ">=", minSum),
						IntConstraintFactory.sum(tabSum, "<=", maxSum));

				solver.post(LogicalConstraintFactory.ifThenElse(IntConstraintFactory.arithm(x, ">", 0), groupCstIf,
						LogicalConstraintFactory.and(allChildrenNill)));

				// Recursive call to construct the model for all children
				for (FeatureSymbol child_i : children1) {
					constructCPModel(child_i);
				}
			}

			Vector<ConstraintSymbol> constraints = f.getConstraintSymbols();

			// Manage attributes
			if (f.getAttributesSymbols() != null) {
				Collection<AttributeSymbol> attributes = f.getAttributesSymbols().values();
				for (AttributeSymbol attrib : attributes) {
					// Create the variable with the right domain for int
					// variables
					if (attrib.getType() == Expression.INT) {
						// TODO should also check all numeric constraints in
						// which the attribute is involved
						int min = 0, max = 0;
						boolean init = false;
						if (constraints != null) {
							for (ConstraintSymbol c : constraints) {
								if (c.getExpression() instanceof InExpression) {
									InExpression inExpr = (InExpression) c.getExpression();
									Expression expression = inExpr.getExpression();
									SetExpression setExpression = inExpr.getSetExpression();
									if ((expression.getType() == Expression.INT)
											&& (setExpression.getType() == Expression.INT)) {
										if (((LongIDExpression) expression).getLongID().contains(attrib.getID())) {
											if (!init) {
												min = Integer.parseInt(setExpression.getMinExpression());
												max = Integer.parseInt(setExpression.getMaxExpression());
												init = true;
											} else {
												if (min < Integer.parseInt(setExpression.getMinExpression()))
													min = Integer.parseInt(setExpression.getMinExpression());
												if (max > Integer.parseInt(setExpression.getMaxExpression()))
													max = Integer.parseInt(setExpression.getMaxExpression());
											}
										}
									}
								}
							}
						}
						if (!init) {
							System.out.println("You should specify an interval for numeric attributes; default is [0,1]");
							min = 0;
							max = 1;
						}
						//To be checked
						idVarMap.put(f.getID() + "." + attrib.getID(),
								VariableFactory.bounded(f.getID() + "." + attrib.getID(), min, max, solver));
					}
				}
			}

			// Manage constraints
			if (constraints != null) {
				for (ConstraintSymbol c : constraints) {
					manageConstraint(c);
				}
			}
		}

	}

	private void manageConstraint(ConstraintSymbol c) {
		Expression expr = c.getExpression();

		if (expr.getType() == Expression.BOOL) {
			BooleanExpression boolExpression = (BooleanExpression) expr;
			boolExpression = boolExpression.toSimplifiedForm().removeArrows();
			boolExpression = boolExpression.distributeNegations();
			boolExpression = boolExpression.distributeDisjunctions();
			Constraint constr = manageBooleanConstraint(boolExpression);
			if (constr != null)
				solver.post(constr);

		}

		// System.out.println(" = "+expr.);
	}

	private Constraint manageBooleanConstraint(BooleanExpression boolExpression) {
		Constraint constr = null;
		if (boolExpression instanceof AndExpression) {
			AndExpression andExpr = (AndExpression) boolExpression;
			constr = LogicalConstraintFactory.and(
					manageBooleanConstraint((BooleanExpression) andExpr.getExpression1()),
					manageBooleanConstraint((BooleanExpression) andExpr.getExpression2()));
		} else if (boolExpression instanceof LongIDExpression) {
			LongIDExpression idExpr = (LongIDExpression) boolExpression;
			String s = idExpr.getLongID();
			IntVar x = null;
			if (!idVarMap.containsKey(s)) {
				x = VariableFactory.bounded(s, 0, 1, solver);
				idVarMap.put(s, x);
			} else {
				x = idVarMap.get(s);
			}
			constr = IntConstraintFactory.arithm(x, ">", 0);
		} else if (boolExpression instanceof NotExpression) {
			constr = LogicalConstraintFactory
					.not(manageBooleanConstraint((BooleanExpression) ((NotExpression) boolExpression).getExpression()));
		} else if (boolExpression instanceof OrExpression) {
			OrExpression orExpr = (OrExpression) boolExpression;
			constr = LogicalConstraintFactory.or(manageBooleanConstraint((BooleanExpression) orExpr.getExpression1()),
					manageBooleanConstraint((BooleanExpression) orExpr.getExpression2()));
		} else if (boolExpression instanceof TrueExpression) {
			constr = IntConstraintFactory.TRUE(solver);
		} else if (boolExpression instanceof FalseExpression) {
			constr = IntConstraintFactory.FALSE(solver);
			;
		} else if (boolExpression instanceof GEQExpression) {
			return manageGEQExpression(boolExpression);
		} else if (boolExpression instanceof GreaterExpression) {
			return manageGreaterExpression(boolExpression);
		} else if (boolExpression instanceof LEQExpression) {
			return manageLEQExpression(boolExpression);
		} else if (boolExpression instanceof LowerExpression) {
			return manageLowerExpression(boolExpression);
		} else if (boolExpression instanceof EqualsExpression) {
			return manageEqualsExpression(boolExpression);
		} else if (boolExpression instanceof InExpression) {
			return manageInExpression(boolExpression);
		}

		return constr;
	}

	private Constraint manageNumericalExpressions(Expression numExpression) {
		Constraint constr = null;
		int type = numExpression.getType();
		if ((type == Expression.INT) || (type == Expression.REAL)) {
			ArrayList<IntVar> vars = new ArrayList<IntVar>();
			boolean finished = false;
			Expression expr = numExpression;
			while (!finished) {
				if (expr instanceof PlusExpression) {

				}
			}
		}
		return constr;
	}

	private Constraint manageEqualsExpression(BooleanExpression boolExpression) {
		Constraint constr = null;
		EqualsExpression lExpr = (EqualsExpression) boolExpression;
		Expression expression1 = lExpr.getExpression1(), expression2 = lExpr.getExpression2();
		if ((expression1 instanceof LongIDExpression) && (expression2 instanceof LongIDExpression)) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			constr = IntConstraintFactory.arithm(x1, "=", x2);

		} else if (expression1 instanceof LongIDExpression) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			if (expression2 instanceof IntExpression) {
				int x2 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x1, "=", x2);
			}

		} else if (expression2 instanceof LongIDExpression) {
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			if (expression2 instanceof IntExpression) {
				int x1 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x2, "=", x1);
			}
		} else {
			System.out.println("Type error : the expression " + lExpr.toString()
					+ " is not valid, you cannot compare two numerical values.");
		}
		return constr;
	}

	private Constraint manageLowerExpression(BooleanExpression boolExpression) {
		Constraint constr = null;
		LowerExpression lExpr = (LowerExpression) boolExpression;
		Expression expression1 = lExpr.getExpression1(), expression2 = lExpr.getExpression2();
		if ((expression1 instanceof LongIDExpression) && (expression2 instanceof LongIDExpression)) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			constr = IntConstraintFactory.arithm(x1, "<", x2);

		} else if (expression1 instanceof LongIDExpression) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			if (expression2 instanceof IntExpression) {
				int x2 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x1, "<", x2);
			}

		} else if (expression2 instanceof LongIDExpression) {
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			if (expression2 instanceof IntExpression) {
				int x1 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x2, ">", x1);
			}
		} else {
			System.out.println("Type error : the expression " + lExpr.toString()
					+ " is not valid, you cannot compare two numerical values.");
		}
		return constr;
	}

	private Constraint manageLEQExpression(BooleanExpression boolExpression) {
		Constraint constr = null;
		LEQExpression leqExpr = (LEQExpression) boolExpression;
		Expression expression1 = leqExpr.getExpression1(), expression2 = leqExpr.getExpression2();
		if ((expression1 instanceof LongIDExpression) && (expression2 instanceof LongIDExpression)) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			constr = IntConstraintFactory.arithm(x1, "<=", x2);

		} else if (expression1 instanceof LongIDExpression) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			if (expression2 instanceof IntExpression) {
				int x2 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x1, "<=", x2);
			}

		} else if (expression2 instanceof LongIDExpression) {
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			if (expression2 instanceof IntExpression) {
				int x1 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x2, ">=", x1);
			}
		} else {
			System.out.println("Type error : the expression " + leqExpr.toString()
					+ " is not valid, you cannot compare two numerical values.");
		}
		return constr;
	}

	private Constraint manageInExpression(BooleanExpression boolExpression) {
		InExpression inExpr = (InExpression) boolExpression;
		Expression expression = inExpr.getExpression();
		SetExpression setExpression = inExpr.getSetExpression();
		IntVar x = null;
		if ((expression.getType() == Expression.INT) && (setExpression.getType() == Expression.INT)) {
			String s = ((LongIDExpression) expression).getLongID();
			int min = Integer.parseInt(setExpression.getMinExpression());
			int max = Integer.parseInt(setExpression.getMaxExpression());
			if (!idVarMap.containsKey(s)) {
				x = VariableFactory.bounded(s, min, max, solver);
				idVarMap.put(s, x);
			} else {
				x = idVarMap.get(s);
			}
		}
		return null;
	}

	private Constraint manageGEQExpression(BooleanExpression boolExpression) {
		Constraint constr = null;
		GEQExpression geqExpr = (GEQExpression) boolExpression;
		Expression expression1 = geqExpr.getExpression1(), expression2 = geqExpr.getExpression2();
		if ((expression1 instanceof LongIDExpression) && (expression2 instanceof LongIDExpression)) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			constr = IntConstraintFactory.arithm(x1, ">=", x2);

		} else if (expression1 instanceof LongIDExpression) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			if (expression2 instanceof IntExpression) {
				int x2 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x1, ">=", x2);
			}

		} else if (expression2 instanceof LongIDExpression) {
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			if (expression2 instanceof IntExpression) {
				int x1 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x2, "<=", x1);
			}
		} else {
			System.out.println("Type error : the expression " + geqExpr.toString()
					+ " is not valid, you cannot compare two numerical values.");
		}
		return constr;
	}

	private Constraint manageGreaterExpression(BooleanExpression boolExpression) {
		Constraint constr = null;
		GreaterExpression gExpr = (GreaterExpression) boolExpression;
		Expression expression1 = gExpr.getExpression1(), expression2 = gExpr.getExpression2();
		if ((expression1 instanceof LongIDExpression) && (expression2 instanceof LongIDExpression)) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			constr = IntConstraintFactory.arithm(x1, ">", x2);

		} else if (expression1 instanceof LongIDExpression) {
			LongIDExpression longIDExpression1 = (LongIDExpression) expression1;
			String s1 = longIDExpression1.getLongID();
			IntVar x1 = null;
			if (!idVarMap.containsKey(s1)) {
				x1 = VariableFactory.bounded(s1, 0, 1, solver);
				idVarMap.put(s1, x1);
			} else {
				x1 = idVarMap.get(s1);
			}
			if (expression2 instanceof IntExpression) {
				int x2 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x1, ">", x2);
			}

		} else if (expression2 instanceof LongIDExpression) {
			LongIDExpression longIDExpression2 = (LongIDExpression) expression2;
			String s2 = longIDExpression2.getLongID();
			IntVar x2 = null;
			if (!idVarMap.containsKey(s2)) {
				x2 = VariableFactory.bounded(s2, 0, 1, solver);
				idVarMap.put(s2, x2);
			} else {
				x2 = idVarMap.get(s2);
			}
			if (expression2 instanceof IntExpression) {
				int x1 = ((IntExpression) expression2).getValue();
				constr = IntConstraintFactory.arithm(x2, "<", x1);
			}
		} else {
			System.out.println("Type error : the expression " + gExpr.toString()
					+ " is not valid, you cannot compare two numerical values.");
		}
		return constr;
	}

	// Solving methods :
	// -----------------

	/**
	 * Checks if the CSP associated with the feature model has at least one
	 * solution.
	 * 
	 * @return True if there exists at least one product satisfying the feature
	 *         model.
	 */
	public boolean isSatisfiable() {
		if (!isSolverLaunched) {
			isSatisfiable = solver.findSolution();
			isSolverLaunched = true;
		}

		return isSatisfiable;
	}

	public boolean isSatisfiable(int ID) {
		System.out.println("isSatifiable(ID)");
		return false;
	}

	/**
	 * Count the number of solutions which satisfies the clauses contained into
	 * the sat4J solver. Be careful, artificial variables are also included into
	 * the computing. So, the solutions number can be erroneous.
	 * 
	 * @return The number of solutions which satisfies the clauses contained
	 *         into sat4J solver.
	 * 
	 * @throws TimeoutException
	 *             Launched by "sat4JSolver" if "timeOut" has been exceeded.
	 */
	public long countSolutions() {
		System.out.println("countSolutions");
		return 0;
	}

	/**
	 * Count the number of solutions which contains the given ID. Be careful,
	 * artificial variables are also included into the computing. So, the
	 * solutions number can be erroneous.
	 * 
	 * @return The number of solutions which contains the given ID.
	 * 
	 * @throws TimeoutException
	 *             Launched by "sat4JSolver" if "timeOut" has been exceeded.
	 */
	public long countSolutions(int ID) {
		System.out.println("countSolutions(0)");
		return 0;
	}

	/**
	 * Return all the solutions of the CSP representing the feature model.
	 * 
	 * @return An array of
	 * 
	 * @throws TimeoutException
	 *             Launched by "sat4JSolver" if "timeOut" has been exceeded.
	 */
	public ArrayList<ArrayList<String>> getAllSolutions() {
		if (solList != null) {
			return solList;
		} else if (!isSolverLaunched) {
			isSolverLaunched = true;
			isSatisfiable = solver.findSolution();
		}
		if (!isSatisfiable)
			return null;
		solList = new ArrayList<ArrayList<String>>();
		Collection<IntVar> tmpAll = idVarMap.values();
		int i = 0;
		IntVar[] variables = new IntVar[tmpAll.size()];
		for (IntVar var : tmpAll) {
			variables[i] = var;
			i++;
		}
		ArrayList<String> sol;

		int j = 1;
		do {
			Solution s = solver.getSolutionRecorder().getLastSolution();
			sol = new ArrayList<String>();
			for (i = 0; i < variables.length; i++) {
				if (s.getIntVal(variables[i]) > 0)
					if (!variables[i].getName().startsWith("ArtificialParent")) {
						if (s.getIntVal(variables[i]) == 1) {
							sol.add(variables[i].getName());
						} else {
							sol.add(variables[i].getName() + " == " + variables[i].getValue());
						}
					}

			}
			solList.add(sol);
		} while (solver.nextSolution());

		return solList;
	}

	/**
	 * Return all the solutions which contains the given ID.
	 * 
	 * @return An array of int arrays containing all the solutions which
	 *         includes the given ID.
	 * 
	 * @throws TimeoutException
	 *             Launched by "sat4JSolver" if "timeOut" has been exceeded.
	 */
	public int[][] getAllSolutions(int featureId) {
		System.out.println("getAllSolutions(featureId)");
		return new int[1][1];
	}

	public List<List<Integer>> explain(int id, List<Integer> state) {
		System.out.println("explain");
		return null;
	}

}
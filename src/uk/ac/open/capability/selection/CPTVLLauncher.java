package uk.ac.open.capability.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import solver.Solver;

/**
 * This class allows transforms the library into a command line tool.
 */
public class CPTVLLauncher {
	
	private final static Logger LOGGER = Logger.getLogger(CPTVLLauncher.class.getName());

	public static void main(String[] args) {
		String usage = "Usage: java -jar TVLParser.jar [option] file.tvl\n" + " with option being one of:\n"
				+ "  -s       perform syntactic checks and print info; the default option,\n"
				+ "  -c       print the number of features,\n" + "  -sat     perform satisfiability check,\n"
				+ "  -prods   list products (may contain duplicates due to differences in \n"
				+ "           dummy variables),\n"
				+ "  -uprods  list products but filter out duplicates (less efficient),\n"
				+ "  -nf      normalise model (prints normalised TVL code on stdout),\n"
				+ " Only specify a single option.\n";

		args = new String[2];
		args[0] = "-prods";
		args[1] = "examples/testRobots.tvl";

		if (args.length == 0) {
			LOGGER.info(usage);
			System.exit(1);
		} else {
			boolean noarg = true, count = false, nf = false, syntax = false, prods = false, uprods = false, sat = false;

			for (int i = 0; i < args.length - 1; i++) {
				noarg = false;

				if (args[i].equals("-c")) {
					count = true;
				} else if (args[i].equals("-nf")) {
					nf = true;
				} else if (args[i].equals("-s")) {
					syntax = true;
				} else if (args[i].equals("-sat")) {
					sat = true;
				} else if (args[i].equals("-prods")) {
					prods = true;
				} else if (args[i].equals("-uprods")) {
					prods = true;
					uprods = true;
				} else {
					LOGGER.info("Unknown option '" + args[i] + "'.\n" + usage);
					System.exit(1);
				}
			}

			if (noarg)
				syntax = true;

			CPTVLParser parser = null;
			try {
				Solver chocoSolver = new Solver("Feature Selection General");
				parser = new CPTVLParser(new File(args[args.length - 1]), chocoSolver);
			} catch (FileNotFoundException e) {
				LOGGER.info("The file " + args[args.length - 1] + " could not be found or opened.");
				System.exit(1);
			}

			if (parser != null) {
				parser.run();
				if (!parser.isValid) {
					parser.printInfo();
					System.exit(1);
				} else if (syntax) {
					parser.printInfo();
				} else if (count) {
					LOGGER.info("nbFeatures = "+parser.nbFeatures());
				} else if (nf) {
					LOGGER.info(parser.getNormalForm());
				} else if (sat || prods) {
					try {
						if (!prods)
							LOGGER.info(parser.isSatisfiable(true) ? "Ok, feature model satisfiable."
									: "Ko, feature model *not* satisfiable.");
						else {
							ArrayList<ArrayList<String>> solutions = parser.getSolutions(true);
							if (solutions == null)
								LOGGER.info("Feature model *not* satisfiable.");
							else {
								LOGGER.info("Found " + solutions.size() + " solution(s):");
								if (!uprods) {
									int i = 1;
									for (ArrayList<String> sol : solutions) {
										LOGGER.info("- Solution " + i + ":  " + sol);
										i++;
									}
								} else {
									LOGGER.info("Caching solutions..");
									String[] solutionStrings = new String[solutions.size()];
									int i = 1;
									for (ArrayList<String> sol : solutions) {
										LOGGER.info("- Solution " + i + ":  " + sol);
										i++;
									}
									LOGGER.info("Sorting..");
									Arrays.sort(solutionStrings);
									String last = "";
									int total = 0;
									LOGGER.info("Printing and counting..");
									for (i = 0; i < solutionStrings.length; i++) {
										if (!solutionStrings[i].equals(last)) {
											LOGGER.info(" - " + solutionStrings[i]);
											total++;
										}
										last = solutionStrings[i];
									}
									LOGGER.info("Found " + total + " unique products.");
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.info("Error, the parser had an exception.");
						System.exit(1);
					}

				}
			}
		}
	}
}

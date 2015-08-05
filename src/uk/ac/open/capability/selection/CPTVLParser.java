package uk.ac.open.capability.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import java_cup.runtime.Symbol;
import solver.Solver;
import be.ac.info.fundp.TVLParser.Parser.lexer;
import be.ac.info.fundp.TVLParser.Parser.parser;
import be.ac.info.fundp.TVLParser.SyntaxTree.Model;
import be.ac.info.fundp.TVLParser.Util.NormalForm;
import be.ac.info.fundp.TVLParser.symbolTables.ConstantsSymbolTable;
import be.ac.info.fundp.TVLParser.symbolTables.FeatureSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.FeaturesSymbolTable;
import be.ac.info.fundp.TVLParser.symbolTables.TypesSymbolTable;

/**
 * This class is the main class of the TVL parser, it contains different methods
 * to : - Parse a .TVL file and to get information about the feature model that
 * this file contains. - Create the normal form of the feature model contained
 * in the input file and to get information about this form. - Create the
 * boolean form of the feature model contained in the input file and to get
 * information about this form. - Compute operations (satisfiability, number of
 * solutions,...) about the feature model described into the input file.
 * 
 * If you want to use TVL parser as a library of you own software, you have to
 * use this class.
 */
public class CPTVLParser {

	@SuppressWarnings("unused")
	// Represents the file which will be parsed (this file may contain includes)
	private File inputFile;

	// The content of "inputFile" with all the includes resolved.
	private String input;

	// Used as base for all relative include paths inside the TVL file, WITHOUT
	// trailing slash!
	private String basePath;

	// Show if the the run() method has been launched.
	private boolean hasRun = false;

	// Show if the solver has compute operations about the FM.
	private boolean hasRunSolver = false;

	// If an error has been thrown during the parsing, it will be recorded in
	// this variable.
	Exception syntacticalException = null;

	// If an error has been thrown during the type checking, it will be recorded
	// in this variable.
	Exception typeException = null;

	// If an error has been thrown during the feature model solving, it will be
	// recorded in this variable.
	Exception solverException = null;

	// Show if the feature model contained in "inputs" is valid or not. The
	// variable only makes sense
	// if "hasRun" is true.
	boolean isValid = false;

	// The feature symbol table resulting from the type checking of "inputs".
	private FeaturesSymbolTable featuresSymbolTable;

	// Show if the parsing of the normal form of the feature has been launched
	// with no exceptions thrown.
	private boolean hasRunNormalizedFormParser = false;

	// Show if the parsing of the boolean form of the feature model has been
	// launched with no exceptions thrown.
	private boolean hasRunChocoCSPFormParser = false;

	// Contain the normalized version of the feature model contains in "input"
	private String normalizedFormText;

	// Contain the boolean version of the feature model contains in "input"
	private String chocoCSPFormText;

	// The TVL parser of the normal form of the feature model contained in
	// "input".
	private CPTVLParser normalizedFormParser;

	// The TVL parser of the boolean form of the feature model contained in
	// "input".
	private CPTVLParser chocoCSPFormParser;

	// The SAT4J solver which will be used to compute operations about the
	// boolean form (if this form exists)
	private CPFeatureSolver mySolver = null;

	private Solver chocoSolver;

	/**
	 * The class constructor, it takes one input file and resolve all its
	 * includes. The content of the file with all the includes resolved is saved
	 * in "input". Be careful, this construct doesn't parse "inputFile". For
	 * parse a file, you have to use the run() method.
	 * 
	 * This constructor also display the new content of "input".
	 * 
	 * @param inputFile
	 *            The .tvl file which will be parsed.
	 * 
	 * @throws FileNotFoundException
	 *             If the file corresponding to "inputFile" has not been found.
	 */
	public CPTVLParser(File inputFile, Solver chocoSolver) throws FileNotFoundException {
		if (inputFile.exists() && inputFile.isFile() && inputFile.canRead()) {
			this.basePath = inputFile.getAbsolutePath();
			this.basePath = basePath.substring(0, this.basePath.length() - inputFile.getName().length());
			if (this.basePath.endsWith("/"))
				this.basePath = this.basePath.substring(0, this.basePath.length() - 1);

			this.inputFile = inputFile;
			this.input = resolveIncludes(inputFile, basePath);
			this.chocoSolver = chocoSolver;
		} else {
			throw new FileNotFoundException("File '" + inputFile.getPath() + "' not found");
		}
	}

	/**
	 * Another class constructor which take a string in input and not a file.
	 * "inputDiagram" can't contain includes. Otherwise, the parser will crash.
	 * Currently, this constructor is only used to create a parser for the
	 * normal form or for the boolean form of the feature model.
	 * 
	 * 
	 * @param inputTextDiagram
	 *            A String containing the feature diagram which will be parsed.
	 */
	public CPTVLParser(String inputDiagram, Solver chocoSolver) {
		this.input = inputDiagram;
		this.chocoSolver = chocoSolver;
	}

	/**
	 * Takes a file and replaces all includes by their content.
	 * 
	 * @param f
	 *            The input file (a TVL file..).
	 * @param basePath
	 *            Used as base for all relative include paths inside the TVL
	 *            file, WITHOUT trailing slash!
	 * @return The content of the input file with all includes replaced by their
	 *         content.
	 * @throws FileNotFoundException
	 *             in case the file does not exist.
	 */
	private String resolveIncludes(File f, String basePath) throws FileNotFoundException {
		String result = "";
		String includeFileName = "";
		String includePath = "";
		File includeFile;

		Scanner scanner = new Scanner(f);
		;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

			while (line.indexOf("include(") != -1 && line.indexOf(");", line.indexOf("include(")) != -1) {
				result += line.substring(0, line.indexOf("include(")); // The
																		// text
																		// before
																		// the
																		// include

				includeFileName = line.substring(line.indexOf("include(") + 8,
						line.indexOf(");", line.indexOf("include(")));

				includePath = includeFileName;
				if (!includePath.startsWith("/")) {
					if (includePath.startsWith("./"))
						includePath = basePath + includePath.substring(1);
					else
						includePath = basePath + "/" + includePath;
				}

				includeFile = new File(includePath);
				if (includeFile.exists() && includeFile.isFile() && includeFile.canRead()) {
					result += resolveIncludes(includeFile, basePath);
				} else {
					System.out.println("Error: The instruction include(" + includeFileName
							+ ") refers to an inexisting file.");
				}

				if (line.length() > line.indexOf(");", line.indexOf("include(")) + 2) {
					line = line.substring(line.indexOf(");", line.indexOf("include(")) + 2); // The
																								// text
																								// after
																								// the
																								// includes
																								// closing
																								// )
				} else {
					line = "";
				}

			}

			// No include in this line
			result += line;

			result += "\n";
		}
		scanner.close();
		return result;
	}

	/**
	 * Launch the parsing of the input file. If an error is thrown, it will be
	 * recorded in "syntacticalException" or in "typeException" and the parsing
	 * will be interrupted.
	 */
	public void run() {
		if (!this.hasRun) {
			this.hasRun = true;

			parser parser_obj = new parser(new lexer(new StringReader(this.input)));
			/* open input files, etc. here */
			Symbol parse_tree = null;
			boolean do_debug_parse = false;

			// TODO: This is always false

			try {
				if (do_debug_parse)
					parse_tree = parser_obj.debug_parse();
				else
					parse_tree = parser_obj.parse();
			} catch (Exception e) {
				this.syntacticalException = e;
			}

			if (parse_tree != null) {
				Model model = (Model) parse_tree.value;
				try {
					TypesSymbolTable typesSymbolTable = new TypesSymbolTable(model.getTypes());
					parser_obj.getFeaturesSymbolTable().lauchConstruction(model.getFeatures(), typesSymbolTable,
							new ConstantsSymbolTable(model.getConstants(), typesSymbolTable));
					this.featuresSymbolTable = parser_obj.getFeaturesSymbolTable();
					this.isValid = true;
				} catch (Exception e) {
					this.typeException = e;
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Display information about the parsing of the input file. If the feature
	 * model contained in the file is valid, it print information about the
	 * number of features, the attributes types,...Otherwise, it shows that
	 * feature model is invalid and print information about the exception which
	 * has been thrown during the parsing or during the type checking.
	 */
	public void printInfo() {
		if (!this.hasRun)
			this.run();

		if (!this.isValid) {
			System.out.print("Feature model invalid, ");

			if (!this.isSyntacticallyCorrect()) {
				System.out.println("syntax error:");
				System.out.println(" '" + this.syntacticalException.getMessage() + "'");
				System.out.println("Stack trace:");
				syntacticalException.printStackTrace(System.out);
			} else if (!this.isCorrectlyTyped()) {
				System.out.println("type error:");
				System.out.println(" '" + this.typeException.getMessage() + "'");
				System.out.println("Stack trace:");
				typeException.printStackTrace(System.out);
			}

		} else {
			System.out.println("Feature model valid");
			System.out.println("  #features   = " + featuresSymbolTable.getNBFeatures());
		}
	}

	/**
	 * If the feature model described in the input file is valid, it generates
	 * the normal form of this feature model and starts to parse this new form.
	 * If no exception is thrown, the .TVL corresponding to the normal form is
	 * saved in the String "normalizedFormText" and the parser which has parsed
	 * this .TVL is saved in "normalizedFormParser".
	 */
	public void runNormalizedFormParser() {
		if (!this.hasRun) {
			this.run();
		}
		if (this.isValid) {
			if (!this.hasRunNormalizedFormParser) {
				NormalForm nf;
				try {
					nf = new NormalForm(this.featuresSymbolTable);
					this.normalizedFormText = nf.getRootFeature().toString("");
					this.normalizedFormParser = new CPTVLParser(this.normalizedFormText, chocoSolver);
					this.normalizedFormParser.run();
					this.hasRunNormalizedFormParser = true;
				} catch (Exception e) {
					System.err.println("There has been an error during the construction of the normal form.");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Get the normalised form of the model.
	 */
	public String getNormalForm() {
		if (!this.hasRunNormalizedFormParser)
			this.runNormalizedFormParser();
		if (this.hasRunNormalizedFormParser)
			return this.normalizedFormText;
		return "";
	}

	/**
	 * If the normal form of the feature model described in the input file is
	 * valid, it generates the boolean form of this feature model and starts to
	 * parse this new form. If no exception is thrown, the .TVL corresponding to
	 * the boolean form is saved in the String "booleanFormText" and the parser
	 * which has parsed this .TVL is saved in "booleanFormParser".
	 */
	public void runChocoCSPFormParser() {
		if (!this.hasRun) {
			this.run();
		}
		if (!this.hasRunNormalizedFormParser)
			this.runNormalizedFormParser();
		if (this.isValid && this.normalizedFormParser.isValid) {

			if (!this.hasRunChocoCSPFormParser) {
				ChocoCSPForm bf;
				try {
					bf = new ChocoCSPForm(this.normalizedFormParser.featuresSymbolTable);
					String rootFeature = bf.getRootFeature().toString("");
					this.chocoCSPFormText = rootFeature;
					this.chocoCSPFormParser = new CPTVLParser(this.chocoCSPFormText, chocoSolver);
					this.chocoCSPFormParser.run();
					this.hasRunChocoCSPFormParser = true;

				} catch (Exception e) {
					System.err.println("There has been an error during the building of the boolean form.");
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Gets the boolean form of the model.
	 */
	public String getChocoCSPForm() {
		if (!this.hasRunChocoCSPFormParser)
			this.runChocoCSPFormParser();
		if (this.hasRunChocoCSPFormParser)
			return this.chocoCSPFormText;
		return "";
	}

	/**
	 * If the boolean form of the FM exists and is valid, try to initialize the
	 * solver with it.
	 */
	public void runSolver(boolean reinitialiseVariables) {
		if (!this.hasRunSolver) {
			FeatureSymbol root = getRoot();
			if (root != null) {
				try {
					this.mySolver = new CPFeatureSolver(root, chocoSolver,reinitialiseVariables);
					this.hasRunSolver = true;
				} catch (Exception e) {
					this.solverException = e;
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns the root of the boolean form.
	 */
	public FeatureSymbol getRoot() {
		if (!this.hasRunChocoCSPFormParser)
			this.runChocoCSPFormParser();
		if (this.hasRunChocoCSPFormParser) {
			if (this.chocoCSPFormParser.isValid) {
				String rootFeatureID = chocoCSPFormParser.featuresSymbolTable.getRootFeatureID();
				return this.chocoCSPFormParser.featuresSymbolTable.getFeatureSymbol(rootFeatureID);
			}
		}
		return null;
	}

	/**
	 * Checks whether the model is satisfiable.
	 * 
	 * @throws TimeoutException
	 *             This method calls Sat4J, so it might timeout.
	 */
	public boolean isSatisfiable(boolean reinitialiseVariables) {
		if (!this.hasRunSolver)
			this.runSolver(reinitialiseVariables);
		if (this.hasRunSolver) {
			return this.mySolver.isSatisfiable();
		}
		return false;
	}

	/**
	 * Returns a list of solutions or NULL if not satisfiable. To print a
	 * solution in textual form, use
	 * "Util.toTextualIDNumericalSolution(solutions[i]));"
	 * 
	 * @throws TimeoutException
	 *             This method calls Sat4J, so it might timeout.
	 */
	public ArrayList<ArrayList<String>> getSolutions(boolean reinitialiseVariables) {
		if (!this.hasRunSolver)
			this.runSolver(reinitialiseVariables);
		if (this.hasRunSolver) {
			if (!this.mySolver.isSatisfiable())
				return null;
			return this.mySolver.getAllSolutions();
		}
		return null;
	}

	/**
	 * @return true if the feature model contained in the input file is valid.
	 */
	public boolean isValid() {
		if (!this.hasRun)
			this.run();
		return this.isValid;
	}

	/**
	 * @return If the input file could be parsed (no reference checks, type
	 *         checks, ...)
	 */
	public boolean isSyntacticallyCorrect() {
		if (!this.hasRun)
			this.run();
		if (this.syntacticalException == null)
			return true;
		else
			return false;
	}

	/**
	 * @return If there are no type errors.
	 */
	public boolean isCorrectlyTyped() {
		if (!this.hasRun)
			this.run();
		if (this.typeException == null)
			return true;
		else
			return false;
	}

	public int nbFeatures() {
		if (!this.hasRun)
			this.run();
		if (this.isValid)
			return featuresSymbolTable.getNBFeatures();
		else
			return 0;
	}

}
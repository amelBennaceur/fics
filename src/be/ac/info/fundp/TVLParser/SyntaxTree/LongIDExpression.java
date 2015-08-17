package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.logging.Logger;

import be.ac.info.fundp.TVLParser.symbolTables.AttributeSymbol;
import be.ac.info.fundp.TVLParser.symbolTables.FeaturesSymbolTable;
import be.ac.info.fundp.TVLParser.symbolTables.Symbol;

public class LongIDExpression implements BooleanExpression, Cloneable {
	
	private final static Logger LOGGER = Logger.getLogger(LongIDExpression.class.getName());
	
	
	String longID;
	String normalFormLongID;
	Symbol symbol = null;

	FeaturesSymbolTable featuresSymbolTable;

	public LongIDExpression(String longID, FeaturesSymbolTable featuresSymbolTable) {
		this.longID = longID;
		this.normalFormLongID = longID;
		this.featuresSymbolTable = featuresSymbolTable;
	}

	public String getLongID() {
		return this.longID;
	}

	@Override
	public int getType() {
		if (featuresSymbolTable == null)
			return Expression.BOOL;
		if (!featuresSymbolTable.isEmpty()) {
			if (featuresSymbolTable.containsSymbol(longID)) {
				symbol = (Symbol) featuresSymbolTable.getSymbol(longID).get(0);
				this.normalFormLongID = (String) featuresSymbolTable.getSymbol(longID).get(1);
				if (symbol.getClass().toString().contains("FeatureSymbol")) {
					return Expression.BOOL;
				} else {
					AttributeSymbol attributeSymbol = (AttributeSymbol) symbol;
					if (attributeSymbol.getType() == Expression.USER_CREATED) {
						return attributeSymbol.getTrueType();
					} else {
						return attributeSymbol.getType();
					}
				}
			} else {
				String[] array = this.longID.split("\\.");
				if (array.length > 1) {
					LOGGER.info("Type error : the symbol corresponding to the path " + this.longID
							+ " cannot be found.");
				} else {
					if (featuresSymbolTable.containsConstant(this.longID)) {
						this.normalFormLongID = featuresSymbolTable.getConstantValue(this.longID);
						return featuresSymbolTable.getConstantType(this.longID);
					} else {
						if (featuresSymbolTable.containsType(this.longID)) {
							LOGGER.info("BadTypeUseException" + this.longID);
						} else {
							this.normalFormLongID = this.longID;
							return Expression.ENUM;
						}
					}
				}
			}
		} else {
			String[] array = this.longID.split("\\.");
			if (array.length > 1) {
				LOGGER.info("Error : the symbol corresponding to the path " + this.longID + " cannot be found.");
			} else {
				// this.normalFormLongID = this.longID;
				return Expression.ENUM;
			}
		}
		return Expression.UNKNOWN;
	}

	@Override
	public LongIDExpression clone() {
		LongIDExpression clonedLongIDExpression = new LongIDExpression(this.longID, this.featuresSymbolTable);
		clonedLongIDExpression.normalFormLongID = this.normalFormLongID;
		return clonedLongIDExpression;
	}

	@Override
	public LongIDExpression toNormalForm() {
		LongIDExpression NormalizedLongIDExpression;
		if (this.symbol == null) {
			NormalizedLongIDExpression = new LongIDExpression(this.normalFormLongID, null);
		} else {
			if (symbol.getClass().toString().contains("FeatureSymbol")) {
				NormalizedLongIDExpression = new LongIDExpression(this.normalFormLongID, null);
			} else {
				NormalizedLongIDExpression = new LongIDExpression(this.normalFormLongID.concat("." + symbol.getID()),
						null);
			}
		}
		return NormalizedLongIDExpression;
	}

	@Override
	public String toString() {
		return this.longID;
	}

	public Symbol getSymbol() {
		return this.symbol;
	}

	public String getNormalizedLongID() {
		return this.normalFormLongID;
	}

	@Override
	public BooleanExpression removeArrows() {
		return this;
	}

	@Override
	public BooleanExpression toSimplifiedForm() {
		return this.toNormalForm();

	}

	@Override
	public BooleanExpression distributeDisjunctions() {
		return this;
	}

	@Override
	public BooleanExpression distributeNegations() {
		return this;
	}

}

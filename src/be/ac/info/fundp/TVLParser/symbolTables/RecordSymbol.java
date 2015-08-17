package be.ac.info.fundp.TVLParser.symbolTables;

import java.util.Map;
import java.util.logging.Logger;

public class RecordSymbol extends AttributeSymbol implements TypeSymbol {

	private final static Logger LOGGER = Logger.getLogger(RecordSymbol.class.getName());
	
	private String id, userType;
	private int type;

	private Map<String, AttributeSymbol> attributeSymbols;

	public RecordSymbol(String userType, int type, String id, Map<String, AttributeSymbol> attributeSymbols) {
		this.userType = userType;
		this.id = id;
		this.attributeSymbols = attributeSymbols;
		this.type = type;
	}

	public RecordSymbol(int type, String id, Map<String, AttributeSymbol> attributeSymbols) {
		this.userType = null;
		this.id = id;
		this.attributeSymbols = attributeSymbols;
		this.type = type;
	}

	@Override
	public boolean isARecordSymbol() {
		return true;
	}

	public AttributeSymbol getAttributeSymbol(String attributeSymbolID) {
		return this.attributeSymbols.get(attributeSymbolID);
	}

	public Map<String, AttributeSymbol> getAttributeSymbols() {
		return this.attributeSymbols;
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public int getType() {
		return this.type;
	}

	public boolean containsRecordField(String recordFieldID) {
		return this.attributeSymbols.containsKey(recordFieldID);
	}

	@Override
	public void printAttribute(String espace) {
		LOGGER.info(espace + "  |      " + this.userType + " struct " + this.id + " {");
		int i = 0;
		Object[] keys = attributeSymbols.keySet().toArray();
		while (i <= attributeSymbols.size() - 1) {
			attributeSymbols.get(keys[i].toString()).printAttribute("  " + espace);
			i++;
		}
		LOGGER.info(espace + "  |      }");
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getUserType() {
		return userType;
	}

	@Override
	public void print() {
		LOGGER.info("  struct " + id + " {");
		int i = 0;
		Object[] keys = attributeSymbols.keySet().toArray();
		while (i <= attributeSymbols.size() - 1) {
			attributeSymbols.get(keys[i].toString()).printAttribute("");
			i++;
		}
		LOGGER.info("  }");
	}

}

package be.ac.info.fundp.TVLParser.SyntaxTree;

public class DataPair {

	String key, text;

	public DataPair(String key, String text) {
		this.key = key;
		this.text = text;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

}

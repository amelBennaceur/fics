package be.ac.info.fundp.TVLParser.SyntaxTree;

public interface Type extends ModelItem {

	public int getType();

	public String getID();

	public boolean isARecord();

	@Override
	public boolean isAFeature();

	@Override
	public boolean isAconstant();

	@Override
	public boolean isAType();
}

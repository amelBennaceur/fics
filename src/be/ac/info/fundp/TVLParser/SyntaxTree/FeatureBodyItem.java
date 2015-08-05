package be.ac.info.fundp.TVLParser.SyntaxTree;

public interface FeatureBodyItem {

	public boolean isAData();

	public boolean isAnAttribute();

	public boolean isAConstraint();

	public boolean isAFeatureGroup();

}

package be.ac.info.fundp.TVLParser.SyntaxTree;

public class FeatureGroup implements FeatureBodyItem {

	Cardinality cardinality;
	HierarchicalFeatures hierarchicalFeatures;

	public FeatureGroup(Cardinality cardinality, HierarchicalFeatures hierarchicalFeatures) {
		this.cardinality = cardinality;
		this.hierarchicalFeatures = hierarchicalFeatures;
	}

	/**
	 * @return the cardinality
	 */
	public Cardinality getCardinality() {
		return cardinality;
	}

	/**
	 * @return the hierarchicalFeatures
	 */
	public HierarchicalFeatures getHierarchicalFeatures() {
		return hierarchicalFeatures;
	}

	@Override
	public boolean isAConstraint() {
		return false;
	}

	@Override
	public boolean isAData() {
		return false;
	}

	@Override
	public boolean isAFeatureGroup() {
		return true;
	}

	@Override
	public boolean isAnAttribute() {
		return false;
	}

}

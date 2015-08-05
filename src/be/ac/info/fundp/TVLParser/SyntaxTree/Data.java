package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;

public class Data implements FeatureBodyItem {

	Vector<DataPair> dataPairs;

	public Data(DataPairList dataPairList) {
		this.dataPairs = dataPairList.getDataPairs();
		;
	}

	/**
	 * @return the dataPairList
	 */
	public Vector<DataPair> getDataPairs() {
		return dataPairs;
	}

	// Utilisé pour la construction d'une feature
	@Override
	public boolean isAData() {
		return true;
	}

	@Override
	public boolean isAnAttribute() {
		return false;
	}

	@Override
	public boolean isAConstraint() {
		return false;
	}

	@Override
	public boolean isAFeatureGroup() {
		return false;
	}

}

package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;

public class FeatureBody {

	Vector<FeatureBodyItem> items;

	public FeatureBody(FeatureBodyItem item, FeatureBody featureBody) {
		if (featureBody == null)
			this.items = new Vector<FeatureBodyItem>();
		else
			this.items = featureBody.getItems();
		this.items.add(item);
	}

	public Vector<FeatureBodyItem> getItems() {
		return this.items;
	}

}

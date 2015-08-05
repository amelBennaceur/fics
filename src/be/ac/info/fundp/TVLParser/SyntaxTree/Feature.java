package be.ac.info.fundp.TVLParser.SyntaxTree;

import java.util.Vector;

public class Feature implements ModelItem {

	boolean optionnal, shared, root, featureGroupDefined;
	String ID, minCardinality, maxCardinality;

	Vector<Data> data;
	Vector<Constraint> constraints;
	Vector<Attribute> attributes;
	Vector<Feature> childrenFeatures;

	// Aussi utilisé pour la construction du modèle
	@Override
	public boolean isAFeature() {
		return true;
	}

	public void setOptional() {
		this.optionnal = true;
	}

	public void setCardinality(Cardinality card) {
		this.maxCardinality = card.getMax();
		this.minCardinality = card.getMin();
		if (card.getMin().equals("0"))
			this.setOptional();
	}

	public String toString(String whiteSpace) {
		String text = new String();
		if (this.isShared()) {
			return whiteSpace + "shared " + this.ID;
		} else {
			if (this.isRoot()) {
				text = whiteSpace + "root " + this.ID + " {\n";
			} else {
				if (this.isOptionnal()) {
					text = whiteSpace + "opt " + this.ID + " {\n";
				} else {
					text = whiteSpace + this.ID + " {\n";
				}

			}
			if (this.hasAttributes()) {
				int i = 0;
				while (i <= this.attributes.size() - 1) {
					text = text.concat(whiteSpace + "    " + this.attributes.get(i).toString() + "\n");
					i++;
				}
			}
			if (this.hasConstraints()) {
				int i = 0;
				while (i <= this.constraints.size() - 1) {
					text = text.concat(whiteSpace + "    " + this.constraints.get(i).toString() + "\n");
					i++;
				}
			}
			if (this.hasChildrenFeatures()) {
				int i = 0;
				text = text.concat(whiteSpace + "    group [ " + this.minCardinality + ".." + this.maxCardinality
						+ " ] {\n");
				while (i <= this.childrenFeatures.size() - 2) {
					text = text.concat(this.childrenFeatures.get(i).toString(whiteSpace + "        ") + ",\n");
					i++;
				}
				text = text.concat(this.childrenFeatures.get(this.childrenFeatures.size() - 1).toString(
						whiteSpace + "        ")
						+ "\n");
				text = text.concat(whiteSpace + "    }\n");
			}
			if (this.hasData()) {
				// TODO
			}
		}
		return text = text.concat(whiteSpace + "}");
	}

	public Feature(String ID, Cardinality card, FeatureBody featureBody) {
		this(ID, featureBody);
		this.setCardinality(card);
	}

	public Feature(String ID, Cardinality card) {
		this(ID);
		this.setCardinality(card);
	}

	public Feature(String ID, FeatureBody featureBody) {
		this.ID = ID;
		this.shared = false;
		this.optionnal = false;
		this.root = false;
		this.featureGroupDefined = false;
		int i = 0;
		if (!(featureBody == null)) {
			while (i <= featureBody.getItems().size() - 1) {
				if (featureBody.getItems().get(i).isAConstraint()) {
					if (this.constraints == null)
						this.constraints = new Vector<Constraint>();
					this.constraints.add((Constraint) featureBody.getItems().get(i));
				}
				if (featureBody.getItems().get(i).isAnAttribute()) {
					if (this.attributes == null)
						this.attributes = new Vector<Attribute>();
					this.attributes.add((Attribute) featureBody.getItems().get(i));
				}
				if (featureBody.getItems().get(i).isAData()) {
					if (this.data == null)
						this.data = new Vector<Data>();
					this.data.add((Data) featureBody.getItems().get(i));
				}
				if (featureBody.getItems().get(i).isAFeatureGroup()) {
					if (this.featureGroupDefined)
						System.out.println("ChildrenFeaturesGroupAlreadySpecifiedException");
					FeatureGroup featureGroup = (FeatureGroup) featureBody.getItems().get(i);
					this.minCardinality = featureGroup.getCardinality().getMin();
					this.maxCardinality = featureGroup.getCardinality().getMax();
					this.childrenFeatures = featureGroup.getHierarchicalFeatures().getFeatures();
					this.featureGroupDefined = true;
				}
				i++;
			}
		}
	}

	public Feature(String ID, FeatureBody featureBody, boolean root) {
		this.ID = ID;
		this.shared = false;
		this.featureGroupDefined = false;
		this.optionnal = false;
		this.root = root;
		int i = 0;
		while (i <= featureBody.getItems().size() - 1) {
			if (featureBody.getItems().get(i).isAConstraint()) {
				if (this.constraints == null)
					this.constraints = new Vector<Constraint>();
				this.constraints.add((Constraint) featureBody.getItems().get(i));
			}
			if (featureBody.getItems().get(i).isAnAttribute()) {
				if (this.attributes == null)
					this.attributes = new Vector<Attribute>();
				this.attributes.add((Attribute) featureBody.getItems().get(i));
			}
			if (featureBody.getItems().get(i).isAData()) {
				if (this.data == null)
					this.data = new Vector<Data>();
				this.data.add((Data) featureBody.getItems().get(i));
			}
			if (featureBody.getItems().get(i).isAFeatureGroup()) {
				if (this.featureGroupDefined)
					System.out.println("ChildrenFeaturesGroupAlreadySpecifiedException");
				FeatureGroup featureGroup = (FeatureGroup) featureBody.getItems().get(i);
				this.minCardinality = featureGroup.getCardinality().getMin();
				this.maxCardinality = featureGroup.getCardinality().getMax();
				this.childrenFeatures = featureGroup.getHierarchicalFeatures().getFeatures();
				this.featureGroupDefined = true;
			}
			i++;
		}
	}

	// Pour les features de listes
	public Feature(boolean optionnal, String ID) {
		this.optionnal = optionnal;
		this.ID = ID;
		this.shared = false;
		this.data = null;
		this.constraints = null;
		this.attributes = null;
		this.childrenFeatures = null;
		this.root = false;
		this.minCardinality = null;
		this.maxCardinality = null;
		this.featureGroupDefined = false;
	}

	public Feature(String ID, boolean shared) {
		this.optionnal = false;
		this.ID = ID;
		this.shared = shared;
		this.data = null;
		this.constraints = null;
		this.attributes = null;
		this.childrenFeatures = null;
		this.root = false;
		this.minCardinality = null;
		this.maxCardinality = null;
		this.featureGroupDefined = false;
	}

	public Feature(String ID) {
		this.optionnal = false;
		this.ID = ID;
		this.shared = false;
		this.data = null;
		this.constraints = null;
		this.attributes = null;
		this.childrenFeatures = null;
		this.root = false;
		this.minCardinality = null;
		this.maxCardinality = null;
		this.featureGroupDefined = false;
	}

	public Feature(String ID, FeatureGroup featureGroup) {
		this.ID = ID;
		this.minCardinality = featureGroup.cardinality.getMin();
		this.maxCardinality = featureGroup.cardinality.getMax();
		this.childrenFeatures = featureGroup.getHierarchicalFeatures().getFeatures();
		this.data = null;
		this.attributes = null;
		this.constraints = null;
		this.root = false;
		this.optionnal = false;
		this.shared = false;
		this.featureGroupDefined = false;
	}

	public Feature(String ID, FeatureGroup featureGroup, boolean root) {
		this.ID = ID;
		this.minCardinality = featureGroup.cardinality.getMin();
		this.maxCardinality = featureGroup.cardinality.getMax();
		this.childrenFeatures = featureGroup.getHierarchicalFeatures().getFeatures();
		this.data = null;
		this.attributes = null;
		this.constraints = null;
		this.root = root;
		this.optionnal = false;
		this.shared = false;
		this.featureGroupDefined = false;
	}

	/**
	 * @return the optionnal
	 */
	public boolean isOptionnal() {
		return optionnal;
	}

	/**
	 * @return the shared
	 */
	public boolean isShared() {
		return shared;
	}

	/**
	 * @return the root
	 */
	public boolean isRoot() {
		return root;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @return the minCardinality
	 */
	public String getMinCardinality() {
		return minCardinality;
	}

	/**
	 * @return the maxCardinality
	 */
	public String getMaxCardinality() {
		return maxCardinality;
	}

	/**
	 * @return the data
	 */
	public Vector<Data> getData() {
		return data;
	}

	/**
	 * @return the constraints
	 */
	public Vector<Constraint> getConstraints() {
		return constraints;
	}

	/**
	 * @return the attributes
	 */
	public Vector<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the childrenFeatures
	 */
	public Vector<Feature> getChildrenFeatures() {
		return childrenFeatures;
	}

	// Utilisé pour la construction du modèle
	@Override
	public boolean isAType() {
		;
		return false;
	}

	@Override
	public boolean isAconstant() {
		return false;
	}

	public boolean hasConstraints() {
		if (this.constraints != null)
			return true;
		else
			return false;
	}

	public boolean hasAttributes() {
		if (this.attributes != null)
			return true;
		else
			return false;
	}

	public boolean hasChildrenFeatures() {
		if (this.childrenFeatures != null)
			return true;
		else
			return false;
	}

	public boolean hasData() {
		if (this.data != null)
			return true;
		else
			return false;

	}
}

package uk.ac.open.capability.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "featuredTransition")
@XmlAccessorType(XmlAccessType.FIELD)
public class FeaturedTransition extends Transition {

	@XmlElement(name = "feature")
	@XmlElementWrapper(name = "features")
	private List<String> featureExpr;

	public FeaturedTransition() {

	}

	public FeaturedTransition(String source, String target, String action, ArrayList<String> featureExpr) {
		this.source = source;
		this.target = target;
		this.action = action;
		this.featureExpr = featureExpr;
	}

	public List<String> getFeatureExpr() {
		return featureExpr;
	}

	public void setFeatureExpr(List<String> featureExpr) {
		this.featureExpr = featureExpr;
	}
	
	@Override
	public String toString() { 
		String str = source+"->"+action+"/"+featureExpr+"->"+target;
		return str;		
	}

}

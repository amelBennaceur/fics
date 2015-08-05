package uk.ac.open.capability.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import be.ac.info.fundp.TVLParser.Util.FeatureTree;

@XmlRootElement(name = "fts")
public class FeaturedTransitionSystem extends TransitionSystem {

	@XmlElement(name = "featuredTransition")
	@XmlElementWrapper(name = "featuredTransitions")
	List<FeaturedTransition> trans = new ArrayList<FeaturedTransition>();

	public FeaturedTransitionSystem() {

	}

	public boolean isSyntaxCorrect(FeatureTree tree) {
		for (FeaturedTransition tr : trans) {
			if ((!myStates.containsKey(tr.source)) || (!myStates.containsKey(tr.target)))
				return false;
			for (String st : tr.getFeatureExpr()) {
				try {
					if (tree.findFeatureByShortId(st) == null)
						return false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public TransitionSystem project(List<String> selectedFeatures) {
		TransitionSystem projected = new TransitionSystem();
		return projected;
	}
	
	@Override
	public String toString() { 
		String str = "States: \n";
		for(State st:states){
			str += "       - "+st+"\n";
		}
		str += "Transitions: \n";
		for(FeaturedTransition tr:trans){
			str += "       - "+tr+"\n";
		}
//		str = "Initial States: \n";
//		for(State st:initialStates){
//			str += "       - "+st+"\n";
//		}
		return str;
	}

}

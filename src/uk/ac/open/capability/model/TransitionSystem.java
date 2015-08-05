package uk.ac.open.capability.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class TransitionSystem {
	@XmlElement(name = "state")
	@XmlElementWrapper(name = "states")
	List<State> states = new ArrayList<State>();

	HashMap<String, State> myStates = new HashMap<String, State>();

	List<Transition> transi = new ArrayList<Transition>();

	@XmlElement(name = "initialState")
	@XmlElementWrapper(name = "initialStates")
	List<State> initialStates = new ArrayList<State>();

	public boolean addState(State st) {
		if (!myStates.containsKey(st.getName())) {
			myStates.put(st.getName(), st);
			states.add(st);
		}
		return false;
	}

	public void synchStates() {
		for (State st : states) {
			if (!myStates.containsKey(st.name))
				myStates.put(st.name, st);
		}
	}

	public boolean addTransition(Transition tr) {
		if ((!myStates.containsKey(tr.source)) || (!myStates.containsKey(tr.target))) {
			return false;
		}
		transi.add(tr);
		return true;
	}

	public boolean addInitialState(String stName) {
		if (!myStates.containsKey(stName))
			return false;
		initialStates.add(myStates.get(stName));
		return true;
	}

	
	@Override
	public String toString() { 
		String str = "States: \n";
		for(State st:states){
			str += "       - "+st+"\n";
		}
		str += "Transitions: \n";
		for(Transition tr:transi){
			str += "       - "+tr+"\n";
		}
//		str = "Initial States: \n";
//		for(State st:initialStates){
//			str += "       - "+st+"\n";
//		}
		return str;
	}
	
	// public boolean isSyntaxCorrect() {
	// // TODO Auto-generated method stub
	// return true;
	// }
}
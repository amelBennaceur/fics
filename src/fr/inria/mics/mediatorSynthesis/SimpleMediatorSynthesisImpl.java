package fr.inria.mics.mediatorSynthesis;

import uk.ac.open.capability.model.LTLGoal;
import uk.ac.open.capability.model.State;
import uk.ac.open.capability.model.Transition;
import uk.ac.open.capability.model.TransitionSystem;

public class SimpleMediatorSynthesisImpl implements MediatorSynthesisInterface {

	@Override
	public TransitionSystem synthesiseAbstractMediator(TransitionSystem[] lts, LTLGoal goals) {
		TransitionSystem med = null;
		med = SampleMediatorCollaborativeRobotsExample();
		return med;
	}

	private TransitionSystem SampleMediatorCollaborativeRobotsExample() {
		TransitionSystem ts = new TransitionSystem();
		ts.addState(new State("S0", "location=0"));
		ts.addState(new State("S1", "location(NAO)"));
		ts.addState(new State("S2", "location(NAO),location(Create)"));
		ts.addState(new State("S3", "location(NAO),location(Create),location(phone)"));
		ts.addState(new State("S4", "location(NAO)=location(phone)"));
		ts.addState(new State("S5", "location(NAO)=location(Create)"));
		ts.addState(new State("S6", "location(Create)=location(phone)"));
		ts.addState(new State("S7", "location(Create)=location.SAFE"));
		ts.addState(new State("S8", "location(Create)=location.SAFE"));
		ts.addState(new State("S9", "location(Create)=location.SAFE"));
		ts.addTransition(new Transition("S0", "S1", "NAO.connect"));
		ts.addTransition(new Transition("S1", "S2", "Create.connect"));
		ts.addTransition(new Transition("S2", "S3", "NAO.locate(phone)"));
		ts.addTransition(new Transition("S3", "S4", "NAO.pick(phone)"));
		ts.addTransition(new Transition("S4", "S5", "Create.move(location(NAO))"));
		ts.addTransition(new Transition("S5", "S6", "NAO.drop(phone)"));
		ts.addTransition(new Transition("S6", "S7", "Create.move(location.SAFE)"));
		ts.addTransition(new Transition("S7", "S8", "Create.disconnect"));
		ts.addTransition(new Transition("S8", "S9", "NAO.disconnect"));
		ts.addInitialState("S0");
		return ts;
	}

}

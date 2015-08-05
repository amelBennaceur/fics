package fr.inria.mics.mediatorSynthesis;

import uk.ac.open.capability.model.LTLGoal;
import uk.ac.open.capability.model.TransitionSystem;

public interface MediatorSynthesisInterface {

	public TransitionSystem synthesiseAbstractMediator(TransitionSystem[] lts, LTLGoal goals);
}

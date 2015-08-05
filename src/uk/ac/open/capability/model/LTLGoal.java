package uk.ac.open.capability.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "goal")
@XmlAccessorType(XmlAccessType.FIELD)
public class LTLGoal {

	@XmlElement(name = "predicate")
	List<Predicate> formula = new ArrayList<Predicate>();

	@Override
	public String toString() {
		String st = "";
		for (Predicate p : formula)
			st += p;
		return st;
	}
}

@XmlRootElement(name = "Predicate")
@XmlAccessorType(XmlAccessType.FIELD)
class Predicate {
	@XmlElement(name = "exist")
	String exist;
	@XmlElement(name = "proposition")
	String proposition;

	@Override
	public String toString() {
		String st = "";
		st += (exist.equalsIgnoreCase("eventually")) ? "<>" : "[]";
		st += proposition;
		return st;
	}
}
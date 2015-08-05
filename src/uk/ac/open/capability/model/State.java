package uk.ac.open.capability.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "state")
@XmlAccessorType(XmlAccessType.FIELD)
public class State {

	@XmlAttribute(name = "name")
	String name;

	@XmlElement(name = "predicate")
	String predicate;

	public State() {

	}

	public State(String name, String predicate) {
		this.name = name;
		this.predicate = predicate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public String toString() { 
		return "State["+name+", "+predicate+"]";		
	}
}

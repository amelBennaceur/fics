package uk.ac.open.capability.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Transition {

	@XmlElement(name = "source")
	String source;

	@XmlElement(name = "target")
	String target;

	@XmlElement(name = "action")
	String action;

	public Transition() {

	}

	public Transition(String source, String target, String action) {
		this.source = source;
		this.target = target;
		this.action = action;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() { 
		return source+"->"+action+"->"+target;		
	}
}

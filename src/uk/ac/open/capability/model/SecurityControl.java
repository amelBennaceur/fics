package uk.ac.open.capability.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "securityControl")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecurityControl {

	@XmlElement(name = "feature")
	@XmlElementWrapper(name = "features")
	List<String> features;

	@XmlElement(name = "attribute")
	@XmlElementWrapper(name = "attributes")
	List<String> attributes;

	@XmlElement(name = "goal")
	LTLGoal goal;

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public LTLGoal getGoal() {
		return goal;
	}

	public void setGoal(LTLGoal goal) {
		this.goal = goal;
	}

	public static SecurityControl loadSecurityControlFromFile(String filePath) {
		try {
			File file = new File(filePath);
			JAXBContext context = JAXBContext.newInstance(SecurityControl.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			SecurityControl sc = (SecurityControl) um.unmarshal(file);
			return sc;
		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Saves the current security control to the specified file.
	 * 
	 * @param file
	 */
	public void saveSecurityControlToFile(String filePath) {
		try {
			File file = new File(filePath);
			JAXBContext context = JAXBContext.newInstance(SecurityControl.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Marshalling and saving XML to the file.
			m.marshal(this, file);

		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		SecurityControl sc = new SecurityControl();
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add("Motion");
		tmp.add("Vision");
		sc.features = tmp;
		tmp = new ArrayList<String>();
		tmp.add("Speed");
		sc.attributes = tmp;
		LTLGoal goal = new LTLGoal();
		List<Predicate> formula = new ArrayList<Predicate>();
		Predicate pred = new Predicate();
		pred.exist = "eventually";
		pred.proposition = "location(phone)=location.SAFE";
		formula.add(pred);
		goal.formula = formula;
		sc.goal = goal;

		sc.saveSecurityControlToFile("examples/TestSecurityControlMarshal.xml");

	}
}

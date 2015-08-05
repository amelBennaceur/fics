package uk.ac.open.capability.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import solver.Solver;

public class Capability {
	String name;

	FeatureModel fm;

	FeaturedTransitionSystem fts;

	public Capability() {

	}

	public Capability(String name) {
		this.name = name;
	}

	public FeatureModel getFm() {
		return fm;
	}

	public void setFm(FeatureModel fm) {
		this.fm = fm;
	}

	public FeaturedTransitionSystem getFts() {
		return fts;
	}

	public void setFts(FeaturedTransitionSystem fts) {
		this.fts = fts;
	}

	public boolean loadCapability(String fmFilePath, String behFilePath, Solver chocoSolver) {
		try {
			fm = new FeatureModel(fmFilePath, chocoSolver);

			name = fm.getRootName();

			loadFeaturedTransitionSystemFromFile(behFilePath);

//			if (!fts.isSyntaxCorrect(fm.getTree()))
//				return false;
//			if (!checkFMAndFTS())
//				return false;
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private boolean checkFMAndFTS() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getName() {
		return name;
	}

	public void setFTS(FeaturedTransitionSystem fts) {
		this.fts = fts;
	}

	public void loadFeaturedTransitionSystemFromFile(String filePath) {
		try {
			File file = new File(filePath);
			JAXBContext context = JAXBContext.newInstance(FeaturedTransitionSystem.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			fts = (FeaturedTransitionSystem) um.unmarshal(file);

			fts.synchStates();

		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
		}
	}

	/**
	 * Saves the current fts to the specified file.
	 * 
	 * @param file
	 */
	public void saveFeaturedTransitionSystemToFile(String filePath) {
		try {
			File file = new File(filePath);
			JAXBContext context = JAXBContext.newInstance(FeaturedTransitionSystem.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Marshalling and saving XML to the file.
			m.marshal(fts, file);

		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// create an FTS
		FeaturedTransitionSystem fts = new FeaturedTransitionSystem();
		fts.states = new ArrayList<State>();
		fts.states.add(new State("S1", ""));
		fts.states.add(new State("S2", "tick"));
		fts.trans = new ArrayList<FeaturedTransition>();
		ArrayList<String> featureExpr = new ArrayList<String>();
		featureExpr.add("feature1");
		featureExpr.add("feature2");
		fts.trans.add(new FeaturedTransition("S1", "S2", "action", featureExpr));
		fts.initialStates = new ArrayList<State>();
		fts.initialStates.add(new State("S1", ""));

		// create a capability with the fts
		Capability capa = new Capability("Capa");
		capa.setFTS(fts);

		capa.saveFeaturedTransitionSystemToFile("examples/TestCapabilityMarshal.xml");

	}
}

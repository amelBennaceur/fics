package uk.ac.open.tosemExperiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.open.fics.MainApp;

public class Evaluation {

	public void generateNAOFiles(String tvlPath, String xmlPath, String replaceStr) {
		try {
			// Feature Model
			FileWriter fw = new FileWriter(tvlPath);
			Reader fr = new FileReader("examples/tests/collaborativeRobots/naoFM.tvl");
			BufferedReader br = new BufferedReader(fr);
			while (br.ready()) {
				fw.write(br.readLine().replaceAll("9", replaceStr) + "\n");
			}
			fw.close();
			br.close();
			fr.close();

			// Behaviour
			fw = new FileWriter(xmlPath);
			fr = new FileReader("examples/tests/collaborativeRobots/naoFTS.xml");
			br = new BufferedReader(fr);
			while (br.ready()) {
				fw.write(br.readLine().replaceAll("9", replaceStr) + "\n");
			}
			fw.close();
			br.close();
			fr.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generateCreateFiles(String str) {

	}

	public void measureOriginalScenario() {

	}

	public void measureNAOAlone() {

	}

	public void generateCapabilities(int capabilityNumber) {
		String path1 = "examples/tests/collaborativeRobots/generatedCapabilities/";

		new File(path1).mkdirs();

		for (int i = 1; i < capabilityNumber; i++) {
			generateNAOFiles(path1 + "naoFM" + i + ".tvl", path1 + "naoFTS" + i + ".xml", i + "");
		}

		// change speed value manually
	}

	public void measureScenario1WithFeatures(int capabilityNumber) {
		String path1 = "examples/tests/collaborativeRobots/generatedCapabilities/";

		MainApp app = new MainApp();
		app.reinit();

		// loading capabilities
		for (int i = 0; i < capabilityNumber; i++) {
			app.addCapability(path1 + "naoFM" + i + ".tvl", path1 + "naoFTS" + i + ".xml");
		}
		// loading security control
		app.setSecurityControl("examples/tests/collaborativeRobots/movePhoneSC.xml");

		// launching the composition
		long t0 = System.currentTimeMillis();
		app.compose(false);
		long processingTime = System.currentTimeMillis() - t0;
		// System.out.println("sol = " + app.getFeatures());
		System.out.println("" + processingTime);

	}

	public void measureScenario1WithoutFeatures(int capabilityNumber) {

	}

	public void measureScenario2WithFeatures(int capabilityNumber) {
		String path1 = "examples/tests/collaborativeRobots/generatedCapabilities/";

		MainApp app = new MainApp();
		app.reinit();

		// loading capabilities
		for (int i = 0; i < capabilityNumber; i++) {
			app.addCapability(path1 + "naoFM" + i + ".tvl", path1 + "naoFTS" + i + ".xml");
		}
		// loading security control
		app.setSecurityControl("examples/tests/collaborativeRobots/movePhoneSCWithoutOptimisation.xml");

		// launching the composition
		long t0 = System.currentTimeMillis();
		app.compose(false);
		long processingTime = System.currentTimeMillis() - t0;
		// System.out.println("sol = " + app.getFeatures());
		System.out.println("" + processingTime);

	}

	public void measureScenario2WithoutFeatures(int capabilityNumber) {

	}

	public static void main(String[] args) {
		Evaluation eCS = new Evaluation();

		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.SEVERE);

		int capabilityNumber = 1;
		int repetitions = 31;
		
//		eCS.generateCapabilities();
		
//		for (int j = 0; j < repetitions; j++) {
//			eCS.measureScenario1WithFeatures(capabilityNumber);
//		}
		
//		for (int j = 0; j < repetitions; j++) {
//			eCS.measureScenario1WithoutFeatures(capabilityNumber);
//		}
		
		for (int j = 0; j < repetitions; j++) {
			eCS.measureScenario2WithFeatures(capabilityNumber);
		}
		
//		for (int j = 0; j < repetitions; j++) {
//			eCS.measureScenario2WithoutFeatures(capabilityNumber);
//		}

	}

}
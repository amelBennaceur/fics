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
	private static final String scPath = "examples/tests/collaborativeRobots/movePhoneSC.xml";
	
	
	public void generateNAOFiles(String tvlPath, String xmlPath, String replaceStr){
		try {
			//Feature Model
			FileWriter fw = new FileWriter(tvlPath);
			Reader fr = new FileReader("examples/tests/collaborativeRobots/naoFM.tvl");
		    BufferedReader br = new BufferedReader(fr);
			while(br.ready()) {
			    fw.write(br.readLine().replaceAll("9", replaceStr) + "\n");
			}
			fw.close();
		    br.close();
		    fr.close();
		    
		    //Behaviour
		    fw = new FileWriter(xmlPath);
			fr = new FileReader("examples/tests/collaborativeRobots/naoFTS.xml");
		    br = new BufferedReader(fr);
			while(br.ready()) {
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
	
	public void generateCreateFiles(String str){
		
	}
	
	
	public void measureOriginalScenario(){
		
	}
	
	public void measureNAOAlone(){
		
	}
	
	public void generateScenario1(){
		String path1 = "examples/tests/collaborativeRobots/scenario1/";
		
		new File(path1).mkdirs();
		
		for(int i=1;i<11;i++){
			generateNAOFiles(path1+"naoFM"+i+".tvl",path1+"naoFTS"+i+".xml",i+"");
		}
		
		//change speed value manually
	}
	
	public void measureScenario1WithFeatures(){
		String path1 = "examples/tests/collaborativeRobots/scenario1/";
		

		MainApp app = new MainApp();
		app.reinit();
		
		
		//loading capabilities
		for(int i=1;i<2;i++){
			app.addCapability(path1+"naoFM"+i+".tvl",path1+"naoFTS"+i+".xml");
		}
		//loading security control
		app.setSecurityControl(scPath);
		
		//launching the composition
		long t0 = System.currentTimeMillis();
		app.compose(false);
		long processingTime = System.currentTimeMillis()-t0;
		//System.out.println("sol = " + app.getFeatures());
		System.out.println(""+processingTime);
		
	}
	
	public void measureScenario1WithoutFeatures(){
		
	}
	
	public static void main(String[] args) {
		Evaluation eCS = new Evaluation();
		
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.SEVERE);
		
		
		
//		eCS.generateScenario1();
		for(int j=0;j<32;j++){
		eCS.measureScenario1WithFeatures();
		}

	}

}
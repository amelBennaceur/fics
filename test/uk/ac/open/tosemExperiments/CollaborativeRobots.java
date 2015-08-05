package uk.ac.open.tosemExperiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.fics.MainApp;

public class CollaborativeRobots {
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
		
		//loading capabilities
		for(int i=1;i<2;i++){
			//app.addCapability(path1+"naoFM"+i+".tvl",path1+"naoFTS"+i+".xml");
			app.addCapability("examples/naoFM.tvl","examples/naoFTS.xml");
		}
		//loading security control
		SecurityControl sc = SecurityControl.loadSecurityControlFromFile(scPath);
		app.setSecurityControl(sc);
		
		//launching the composition
		long t0 = System.currentTimeMillis();
//		app.compose();
//		long processingTime = System.currentTimeMillis()-t0;
//		if (MainApp.DEBUG) System.out.println("Time = "+processingTime);
		
	}
	
	public void measureScenario1WithoutFeatures(){
		
	}
	
	public static void main(String[] args) {
		CollaborativeRobots eCS = new CollaborativeRobots();
		
//		eCS.generateScenario1();
		eCS.measureScenario1WithFeatures();

	}

}

package uk.ac.open.capabilitySelection;

import java.io.File;

import uk.ac.open.capability.model.SecurityControl;
import uk.ac.open.fics.MainApp;

public class Tmp {
	String path;
	private static final String scPath = "examples/tests/collaborativeRobots/movePhoneSC.xml";
	public void generate(){
		path = "examples/tests/collaborativeRobots/"+System.nanoTime();
		new File(path).mkdirs();
		
		for(int i=1;i<11;i++){
			generateNAOFile(path+"naoFM"+i+".tvl",path+"naoFTS"+i+".xml",i+"");
		}
	}

	public void scenario1(){
		MainApp app = new MainApp();
		
		//loading capabilities
		for(int i=1;i<11;i++){
			app.addCapability(path+"naoFM"+i+".tvl",path+"naoFTS"+i+".xml");
		}
		//loading security control
		SecurityControl sc = SecurityControl.loadSecurityControlFromFile(scPath);
		app.setSecurityControl(sc);
		
		//launching the composition
		
		app.compose();
		
		
		
	}
	
	
	
	
	
	
	
	
	private void generateNAOFile(String string, String string2, String string3) {
		// TODO Auto-generated method stub
		
	}

}

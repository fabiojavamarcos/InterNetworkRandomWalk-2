package control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import Application.OSType;
import Model.OntologyManagerTab;
import Model.OntologyManagerTab.MinimizeGraph;
import Model.OntologyManagerTab.OpenO1;
import Model.OntologyManagerTab.OpenO2;
import Model.OntologyManagerTab.RunOnt;
import Model.OntologyManagerTab.SaveR;
import Model.OntologyManagerTab.ShowHideIRI1;
import Model.OntologyManagerTab.ShowHideIRI2;
import Model.OntologyManagerTab.ShowHideIRIResults;
import Ontology.Graph;
import Ontology.RandomWalk;

public class Control {
	
	private String Log;
	boolean ProjOp = false;
	boolean HideIRI1 = false;
	boolean HideIRI2 = false;
	boolean HideIRIResults = false;
	boolean CleanUp = false;
	
    private String pathOnt1; 			//Ontology1 Path
    private String nameOnt1; 			//Ontology1 Name
    private Object[] Ont1; 				//Object used to store Ontology 1
    
    /*		obj[0] = ontology;
			obj[1] = factory;
			obj[2] = manager;
            obj[3] = ontPath;
  
            */
    private Graph gOnt1; 				//Graph for Ontology 1
    private Graph [] agOnt1; 				//Array Graph for Ontology 1
    private String IRIOnt1;				//IRI for the original Ontology 1
    private String pathOnt2; 			//Ontology2 Path
    private String nameOnt2; 			//Ontology2 Name
    private Object[] Ont2;				//Object used to store Ontology 2
    private Graph gOnt2; 				//Graph for Ontology 2
    private Graph [] agOnt2; 				//Graph for Ontology 2
    private String IRIOnt2;				//IRI for the original Ontology 2
    private Graph gResults; 			//Graph for resulting Ontology
    private String pathOntResults; 		//Resulting Ontology save path
    private String nameOntResults;		//Resulting Ontology save name
    private boolean ShowBottomWarning;

    private ArrayList <String> network1 = new ArrayList(); // network 1 to match
    private ArrayList <String> network2 = new ArrayList(); // network 2 to match
    private String path; // path to OWL files
    private int n1; // n ontologies to compose network 1
    private int n2; // n ontologies to compose network 2
    private String LogBatch = " ";
    private ArrayList ontologiesProcessed = new ArrayList(); // ontologies processed including their graphs 
    
 // salva trabalhos temporários
 	private	Map <String,String> partialUnionResultsN1 = new HashMap<String,String>();
 	private	Map <String,String> partialUnionResultsN2 = new HashMap<String,String>();
 	private	Map <String,String> partialIntersectionResultsN1N2Names = new HashMap<String,String>();
 		
 	private	List <String> partialIntersectionResultsN1N2 = new ArrayList<String>();
 	private	Map <String,String> partialDifferenceResultsN1 = new HashMap<String,String>();
 	private	Map <String,String> partialDifferenceResultsN2 = new HashMap<String,String>();
 	
 	private String lastUnionN1;
    private String lastUnionN2;
    private String lastDifferenceN1;
    private String lastDifferenceN2;
    
	public OSType osType;
	
    private String pathcsv; // path to write csvs 
    private String breakIfBackToNode; //Y = do not use the link to itself
    private String randomizeFirstNodeEachRW; // Y = random the next start N = run rw 1 time for each possible node 
    private String restartSameRWIfLoop; // Y  = if visit the same node in one rw break 
    private String trace; // Y show prints
    private int forceStartNodeNummber; // number indicating the node to start always at each rw. if number > size of vertices use the ramdom 
    private String runMode; // C = class, P = properties, B = Both
    
	/**
	 *  function that run in batch loading ontologies from args
	 * 
	 * @param args
	 * args[0] = path
	 * args[1] = number of ontologies in net1
	 * args[2] = number of ontologies in net2
	 * args[3..n] = name of files containing ontologies from net 1 and 2
	 * 
	 * for instance: /Users/fabiomarcosdeabreusantos/Documents/Ontologias/ 2 cmt.owl dblp.owl cmt.owl foaf.owl
	 */
	public void batch(String[] args) {
		// TODO Auto-generated method stub
		// carrega OS da máquina
		getOSType();
		
		//Log = new JTextArea(); // to keep compatibility with the log 
		
		this.writeTimeStamp("inicio ontology manager");
		System.out.println("inicio ontology manager");
		path = args[0];
		System.out.println("path: "+ path);
		
		n1 = Integer.parseInt(args[1]);
		n2 = Integer.parseInt(args[2]);
		System.out.println("n1 = " + n1);
		System.out.println("n2 = " + n2);
		
		int max = Integer.parseInt(args[3]);
		
		OntologyManagerTab omt = new OntologyManagerTab();
		omt.openBatch1(path, args[4]);
		agOnt1 = omt.getAgOnt1();
		
		pathcsv= args[5];
		breakIfBackToNode = args[6];
	    randomizeFirstNodeEachRW = args[7];
	    restartSameRWIfLoop = args[8];
	    trace = args[9];
	    forceStartNodeNummber = Integer.parseInt(args[10]);
	    runMode = args[11];
		if (runMode.equals("B")) {		
			// run first randonWalk
			System.out.println("Running Random Walk n "+max+" times (max) in Classes:"+args[4] );
			RandomWalk rw0 = new RandomWalk(agOnt1[0], args[4]+"_class_"+max, max);
			
			rw0.setSetBreakIfBackToNode(breakIfBackToNode);
			rw0.setPathcsv(pathcsv);
			rw0.setRandomizeFirstNodeEachRW(randomizeFirstNodeEachRW);
			rw0.setRestartSameRWIfLoop(restartSameRWIfLoop);
			rw0.setTrace(trace);
			rw0.setForceStartNodeNummber(forceStartNodeNummber);
			
			//Random randRoot = new Random();
			//int root = randRoot.nextInt((gOnt1.getNumVertices() - 1) + 1);
			//System.out.println("calling RandomWalk with root:"+ root + " selecting "+ gOnt1.getNumVertices());
			//rw1.rw(root);
			rw0.fullrw();
			
			System.out.println("Running Random Walk n "+max+" times (max) in Properties:"+args[4] );
			RandomWalk rw1 = new RandomWalk(agOnt1[1], args[4]+"_prop_"+max, max);
			
			rw1.setSetBreakIfBackToNode(breakIfBackToNode);
			rw1.setPathcsv(pathcsv);
			rw1.setRandomizeFirstNodeEachRW(randomizeFirstNodeEachRW);
			rw1.setRestartSameRWIfLoop(restartSameRWIfLoop);
			rw1.setTrace(trace);
			rw1.setForceStartNodeNummber(forceStartNodeNummber);
	
			rw1.fullrw();
			
		}	else { 
				if (runMode.equals("C")) {	
					System.out.println("Running Random Walk n "+max+" times (max) in Classes:"+args[4] );
					RandomWalk rw0 = new RandomWalk(agOnt1[0], args[4]+"_class_"+max, max);
					
					rw0.setSetBreakIfBackToNode(breakIfBackToNode);
					rw0.setPathcsv(pathcsv);
					rw0.setRandomizeFirstNodeEachRW(randomizeFirstNodeEachRW);
					rw0.setRestartSameRWIfLoop(restartSameRWIfLoop);
					rw0.setTrace(trace);
					rw0.setForceStartNodeNummber(forceStartNodeNummber);
					
					//Random randRoot = new Random();
					//int root = randRoot.nextInt((gOnt1.getNumVertices() - 1) + 1);
					//System.out.println("calling RandomWalk with root:"+ root + " selecting "+ gOnt1.getNumVertices());
					//rw1.rw(root);
					rw0.fullrw();
			
				} else {
					System.out.println("Running Random Walk n "+max+" times (max) in Properties:"+args[4] );
					RandomWalk rw1 = new RandomWalk(agOnt1[1], args[4]+"_prop_"+max, max);
					
					rw1.setSetBreakIfBackToNode(breakIfBackToNode);
					rw1.setPathcsv(pathcsv);
					rw1.setRandomizeFirstNodeEachRW(randomizeFirstNodeEachRW);
					rw1.setRestartSameRWIfLoop(restartSameRWIfLoop);
					rw1.setTrace(trace);
					rw1.setForceStartNodeNummber(forceStartNodeNummber);
			
					rw1.fullrw();

				}
			
		}
		System.out.println("--LogBatch ---");
		System.out.println(LogBatch);
		
		try{ 
			File f = new File(path+"log.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		    writer.write (LogBatch);
	
		    //Close writer
		    writer.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
	}


	/**
	 * Basic GUI Design plus initializing Class variables 
	 */
	public void initialize() 
	{
		getOSType();
        
		pathOnt1 = "";		
		pathOnt2 = "";
		nameOnt1 = "";
		nameOnt2 = "";
		pathOntResults = "";
		nameOntResults = "";
		gOnt1 = null;
		gOnt2 = null;
		gResults = null;

		HideIRI1=false;

		HideIRI2=false;

	}
	
	private void getOSType() {
		// TODO Auto-generated method stub
		//Getting OS type
		String os = System.getProperty("os.name");
		os = os.substring(0, 3);
        if(os.equals("Mac")) 
        {
        	osType = OSType.Mac;
        }
        else if(os.equals("Lin"))
        {
        	osType = OSType.Linux;
        }
        else if(os.equals("Win")) 
        {
        	osType = OSType.Windows;
        }
	}
	private void writeTimeStamp(String message){
		java.util.Date date;
    	date = new java.util.Date();
		String stamp;
		stamp = message +" \n";
		stamp = stamp + (new Timestamp(date.getTime())).toString() + "\n";
		System.out.println(stamp);
	}
}

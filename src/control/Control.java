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
	/*private JButton btnLoadOnt1 = new JButton("Load Ontology 1");
	private JButton btnShowHideIRI1 = new JButton("Show/Hide Ontology 1 IRIs");
	private JButton btnLoadOnt2 = new JButton("Load Ontology 2");
	private JButton btnShowHideIRI2 = new JButton("Show/Hide Ontology 2 IRIs");
	private JButton btnRun = new JButton("Run");
	private JButton btnSave = new JButton("Save Results");
	private JButton btnShowHideIRIResults = new JButton("Show/Hide Results IRIs");
	private JButton btnCleanUp = new JButton("Minimize Graph");
    private JTextArea Log;
    
    private DefaultCaret LogCaret;
    private JScrollPane jScrollPaneLog;
    private JLabel warninglbl = new JLabel();
	private JLabel lbl1 = new JLabel();
	private JLabel lbl2 = new JLabel();
	private JComboBox jComboBoxOperation;*/
	private String Log;
	boolean ProjOp = false;
	boolean HideIRI1 = false;
	boolean HideIRI2 = false;
	boolean HideIRIResults = false;
	boolean CleanUp = false;
	/*private JTable jTable1; 			//Ontology1 Table
    private JScrollPane jScrollPane1;
    private JTable jTable2; 			//Ontology2 Table
    private JScrollPane jScrollPane2;
    private JTable jTableInc; 			//Inclusion Constraints Table
    private JScrollPane jScrollPaneInc;
    private JScrollPane jScrollPaneMain;
    private Component root;*/
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
		
		/*for (int i=3; i < args.length; i++){
			
			if (i < n1+3){ // < n: to network 1
				network1.add(args[i]);
				System.out.println("net 1: "+ args[i]);
			}
			if (i >= n1+3){ // >= n: to network 2
				network2.add(args[i]);
				System.out.println("net 2: "+ args[i]);
			}
		}*/
		
		// testando com redes de 2 elementos para debug fácil
		/*if (n1==2&&n2==2){
			
			compute2x2();
			
			
			
		} else { */
			// more then 2 elements each network
		//Thread worker1 = new Thread() {
			//public void run() {
				//runNet1Union(network1);
				
				// run first randonWalk
				System.out.println("Running Random Walk n "+max+" times (max) in Classes:"+args[4] );
				RandomWalk rw0 = new RandomWalk(agOnt1[0], args[4], max);
				//Random randRoot = new Random();
				//int root = randRoot.nextInt((gOnt1.getNumVertices() - 1) + 1);
				//System.out.println("calling RandomWalk with root:"+ root + " selecting "+ gOnt1.getNumVertices());
				//rw1.rw(root);
				rw0.fullrw();
				
				System.out.println("Running Random Walk n "+max+" times (max) in Properties:"+args[4] );
				RandomWalk rw1 = new RandomWalk(agOnt1[1], args[4], max);
		
				rw1.fullrw();
						
				//runNet2Union(network2);
				
				// run second randonWalk
				//RandomWalk rw2 = new RandomWalk(gOnt2);
				//rw2.fullrw();
							//}
		//};
		//worker1.start();
		
			/*String lastUnionN1 = network1.get(0);
			// process operations
			// network1 creation with Union
			System.out.println("opening..." + path + network1.get(0));
			openBatch1(path, network1.get(0));
			for (int i=1; i<network1.size(); i++){
				int pos = findLoaded(network1.get(i));
				if (pos == -1){
					System.out.println("batch opening..." + path + network1.get(i));
				
					openBatch2(path, network1.get(i)); // second element from network1
				}
				else{
					
					System.out.println("load from memory ..." + path + network1.get(i) + " pos: "+ pos);
					loadFromMemory2(pos);
					
				}
				System.out.println("calling union1 in batch");
				runOntBatch("Union");
				
				System.out.println("Saving union1 in batch: "+ lastUnionN1+ "U"+network1.get(i));
				lastUnionN1 += "U"+network1.get(i);
				
				//saveRBatch(path, lastUnionN1+"U"+network1.get(i)); // save each union
				partialUnionResultsN1.put(lastUnionN1+"U"+network1.get(i), lastUnionN1+"U"+network1.get(i)); // save partial result for later!
				
				pos = findLoaded(lastUnionN1);
				if (pos == -1){
					System.out.println("batch opening..." + path + lastUnionN1);
				
					openBatch1(path, lastUnionN1); // open the result of o1 U o2 as o1 again
				} else {
					System.out.println("load from memory ..." + path + lastUnionN1 + " pos: "+ pos);
					loadFromMemory1(pos);

				}
				
				System.out.println("last Union n1: "+lastUnionN1);
			}
			*/
			/*String lastUnionN2 = network2.get(0);
			// process operations
			// network2 creation with Union
			System.out.println("opening..." + path + network2.get(0));
			openBatch1(path, network2.get(0));
			for (int i=1; i<network2.size(); i++){
				int pos = findLoaded(network2.get(i));
				if (pos == -1){
					System.out.println("batch opening..." + path + network2.get(i));
				
					openBatch2(path, network2.get(i)); // second element from network1
				}
				else{
					
					System.out.println("load from memory ..." + path + network2.get(i) + " pos: "+ pos);
					loadFromMemory2(pos);
					
				}
				System.out.println("calling union2 in batch");
				runOntBatch("Union");
				
				System.out.println("Saving union2 in batch: "+ lastUnionN2+ "U"+network2.get(i));
				lastUnionN2 += "U"+network2.get(i);
				
				//saveRBatch(path, lastUnionN2+"U"+network2.get(i)); // save each union
				partialUnionResultsN2.put(lastUnionN2+"U"+network2.get(i), lastUnionN2+"U"+network2.get(i)); // save partial result for later!
				
				pos = findLoaded(lastUnionN2);
				if (pos == -1){
					System.out.println("batch opening..." + path + lastUnionN2);
				
					openBatch1(path, lastUnionN2); // open the result of o1 U o2 as o1 again
				} else {
					System.out.println("load from memory ..." + path + lastUnionN2 + " pos: "+ pos);
					loadFromMemory1(pos);

				}
				
				System.out.println("last Union n2: "+lastUnionN2);
			}
			*/
			//writeTimeStamp("finishing 2 unions ...");
			//writeTimeStamp("starting intersections ...");
			
			//runNetsIntersections(network1,network2);
			// intersection 1x2
			/*
			int totalIntersections = network1.size()*network2.size();
			 
			System.out.println("starting batch intersection ="+totalIntersections);
			String lastIntersection = "";
			// computes intersection over N1 and N2
			// load both i and j ontologies from net1 and net2 

			for (int i=0; i<network1.size(); i++){
				
				int pos = findLoaded(network1.get(i));
				if (pos == -1){
					System.out.println("batch opening..." + path + network1.get(i));
					
					openBatch1(path, network1.get(i));
				}
				else {
					System.out.println("load from memory ..." + path + network1.get(i) + " pos: "+ pos);
					loadFromMemory1(pos);

				}
				for (int j=0; j<network2.size(); j++){
					pos = findLoaded(network2.get(j));
					if (pos == -1){
						System.out.println("batch opening..." + path + network2.get(j));
						
						openBatch2(path, network2.get(j));
					} else {
						System.out.println("load from memory ..." + path + network2.get(j) + " pos: "+ pos);
						loadFromMemory2(pos);

					}
					
					System.out.println("calling intersection in batch..." );
					runOntBatch("Intersection");
					
					System.out.println("Saving intersection in batch"+ network1.get(i)+"I"+network2.get(j));
					//saveRBatch(path, network1.get(i)+"I"+network2.get(j)); // save each intersection
					
					partialIntersectionResultsN1N2Names.put(network1.get(i)+"I"+network2.get(j), network1.get(i)+"I"+network2.get(j)); // save partial result for later!
					partialIntersectionResultsN1N2.add(network1.get(i)+"I"+network2.get(j));
					lastIntersection = network1.get(i)+"I"+network2.get(j);
					System.out.println("last Intersection batch: "+network1.get(i)+"I"+network2.get(j));
	
				}
			}
			*/
			//writeTimeStamp("finishing intersections ...");
			//writeTimeStamp("starting 1st difference ...");
			
			//runNet1Difference(network1);
			//runNet2Difference(network2);
			
			/*
			// difference N1
			String lastDifferenceN1 = "";
			// computes differences from network1 and intersections
			int pos = findLoaded(lastUnionN1);
			if (pos == -1){
				System.out.println("Opening union1 in batch"+ lastUnionN1);
				
				openBatch1(path, lastUnionN1);
			}else {
				System.out.println("load from memory ..." + lastUnionN1 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
			for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
				pos = findLoaded(partialIntersectionResultsN1N2.get(i));

				if (pos == -1){
					System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));
	
					openBatch2(path, partialIntersectionResultsN1N2.get(i));
				} else {
					System.out.println("load from memory ..." + partialIntersectionResultsN1N2.get(i) + " pos: "+ pos);
					loadFromMemory2(pos);
				}
				System.out.println("Running dif in batch: ");

				runOntBatch("Difference");
				if (i==0){
					//System.out.println("saving dif in batch: "+lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i));
					//saveRBatch(path, lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i));
					partialDifferenceResultsN1.put(lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i), lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
					lastDifferenceN1 = lastUnionN1+"D"+partialIntersectionResultsN1N2.get(i);

				} else {
					//System.out.println("saving dif in batch: "+lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i));
					//saveRBatch(path, lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i));
					partialDifferenceResultsN1.put(lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i), partialIntersectionResultsN1N2.get(i-1)+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
					lastDifferenceN1 = lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i);

				}
				
				pos = findLoaded(lastDifferenceN1);
				if (pos == -1){
					System.out.println("Opening intersection in batch: "+ i + lastDifferenceN1);
	
					openBatch1(path, lastDifferenceN1); // open the result of o1 D o2 as o1 again
				}
				else {
					System.out.println("load from memory ..." + lastDifferenceN1 + " pos: "+ pos);
					loadFromMemory1(pos);

				}
			}
			System.out.println("last Difference n1: "+lastDifferenceN1);
			System.out.println("saving dif in batch: "+lastDifferenceN1+ " as net1");
			saveRBatch(path, "net1");
			
			*/
			//writeTimeStamp("finishing 1st difference ...");
			//writeTimeStamp("starting 2nd difference ...");
			/*
			// difference N2
			String lastDifferenceN2 = "";
			// computes differences from network2 and intersections
			pos = findLoaded(lastUnionN2);
			if (pos == -1){
				System.out.println("Opening union2 in batch"+ lastUnionN2);
				
				openBatch1(path, lastUnionN2);
			} else {
				System.out.println("load from memory ..." + lastUnionN2 + " pos: "+ pos);
				loadFromMemory1(pos);
			}
			for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
				pos = findLoaded(partialIntersectionResultsN1N2.get(i));

				if (pos == -1){
					System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));
	
					openBatch2(path, partialIntersectionResultsN1N2.get(i));
				} else {
					System.out.println("load from memory ..." + partialIntersectionResultsN1N2.get(i) + " pos: "+ pos);
					loadFromMemory2(pos);

				}
				System.out.println("Running dif in batch: ");

				runOntBatch("Difference");
				if (i==0){
					System.out.println("saving dif in batch: "+lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i));
					//saveRBatch(path, lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i));
					partialDifferenceResultsN2.put(lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i), lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
					lastDifferenceN2 = lastUnionN2+"D"+partialIntersectionResultsN1N2.get(i);

				} else {
					System.out.println("saving dif in batch: "+lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i));
					//saveRBatch(path, lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i));
					partialDifferenceResultsN2.put(lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i), partialIntersectionResultsN1N2.get(i-1)+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
					lastDifferenceN2 = lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i);

				}
				pos = findLoaded(lastDifferenceN2);
				if (pos == -1){
					System.out.println("Opening intersection in batch: "+ i + lastDifferenceN2);
	
					openBatch1(path, lastDifferenceN2); // open the result of o1 D o2 as o1 again
				} else {
					System.out.println("load from memory ..." + lastDifferenceN2 + " pos: "+ pos);
					loadFromMemory1(pos);

				}
			}
			System.out.println("last Difference n2: "+lastDifferenceN2);
			System.out.println("saving dif in batch: "+lastDifferenceN2+ " as net2");
			saveRBatch(path, "net2");
			*/
			//writeTimeStamp("finishing 2nd difference ...");
			
			//writeTimeStamp("Finishing 2 networks  ...");

			// finally call alin to match resulting results lastDifferenceN1 and lastDifferenceN2
			//System.out.println(" Calling alin to match "+ lastDifferenceN1 + "x " + lastDifferenceN2);



		//}// else net1 and 2 > 2
		
		System.out.println("--Log JTextArea ---");
		System.out.println(Log);
		System.out.println("--LogBatch ---");
		//System.out.println(LogBatch);
		
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

	/*private void compute2x2() {
		// TODO Auto-generated method stub
		// Un1
		String union1 ="";
		String union2 =""; // save union file
		
		writeTimeStamp("Starting 2 networks 2x2 beginning");
		
		System.out.println("batch opening..." + path + network1.get(0));
		openBatch1(path, network1.get(0));
		
		int pos = findLoaded(network1.get(1));
		if (pos == -1){
			System.out.println("batch opening..." + path + network1.get(1));
		
			openBatch2(path, network1.get(1)); // second element from network1
		}
		else{
			
			System.out.println("load from memory ..." + path + network1.get(1) + " pos: "+ pos);
			loadFromMemory2(pos);
			
		}
		System.out.println("calling union1 in batch");
		runOntBatch("Union");
		
		System.out.println("Saving union1 in batch: "+ network1.get(0)+"U"+network1.get(1));
		union1 = network1.get(0)+"U"+network1.get(1);
		
		//saveRBatch(path, network1.get(0)+"U"+network1.get(1)); // save union
		partialUnionResultsN1.put("Un1",network1.get(0)+"U"+network1.get(1));
		// Un2
		
		pos = findLoaded(network2.get(0));
		if (pos == -1){
			System.out.println("batch opening..." + path + network2.get(0));
		
			openBatch1(path, network2.get(0)); // first element from network2
		}
		else{
			System.out.println("load from memory ..." + path + network2.get(0) + " pos: "+ pos);
			loadFromMemory1(pos);
			
		}
		
		pos = findLoaded(network2.get(1));
		if (pos == -1){
			System.out.println("batch opening..." + path + network2.get(1));
			openBatch2(path, network2.get(1)); // second element from network1	
		}
		else{
			
			System.out.println("load from memory ..." + path + network2.get(1) + " pos: "+ pos);
			loadFromMemory2(pos);

		}
		System.out.println("calling union2 in batch");	
		runOntBatch("Union");
		
		System.out.println("Saving union2 in batch: "+ network2.get(0)+"U"+network2.get(1));
		union2 = network2.get(0)+"U"+network2.get(1);
		//saveRBatch(path, network2.get(0)+"U"+network2.get(1)); // save union
		partialUnionResultsN2.put("Un2",network2.get(0)+"U"+network2.get(1));
		
		writeTimeStamp("finishing 2 unions ...");
		writeTimeStamp("starting intersections ...");
		
		// intersection 1x2
		int totalIntersections = network1.size()*network2.size();
		System.out.println("starting batch intersection ="+totalIntersections);
		String lastIntersection = "";
		// computes intersection over N1 and N2
		// load both i and j ontologies from net1 and net2 

		for (int i=0; i<network1.size(); i++){
			
			pos = findLoaded(network1.get(i));
			if (pos == -1){
				System.out.println("batch opening..." + path + network1.get(i));
				
				openBatch1(path, network1.get(i));
			}
			else {
				System.out.println("load from memory ..." + path + network1.get(i) + " pos: "+ pos);
				loadFromMemory1(pos);

			}
			for (int j=0; j<network2.size(); j++){
				pos = findLoaded(network2.get(j));
				if (pos == -1){
					System.out.println("batch opening..." + path + network2.get(j));
					
					openBatch2(path, network2.get(j));
				} else {
					System.out.println("load from memory ..." + path + network2.get(j) + " pos: "+ pos);
					loadFromMemory2(pos);

				}
				
				System.out.println("calling intersection in batch..." );
				runOntBatch("Intersection");
				
				System.out.println("Saving intersection in batch"+ network1.get(i)+"I"+network2.get(j));
				//saveRBatch(path, network1.get(i)+"I"+network2.get(j)); // save each intersection
				
				partialIntersectionResultsN1N2Names.put(network1.get(i)+"I"+network2.get(j), network1.get(i)+"I"+network2.get(j)); // save partial result for later!
				partialIntersectionResultsN1N2.add(network1.get(i)+"I"+network2.get(j));
				lastIntersection = network1.get(i)+"I"+network2.get(j);
				System.out.println("last Intersection batch: "+network1.get(i)+"I"+network2.get(j));

			}
		}
		
		writeTimeStamp("finishing intersections ...");
		writeTimeStamp("staring 1st difference ...");
		
		// difference N1
		String lastDifferenceN1 = "";
		// computes differences from network1 and intersections
		pos = findLoaded(union1);
		if (pos == -1){
			System.out.println("Opening union1 in batch"+ union1);
			
			openBatch1(path, union1);
		}else {
			System.out.println("load from memory ..." + union1 + " pos: "+ pos);
			loadFromMemory1(pos);

		}
		for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
			pos = findLoaded(partialIntersectionResultsN1N2.get(i));

			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));

				openBatch2(path, partialIntersectionResultsN1N2.get(i));
			} else {
				System.out.println("load from memory ..." + partialIntersectionResultsN1N2.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);
			}
			System.out.println("Running dif in batch: ");

			runOntBatch("Difference");
			if (i==0){
				System.out.println("saving dif in batch: "+union1+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, union1+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN1.put(union1+"D"+partialIntersectionResultsN1N2.get(i), union1+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN1 = union1+"D"+partialIntersectionResultsN1N2.get(i);

			} else {
				System.out.println("saving dif in batch: "+lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN1.put(lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i), partialIntersectionResultsN1N2.get(i-1)+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN1 = lastDifferenceN1+"D"+partialIntersectionResultsN1N2.get(i);

			}
			
			pos = findLoaded(lastDifferenceN1);
			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + lastDifferenceN1);

				openBatch1(path, lastDifferenceN1); // open the result of o1 D o2 as o1 again
			}
			else {
				System.out.println("load from memory ..." + lastDifferenceN1 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
		}
		System.out.println("last Difference n1: "+lastDifferenceN1);
		System.out.println("saving dif in batch: "+lastDifferenceN1+ " as net1");
		saveRBatch(path, "net1");
		
		writeTimeStamp("finishing 1st difference ...");
		writeTimeStamp("starting 2nd difference ...");

		// difference N2
		String lastDifferenceN2 = "";
		// computes differences from network2 and intersections
		pos = findLoaded(union2);
		if (pos == -1){
			System.out.println("Opening union2 in batch"+ union2);
			
			openBatch1(path, union2);
		} else {
			System.out.println("load from memory ..." + union2 + " pos: "+ pos);
			loadFromMemory1(pos);
		}
		for (int i=0; i<partialIntersectionResultsN1N2.size(); i++){
			pos = findLoaded(partialIntersectionResultsN1N2.get(i));

			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + partialIntersectionResultsN1N2.get(i));

				openBatch2(path, partialIntersectionResultsN1N2.get(i));
			} else {
				System.out.println("load from memory ..." + partialIntersectionResultsN1N2.get(i) + " pos: "+ pos);
				loadFromMemory2(pos);

			}
			System.out.println("Running dif in batch: ");

			runOntBatch("Difference");
			if (i==0){
				System.out.println("saving dif in batch: "+union2+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, union2+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN2.put(union2+"D"+partialIntersectionResultsN1N2.get(i), union2+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN2 = union2+"D"+partialIntersectionResultsN1N2.get(i);

			} else {
				System.out.println("saving dif in batch: "+lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i));
				//saveRBatch(path, lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i));
				partialDifferenceResultsN2.put(lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i), partialIntersectionResultsN1N2.get(i-1)+"D"+partialIntersectionResultsN1N2.get(i)); // save partial result for later!
				lastDifferenceN2 = lastDifferenceN2+"D"+partialIntersectionResultsN1N2.get(i);

			}
			pos = findLoaded(lastDifferenceN2);
			if (pos == -1){
				System.out.println("Opening intersection in batch: "+ i + lastDifferenceN2);

				openBatch1(path, lastDifferenceN2); // open the result of o1 D o2 as o1 again
			} else {
				System.out.println("load from memory ..." + lastDifferenceN2 + " pos: "+ pos);
				loadFromMemory1(pos);

			}
		}
		System.out.println("last Difference n2: "+lastDifferenceN2);
		System.out.println("saving dif in batch: "+lastDifferenceN2+ " as net2");
		saveRBatch(path, "net2");
		
		writeTimeStamp("finishing 2nd difference ...");

		writeTimeStamp("Finishing 2 networks 2x2 ...");
		
		// finally call alin to match resulting results lastDifferenceN1 and lastDifferenceN2
		System.out.println(" Calling alin to match "+ lastDifferenceN1 + "x " + lastDifferenceN2);

	}
*/
	/**
	 * Basic GUI Design plus initializing Class variables 
	 */
	public void initialize() 
	{
		getOSType();
        //this.setLabel("Ontology Manager Tab"); //Tab name
        //this.setTitle("Ontology Manager Stand Alone");
		pathOnt1 = "";		
		pathOnt2 = "";
		nameOnt1 = "";
		nameOnt2 = "";
		pathOntResults = "";
		nameOntResults = "";
		gOnt1 = null;
		gOnt2 = null;
		gResults = null;
		//this.setLayout(null);
		//lbl2.setBounds(640, 420, 60, 20);
		//lbl2.setText("Log: ");
		//this.add(lbl2);
		//Log = new JTextArea();
		//jScrollPaneLog = new JScrollPane();
		//Log.setColumns(20);
		//Log.setRows(10);
		//Log.setBackground(Color.WHITE);
		//Log.setEditable(false);
		//LogCaret = (DefaultCaret)Log.getCaret();
		//LogCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//jScrollPaneLog.setViewportView(Log);
		//jScrollPaneLog.setBounds(640, 440, 620, 215);
		//this.add(jScrollPaneLog);
		//lbl1.setBounds(10, 6, 100, 20);
		//lbl1.setText("Operation: ");
		//this.add(lbl1);
		//warninglbl.setForeground(Color.red);
		//warninglbl.setBounds(8, 375, 1500, 20);
		//warninglbl.setFont(new Font("Lucida Grande", Font.BOLD, 10));
		//warninglbl.setText("");
		//warninglbl.setVisible(false);
		//this.add(warninglbl);		
		//setting up table forOntology 1
		//jTable1 = new JTable();
		//jScrollPane1 = new JScrollPane();
		//DefaultTableModel model1 = new DefaultTableModel(
	     //          new Object [][] {
//
	     //          },
	     //          new String [] {
	     //              "Ontology 1", ""
	      //         });				
		//jTable1.setModel(model1);     
		//jScrollPane1.setBounds(6, 40, 620, 320);
        //jScrollPane1.setViewportView(jTable1);
        //this.add(jScrollPane1);      
        //btnLoadOnt1.setBounds(6, 365, 305, 29);
		//this.add(btnLoadOnt1);
		//btnLoadOnt1.addActionListener(new OpenO1());
		//btnShowHideIRI1.setBounds(321, 365, 305, 29);
		//this.add(btnShowHideIRI1);
		//btnShowHideIRI1.addActionListener(new ShowHideIRI1());
		HideIRI1=false;
		//btnShowHideIRI1.setEnabled(false);
		//setting up table forOntology 2
        //jTable2 = new JTable();
		//jScrollPane2 = new JScrollPane();
		//DefaultTableModel model2 = new DefaultTableModel(
	     //          new Object [][] {

	     //          },
	     //          new String [] {
	     //              "Ontology 2", ""
	     //          });				
		//jTable2.setModel(model2);
       // jScrollPane2.setBounds(640, 40, 620, 320);
        //jScrollPane2.setViewportView(jTable2);        
        //this.add(jScrollPane2);
		//btnLoadOnt2.setBounds(640, 365, 305, 29);		
		//this.add(btnLoadOnt2);
		//btnLoadOnt2.addActionListener(new OpenO2());
		//btnShowHideIRI2.setBounds(955, 365, 305, 29);
		//this.add(btnShowHideIRI2);
		//btnShowHideIRI2.addActionListener(new ShowHideIRI2());
		HideIRI2=false;
		///btnShowHideIRI2.setEnabled(false);
		//setting up table forResults, Inclusion Constraints
		//jTableInc = new JTable();
		//jScrollPaneInc = new JScrollPane();
		//jTableInc.setModel(new javax.swing.table.DefaultTableModel(
        /*    new Object [][] {

            },
            new String [] {
                "Resulting ontology", ""
            }
        ));
		jTableInc.setEnabled(false);
		jScrollPaneInc.setBounds(6, 420, 620, 200);
        jScrollPaneInc.setViewportView(jTableInc);
        this.add(jScrollPaneInc);
        btnSave.setBounds(420, 625, 202, 29);
		this.add(btnSave);
		btnSave.addActionListener(new SaveR());
		btnSave.setEnabled(false);
		btnShowHideIRIResults.setBounds(213, 625, 202, 29);
		this.add(btnShowHideIRIResults);
		btnShowHideIRIResults.addActionListener(new ShowHideIRIResults());
		btnShowHideIRIResults.setEnabled(false);
		HideIRIResults = false;
		btnCleanUp.setBounds(6, 625, 202, 29);
		this.add(btnCleanUp);
		btnCleanUp.addActionListener(new MinimizeGraph());
		btnCleanUp.setEnabled(false);
		CleanUp = false;
        //ComboBox for operation selection
        jComboBoxOperation = new JComboBox();
		jComboBoxOperation.setBounds(80, 6, 100, 20);
		jComboBoxOperation.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Union", "Intersection", "Projection", "Difference"}));
		jComboBoxOperation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	jComboBoxOperationActionPerformed(evt);
            }
        });
		jComboBoxOperation.setSelectedIndex(0);
		this.add(jComboBoxOperation);
		btnRun.setBounds(190, 6, 117, 22);
		this.add(btnRun);
		btnRun.addActionListener(new RunOnt());
		ShowBottomWarning = true;
		//Welcome Message
		Log.setText("Welcome to the Ontology Manager Tab! \nDeveloped by Romulo de Carvalho Magalhaes \n");
		this.pack();
		this.setVisible(true);*/
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

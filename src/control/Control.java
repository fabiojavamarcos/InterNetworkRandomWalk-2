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

import Model.OntologyManagerTab.RunOnt;
//import Model.OntologyManagerTab.SaveR;

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
    private int topItens; // number of top visited to return 
    private int windowSize = 0; // Number of considered nodes visits (sliding window)
    private int offSet = 0; // next start point from the last window
    private int turn = 1; // number of the turn -> now its part of the file path! 
    private int quartil = 0; // percent limit to consider in rw

 	private int threshold = 1; // percent limit to consider in rw
 	private String mode = "RW"; // RW regular RW. BASELINE: gets the max and randomly select max (args[3]) nodes from the ontology graph.

    private ArrayList toDelete = new ArrayList();
     
    private HashMap top;
	/**
	 *  function that run in batch loading ontologies from args
	 * 
	 * @param args
	 * args[0] = path
	 * args[1] = number of ontologies in net1 <- not used
	 * args[2] = number of ontologies in net2 <- not used
	 * args[3..n] = name of files containing ontologies from net 1 and 2 <- not used
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
		
		n1 = Integer.parseInt(args[1]); //<- not used
		n2 = Integer.parseInt(args[2]);  // <- not used
		System.out.println("n1 = " + n1);
		System.out.println("n2 = " + n2);
		
		int max = Integer.parseInt(args[3]); // limits the RW
		
		pathcsv= args[5]; // where the csv are stored
		System.out.println("path csv: "+ pathcsv);
		
		breakIfBackToNode = args[6]; // 
		System.out.println("breakIfBackToNode: "+ breakIfBackToNode);
		
	    randomizeFirstNodeEachRW = args[7]; // set a random 
	    System.out.println("randomizeFirstNodeEachRW: "+ randomizeFirstNodeEachRW);
	    
	    restartSameRWIfLoop = args[8];
	    System.out.println("restartSameRWIfLoop: "+ restartSameRWIfLoop);

	    trace = args[9];
	    System.out.println("trace: "+ trace);

	    forceStartNodeNummber = Integer.parseInt(args[10]);
	    System.out.println("forceStartNodeNummber: "+ forceStartNodeNummber); // start always from the same vertice (ramdom must be Y)
		// to avoid that use a number over the last vertice size
	    
	    runMode = args[11];  // B (Both) C (Classes) P (Properties) T (All)
	    System.out.println("runMode: "+ runMode);
	    
	    windowSize = Integer.parseInt(args[12]); //Max Size of the window in slidding window
	    System.out.println("windowSize: "+ windowSize);

	    offSet = Integer.parseInt(args[13]); // size of the jump whem moving the window
	    System.out.println("offSet: "+ offSet);

	    turn = Integer.parseInt(args[14]); // the turn will be added to the path (used when running multiples times like a chain, improving the results)
	    System.out.println("turn: "+ turn);
	    
		// if quartil has a valid value, the threshold value will be the quartil + the informed threshold parameter in %.
		// else the threshold value will be the absolute value to the threshold parameter (useful when you know the dataset and want a specific value)
		// ex: quartil 1 threshold 10. Get 1st quartil more 10%
		// ex: quartil 5 (impossible) threshold 10. !0 will be the absolute value for cut data
		// Obs: quartil -1 = dif from quartil 3 and 1
		// quartil 5 = median. Quartil 0 = min value.

	    quartil = Integer.parseInt(args[15]); // number of the quartil to consider when deciding were to cut a random walk
	    System.out.println("quartil: "+ quartil);
	    
	    threshold = Integer.parseInt(args[16]); //
	    System.out.println("threshold: "+ threshold);
	    
	    mode = args[17]; // BASELINE (generate random result for the visits. Use args[3] to determine how much results must be generated) or RUN
	    

	    if (turn != 1) {
	    	pathcsv=pathcsv+turn; // add the number of the turn to the path do distinguish csv files
	    }
	    int k = 18; // position to start receiving the nodes to ignore
	    
	    while(k<args.length) {
	    	System.out.println("To Delete:"+args[k]);
	    	toDelete.add(args[k]);
	    	k++;
	    }

	   
		OntologyManagerTab omt = new OntologyManagerTab();
		omt.openBatch1(path, args[4]);
		agOnt1 = omt.getAgOnt1();
		
	    
		RandomWalk rw0 = new RandomWalk(agOnt1[0], args[4]+"_class_"+max, max);
		
		rw0.setSetBreakIfBackToNode(breakIfBackToNode);
		rw0.setPathcsv(pathcsv);
		rw0.setRandomizeFirstNodeEachRW(randomizeFirstNodeEachRW);
		rw0.setRestartSameRWIfLoop(restartSameRWIfLoop);
		rw0.setTrace(trace);
		rw0.setForceStartNodeNummber(forceStartNodeNummber);
		rw0.setTop(topItens);
		rw0.setOffSet(offSet);
		rw0.setWindowSize(windowSize);
		rw0.setTurn(turn);
		rw0.setToDelete(toDelete);
		rw0.setThreshold(threshold);
		rw0.setQuartil(quartil);
		rw0.setMode(mode);

		RandomWalk rw1 = new RandomWalk(agOnt1[1], args[4]+"_prop_"+max, max);
		
		rw1.setSetBreakIfBackToNode(breakIfBackToNode);
		rw1.setPathcsv(pathcsv);
		rw1.setRandomizeFirstNodeEachRW(randomizeFirstNodeEachRW);
		rw1.setRestartSameRWIfLoop(restartSameRWIfLoop);
		rw1.setTrace(trace);
		rw1.setForceStartNodeNummber(forceStartNodeNummber);
		rw1.setTop(topItens);
		rw1.setOffSet(offSet);
		rw1.setWindowSize(windowSize);
		rw1.setTurn(turn);
		rw1.setToDelete(toDelete);
		rw1.setThreshold(threshold);
		rw1.setQuartil(quartil);
		rw1.setMode(mode);

	    
		if (runMode.equals("B")) {		
			// run first randonWalk
			System.out.println("Running Random Walk n "+max+" times (max) in Classes:"+args[4] );
			
			//Random randRoot = new Random();
			//int root = randRoot.nextInt((gOnt1.getNumVertices() - 1) + 1);
			//System.out.println("calling RandomWalk with root:"+ root + " selecting "+ gOnt1.getNumVertices());
			//rw1.rw(root);
			top = rw0.fullrw();
			
			System.out.println("Running Random Walk n "+max+" times (max) in Properties:"+args[4] );

	
			top =  rw1.fullrw();
			
		}	else { 
				if (runMode.equals("C")) {	
					System.out.println("Running Random Walk n "+max+" times (max) in Classes:"+args[4] );

					//Random randRoot = new Random();
					//int root = randRoot.nextInt((gOnt1.getNumVertices() - 1) + 1);
					//System.out.println("calling RandomWalk with root:"+ root + " selecting "+ gOnt1.getNumVertices());
					//rw1.rw(root);
					top =  rw0.fullrw();
			
				} else { 
					if (runMode.equals("C")) {
						System.out.println("Running Random Walk n "+max+" times (max) in Properties:"+args[4] );

				
						top = rw1.fullrw();
					} else { 
						if (runMode.equals("T")) {
							System.out.println("Running Random Walk n "+max+" times (max) in Class and Properties:"+args[4] );
					
							top = rw1.fullrw();
						}
					}

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
		/*getOSType();
        
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

		HideIRI2=false;*/
		/*args example:
			/Users/fd252/Documents/dev/conferenceOAEI/
			2
			2
			601
			ekaw.owl
			"/Users/fd252/Google Drive (fabiojavamarcos@gmail.com)/ED6/visits/data"
			Y
			Y
			N
			Y
			9999
			C
			5
			5
			1
			3
			0
			RUN*/
		String parms [][] = new String [48][18];
		// w = 1 o = 1 q = 1,2,3
		parms [0][12] =		"1";
		parms [0][13] = 	"1";
		parms [0][15] = 	"1";
		parms [1][12] =		"1";
		parms [1][13] = 	"1";
		parms [1][15] = 	"2";
		parms [2][12] =		"1";
		parms [2][13] = 	"1";
		parms [2][15] = 	"3";
		
		// w = 2 o = 1 q = 1,2,3
		parms [3][12] =		"2";
		parms [3][13] = 	"1";
		parms [3][15] = 	"1";
		parms [4][12] =		"2";
		parms [4][13] = 	"1";
		parms [4][15] = 	"2";
		parms [5][12] =		"2";
		parms [5][13] = 	"1";
		parms [5][15] = 	"3";
		
		// w = 2 o = 2 q = 1,2,3
		parms [6][12] =		"2";
		parms [6][13] = 	"2";
		parms [6][15] = 	"1";
		parms [7][12] =		"2";
		parms [7][13] = 	"2";
		parms [7][15] = 	"2";
		parms [8][12] =		"2";
		parms [8][13] = 	"2";
		parms [8][15] = 	"3";

		// w = 3 o = 1 q = 1,2,3
		parms [9][12] =		"3";
		parms [9][13] = 	"1";
		parms [9][15] = 	"1";
		parms [10][12] =	"3";
		parms [10][13] = 	"1";
		parms [10][15] = 	"2";
		parms [11][12] =	"3";
		parms [11][13] = 	"1";
		parms [11][15] = 	"3";

		// w = 3 o = 2 q = 1,2,3
		parms [12][12] =	"3";
		parms [12][13] = 	"2";
		parms [12][15] = 	"1";
		parms [13][12] =	"3";
		parms [13][13] = 	"2";
		parms [13][15] = 	"2";
		parms [14][12] =	"3";
		parms [14][13] = 	"2";
		parms [14][15] = 	"3";
		
		// w = 3 o = 3 q = 1,2,3
		parms [15][12] =	"3";
		parms [15][13] = 	"3";
		parms [15][15] = 	"1";
		parms [16][12] =	"3";
		parms [16][13] = 	"3";
		parms [16][15] = 	"2";
		parms [17][12] =	"3";
		parms [17][13] = 	"3";
		parms [17][15] = 	"3";

		// w = 4 o = 1 q = 1,2,3
		parms [18][12] =	"4";
		parms [18][13] = 	"1";
		parms [18][15] = 	"1";
		parms [19][12] =	"4";
		parms [19][13] = 	"1";
		parms [19][15] = 	"2";
		parms [20][12] =	"4";
		parms [20][13] = 	"1";
		parms [20][15] = 	"3";

		// w = 4 o = 2 q = 1,2,3
		parms [21][12] =	"4";
		parms [21][13] = 	"2";
		parms [21][15] = 	"1";
		parms [22][12] =	"4";
		parms [22][13] = 	"2";
		parms [22][15] = 	"2";
		parms [23][12] =	"4";
		parms [23][13] = 	"2";
		parms [23][15] = 	"3";
		
		// w = 4 o = 3 q = 1,2,3
		parms [24][12] =	"4";
		parms [24][13] = 	"3";
		parms [24][15] = 	"1";
		parms [25][12] =	"4";
		parms [25][13] = 	"3";
		parms [25][15] = 	"2";
		parms [26][12] =	"4";
		parms [26][13] = 	"3";
		parms [26][15] = 	"3";

		// w = 4 o = 4 q = 1,2,3
		parms [27][12] =	"4";
		parms [27][13] = 	"4";
		parms [27][15] = 	"1";
		parms [28][12] =	"4";
		parms [28][13] = 	"4";
		parms [28][15] = 	"2";
		parms [29][12] =	"4";
		parms [29][13] = 	"4";
		parms [29][15] = 	"3";

		// w = 5 o = 1 q = 1,2,3
		parms [30][12] =	"5";
		parms [30][13] = 	"1";
		parms [30][15] = 	"1";
		parms [31][12] =	"5";
		parms [31][13] = 	"1";
		parms [31][15] = 	"2";
		parms [32][12] =	"5";
		parms [32][13] = 	"1";
		parms [32][15] = 	"3";

		// w = 5 o = 2 q = 1,2,3
		parms [33][12] =	"5";
		parms [33][13] = 	"2";
		parms [33][15] = 	"1";
		parms [34][12] =	"5";
		parms [34][13] = 	"2";
		parms [34][15] = 	"2";
		parms [35][12] =	"5";
		parms [35][13] = 	"2";
		parms [35][15] = 	"3";

		// w = 5 o = 3 q = 1,2,3
		parms [36][12] =	"5";
		parms [36][13] = 	"3";
		parms [36][15] = 	"1";
		parms [37][12] =	"5";
		parms [37][13] = 	"3";
		parms [37][15] = 	"2";
		parms [38][12] =	"5";
		parms [38][13] = 	"3";
		parms [38][15] = 	"3";

		// w = 5 o = 4 q = 1,2,3
		parms [39][12] =	"5";
		parms [39][13] = 	"4";
		parms [39][15] = 	"1";
		parms [40][12] =	"5";
		parms [40][13] = 	"4";
		parms [40][15] = 	"2";
		parms [41][12] =	"5";
		parms [41][13] = 	"4";
		parms [41][15] = 	"3";

		// w = 5 o = 5 q = 1,2,3
		parms [42][12] =	"5";
		parms [42][13] = 	"5";
		parms [42][15] = 	"1";
		parms [43][12] =	"5";
		parms [43][13] = 	"5";
		parms [43][15] = 	"2";
		parms [44][12] =	"5";
		parms [44][13] = 	"5";
		parms [44][15] = 	"3";

		// w = 6 o = 1 q = 1,2,3
		parms [45][12] =	"6";
		parms [45][13] = 	"1";
		parms [45][15] = 	"1";
		parms [46][12] =	"6";
		parms [46][13] = 	"1";
		parms [46][15] = 	"2";
		parms [47][12] =	"6";
		parms [47][13] = 	"1";
		parms [47][15] = 	"3";

		for (int i = 0; i <48; i++) {
			
		
			parms [i][0] = "/Users/fd252/Documents/dev/conferenceOAEI/";
			parms [i][1] = 		"2";
			parms [i][2] =		"2";
			parms [i][3] =		"1040";
			parms [i][4] = 		"edas.owl";
			parms [i][5] = 		"/Users/fd252/Google Drive (fabiojavamarcos@gmail.com)/ED6/visits/OldVersion/data";
			parms [i][6] = 		"Y";
			parms [i][7] = 		"Y";
			parms [i][8] =		"N";
			parms [i][9] =		"Y";
			parms [i][10] =		"9999";
			parms [i][11] = 	"C";
			//parms [i][12] =	"5";
			//parms [i][13] = 	"5";
			parms [i][14] = 	"1";
			//parms [i][15] = 	"3";
			parms [i][16] = 	"0";
			parms [i][17] = 	"RUN";
			
			System.out.println("Calling: "+ parms[1]);
			batch(parms[i]);
		}

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

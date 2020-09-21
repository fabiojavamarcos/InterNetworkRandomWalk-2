package Ontology;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import Model.Bag;
import Model.Visit;


/**
 * This Class implements the Breadth First Search algorithm over the Graph Class to discover if there are paths between certain nodes
 *
 * @author Rômulo de Carvalho Magalhães - adapted by Fabio Marcos de Abreu Santos - adapted by Fabio Marcos de Abreu Santos
 *
 */
@SuppressWarnings("unused")
public class RandomWalk {

	private Graph g;
	private int numVertices;
	private int calculated[];
	private ArrayList<LinkedList<Integer>> reachable;
	private Boolean calculatedFull;
	private ArrayList <Integer> overAllVisits; //summarizes visits
	private ArrayList <String> overAllPaths; //aggregate paths (concatenate)
	private HashMap<Node, Node> visited_ant = new HashMap<Node, Node>();
	private FileWriter writer = null;
	private ArrayList<Visit> visitList= new ArrayList();
	private String name;
	private String printAnt = "";
	private int max;
	private String pathcsv;
	private String setBreakIfBackToNode;
    private String randomizeFirstNodeEachRW;
    private String restartSameRWIfLoop;
    private String trace;
    private int forceStartNodeNummber;
    private String runAllNodesOnce;
    private int top;
    private HashMap<Integer,String>  nodesNames = new HashMap();
    private HashSet<String> words = new HashSet();
    private ArrayList<Integer> binaryFlag = new ArrayList();
    private int windowSize;
    private int offSet;
    private ArrayList<String> visitRWLap = new ArrayList();
    private int randomInit = 0;
    private ArrayList<Bag> bags = new ArrayList(); 
    private ArrayList toDelete = new ArrayList();
    private int turn =1;

	
	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public ArrayList getToDelete() {
		return toDelete;
	}

	public void setToDelete(ArrayList toDelete) {
		this.toDelete = toDelete;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public int getOffSet() {
		return offSet;
	}

	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public String getRunAllNodesOnce() {
		return runAllNodesOnce;
	}

	public void setRunAllNodesOnce(String runAllNodesOnce) {
		this.runAllNodesOnce = runAllNodesOnce;
	}

	public int getForceStartNodeNummber() {
		return forceStartNodeNummber;
	}

	public void setForceStartNodeNummber(int forceStartNodeNummber) {
		this.forceStartNodeNummber = forceStartNodeNummber;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getPathcsv() {
		return pathcsv;
	}

	public void setPathcsv(String pathcsv) {
		this.pathcsv = pathcsv;
	}


	public String getSetBreakIfBackToNode() {
		return setBreakIfBackToNode;
	}

	public void setSetBreakIfBackToNode(String setBreakIfBackToNode) {
		this.setBreakIfBackToNode = setBreakIfBackToNode;
	}

	public String getRandomizeFirstNodeEachRW() {
		return randomizeFirstNodeEachRW;
	}

	public void setRandomizeFirstNodeEachRW(String randomizeFirstNodeEachRW) {
		this.randomizeFirstNodeEachRW = randomizeFirstNodeEachRW;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	public String getRestartSameRWIfLoop() {
		return restartSameRWIfLoop;
	}

	public void setRestartSameRWIfLoop(String restartSameRWIfLoop) {
		this.restartSameRWIfLoop = restartSameRWIfLoop;
	}

	public RandomWalk() 
	{
		this.g = null;
		this.numVertices = 0;
	}
	
	/**
	 * Initializes class with a given Graph
	 * 
	 * @param original
	 */
	public RandomWalk(Graph original, String name, int max) 
	{
		this.calculatedFull = false;
		this.g = original;
		this.numVertices = original.getNumVertices();
		this.calculated = new int[this.numVertices];
		this.reachable = new ArrayList<LinkedList<Integer>>();
		this.name = name;
		this.max = max;
		for(int i = 0; i < this.numVertices; i++)
		{
			LinkedList<Integer> temp = new LinkedList<Integer>();
			temp.add(i);
			this.reachable.add(temp);
			this.calculated[i] = 0;
		}
		Set nodes = g.getVertices().entrySet();
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			Map.Entry<Integer, Node> nMap = (Map.Entry<Integer, Node>) i.next();
			String nodeName = nMap.getValue().getFullName();
			int nodeId = nMap.getValue().getId();
			nodesNames.put(nodeId, nodeName);
		}
	}
	
	/**
	 * Searches a path from idO to idD
	 * 
	 * @param idO
	 * @param idD
	 * @return
	 */
	public boolean existsPath(int idO, int idD)
	{
		if(idO==idD)
		{
			return true;
		}
		if(this.calculated[idO]==0)
		{//calculate bfs
			ArrayList<Integer> vis = rw(idO,0); // ??? idO,0?
			this.calculated[idO] = 1;
		}
		//search the visited nodes for answer
		LinkedList<Integer> temp = this.reachable.get(idO);
		for(Integer i: temp)
		{
			if(i==idD)
			{
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * Calculates all the reachable nodes from all the nodes for the graph in memory
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap fullrw()
	{
		Set cont = g.getVertices().entrySet();
		int sumVertices = cont.size();
	    overAllVisits = new ArrayList<Integer>(Collections.nCopies(this.numVertices, 0));
	    overAllPaths  = new ArrayList<String>(Collections.nCopies(this.numVertices, " "));
	    //String fileName = System.getProperty("user.home")+"/visits_ant_"+name+".csv";
	           
        int rwNumber = 0;
        while (rwNumber < max) {
        	Iterator i = (Iterator) cont.iterator();
        	int countEntries = 0;
        	//int randomInit = (int) (Math.random()*(sumVertices-1))+1;
        	randomInit = (int) (Math.random()*(sumVertices));
        	
        	if(this.getForceStartNodeNummber()<sumVertices) {
        		randomInit = this.getForceStartNodeNummber(); // start always from the same vertice (ramdom must be Y)
        		// to avoid that use a number over the last vertice size
        	}
        	if(this.getTrace().contentEquals("Y")) {
        		System.out.println("rw number: "+ rwNumber);
        		System.out.println(" Count Entries "+countEntries);
        		System.out.println(" randomInit "+ randomInit);
        	}
	        while(i.hasNext()) 
	        {
	        	Map.Entry<Integer, Node> n = (Map.Entry<Integer, Node>) i.next();
	        	int nID = n.getValue().getId();
	        	//System.out.println("id: "+ nID + " name: " + n.getValue().getFullName());
	        	if (this.getRandomizeFirstNodeEachRW().equals("Y")) {
		        	if (countEntries == randomInit){ //random start
		        		if (getTurn()>1) {
		        			String shortName = getShortName(n.getValue().getFullName());
		        			if (!findDeleted(shortName)) {
				        		printName(n.getValue().getFullName(), "node ID: "+  nID+" randomInit: "+randomInit);
				        		rw(nID, rwNumber);
				        		//printVisitOrder(count);
				        		//printFrequency(count, cont);
				        		//printAnt(count);
				        		genRWBags(rwNumber);
		        			}
		        		} else {
			        		printName(n.getValue().getFullName(), "node ID: "+  nID+" randomInit: "+randomInit);
			        		rw(nID, rwNumber);
			        		//printVisitOrder(count);
			        		//printFrequency(count, cont);
			        		//printAnt(count);
			        		genRWBags(rwNumber);

		        		}
		        	}
	        	}
		        else {
	        		if (getTurn()>1) {
	        			String shortName = getShortName(n.getValue().getFullName());
	        			if (!findDeleted(shortName)) {

				        	printName(n.getValue().getFullName(), nID+"");
			        		rw(nID, rwNumber);
	        			}
	        		}else {
			        	printName(n.getValue().getFullName(), nID+"");
		        		rw(nID, rwNumber);

	        		}
		        }
				this.calculated[nID] += 1;
				countEntries++;
	        }
	        rwNumber ++;
        }
        printVisitOrder(max);
		printFrequency(max, cont);
		binaryCSV();
		bagCSV();

		printAnt(max);
        this.calculatedFull = true;
        // fecha o arquivo de ordem de visita
		try {
			writer.flush();
			writer.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Collections.sort(overAllVisits);
		ArrayList<Integer> top3 = new ArrayList<Integer>(overAllVisits.subList(overAllVisits.size() -3, overAllVisits.size()));
		return visited_ant;

		
	    //return returnTop(max,cont,top);
 
        
        // print the visit chain root and child
        /*
        cont = visited_ant.entrySet();
        it = (Iterator) cont.iterator();
        fileName = System.getProperty("user.home")+"/visits_ant.csv";
        writer = null;
        try {
			writer = new FileWriter(fileName);
			writer.append("root");
			writer.append(",");
			writer.append("child\n");
	        while (it.hasNext()) {
	        	Map.Entry<Node, Node> nIt = (Map.Entry<Node, Node>) it.next();
	        	int nIDiT = nIt.getValue().getId();
	        	int nKey  = nIt.getKey().getId();
	        	
	        	System.out.println(" root " + formatName(nIt.getKey().getFullName()) + "followed by child" + formatName(nIt.getValue().getFullName()));
	        
					writer.append(formatName(nIt.getKey().getFullName()));
					writer.append(",");
					writer.append(formatName(nIt.getValue().getFullName()));
	        	
	        }
        
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
        //for (int i = 0; i < overAllVisits.size(); i++) {
        //	System.out.println(" node: " + i + " nome " + cont.get(i).getFullName());
        //}
	}
	
	private boolean findDeleted(String shortName) {
		// TODO Auto-generated method stub
		for (int i=0; i<toDelete.size(); i++) {
			String toDeleteAux = (String) toDelete.get(i);
			if (shortName.equals(toDeleteAux)) {
				System.out.println("I found one. Ignoring...");
				return true;
			}
		}
		return false;
	}

	/**
	 * Do the Breadth First Search for a given root node and save all the nodes reachable from that 1st one
	 * 
	 * @param root
	 */
	public ArrayList rw(int root, int rwNumber)
	{
		ArrayList<Integer> visited = new ArrayList<Integer>(Collections.nCopies(this.numVertices, 0));
		Queue<Integer> queue = new LinkedList<Integer>();
	    queue.add(root);
	    visited.set(root, 1);
	    visitRWLap = new ArrayList(); // initialize (erase last lap info)
	   
	    String path = "";
	    ArrayList<LinkedList<Integer>> tempAdj = this.g.getAdj();
	    HashMap<Integer, Node> grafo = g.getVertices();
	    Node nodeRoot = grafo.get(root); 
	    NodeType tipo = nodeRoot.getNodeType();
    	String obs = "";
    	if (tipo == NodeType.Class) {
    		NodeClass noClasse = (NodeClass) nodeRoot;
    		obs = "classe "+noClasse.getDescClass();
    		path = noClasse.getDescClass();
    	} 
    	if (tipo == NodeType.Property) {
    		NodeProperty noPropriedade = (NodeProperty) nodeRoot;
    		obs ="Propriedade "+noPropriedade.getDescProp();
    		path = noPropriedade.getDescProp();
    	}
    	if (tipo == NodeType.RestrictionComplementOfClass) {
    		NodeRestrictionComplementOfClass noRestCompClass = (NodeRestrictionComplementOfClass) nodeRoot;
    		obs = "Rest classe complemento " + noRestCompClass.getDescClass();
    		path = noRestCompClass.getDescClass();
    	}
    	if (tipo == NodeType.RestrictionComplementOfProperty) {
    		NodeRestrictionComplementOfProperty noRestCompProp = (NodeRestrictionComplementOfProperty) nodeRoot;
    		obs = "Rest propriedade complemento "+noRestCompProp.getDescProp();
    		path = noRestCompProp.getDescProp();
    	}
    	if (tipo == NodeType.RestrictionCardinality) {
    		NodeRestrictionCardinality noRestCard = (NodeRestrictionCardinality) nodeRoot;
    		obs = "Rest Cardinalidade "+noRestCard.getDescProp()+" "+noRestCard.getDescRangeOrDomain();
    		path = noRestCard.getDescProp()+" "+noRestCard.getDescRangeOrDomain();
    	}
    	if (tipo == NodeType.RestrictionComplementOfRestrictionCardinality) {
    		NodeRestrictionComplementOfRestrictionCardinality noRestCompCard = (NodeRestrictionComplementOfRestrictionCardinality) nodeRoot;
    		obs = "Rest Complemento Rest Cardinalidade "+ noRestCompCard.getDescProp()+" "+noRestCompCard.getDescRangeOrDomain();
    		path = noRestCompCard.getDescProp()+" "+noRestCompCard.getDescRangeOrDomain();
    	}
	    printName(nodeRoot.getFullName(), obs);
	    visited_ant.put(nodeRoot, null); // dataset with dad child visit
	    String tempPath = overAllPaths.get(root);
	    words.add(path); // creates a new word in the dictionary of all words
	    
	    tempPath = tempPath + " " + path;
	    int total = overAllVisits.get(root);
	    total++;
	    overAllVisits.set(root, total); //dataset with total number of visits per node
	    overAllPaths.set(root, tempPath); //dataset with accumulate paths  of visits per node
	    Visit vis = new Visit();
	    vis.setRwNumber(rwNumber);
	    vis.setNode(nodeRoot);
	    vis.setHop(0);
	    vis.setPath(path);
	    visitList.add(vis); // dataset with the order of visit
	    visitRWLap.add(path); // dataset with paths per lap
	    int countVisited = 0;
	    //while(!queue.isEmpty())
	    boolean hasPath = true;
	    while (hasPath) // não precisa limitar em número = max. Tem que limitar apenas pra não ficar em loop eterno.
	    {
	    	// inserir aqui o random para ver qual nó seguir
	    	
	        //int node = (int)queue.remove();
	        // não precisa verificar se a lista de adjacência está vazia
	        //if(!this.g.isAdjEmpty(node))
	    	//if(!this.g.isAdjEmpty(root))
	        //{
	        	//LinkedList<Integer> tempChilds = tempAdj.get(root);
        	if(this.getTrace().contentEquals("Y"))

	    	    System.out.println("I am in : "+ root);
    		LinkedList<Integer> tempChilds = g.getList(root);
        	if(tempChilds != null && tempChilds.size() > 1)
        	{
        		// position random will be one of the tempChilds.
        		int possibilities = tempChilds.size();
        		//int randomPosition = (int)(Math.random()*possibilities)+1; // check to avoid bias
        		//Random rand = new Random();

        		// nextInt as provided by Random is exclusive of the top value so you need to add 1 

        		//int randomPosition = rand.nextInt((possibilities - 1) + 1) + 1;
        		//int randomPosition = rand.nextInt((possibilities - 1) + 1);
        		int randomPosition = 0;
        		if (this.getSetBreakIfBackToNode().equals("N")) {
        			float sorteio = (float)(Math.random()*(possibilities-1))+1;
            		randomPosition = Math.round(sorteio);
				} else {
					float sorteio = (float)(Math.random()*(possibilities-2))+2; // excludes the first position in adj matrix that is always a link to yourself
            		randomPosition = Math.round(sorteio);
				}
        		
        		if(this.getTrace().contentEquals("Y"))
        			System.out.println(" position: "+ randomPosition + " from 1 to "+possibilities);
        		Iterator<Integer> iterator = tempChilds.listIterator();
        		
        		int tries = 1;
        		while(iterator.hasNext())// está lendo menos 1!!! certificar o número de elementos na lista de adjacência
        		//do 
        		{
        			int child = iterator.next();
        			//int child2 = tempChilds.get(randomPosition);// está errado! tem que pegar um elemento do tempChilds dentro da linked list
    	        	if(this.getTrace().contentEquals("Y"))
    	        		System.out.println("Child to evaluate:" + child);
        			//System.out.println("Child to evaluate 2:" + child2);
                    //if (visited.get(child)==0) 
    	        	if(this.getTrace().contentEquals("Y"))
    	        		System.out.println("child "+ child + " #visits until now: " + visited.get(child) + " and check if "+ tries + " == position to visit: " + randomPosition );
        			//if (visited.get(child)==0 && count==randomPosition) 
        			if (tries==randomPosition)
                    {
        				Node nodeVisited = grafo.get(child);
                    	//Node nodeVisited = grafo.get(child);
                    	tipo = nodeVisited.getNodeType();
                    	//obs = visits+"";
                     	String nname = ""; 
                    	if (tipo == NodeType.Class) {
                    		NodeClass noClasse = (NodeClass) nodeVisited;
                    		obs +=  "classe "+noClasse.getDescClass();
                    		path +=  " "+noClasse.getDescClass();
                    		nname =  noClasse.getDescClass();
                    	} 
                    	if (tipo == NodeType.Property) {
                    		NodeProperty noPropriedade = (NodeProperty) nodeVisited;
                    		obs += "Propriedade "+noPropriedade.getDescProp();
                    		path += "  "+noPropriedade.getDescProp();
                    		nname = noPropriedade.getDescProp();
                    	}
                    	if (tipo == NodeType.RestrictionComplementOfClass) {
                    		NodeRestrictionComplementOfClass noRestCompClass = (NodeRestrictionComplementOfClass) nodeVisited;
                    		obs += "Rest classe complemento " +noRestCompClass.getDescClass();
                    		path += " " +noRestCompClass.getDescClass();
                    		nname = noRestCompClass.getDescClass();
                    	}
                    	if (tipo == NodeType.RestrictionComplementOfProperty) {
                    		NodeRestrictionComplementOfProperty noRestCompProp = (NodeRestrictionComplementOfProperty) nodeVisited;
                    		obs += "Rest propriedade complemento "+noRestCompProp.getDescProp();
                    		path += " "+noRestCompProp.getDescProp();
                    		nname = noRestCompProp.getDescProp();
                    	}
                    	if (tipo == NodeType.RestrictionCardinality) {
                    		NodeRestrictionCardinality noRestCard = (NodeRestrictionCardinality) nodeVisited;
                    		obs += "Rest Cardinalidade "+noRestCard.getDescProp()+" "+noRestCard.getDescRangeOrDomain();
                    		path += "  "+noRestCard.getDescProp()+" "+noRestCard.getDescRangeOrDomain();
                    		nname = noRestCard.getDescProp()+" "+noRestCard.getDescRangeOrDomain();
                    	}
                    	if (tipo == NodeType.RestrictionComplementOfRestrictionCardinality) {
                    		NodeRestrictionComplementOfRestrictionCardinality noRestCompCard = (NodeRestrictionComplementOfRestrictionCardinality) nodeVisited;
                    		obs += "Rest Complemento Rest Cardinalidade "+noRestCompCard.getDescProp()+" "+noRestCompCard.getDescRangeOrDomain();
                    		path += " "+noRestCompCard.getDescProp()+" "+noRestCompCard.getDescRangeOrDomain();
                    		nname = noRestCompCard.getDescProp()+" "+noRestCompCard.getDescRangeOrDomain();
                    	}

        				int visits = visited.get(child);
        				
                       	//if (visits < 2) {// to only print once the node (!?) verificar se ok - nao deveria marcar como visitado aqui denro???? Na verdade nao estou descartando ninguem!!! So no python
                    		//printName(nodeVisited.getFullName(), obs);
 
	        			visits++;
	        	        if(this.getTrace().contentEquals("Y"))

        	        		System.out.println("Child to visit:" + child + " number of times in this rw: " + visits);

	        	        countVisited++;
                        

	        	        
        				if (turn>1) {
        					
        					if (!findDeleted(nname)){
        						
        	                   	printName(nodeVisited.getFullName(), obs);

		                    	visited.set(child,visits); // acho que este cara tem que ficar dentro do if para somente contar qdo nao for o unico caminho possivel e igual ao pai dentro da mesma rw (dentro da full rw pode) 
		                    	
		                    	
		                    	visited_ant.put(nodeVisited, nodeRoot); // dataset with dad child visits 
		                    	//if (visits < 2) // to only print once the node (!?) verificar se ok - nao deveria marcar como visitado aqui denro???? Na verdade nao estou descartando ninguem!!! So no python
		                    		//printName(nodeVisited.getFullName(), obs);
		                    	tempPath = overAllPaths.get(child);
		                    	tempPath=tempPath + " " + path;
		                    	overAllPaths.set(child, tempPath); // dataset with total visits per node
		                    	words.add(nname); // creates a new word in the dictionary of all words
		                    	
		                    	
		                    	total = overAllVisits.get(child);
		                	    total++;
		                	    overAllVisits.set(child, total); // dataset with total visits per node
		                	    accumulateAnt(root, nodeRoot.getFullName(),child, nodeVisited.getFullName(), total, path);
		 
		                	    vis = new Visit();
		                	    vis.setRwNumber(rwNumber);
		                	    vis.setNode(nodeVisited);
		                	    vis.setHop(countVisited);
		                	    vis.setPath(path);
		                	    visitList.add(vis); // dataset with the order of visit
		                	    visitRWLap.add(nname); // dataset with nodes visited per lap
		
		                    	queue.add(child);
		                    	LinkedList<Integer> tempR = this.reachable.get(root);
		                    	tempR.add(child);
		                    	
		                       	
           					}
            			} else {
            				
	                    	visited.set(child,visits); // acho que este cara tem que ficar dentro do if para somente contar qdo nao for o unico caminho possivel e igual ao pai dentro da mesma rw (dentro da full rw pode) 
	                    	
	                    	
	                    	visited_ant.put(nodeVisited, nodeRoot); // dataset with dad child visits 
	                    	//if (visits < 2) // to only print once the node (!?) verificar se ok - nao deveria marcar como visitado aqui denro???? Na verdade nao estou descartando ninguem!!! So no python
	                    		//printName(nodeVisited.getFullName(), obs);
	                    	tempPath = overAllPaths.get(child);
	                    	tempPath=tempPath + " " + path;
	                    	overAllPaths.set(child, tempPath); // dataset with total visits per node
	                    	words.add(nname); // creates a new word in the dictionary of all words
	                    	
	                    	
	                    	total = overAllVisits.get(child);
	                	    total++;
	                	    overAllVisits.set(child, total); // dataset with total visits per node
	                	    accumulateAnt(root, nodeRoot.getFullName(),child, nodeVisited.getFullName(), total, path);
	 	                        
	                	    vis = new Visit();
	                	    vis.setRwNumber(rwNumber);
	                	    vis.setNode(nodeVisited);
	                	    vis.setHop(countVisited);
	                	    vis.setPath(path);
	                	    visitList.add(vis); // dataset with the order of visit
	                	    visitRWLap.add(nname); // dataset with nodes visited per lap
	
	                    	queue.add(child);
	                    	LinkedList<Integer> tempR = this.reachable.get(root);
	                    	tempR.add(child);
	                       

            			}
        	        	if(this.getTrace().contentEquals("Y"))
        	        		
        	        		System.out.println("rw hop "+countVisited);

                    	// now the child became root 
                    	
        	        	
                     	root = child;
                     	nodeRoot = nodeVisited;

     
                    }
        			
        			tries++;
        			if(this.getTrace().contentEquals("Y"))
        				System.out.println("count "+tries);
        			
        		}//while(iterator.hasNext());
        	}
        	else { //tempChils == null or tempChild <2 tempChilds == 1 means we have only link to itself = no outlet !!!
        		hasPath=false;
        		if(this.getTrace().contentEquals("Y"))
            		System.out.println(" No outlet! " + root + " Visited #"+ visited.get(root));
        	}
        	if(this.getTrace().contentEquals("Y"))
        		System.out.println(" (new root): " + root + " Visited #"+ visited.get(root));
        	if(this.getRestartSameRWIfLoop().contentEquals("Y")) {
	        	if (visited.get(root)>1) { // se este root jah foi nesta rw nao repete
		        	if(this.getTrace().contentEquals("Y"))

		        		System.out.println("Vazando pq: estou em: " + root + " e jah visitei esse cara # vezes: "+ visited.get(root));
	        		break;
	        	}
			}
	       // }
	    } //while(hasPath)
	    if(this.getTrace().contentEquals("Y"))
    		System.out.println(" Exit RW " + root + " Visited #"+ visited.get(root));
	    return visited;
	}
	
    private void genRWBags(int rwNumber) {
    	int start = 0;
    	int end = windowSize;
    	String bag = "";
    	boolean hasStep = true; // check if is possible to get another bag
    	if (end > visitRWLap.size()) {
        	if(this.getTrace().contentEquals("Y")) {
        		System.out.println(" start:"+start+ " end:"+ end + " windowSize: "+ windowSize + " offSet:" + offSet);
        		System.out.println("Not possible to have a bag with that window size. Started in " + randomInit + " RW # : "+ rwNumber);
        	}
    	} else {
    		while (hasStep) {
    			for (int i = start; i<end; i++)// get paths of the lap
    				bag += visitRWLap.get(i) + " ";
    			
    			Bag b = new Bag();
    			b.setStartNode(randomInit);
    			b.setBag(bag);
    			bags.add(b);
    			start += offSet;
    			end = start+windowSize;
    			bag = ""; 
    		   	if (end > visitRWLap.size()) {
    	        	if(this.getTrace().contentEquals("Y")) {
    	        		System.out.println(" start:"+start+ " end:"+ end + " windowSize: "+ windowSize + " offSet:" + offSet);

    	        		System.out.println("Not possible to have a bag with that window size. Started in " + randomInit + " RW # : "+ rwNumber);
    	        	}
    	        	hasStep = false;
    	    	}
    		}
    	}
    	
    }
	
	private void bagCSV() {
		// TODO Auto-generated method stub
		Iterator i = words.iterator();
		//Iterator j = overAllPaths.iterator();
		//String fileName = "/Users/fd252/Dropbox/UniRio/ED5/visits/data"+"/visits_bin_bag_"+windowSize+"_"+offSet+"_"+name+".csv";
		String fileName = pathcsv+"/visits_bin_bag_"+windowSize+"_"+offSet+"_"+name+".csv";
		writer = null;
		try {
			writer = new FileWriter(fileName);
			String header = "nodeNumber,";
			boolean isFirst = true;
			while(i.hasNext()) {
				if (!isFirst) {
					header+=",";
					
				} else {
					isFirst = false;
				}
				header+=i.next();
				
			}
			header+="\n";
			writer.append(header);
			String line = "";
			
			
			for (int j = 0; j<bags.size();j++) {
				//line = (String) j.j.next()+",";
				Bag b = bags.get(j);
				line = b.getStartNode()+",";
				//while(j.hasNext()) {
				i = words.iterator();
				
				String find = (String) b.getBag();
				isFirst = true;
				while(i.hasNext()) {
					String tpath = (String) i.next();
					if (!isFirst) {
						line+=",";
						
					} else {
						isFirst = false;
					}
					//String find = (String) j.next();
					if (find.length()>2)
						find = find.trim(); 
					if (find.indexOf(tpath)!=-1) { //optimistic if the node is a substring then match!
						line+="1";
					} else {
						line+="0";
					}
				}
				writer.append(line+"\n");
			}
			writer.flush();
			writer.close();
			
		}
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void binaryCSV() {
		Iterator i = words.iterator();
		//Iterator j = overAllPaths.iterator();
		//String fileName = "/Users/fd252/Dropbox/UniRio/ED5/visits/data"+"/visits_bin_"+name+".csv";
		String fileName = pathcsv+"/visits_bin_"+name+".csv";
		writer = null;
		try {
			writer = new FileWriter(fileName);
			String header = "nodeNumber,";
			boolean isFirst = true;
			while(i.hasNext()) {
				if (!isFirst) {
					header+=",";
					
				} else {
					isFirst = false;
				}
				header+=i.next();
				
			}
			header+="\n";
			writer.append(header);
			String line = "";
			
			
			for (int j = 0; j<overAllPaths.size();j++) {
				//line = (String) j.j.next()+",";
				line = j+",";
				//while(j.hasNext()) {
				i = words.iterator();
				String find = (String) overAllPaths.get(j);
				isFirst = true;
				while(i.hasNext()) {
					String tpath = (String) i.next();
					if (!isFirst) {
						line+=",";
						
					} else {
						isFirst = false;
					}
					//String find = (String) j.next();
					if (find.length()>2)
						find = find.trim(); 
					if (find.indexOf(tpath)!=-1) {
						line+="1";
					} else {
						line+="0";
					}
				}
				writer.append(line+"\n");
			}
			writer.flush();
			writer.close();
			
		}
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printVisitOrder(int count) {
		// print visit order
        Iterator it = (Iterator) visitList.iterator();
        
        //String fileName = System.getProperty("user.home")+"/visits_order_"+name+".csv";
        //String fileName = "/Users/fd252/Dropbox/UniRio/ED5/visits/data"+"/visits_order_"+name+".csv";
        String fileName = pathcsv+"/visits_order_"+name+".csv";
       writer = null;
        try {
			writer = new FileWriter(fileName);
			writer.append("nodeId,nodeName,nodeType,nodeLevel,loop,hop,path\n");
			//writer.append(",");
			//writer.append("visits\n");
	        while (it.hasNext()) {
	        	Visit v = (Visit) it.next();
	        	
	        	Node nd = v.getNode();
	        	int hop = v.getHop();
	        	int loop = v.getRwNumber();
	        	String path = v.getPath();
	        	int ndId = nd.getId();
	        	String ndName = nd.getFullName();
	        	NodeType ndType = nd.getNodeType();
	        	String typeName = ndType.name();
	        	int ndLevel = nd.getLevel();
	        	if(this.getTrace().contentEquals("Y"))
	        		System.out.println(" node: " + ndId + " nome " + formatName(ndName) + " type: "+ typeName + " level: "+ ndLevel + " loop: "+ loop + " hop: "+ hop + " path" + path);
	        
				writer.append(ndId+",");
				
				writer.append(formatName(ndName)+",");
				writer.append(typeName+",");
				writer.append(ndLevel+",");
				writer.append(loop+",");
				writer.append(hop+",");
				writer.append(path+"\n");
        	
	        }
        
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	private void printFrequency(int count, Set cont) {
	       // print frequency
        Iterator it = (Iterator) cont.iterator();
        
        //fileName = System.getProperty("user.home")+"/visits_"+name+".csv";
       // String fileName = "/Users/fd252/Dropbox/UniRio/ED5/visits/data"+"/visits_"+name+".csv";
        String fileName = pathcsv+"/visits_"+name+".csv";
       writer = null;
        try {
			writer = new FileWriter(fileName);
			writer.append("nodeNumber");
			writer.append(",");
			writer.append("node");
			writer.append(",");
			writer.append("visits");
			writer.append(",");
			writer.append("paths");
			writer.append(",");
			writer.append("uniquepaths\n");
	        while (it.hasNext()) {
	        	Map.Entry<Integer, Node> nIt = (Map.Entry<Integer, Node>) it.next();
	        	int nIDiT = nIt.getValue().getId();
	        	int totalVisits = overAllVisits.get(nIDiT);
	        	String totalPath = overAllPaths.get(nIDiT);
	        	String cleanPath = cleanPath(totalPath);
	        	if(this.getTrace().contentEquals("Y"))

	        		System.out.println(" node: " + nIDiT + " nome " + formatName(nIt.getValue().getFullName()) + " Total Visits: "+ totalVisits);
	        
	        	writer.append(nIDiT+"");
				writer.append(",");
				writer.append(formatName(nIt.getValue().getFullName()));
				writer.append(",");
				writer.append(totalVisits+"");
				writer.append(",");
				writer.append(totalPath);
				writer.append(",");
				writer.append(cleanPath+"\n");
	        	
	        }
        
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String cleanPath(String totalPath) {
		
		Set <String> uniquePath = new HashSet();
		String cleanPath = " "; 
		
		// TODO Auto-generated method stub
		StringTokenizer st = new StringTokenizer(totalPath);
	     while (st.hasMoreTokens()) {
	         uniquePath.add(st.nextToken());
	     }
	     
	     Iterator it = uniquePath.iterator();
	     
	     while (it.hasNext()) {
	    	 cleanPath += " " + it.next();
	     }
		
		return cleanPath;
	}

	private void printName(String nodeFullName, String obs) {
		// TODO Auto-generated method stub
		//if (n.getValue().getId()==290){
		//	System.out.println(" id ="+ n.getValue().getId());
		//}
		if (nodeFullName.indexOf("MinCardinality")==-1&&nodeFullName.indexOf("ComplementOf")==-1&&nodeFullName.indexOf("~")==-1) {
			int initialPosition = nodeFullName.lastIndexOf("/");
			int finalPosition = nodeFullName.lastIndexOf(">");
			if (finalPosition == -1){
				finalPosition = nodeFullName.length();
			}
			String nodeName = nodeFullName.substring(initialPosition+1,finalPosition);
			//String nodeName = n.getValue().getFullName().substring(initialPosition+1);
        	if(this.getTrace().contentEquals("Y"))

        		System.out.println(" visitei: " + nodeName + " "+ obs);
			
		} else {
        	if(this.getTrace().contentEquals("Y"))

        		System.out.println(" descartei: " + nodeFullName + " "+ obs);
		}
			
	}
	
	private String getShortName(String nodeFullName) {
		// TODO Auto-generated method stub
		//if (n.getValue().getId()==290){
		//	System.out.println(" id ="+ n.getValue().getId());
		//}
		if (nodeFullName.indexOf("MinCardinality")==-1&&nodeFullName.indexOf("ComplementOf")==-1&&nodeFullName.indexOf("~")==-1) {
			int initialPosition = nodeFullName.lastIndexOf("/");
			int finalPosition = nodeFullName.lastIndexOf(">");
			if (finalPosition == -1){
				finalPosition = nodeFullName.length();
			}
			String nodeName = nodeFullName.substring(initialPosition+1,finalPosition);
			//String nodeName = n.getValue().getFullName().substring(initialPosition+1);
        	if(this.getTrace().contentEquals("Y"))

        		System.out.println(" ShortName: " + nodeName );
			return nodeName;
			
		} else {
        	if(this.getTrace().contentEquals("Y"))

        		System.out.println(" ShortName not considered (complement or negation): " + nodeFullName );
        	return null;
		}
			
	}


	private String formatName(String nodeFullName) {
		// TODO Auto-generated method stub
		//if (n.getValue().getId()==290){
		//	System.out.println(" id ="+ n.getValue().getId());
		//}
		if (nodeFullName.indexOf("MinCardinality")==-1&&nodeFullName.indexOf("ComplementOf")==-1) {
			int initialPosition = nodeFullName.lastIndexOf("/");
			int finalPosition = nodeFullName.lastIndexOf(">");
			if (finalPosition == -1){
				finalPosition = nodeFullName.length();
			}
			String nodeName = nodeFullName.substring(initialPosition+1,finalPosition);
			//String nodeName = n.getValue().getFullName().substring(initialPosition+1);
			return nodeName;
			
		} else {
			return nodeFullName;
		}
			
	}
	
	private void accumulateAnt(int root, String rootName, int child, String nodeName, int visits, String path) {
		printAnt = printAnt + root + ","+ formatName(rootName) + ","+ child + ","+ formatName(nodeName) + ","+ visits + ","+ path+ "\n";
	}
	private void printAnt(int count) {
		 String fileName = "/Users/fd252/Dropbox/UniRio/ED5/visits/data"+"/visits_ant_"+name+".csv";
	     writer = null;
        
		try {
			writer = new FileWriter(fileName);
			writer.append("rootId");
			writer.append(",");
			writer.append("root");
			writer.append(",");
			writer.append("childId");
			writer.append(",");
			writer.append("child");
			writer.append(",");
			writer.append("incremental visits\n");
			writer.append(printAnt);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*try {
			writer.append(formatName(rootName));
			writer.append(",");
			writer.append(formatName(nodeName));
			writer.append(",");
			writer.append(visits+"\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	private HashMap returnTop(int count, Set cont, int top) {
	    HashMap topItens = new HashMap();
	    TreeSet ts = new TreeSet(cont);
	    
	    int c = 0;
	    Iterator it =  ts.descendingIterator();	
	    while (it.hasNext() && c<top ) {
	    	Map.Entry<Integer, Node> nIt = (Map.Entry<Integer, Node>) it.next();
	    	int nIDiT = nIt.getValue().getId();
	    	int totalVisits = overAllVisits.get(nIDiT);
	    	if(this.getTrace().contentEquals("Y"))
	
	    		System.out.println(" node: " + nIDiT + " nome " + formatName(nIt.getValue().getFullName()) + " Total Visits: "+ totalVisits);
	    	topItens.put(nIDiT, totalVisits);
	    	c++;
	    	
	    }
     
	    return topItens;
	}

	/**
	 * If the full BFS has been calculated returns the transitive closure of graph g,
	 * otherwise calculates the Full BFS and then returns the transitive closure
	 * @return
	 */
	public ArrayList<LinkedList<Integer>> getReachable()
	{
		if(this.calculatedFull)
		{
			return this.reachable;
		}
		fullrw();
		return this.reachable;
	}
	
	/**
	 * May be used to force full BFS recalculation when getting the transitive closure of g
	 */
	public void resetCalculatedFull()
	{
		this.calculatedFull = false;
	}
	
	public Boolean getCalculatedFull()
	{
		return this.calculatedFull;
	}

}

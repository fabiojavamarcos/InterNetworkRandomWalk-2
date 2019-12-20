package Ontology;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


/**
 * This Class implements the Breadth First Search algorithm over the Graph Class to discover if there are paths between certain nodes
 *
 * @author RÃ´mulo de Carvalho MagalhÃ£es
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
	private HashMap<Node, Node> visited_ant = new HashMap<Node, Node>();
	FileWriter writer = null;
	
	private String name;
	private int max;
	
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
			rw(idO);
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
	 * Do the Breadth First Search for a given root node and save all the nodes reachable from that 1st one
	 * 
	 * @param root
	 */
	public void rw(int root)
	{
		ArrayList<Integer> visited = new ArrayList<Integer>(Collections.nCopies(this.numVertices, 0));
		Queue<Integer> queue = new LinkedList<Integer>();
	    queue.add(root);
	    visited.set(root, 1);
	    
	    ArrayList<LinkedList<Integer>> tempAdj = this.g.getAdj();
	    HashMap<Integer, Node> grafo = g.getVertices();
	    Node nodeRoot = grafo.get(root); 
	    NodeType tipo = nodeRoot.getNodeType();
    	String obs = "nada";
    	if (tipo == NodeType.Class) {
    		NodeClass noClasse = (NodeClass) nodeRoot;
    		obs = "classe "+noClasse.getDescClass();
    	} 
    	if (tipo == NodeType.Property) {
    		NodeProperty noPropriedade = (NodeProperty) nodeRoot;
    		obs ="Propriedade "+noPropriedade.getDescProp();
    	}
    	if (tipo == NodeType.RestrictionComplementOfClass) {
    		NodeRestrictionComplementOfClass noRestCompClass = (NodeRestrictionComplementOfClass) nodeRoot;
    		obs = "Rest classe complemento " + noRestCompClass.getDescClass();
    	}
    	if (tipo == NodeType.RestrictionComplementOfProperty) {
    		NodeRestrictionComplementOfProperty noRestCompProp = (NodeRestrictionComplementOfProperty) nodeRoot;
    		obs = "Rest propriedade complemento "+noRestCompProp.getDescProp();
    	}
    	if (tipo == NodeType.RestrictionCardinality) {
    		NodeRestrictionCardinality noRestCard = (NodeRestrictionCardinality) nodeRoot;
    		obs = "Rest Cardinalidade "+noRestCard.getDescProp()+" "+noRestCard.getDescRangeOrDomain();
    	}
    	if (tipo == NodeType.RestrictionComplementOfRestrictionCardinality) {
    		NodeRestrictionComplementOfRestrictionCardinality noRestCompCard = (NodeRestrictionComplementOfRestrictionCardinality) nodeRoot;
    		obs = "Rest Complemento Rest Cardinalidade "+ noRestCompCard.getDescProp()+" "+noRestCompCard.getDescRangeOrDomain();
    	}
	    printName(nodeRoot.getFullName(), obs);
	    visited_ant.put(nodeRoot, null);
	    int total = overAllVisits.get(root);
	    total++;
	    overAllVisits.set(root, total);
	    int countVisited = 0;
	    //while(!queue.isEmpty())
	    while (countVisited < max)
	    {
	    	// inserir aqui o random para ver qual nÃ³ seguir
	    	
	        //int node = (int)queue.remove();
	        // nÃ£o precisa verificar se a lista de adjacÃªncia estÃ¡ vazia
	        //if(!this.g.isAdjEmpty(node))
	    	//if(!this.g.isAdjEmpty(root))
	        //{
	        	//LinkedList<Integer> tempChilds = tempAdj.get(root);
	    		LinkedList<Integer> tempChilds = g.getList(root);
	        	if(tempChilds != null && tempChilds.size() > 0)
	        	{
	        		// position random will be one of the tempChilds.
	        		int possibilities = tempChilds.size();
	        		//int randomPosition = (int)(Math.random()*possibilities)+1; // check to avoid bias
	        		//Random rand = new Random();

	        		// nextInt as provided by Random is exclusive of the top value so you need to add 1 

	        		//int randomPosition = rand.nextInt((possibilities - 1) + 1) + 1;
	        		//int randomPosition = rand.nextInt((possibilities - 1) + 1);
	        		float sorteio = (float)(Math.random()*(possibilities-1))+1;
	        		int randomPosition = Math.round(sorteio);
	        		//System.out.println(" position: "+ randomPosition + "from 1 to "+possibilities);
	        		Iterator<Integer> iterator = tempChilds.listIterator();
	        		int count = 1;
	        		while(iterator.hasNext())// estÃ¡ lendo menos 1!!! certificar o nÃºmero de elementos na lista de adjacÃªncia
	        		//do 
	        		{
	        			int child = iterator.next();
	        			//int child2 = tempChilds.get(randomPosition);// estÃ¡ errado! tem que pegar um elemento do tempChilds dentro da linked list
	        			//System.out.println("Child to evaluate:" + child);
	        			//System.out.println("Child to evaluate 2:" + child2);
                        //if (visited.get(child)==0) 
	        			//System.out.println("testing if child "+ child + " have been visited " + visited.get(child) + " and if count "+ count + " == "+ randomPosition );
	        			//if (visited.get(child)==0 && count==randomPosition) 
	        			if (count==randomPosition)
                        {
	        				int visits = visited.get(child);
	        				visits++;
	        				//System.out.println("Child to visit:" + child + " number of times in this rw " + visits);
                        	visited.set(child,visits); // acho que este cara tem que ficar dentro do if para somente contar qdo não for o único caminho possível e igual ao pai dentro da mesma rw (dentro da full rw pode) 
                        	
                        	Node nodeVisited = grafo.get(child);
                        	tipo = nodeVisited.getNodeType();
                        	obs = "nada";
                        	if (tipo == NodeType.Class) {
                        		NodeClass noClasse = (NodeClass) nodeVisited;
                        		obs =  "classe "+noClasse.getDescClass();
                        	} 
                        	if (tipo == NodeType.Property) {
                        		NodeProperty noPropriedade = (NodeProperty) nodeVisited;
                        		obs = "Propriedade "+noPropriedade.getDescProp();
                        	}
                        	if (tipo == NodeType.RestrictionComplementOfClass) {
                        		NodeRestrictionComplementOfClass noRestCompClass = (NodeRestrictionComplementOfClass) nodeVisited;
                        		obs = "Rest classe complemento " +noRestCompClass.getDescClass();
                        	}
                        	if (tipo == NodeType.RestrictionComplementOfProperty) {
                        		NodeRestrictionComplementOfProperty noRestCompProp = (NodeRestrictionComplementOfProperty) nodeVisited;
                        		obs = "Rest propriedade complemento "+noRestCompProp.getDescProp();
                        	}
                        	if (tipo == NodeType.RestrictionCardinality) {
                        		NodeRestrictionCardinality noRestCard = (NodeRestrictionCardinality) nodeVisited;
                        		obs = "Rest Cardinalidade "+noRestCard.getDescProp()+" "+noRestCard.getDescRangeOrDomain();
                        	}
                        	if (tipo == NodeType.RestrictionComplementOfRestrictionCardinality) {
                        		NodeRestrictionComplementOfRestrictionCardinality noRestCompCard = (NodeRestrictionComplementOfRestrictionCardinality) nodeVisited;
                        		obs = "Rest Complemento Rest Cardinalidade "+noRestCompCard.getDescProp()+" "+noRestCompCard.getDescRangeOrDomain();
                        	}
                        	visited_ant.put(nodeVisited, nodeRoot);
                        	if (visits < 2) // to only print once the node (!?) verificar se ok - não deveria marcar como visitado aqui denro???? Na verdade não estou descartando ninguém!!! Só no python
                        		printName(nodeVisited.getFullName(), obs);
                        	
                        	total = overAllVisits.get(child);
                    	    total++;
                    	    overAllVisits.set(child, total);
                    	    imprime(nodeRoot.getFullName(),nodeVisited.getFullName(), total);
                    	    
                        	queue.add(child);
                        	LinkedList<Integer> tempR = this.reachable.get(root);
                        	tempR.add(child);
                        	// now the child became root 
                         	root = child;
                         	nodeRoot = nodeVisited;
                        	
                        	countVisited++;
                        	//System.out.println("count visited "+countVisited);
                        	
                        }
	        			
	        			count++;
	        			//System.out.println("count "+count);
	        			
	        		}//while(iterator.hasNext());
	        	}
	        	//System.out.println(" (root): " + root + " Visited "+ visited.get(root));
	        	if (visited.get(root)>1) { // se este root já foi nesta rw não repete
	        		break;
	        	}
	       // }
	    }
	}
	
	/**
	 * Calculates all the reachable nodes from all the nodes for the graph in memory
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fullrw()
	{
		Set cont = g.getVertices().entrySet();
		int sumVertices = cont.size();
	    overAllVisits = new ArrayList<Integer>(Collections.nCopies(this.numVertices, 0));

	    String fileName = System.getProperty("user.home")+"/visits_ant_"+name+".csv";
        writer = null;
        
		try {
			writer = new FileWriter(fileName);
			writer.append("root");
			writer.append(",");
			writer.append("child");
			writer.append(",");
			writer.append("incremental visits\n");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        int count = 0;
        while (count < max) {
        	Iterator i = (Iterator) cont.iterator();
        	int countEntries = 0;
        	int randomInit = (int) (Math.random()*(sumVertices-1))+1;
        	//System.out.println("count: "+ count);
        	//System.out.println(" Count Entries "+countEntries);
        	//System.out.println(" randomInit "+ randomInit);
	        while(i.hasNext()) 
	        {
	        	Map.Entry<Integer, Node> n = (Map.Entry<Integer, Node>) i.next();
	        	int nID = n.getValue().getId();
	        	//System.out.println("id: "+ nID + " name: " + n.getValue().getFullName());
	        	if (countEntries == randomInit){
	        		//printName(n.getValue().getFullName());
	        		rw(nID);
	        	}
				this.calculated[nID] = 1;
				countEntries++;
	        }
	        count ++;
        }
        this.calculatedFull = true;
        // fecha o arquivo de ordem de visita
		try {
			writer.flush();
			writer.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // print frequency
        Iterator it = (Iterator) cont.iterator();
        fileName = System.getProperty("user.home")+"/visits_"+name+".csv";
        writer = null;
        try {
			writer = new FileWriter(fileName);
			writer.append("node");
			writer.append(",");
			writer.append("visits\n");
	        while (it.hasNext()) {
	        	Map.Entry<Integer, Node> nIt = (Map.Entry<Integer, Node>) it.next();
	        	int nIDiT = nIt.getValue().getId();
	        	int totalVisits = overAllVisits.get(nIDiT);
	        	System.out.println(" node: " + nIDiT + " nome " + formatName(nIt.getValue().getFullName()) + " Total Visits: "+ totalVisits);
	        
					writer.append(formatName(nIt.getValue().getFullName()));
					writer.append(",");
					writer.append(totalVisits+"\n");
	        	
	        }
        
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
			System.out.println(" visitei: " + nodeName + " "+ obs);
			
		} else {
			System.out.println(" descartei: " + nodeFullName + " "+ obs);
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
	
	private void imprime(String rootName, String nodeName, int visits) {
		try {
			writer.append(formatName(rootName));
			writer.append(",");
			writer.append(formatName(nodeName));
			writer.append(",");
			writer.append(visits+"\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

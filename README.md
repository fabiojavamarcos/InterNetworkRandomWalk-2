# InterNetworkRandomWalk-2
Branch name skipDeleted:

Arguments:

/**
	 *  function that run in batch loading ontologies from args
	 * 
	 * @param args
	 
	 * args[0] = path
	 
	 * args[1] = number of ontologies in net1 <- not used
	 
	 * args[2] = number of ontologies in net2 <- not used
	 
	 * args[3] = limits the RW (number of RW)
	 
   * args[4] = name of ontology
   * 
	 * 
	 * 
	 */
	 
   args - argument name // semantic
   5 - pathcsv// path to write csvs 
   6 - breakIfBackToNode // Y = do not use the link to itself
   7 - randomizeFirstNodeEachRW // Y = random the next start N = run rw 1 time for each possible node 
   8 - restartSameRWIfLoop // Y  = if visit the same node in one rw break 
   9 - trace // Y show prints
   10 - forceStartNodeNummber// number indicating the node to start always at each rw. if number > size of vertices use the ramdom 
   // start always from the same vertice (ramdom must be Y)
		// to avoid that use a number over the last vertice size
   11 - runMode // C = class, P = properties, B = Both
   // B (Both) C (Classes) P (Properties) T (All)
   
   12 - windowSize // Number of considered nodes visits (sliding window) //Max Size of the window in slidding window
   13 - offSet // next start point from the last window // size of the jump whem moving the window
   14 - turn // number of the turn -> now its part of the file path! // the turn will be added to the path (used when running multiples times like a chain, improving the results)
   15 - quartil // percent limit to consider in rw // number of the quartil to consider when deciding were to cut a random walk
   		// if quartil has a valid value, the threshold value will be the quartil + the informed threshold parameter in %.
		// else the threshold value will be the absolute value to the threshold parameter (useful when you know the dataset and want a specific value)
		// ex: quartil 1 threshold 10. Get 1st quartil more 10%
		// ex: quartil 5 (impossible) threshold 10. !0 will be the absolute value for cut data
		// Obs: quartil -1 = dif from quartil 3 and 1
		// quartil 5 = median. Quartil 0 = min value.


 	 16 - threshold // percent limit to consider in rw
 	 17 - mode // RW regular RW. // BASELINE (generate random result for the visits. Use args[3] to determine how much results must be generated) or RUN

   18  - list of nodes to be ignored // position to start receiving the nodes to ignore






Example:

/Users/fd252/Documents/dev/conferenceOAEI/
2
2
1410
iasted.owl
"/Users/fd252/Google Drive (fabiojavamarcos@gmail.com)/ED6/visits/data"
Y
Y
N
Y
9999
C
2
2
1
3
0
5
RUN
noinitialize

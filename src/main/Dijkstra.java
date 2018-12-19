package main;

import java.util.PriorityQueue;

/**
 * 
 * Dijkstra class that can run Dijkstra's algorithm on a given graph.
 *
 */
public class Dijkstra extends CLILogger {
    /**
     * The time when the algorithm started running. Will be set when using the start() method.
     */
	public long startTime;
	
	/**
	 * The graph upon which this Dijkstra instance works
	 */
    private Graph graph;
    
    /**
     * From this node to every node of the graph, the shortest distance is searched in the algorithm.  
     */
    private int startNode;

    /**
     * Create instance of Dijkstra's algorithm. When using start() method, it will find the shortest distances
     * from a given start node to all nodes in the given graph object.
     * 
     * The distances will be saved in the graph through the setDistance method.
     *  
     * @param graph	The graph to operate upon
     * @param start	Shortest distance from this one to every node of the graph will be found.  
     */
    public Dijkstra(Graph graph, int start) {
        this.graph = graph;
        this.startNode = start;
        
        graph.setDistance(start, 0.0);
    }


    
    /**
     * Starts Dijkstra's algorithm.
     * In the graph given to this instance upon construction, it will overwrite the distance properties with
     * the shortest distances to the starting node.
     * 
     * Saves the preceding node on a path from start to node n in the graph's array predecessor[]. 
     * 
     */
    public void start() {
    	print("Dijkstra's algorithm running...");
        this.startTiming();
        
        NodeHeap heap = new NodeHeap(graph, startNode);
        PriorityQueue<Node> queue = new PriorityQueue<>();
        
        Node node;
        Node nodeArray[] = new Node[graph.getNodesCount()];
        
        for (int i = 0; i < graph.getNodesCount(); i++) {
        	node = new Node(i, graph);
        	nodeArray[i] = node;
        	queue.add(node);
//        	System.out.println("added node " + node.getID() + " to queue");
		}
        
        
        
        //TODO: clean up comments and possible debug sysouts
        
        /* For reference: in the graph,
         * edges[n]	 contains destination of edge # n
         * weight[n] contains weight of edge # n
         * 
         * offset[n] is where the first edge for node n is stored
         * offset[n+1] -1 is the last one (can overflow! last edge is last entry. can be -1.)
         * 			is taken care off in new implementation of countOutgoingEdges
         *
         */


      
        int count = 0; //TODO: remove debug counters
        int countEdges = 0;
//        debugPrint("Heap Top: " +heap.getTopElementsPeek());
        
        
//        int currentNode = heap.getAndRemoveNext();
        int currentNode = queue.remove().getID();
        		
        
        while (currentNode >= 0) {
        	count++;
        	if (count % 50000 == 0) {
				print("at node " + count + " of " + graph.getNodesCount());
			}
        	
        	int firstEdge = graph.getOffset(currentNode);
        	
        	int currentDestination;
        	double newDistance;
        	
//        	if (count <= 40 || count > graph.getMaxEdgesCount() - 50) {
//				debugPrint("Node: " + currentNode);
//			}
        	
        	for (int i = firstEdge; i < firstEdge + graph.countOutgoingEdges(currentNode); i++) {
        		countEdges++;
        		
        		if (countEdges % 50000 == 0) {
					print("at edge " + countEdges + " of "+ graph.getMaxEdgesCount());
				}
        		
//        		if (graph.getWeight(i) >= 0) {
            		// graph.getWeight(i) can be -1 if edge doesn't exist. 
        			
        			currentDestination = graph.getDestination(i);
        			newDistance = graph.getDistance(currentNode) + graph.getWeight(i);
        			
//        			if (count <= 40 || count > graph.getMaxEdgesCount() - 50) {
//        				debugPrint("\tedge: to " + currentDestination + " (cost " + graph.getWeight(i) + ")");
//        			}
        			
        			
        			if (graph.getDistance( currentDestination ) > newDistance) {
        				
//        				if (count <= 40 || count > graph.getMaxEdgesCount() - 50) {
//            				debugPrint("     decreasing and reheap (" + graph.getDistance( currentDestination ) + ">" + newDistance + ")");
//            			}
						//found shorter path to this neighbour. Update distance, predecessor and heap.
        				
        				//TODO: added prioqueue instead of heap decreasekey
        				queue.remove(nodeArray[currentDestination]);
//        				heap.decreaseDistance(currentDestination, newDistance);
        				graph.setDistance(currentDestination, newDistance);
        				queue.add(nodeArray[currentDestination]);
        				graph.setPredecessor(currentDestination, currentNode);

//        				if (count <= 40 || count > graph.getMaxEdgesCount() - 50) {
//            				debugPrint("       distance of " + currentDestination + " has been set to " + graph.getDistance(currentDestination));
//            			}
        				
					}
        			
//        			if (count <= 40 || count > graph.getMaxEdgesCount() - 50) {
//        				debugPrint("\n");//TODO: remove if blocks with extensive debug code
//        			}
        			
        			
        			
//				} // von if weight >= 0

        			
        			
			}
        	
//        	if (count <= 40 || count > graph.getMaxEdgesCount() - 50) {
//				debugPrint("location of 3096359 is " + heap.getPositionOf(3096359)  + ". Determine next node now.");
//				debugPrint("Heap Top: "+ heap.getTopElementsPeek() + "\n");
//			}

//        	currentNode = heap.getAndRemoveNext();//TODO: replaced heap with prioqueue
        	try {
        		currentNode = queue.remove().getID();
			} catch (Exception e) {
				currentNode = -1;// end while loop when queue is empty
			}
        	

        }


        //passes info to CLI
        this.stop();
        print("Dijkstra's algorithm completed in " + CLILogger.runtimeInSeconds(this.runtime) + " seconds.");
        
        print(count + " nodes and " + countEdges + " edges visited.");
        
//        print("1: Node 16743651 has distance " + graph.getDistance(16743651));
//        print("2: Node 16743652 has distance " + graph.getDistance(16743652));
//        print("3: Node 16743653 has distance " + graph.getDistance(16743653));
//        print("4: Node 16743654 has distance " + graph.getDistance(16743654));
//        print("5: Node 16743655 has distance " + graph.getDistance(16743655));
//        print("6: Node 16743656 has distance " + graph.getDistance(16743656));
//        print("7: Node 16743657 has distance " + graph.getDistance(16743657));
//        print("8: Node 16743658 has distance " + graph.getDistance(16743658));
//        print("9: Node 16743659 has distance " + graph.getDistance(16743659));
//        print("10: Node 16743660 has distance " + graph.getDistance(16743660));
    }
    
    //TODO: assertTimeout: run under 20 seconds
}

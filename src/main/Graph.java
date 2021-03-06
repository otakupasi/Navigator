package main;

import java.util.Arrays;

/**
 * A class for a directed graph.
 * <p>
 * It can be used with the Dijkstra class to find minimum distances from all nodes to one starting node.
 */
public class Graph extends CLILogger {

    /**
     * stores the destination of an edge
     */
    private int[] edges;
    /**
     * stores the offset
     */
    private int[] offset;
    /**
     * the first unused index (of edges, it seems)
     */
    private int current;
    /**
     * stores the weight of a edge
     */
    private double[] weight;
    /**
     * stores the latitude of a node
     */
    private double[] latitude;
    /**
     * stores the longitude of a node
     */
    private double[] longitude;
    /**
     * stores the distance to a node
     */
    private double[] distance;

    /**
     * stores any node's preceding node on the ideal path from Dijkstra's starting node to that node, if Dijkstra's algorithm has been run.
     * Values will be -1 if there is no path or if Dijkstra has not run yet.
     */
    private int[] predecessor;

    /**
     * the starting node
     * used to check if dijkstra did run for this node
     */
    private int start = -1;
    
    /**
     * Heap to be given to Dijkstra class. Musz be reset and given new starting node before running Dijkstra again.
     */
    private NodeHeap heap;

    /**
     * Constructor.
     * <p>
     * Edges have to be manually added to the graph using the method insertEdge().
     * <p>
     *
     * @param nodes number of nodes that are used in the graph
     * @param edges number of edges that are to be used in the graph
     * @see Graph#insertEdge(int, int, double)
     */
    public Graph(int nodes, int edges) {
        this.offset = new int[nodes];
        this.predecessor = new int[nodes];
        this.distance = new double[nodes];

        this.edges = new int[edges];
        this.weight = new double[edges];

        this.latitude = new double[nodes];
        this.longitude = new double[nodes];

        this.current = 0;
        
        this.heap = new NodeHeap(this, 0);

        //initializes all values in arrays with their default value, if there is one
        Arrays.fill(offset, -1);
        Arrays.fill(predecessor, -1);
        Arrays.fill(distance, Double.POSITIVE_INFINITY);

    }

    /**
     * runs dijkstra on this graph with start as start node
     *
     * @param start the start node
     * @see Dijkstra#start()
     */
    public void runDijkstra(int start) {
        if (this.start != start) {

            if (this.start != -1)
                Arrays.fill(distance, Double.POSITIVE_INFINITY);

            this.start = start;
            heap.resetHeap(start);
            System.out.println("Starting Dijkstra for " + start);
            Dijkstra dijkstra = new Dijkstra(this, start, heap);
            dijkstra.setDebug(this.debug);
            dijkstra.setVerbose(this.verbose);
            dijkstra.start();
        }
    }

    /**
     * runs query on this graph with start as start node and dest as destination
     *
     * @param start the start node
     * @param dest  the destination
     * @return the distance from start to dest
     * @see Graph#runDijkstra(int)
     */
    public double runQuery(int start, int dest) {
        runDijkstra(start);
        double dist = getDistance(dest);
        debugPrint("Distance: " + dist);
        return dist;
    }


    /**
     * sets the latitude of a node
     *
     * @param node     the node
     * @param latitude the latitude
     */
    void setLatitude(int node, double latitude) {
        this.latitude[node] = latitude;
    }

    /**
     * sets the longitude
     *
     * @param node      the node
     * @param longitude the longitude
     */
    void setLongitude(int node, double longitude) {
        this.longitude[node] = longitude;
    }

    /**
     * sets both geo information at once
     *
     * @param node      the node
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    void setGeo(int node, double latitude, double longitude) {
        this.setLatitude(node, latitude);
        this.setLongitude(node, longitude);
    }

    /**
     * returns the latitude
     *
     * @param node the node
     * @return the latitude
     */
    public double getLatitude(int node) {
        try {
            return this.latitude[node];
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    /**
     * returns the longitude
     *
     * @param node the node
     * @return the longitude
     */
    public double getLongitude(int node) {
        try {
            return this.longitude[node];
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    /**
     * adds a new Edge to the Graph
     *
     * @param start  the start node
     * @param dest   the destination node
     * @param weight the weight
     * @throws UnorderedGraphException A special Exception which tells caller to try insert method
     */
    void addEdge(int start, int dest, double weight) throws UnorderedGraphException {
//    	print("adding it! from "+ start + " to " + dest + " and weight " + weight);
        //edges are added in right order just append in array
        if (hasOutgoingEdges(start + 1)) {
            throw new UnorderedGraphException("The next Node has already established edges. Please ty to use insertEdge(...) instead");
        }
        if (this.current >= edges.length) increase(edges);
        if (!hasOutgoingEdges(start)) {
            this.setOffset(start, this.current);
        }
        this.edges[this.current] = dest;
//        this.weight[getEdge(start, dest)] = weight; //Need to use real index. Can use this.current
        this.weight[this.current] = weight;
        this.current++;
    }

    /**
     * increases the length of a double array
     * <p>
     * If array is a known array to this class e.g latitude it will automatically be replaced
     *
     * @param arr the double array to be increased
     * @return the increased array
     */
    private double[] increase(double[] arr) {
        this.debugPrint("need to increase array");
        double[] array = new double[arr.length + 2];
        //copies array into other array
        System.arraycopy(arr, 0, array, 0, arr.length);
        if (arr == this.weight) this.weight = array;
        else if (arr == this.distance) this.distance = array;
        else if (arr == this.latitude) this.latitude = array;
        else if (arr == this.longitude) this.longitude = array;
        this.debugPrint("array was increased");
        return array;

    }

    /**
     * increases the length of a int array
     * <p>
     * If array is a known array to this class e.g. edges it will automatically be replaced
     *
     * @param arr the int array to be increased
     * @return the increased array
     */
    private int[] increase(int[] arr) {
        this.debugPrint("need to increase array");
        int[] array = new int[arr.length + 2];
        System.arraycopy(arr, 0, array, 0, arr.length);
        if (arr == this.edges) this.edges = array;
        else if (arr == this.offset) this.offset = array;
        else if (arr == this.predecessor) this.predecessor = array;
        this.debugPrint("array was increased");
        return array;

    }

    /**
     * adds a new Edge to the Graph and moves indices if needed
     *
     * @param start  the start node
     * @param dest   the destination node
     * @param weight the weight
     * @param skip   if true skips the test and forces indices to be moved
     * @see Graph#addEdge(int, int, double)
     */
    public void insertEdge(int start, int dest, double weight, boolean skip) {

        if (getOffset(start + 1) == -1 || !skip) {
            try {
                this.verbosePrint("trying to add Edge");
                addEdge(start, dest, weight);
            } catch (UnorderedGraphException e) {
                this.debugPrint("Exception handled");
                insertEdge(start, dest, weight, true);
            }
        } else {

            int index = getOffset(start + 1);
            if (this.edges.length <= current + 1) increase(this.edges);
            if (this.weight.length <= current + 1) increase(this.weight);

            if (this.current + 1 - index >= 0)
                System.arraycopy(this.edges, index, this.edges, index + 1, this.current + 1 - index);


            if (this.current + 1 - index >= 0)
                System.arraycopy(this.weight, index, this.weight, index + 1, this.current + 1 - index);


            for (int i = start + 1; i < offset.length; i++) {
                if (getOffset(i) >= 0) setOffset(i, getOffset(i) + 1);
            }


            edges[index] = dest;
            this.weight[index] = weight;
            this.current++;

        }
    }

    /**
     * adds a new Edge to the Graph and moves indices if needed
     *
     * @param start  the start node
     * @param dest   the destination node
     * @param weight the weight
     * @see Graph#insertEdge(int, int, double)
     */
    public void insertEdge(int start, int dest, double weight) {
        insertEdge(start, dest, weight, false);
    }

    /**
     * Checks if a node has a outgoing edge
     *
     * @param node the node to be checked
     * @return true if has outgoing edge else false
     * @see Graph#getOffset(int)
     */
    public boolean hasOutgoingEdges(int node) {
//        return this.offset[node] != -1;
        return getOffset(node) != -1;
    }


    /**
     * Returns the Offset of a node.
     * Will return -1 if
     * A) no edges exist for given node
     * B) given node index is the first index beyond the node array (node == offset.length), meaning that node does not exist. This is to simplify iterative operations.
     * <p>
     * Other non-existent nodes will result in an exception.
     *
     * @param node the node
     * @return the offset or -1 if it has no edges.
     * @throws IllegalArgumentException if node index is < 0 or node index > offset.length
     */
    public int getOffset(int node) throws IllegalArgumentException {
        if (node > offset.length || node < 0)
            throw new IllegalArgumentException("Can't give offset, since node " + node + " doesn't exist. Offset-Array length is " + offset.length);
        if (node == offset.length)
            return -1;

        return this.offset[node];
    }


    /**
     * Sets the offset of a node,
     * does nothing if parameter is 1 above highest node ID.
     *
     * @param node   the node
     * @param offset the offset
     * @throws IllegalArgumentException only if node does not exist and isn't equal to offset.length
     */
    void setOffset(int node, int offset) throws IllegalArgumentException {
        if (node > this.offset.length || node < 0)
            throw new IllegalArgumentException("Can't set offset, since node " + node + " doesn't exist. Offset-Array length is " + this.offset.length);
        if (node == this.offset.length)
            return;

        this.offset[node] = offset;
    }


    /**
     * Returns the node which is nearest to [node]
     *
     * @param node the node
     * @return the next node or -1 if it does not exist
     */
    int nextNode(int node) {
        double minVal = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        int offset = this.getOffset(node);
        if (offset == -1) {
            this.debugPrint("no nextNode: offset invalid");
            return -1;
        }
        int elem = this.countOutgoingEdges(node);
        if (elem < 1) {
            this.debugPrint("no nextNode: zero outgoing Edges");
            return -1;
        }
        for (int i = 0; i < elem; i++) {
            double var = this.getWeight(offset + i);
            if (var < minVal) {
                minVal = var;
                minIndex = i;
            }
        }
        return this.edges[offset + minIndex];
    }


    /**
     * Returns the number of outgoing edges
     *
     * @param node the node
     * @return number of outgoing edges, 0 if none found
     */
    public int countOutgoingEdges(int node) {
        if (!this.hasOutgoingEdges(node)) return 0;

        int lastEdge = -1;

        for (int i = node + 1; i < offset.length; i++) {
            lastEdge = getOffset(i);
            if (lastEdge != -1) break;
        }

        if (lastEdge == -1) {
            // no other node after it that has edges
            return this.current - offset[node];
        }

        return lastEdge - offset[node];
    }


    /**
     * Returns weight of an edge
     *
     * @param edge the edge
     * @return weight or -1 if it doesn't exist
     */
    public double getWeight(int edge) {
        if (edge >= this.current || edge < 0) {
            return -1;
        }

        return this.weight[edge];
    }


    /**
     * Returns the distance
     *
     * @param dest the destination node
     * @return the distance to a node or POSITIVE_INFINITY if {@link Dijkstra} did not run yet or if unreachable
     * @throws IllegalArgumentException if node index is out of bounds
     */
    public double getDistance(int dest) throws IllegalArgumentException {
        if (dest < 0 || dest >= offset.length)
            throw new IllegalArgumentException("Given destination " + dest + " is not a node ID in [0," + (offset.length - 1) + " ]");

        return this.distance[dest];
    }


    /**
     * Sets the Distance to dest.
     *
     * @param dest the destination node
     * @param dist the distance
     * @return true if distance has changed else false
     * @throws IllegalArgumentException if dest index is out of bounds
     */
    public boolean setDistance(int dest, double dist) throws IllegalArgumentException {
        if (dest < 0 || dest >= offset.length)
            throw new IllegalArgumentException("Given destination " + dest + " is not a node ID in [0," + (offset.length - 1) + " ]");

        if (this.getDistance(dest) == dist) return false;
        this.distance[dest] = dist;
        return true;
    }

    /**
     * Sets the node's predecessor on a path from Dijkstra's starting node to given node. Used by Dijkstra class while the algorithm runs.
     *
     * @param node        The node of which a predecessor gets set.
     * @param predecessor The preceding node on a path to Dijkstra's start.
     * @throws IllegalArgumentException if node index is out of bounds
     */
    public void setPredecessor(int node, int predecessor) throws IllegalArgumentException {
        if (node < 0 || node >= this.predecessor.length)
            throw new IllegalArgumentException("Given node id " + node + " is not a legal node ID in [0," + (this.predecessor.length - 1) + " ]. Offset.length is " + offset.length);

        this.predecessor[node] = predecessor;
    }

    /**
     * Returns the node's predecessor on a path from Dijkstra's starting node to given node.
     * The starting node is set when creating an instance of Dijkstra's algorithm for this graph.
     * If there is no path or if Dijkstra has not run yet, -1 will be returned!
     *
     * @param node From this node, by going to the returned node and that one's predecessors, you can recursively find the ideal path to the starting node.
     * @return preceding node on a path to start, or -1
     * @throws IllegalArgumentException if node index is out of bounds
     */
    public int getPredecessor(int node) throws IllegalArgumentException {
        if (node < 0 || node >= this.predecessor.length)
            throw new IllegalArgumentException("Given node id " + node + " is not a legal node ID in [0," + (this.predecessor.length - 1) + " ]. Offset.length is " + offset.length);

        return this.predecessor[node];
    }

    /**
     * Returns list of all nodes with their offsets
     *
     * @return offset array
     */
    int[] getNodeList() {
        return this.offset;
    }

    /**
     * Returns the destination node of edge
     *
     * @param edge The Edge
     * @return destination of given edge
     */
    public int getDestination(int edge) {
        return this.edges[edge];
    }


    /**
     * Returns amount of edges that have been added to this directed graph.
     */
    public int getCurrentEdgesCount() {
        return this.current;
    }

    /**
     * Returns maximum number of edges supported in this directed graph.
     * <p>
     * This value is determined upon construction of a graph instance via parameter 'edges'.
     */
    public int getMaxEdgesCount() {
        return this.edges.length;
    }

    /**
     * Returns the amount of nodes in the graph.
     */
    public int getNodesCount() {
        return this.distance.length;
    }
    
    /**
     * returns most recently set starting node for this graph, which is the start of a route when running Dijkstra
     * @return start as integer between 0 and getNodesCount()
     */
    public int getStartingNode() {
    	return start;
    }
    
    /**
     * Returns the node in this graph that is the closest to the given coordinates.
     * 
     * @param latitide	latitude in degrees
     * @param longitude	longitude in degrees
     * @return	node as an integer number between 0 and getNodesCount()
     */
    public int getClosestNode(double latitide, double longitude) {
    	
    	int tempNode = 0;
    	double tempDist = getGeoDistance(latitide, longitude, this.getLatitude(0), this.getLongitude(0));
    	double tempDist2;
    	
    	for (int i = 1; i < latitude.length; i++) {
			tempDist2 = getGeoDistance(latitide, longitude, this.getLatitude(i), this.getLongitude(i));
			
			if (tempDist2 < tempDist) {
				tempDist = tempDist2;
				tempNode = i;
			}
		}
    	
    	return tempNode;
    }
    
    /**
     * Calculates rough distance in m between two points on earth given as latitude and longitude.
     * 
     * This is a helper method for getClosestNode() in the Graph class.
     * 
     * @param lat1 Latitude of point 1
     * @param lng1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lng2 Longitude of point 2
     * @return distance in meters as double
     */
    public static double getGeoDistance(double lat1, double lng1, double lat2, double lng2) {
    	
    	// Using approximation - might even be good enough to interpret lat/lng as 2D coordinates, especially when limited to positive values on the Germany map
    	// but haversine formula gives more accurate approximation relatively cheaply
    	
    	double deltaLat = Math.toRadians(lat2 - lat1);
    	double deltaLng = Math.toRadians(lng2 - lng1);
    	lat1 = Math.toRadians(lat1);
    	lat2 = Math.toRadians(lat2);
    	
    	double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLng/2) * Math.sin(deltaLng/2);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    	// use 6371000 as  earth radius assuming perfect sphere
    	double dist = 6371000 * c;  
    	
    	
    	return dist;
    }

    public double[] getLatitude() {
        return latitude;
    }

    public double[] getLongitude() {
        return longitude;
    }

    /**
     * Returns node for given geoLocations
     *
     * @param latitude  The latitude
     * @param longitude The longitude
     * @return The node number or -1
     */
    public int getNode(double latitude, double longitude) {
        for (int i = 0; i < this.latitude.length; i++) {
            if (this.latitude[i] == latitude && this.longitude[i] == longitude)
                return i;
        }
        return -1;
    }
}
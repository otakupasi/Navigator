package main;

import java.io.*;

/**
 * The IOHandler which reads the graph .fmi file
 */
public class IOHandler {
    /**
     * the starttime for time tracking
     */
    public double startTime;
    /**
     * the runtime for a certain task
     */
    public double runtime;

    /**
     * starts the timer
     */
    public void start() {
        //reset runtime
        this.runtime = 0;
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        this.runtime = System.currentTimeMillis() - startTime;
    }

    /**
     * imports the Graph from a graph .fmi file also stops runtime for this task
     *
     * @param path the path to the file
     * @return a Graph object or null
     */
    public Graph importGraph(String path) {
        Graph graph;
        try {
            var fileReader = new FileReader(path);
            var file = new BufferedReader(fileReader);

            //ignore the 4 comment lines
            file.readLine();
            file.readLine();
            file.readLine();
            file.readLine();
            //ignore blank line
            file.readLine();

            String nodesStr = file.readLine();
            String edgesStr = file.readLine();

            int nodes = Integer.parseInt(nodesStr);
            int edges = Integer.parseInt(edgesStr);

            graph = new Graph(nodes, edges);

            for (int i = 0; i < nodes; i++) {
                String line = file.readLine();
                String[] data = line.split(" ");
                int node = Integer.parseInt(data[0]);
                double latitude = Double.parseDouble(data[2]);
                double longitude = Double.parseDouble(data[3]);
                graph.setGeo(node, latitude, longitude);
            }
            for (int i = 0; i < edges; i++) {
                String line = file.readLine();
                String[] data = line.split(" ");
                int start = Integer.parseInt(data[0]);
                int dest = Integer.parseInt(data[1]);
                double cost = Double.parseDouble(data[2]);
            }
            this.stop();
            //EOL expected

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return graph;
    }


    //TODO: importQueryFrom(String path)
    //TODO: assertTimeout: Graphimport under 2 minutes
    //TODO: exportSolutionTo(String path, solution):void
    //TODO: diff(String pathSol, String pathSolution):bool
}

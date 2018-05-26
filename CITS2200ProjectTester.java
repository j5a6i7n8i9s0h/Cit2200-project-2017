import java.io.*;
import java.util.*;

public class CITS2200ProjectTester {
	public static void loadGraph(CITS2200Project project, String path) {
		// The graph is in the following format:
		// Every pair of consecutive lines represent a directed edge.
		// The edge goes from the URL in the first line to the URL in the second line.
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			while (reader.ready()) {
				String from = reader.readLine();
				String to = reader.readLine();
				System.out.println("Adding edge from " + from + " to " + to);
				project.addEdge(from, to);
			}
		} catch (Exception e) {
			System.out.println("There was a problem:");
			System.out.println(e.toString());
		}
	}

	public static void main(String[] args) {
		// Change this to be the path to the graph file.
		String pathToGraphFile = "C:/Users/Jainish Pithadiya/Desktop/1024n16384e.txt";
		// Create an instance of your implementation.
		CITS2200Project proj = new WikiGraphs();
		// Load the graph into the project.
		
		loadGraph(proj, pathToGraphFile);
		// Write your own tests!
		//System.out.println(proj.getShortestPath("A","E"));
		//System.out.println(proj.getShortestPath("A","C"));
		System.out.println(" ");
		System.out.println("The centers for this graph are");
		for(int i=0;i<proj.getCenters().length;i++){
			System.out.println(proj.getCenters()[i]);
		}
	String[][] scc = proj.getStronglyConnectedComponents();
	System.out.println(" ");
	System.out.println("SCC's are");
	System.out.println(" ");
	for(String[] a: scc){
		for(String b:a){
			System.out.print(b+" ");
		}
		System.out.println(" ");
	}
	System.out.println("");
	System.out.println("DIST TO TO " + proj.getShortestPath("A", "E"));
	
	//((WikiGraphs) proj).printV();
	
	//System.out.println(((WikiGraphs) proj).containsAll());
	}
	
	
	
}
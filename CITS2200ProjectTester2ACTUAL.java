import java.io.*;
import java.util.*;

public class CITS2200ProjectTester2ACTUAL {
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
		String pathToGraphFile = "C:/Users/Jainish Pithadiya/Desktop/cits2200projectjunk/example.txt";
		// Create an instance of your implementation.
		CITS2200Project proj = new MyCITS2200Project();
		// Load the graph into the project.
		
		loadGraph(proj, pathToGraphFile);
		// Write your own tests!
		//System.out.println(proj.getShortestPath("A","E"));
		//System.out.println(proj.getShortestPath("A","C"));
		System.out.println(" ");
		System.out.println("The centers for this graph are");
		String[] x= proj.getCenters();
		for(String b: x){
			System.out.println(b);
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
	String aa[]= proj.getCenters();
	System.out.println("get centers" ); 
	int i=1;
	for(String b:aa){
		System.out.println( i++ + ": " +b);
	}
	}
	
	
	
}
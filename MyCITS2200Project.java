import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
// By Jainish Pithadiya And Ruan Scheepers
public class MyCITS2200Project implements CITS2200Project{
	
	private ArrayList<vertex> vertices;
	private long elapsed; 
	
	public MyCITS2200Project(){
		vertices = new ArrayList<vertex>();
	}
	private int numOfcomps;
	/**
	 * Adds an edge to the Wikipedia page graph. If the pages do not
	 * already exist in the graph, they will be added to the graph.
	 * created vertex urlTo is added to created vertex urlFrom's Linkedlist called adjacent's
	 * @param urlFrom the URL which has a link to urlTo.
	 * @param urlTo the URL which urlFrom has a link to.
	 */
	public void addEdge(String urlFrom, String urlTo) {
		//First we check if urlFrom or urlTO exist in graph 
		Iterator<vertex> it = vertices.iterator();
		//Assume urlFrom and urlTo dont exist in graph 
		boolean foundFrom = false;
		boolean foundTo = false;
		vertex from = null;
		vertex to = null;
		// Use iterator to check if either vertices exist. 
		while((!foundFrom||!foundTo) && it.hasNext()){
			vertex temp = it.next();
			// If urlFrom is found in graph assign it to a value of temp 
			if(temp.url.equals(urlFrom)){
				foundFrom = true;
				from = temp;
			}
			// if urlTO is found in graph assign it to value of temp
			if(temp.url.equals(urlTo)){
				foundTo = true;
				to = temp;
			}
		}
		// if urlFrom is not found add it to graph 
		if(!foundFrom){
			from = new vertex(urlFrom);
			vertices.add(from);
		}
		// if urlTo is not found and urlTo && urlFrom aren't the same (for special cases), then add it to graph
		if(!foundTo && !urlFrom.equals(urlTo)){
			to = new vertex(urlTo);
			vertices.add(to);
		} else if (urlFrom.equals(urlTo)) {
			// if urlTo and urlFrom are the same 
			to = from;
		}
		from.addEdge(to);
	}
	
	/**
	 * Finds the minimum distance between two pages in the Wikipedia graph 
	 * Implements a BFS to accomplish this with a time complexity of O(E+V) on a graph with E number of edges and V number of vertices  
	 * @param urlFrom- Start vertex
	 * @param urlTo- End vertex 
	 * @returns Distance between Start and End vertex on Graph or -1 if vertices are not connected or otherwise 
	 */
	public int getShortestPath(String urlFrom, String urlTo) {
		int k=0;
		vertex start=null;
		// checks if both vertices are in the graph 
		for (vertex v: vertices){ 
			// allows us to note visited and unvisited vertices , if -1 means its unvisited 
			v.gsp=-1;
			// find start and end vertices in graph, and initialize distance of start vertex to zero. 
			if((v.url).equals(urlFrom)||(v.url).equals(urlTo)){
				if((v.url).equals(urlFrom)) {start=v; start.gsp=0;}
				k++;
			}
			// if start and end vertex are the same value, consider Distance between vertices. 
			//However due to the implementation of BFS, this value will always be 0 or any other distance provided for a vertex to itself 
			if((v.url).equals(urlFrom)&&(v.url).equals(urlTo))k=2;
		}
		if(k<2)return -1;
		//neither points in graph
			//if points in graph do BFS 
			Queue<vertex> q= new LinkedList<vertex>();
			q.add(start);
			while(!q.isEmpty()){
				vertex point= q.poll();
				//Since its a unweighted directed graph the first adjacent edge to urlTo rooting from urlFrom and its adjacent vertex's will ALWAYS be the min distance 
				if((point.url).equals(urlTo))return point.gsp; 
				for(vertex v: point.adjacents){
					// for unvisited vertex's 
					if(v.gsp==-1){
						// distance from vertex urlFrom to vertex v is assigned
						v.gsp=point.gsp+1; 
						q.add(v);
					}
				}

			}
		// if vertices arent connected and are in graph , return distance as -1
		return -1;
	}
	/**
	 * Finds all the centers of the page graph. The order of pages
	 * in the output does not matter. Any order is correct as long as
	 * all the centers are in the array, and no pages that aren't centers
	 * are in the array.
	 * Time complexity of this method is O(V^3+V^2*E)
	 * @return an array containing all the URLs that correspond to pages that are centers.
	 * METHOD: 
	 * 1) FOR EACH VERTEX NOTE THE AMOUNT OF VERTEX'S IT CAN GET TO AND THE MAX DISTANCE IT CAN TRANSVERSE IN THE GRAPH TO ANY VERTEX ALREADY EXISTING IN THE GRAPH
	 * 2) FOR THE VERTEX'S WHICH HAVE A POSTIVE MAX DISTANCE AND WHICH TOUCH ALL OTHER VERTEX'S NOTE THE MINIMUM VALUE FOR EACH VERTEX'S MAX DISTANCE WHICH SATISFIES PREVIOUS CONDITIONS
	 * 3) USING THIS MIN VALUED DISTANCE , FIND ALL VERTICE'S WITH THE MIN VALUED DISTANCE
	 * 4) VERTEX'S NOTED ARE THE CENTERS OF THE GRAPH 
	 * Sources used : https://www.youtube.com/watch?v=YbCn8d4Enos
	 */
	public String[] getCenters() {
		//Array list contains url's of all centers discovered in THE GRAPH
		ArrayList<String> center= new ArrayList<String>();
		for(vertex v:vertices){
			//This allows us to keep track of how many vertices our current vertex touches
			// by definition it must be able to touch all the vertices in the graph excluding itself 
			v.noOfTouchableVertices=0;
			int w=0; 
			// helps keep track of the MAX distance of current vertex to any other vertex on the graph
			for(vertex u:vertices){
				// if the distance of one vertex to a compliment vertex in the graph is a positive value, it indicates vertex v can get it. 
				if(0<getShortestPath(v.url,u.url)){
					v.noOfTouchableVertices++;
					// how many vertices a vertex can go to 
					if(w<getShortestPath(v.url,u.url)){
						//find the MAX distance a vertex has on a graph to compliment vertex pairs 
						w=getShortestPath(v.url,u.url);
					}
				}
			}
			// maxGreatestShorttestpath is recorded for each vertex 
			v.maxGst=w;
		}
		int radius=Integer.MAX_VALUE;
		//Find minimum maxGst from all the vertices and note it as value radius;
		for(vertex v:vertices)if(radius>v.maxGst&& v.maxGst>0 && v.noOfTouchableVertices==vertices.size()-1) radius=v.maxGst;
		int mins=0;
		// find all vertices with maxgst value of radius and ones with noOfTouchableVertices equal to |v|-1 (exclude self as vertex) and add it to arraylist tracking all centers of graph
		for(vertex v:vertices){
			if(v.maxGst==radius &&  v.noOfTouchableVertices==vertices.size()-1){
				mins++; // if center exists increment this value
				center.add(v.url);
			}
		}
		String[] centers= new String[mins];
		String[] noCenter= new String[0];
		// No centers exist as mins hasnt been incremented as a result
		if(mins==0) return noCenter ;
		// if centers do exist and mins>0 then return array.
		return center.toArray(centers);
	}

	@Override
	public String[][] getStronglyConnectedComponents() {
		// Tarjan's strongly connected components algorithm
		int index = 0;
		Stack<vertex> verticesInOrder = new Stack<vertex>();
		numOfcomps = 0;
		Stack<vertex> s = new Stack<vertex>();
		for(vertex v:vertices){
			if(v.strongIndex == -1) index = strongconnect(v, index, s, verticesInOrder);
		}
		
		String[][] scc = new String[numOfcomps][];
		Stack<vertex> a = new Stack<vertex>();
		int compCounter = 0;
		while(!verticesInOrder.isEmpty()){
			boolean found = false;
			int groupSize = 0;
			while(!found){
				a.push(verticesInOrder.pop());
				if(verticesInOrder.isEmpty()){
					found = true;
				}else if(verticesInOrder.peek().strongIndex == verticesInOrder.peek().strongLowest){
					found = true;
				}
				groupSize ++;
			}
			String[] group = new String[groupSize];
			for(int i = 0; i<groupSize; i++){
				group[i] = a.pop().url;
			}
			scc[compCounter] = group;
			compCounter++;
		}
		
		for(vertex v:vertices){
			v.strongIndex = -1;
			v.strongLowest = -1;
		}
		return scc;
	}

	private int strongconnect(vertex v, int i, Stack<vertex> s, Stack<vertex> inOrder) {
		int index = i;
		v.strongIndex = i;
		v.strongLowest = i;
		index ++;
		s.push(v);
		v.onStack = true;
		
		for (vertex adjV:v.adjacents){
			if(adjV.strongIndex == -1){
				index = strongconnect(adjV, index, s, inOrder);
				v.strongLowest = Math.min(v.strongLowest, adjV.strongLowest);
			}else if(adjV.onStack){
				v.strongLowest = Math.min(v.strongLowest, adjV.strongIndex);
			}
		}
				
		if(v.strongLowest == v.strongIndex){
			vertex w;
			do{
				w = s.pop();
				w.onStack = false;
				inOrder.push(w);
			}while (w.url != v.url);
			numOfcomps ++;
		}
		return index;
	}

	@Override
	public String[] getHamiltonianPath() {
	
		int numV = vertices.size();
		int pset = (int) Math.pow(2,numV);
		int inf = numV + 100;
		int[][] dp = new int[pset][numV];
		int min = 0;
		int tmin = 0;

		//Iterates through all the subsets of the vertices, m holds the 'mask' of the current subset
		//for example 6 = 0110 which holds the second and third vertex.
		//within this 'm' loop we have a 'v' loop which iterates through each vertex
		//it then checks if the vertex within that subset has a hamiltonian path ending on 'v'
		//so for the subset [A] at vertex A it will contain 0
		//for the subset [AB] if the edge A->B exists at vertex A it becomes infinity (since B->A does not exist)
		//but for vertex B it becomes 1;
		//Using the smaller subsets it uses to fill larger subsets util we get to the mask involving all vertices
		
		for (int m = 0; m < pset; m++){
			for (int v = 0; v < numV; v ++){
				if(nbit(v,m) == false) dp[m][v] = inf;
				else if(countSubset(m, numV) == 1) dp[m][v] = 0;
				else{
					min = inf;
					for(int j = 0; j < numV; j ++){
						if(nbit(j,m)){ 
							if(isEdge(vertices.get(j), vertices.get(v))){
								tmin = dp[(m) ^ (int)(Math.pow(2,v))][j] + 1;
								if(tmin<min) min = tmin;
							}
						}
					}
					dp[m][v]= min;
				}
			}
		}
		
		String[] path = new String[numV];
		boolean found = false;
		int endP = 0;
		int currentM = (int)Math.pow(2, numV)-1;
		int counter = 1;
		while(!found && endP < numV){
			if (dp[currentM][endP] < inf) found = true;
			else endP++;
		}
		if(!found) return new String[0];
		else found = false;
		path[numV-counter] = vertices.get(endP).url;
		counter++;
		for(int i = 1; i < numV; i ++){
			int j = 0;
			while(!found && j < numV){
				if(dp[currentM][endP] == (dp[currentM ^ (int)Math.pow(2, endP)][j] +1)){
					if(j!=endP && isEdge(vertices.get(j),vertices.get(endP))) found = true;
				}
				if(!found) j++;
			}
			if (!found) System.out.println("we got a problem ");
			currentM = (currentM ^ (int)Math.pow(2, endP));
			endP = j;
			path[numV-counter] = vertices.get(endP).url;
			counter++;
			found = false;
		}
		return path;
	}
	
	private int countSubset(int subset, int maxV){
		int sum = 0;
		for(int i = 0; i < maxV; i++){
			sum = sum + (1 & (subset>>i));
		}
		return sum;
	}
	private boolean nbit(int n, int subset){
		// checks if the subset has a 1 or 0 at the n'th position
		// aka check whether the subset contains a particular vertex
		return ((1 & (subset>>n)) == 1);  
	}
	
	private boolean isEdge(vertex a, vertex b){
		return a.adjacents.contains(b);
	}
	
	private class vertex{
		String url;
		ArrayList<vertex> adjacents;
		int strongIndex; // indexing used in get strongly connected
		int strongLowest; // indexing used in get strongly connected
		/* onStack prevents iterating through stacks, so any function using vertices can 
		 * quickly determine of a vertex has been visited or not
		 * Important that any function returns this value to false after popping it from stack  */
		boolean onStack; 
		int noOfTouchableVertices;
		int gsp; // Distance between two vertex's
		/*used for get shortest path where GSP notes the minimum length from root vertex (urlFrom) to current vertex*/
		int maxGst;		
		/* Used for getCenters to note the maximum distance a vertex can traverse */
		private vertex(String url){
			this.url = url; 
			adjacents = new ArrayList<vertex>();
			strongIndex = -1;
			strongLowest = -1;
			onStack = false;

		}
		
		private void addEdge(vertex v){
			adjacents.add(v);
		}
	}
}
// By Jainish Pithadiya And Ruan Scheepers

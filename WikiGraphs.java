import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


public class WikiGraphs implements CITS2200Project{

	private LinkedList<vertex> vertices;
	private int time; 
	public WikiGraphs(){
		vertices = new LinkedList<vertex>();
	}
	@Override
	public void addEdge(String urlFrom, String urlTo) {
		Iterator<vertex> it = vertices.iterator();
		boolean foundFrom = false;
		boolean foundTo = false;
		vertex from = null;
		vertex to = null;
		while((!foundFrom||!foundTo) && it.hasNext()){
			vertex temp = it.next();
			if(temp.url.equals(urlFrom)){
				foundFrom = true;
				from = temp;
			}else if(temp.url.equals(urlTo)){
				foundTo = true;
				to = temp;
			}
		}
		if(!foundFrom){
			from = new vertex(urlFrom);
			vertices.add(from);
		}
		if(!foundTo){
			to = new vertex(urlTo);
			vertices.add(to);
		}

		from.adjacents.add(to);
	}

	@Override
	// implements BFS onto graph 
	// uses Algorithm from Lecture slides 
	public int getShortestPath(String urlFrom, String urlTo) {
		int k=0;
		vertex start=null;
		for (vertex v: vertices){ 
			v.key=-1;
			if((v.url).equals(urlFrom)||(v.url).equals(urlTo)){
				if((v.url).equals(urlFrom)) {start=v; start.key=0;}
				k++;
			}
		}
		if(k<2) return -1; 
		//neither points in graph
		else{
			//if points in graph
			Queue<vertex> q= new LinkedList<vertex>();
			q.add(start);
			while(!q.isEmpty()){
				vertex point= q.poll();
				//IF POINTS EXIST AND ARE CONNECTED 
				if((point.url).equals(urlTo)) return point.key; 
				for(vertex v: point.adjacents){
					if(v.key==-1){
						v.key=point.key+1; 
						q.add(v);
					}
				}

			}
		}
		return -1;
	}

	public String[] getCenters() {
		ArrayList<String> center= new ArrayList<String>();
		for(vertex v:vertices){
			v.noOfTouchableVertices=0;
			int w=0; 
			for(vertex u:vertices){
				if(0<getShortestPath(v.url,u.url)){
					v.noOfTouchableVertices++;
					if(w<getShortestPath(v.url,u.url)){
						w=getShortestPath(v.url,u.url);
					}
				}
			}
			v.maxGst=w;
		}
		
		int radius=Integer.MAX_VALUE;
		for(vertex v:vertices)if(radius>v.maxGst&& v.maxGst>0 && v.noOfTouchableVertices==vertices.size()-1) radius=v.maxGst;
		int mins=0;
		for(vertex v:vertices){
			if(v.maxGst==radius &&  v.noOfTouchableVertices==vertices.size()-1){
				mins++;
				center.add(v.url);
			}
		}
		String[] centers= new String[mins];
		if(mins==0) System.out.print("No Centers on this graph");
		return center.toArray(centers);
	}

	@Override
	//Uses kosaraju's algorithm 
	public String[][] getStronglyConnectedComponents() {
		String[][] scc= new String[vertices.size()][vertices.size()];
		for(String[] a: scc){for(int i=0;i<a.length;i++) a[i]="";}
		Set<String> visited = new LinkedHashSet<String>();
		Stack<vertex> stackeddfs=new Stack<vertex>(); 
		DFS(vertices,stackeddfs);
		LinkedList<vertex> transposed= Transposedgraph();
		int x=0;
		while(!stackeddfs.isEmpty()){
			vertex v= stackeddfs.pop();
			if(!visited.contains(v.url)){
				visited.add(v.url);
				scc[x][0]=v.url;
				for(vertex T:transposed){
					if((T.url).equals(v.url)){
						DFS3(T,visited,scc,x,1);
					}
				}
				x++;
			}
		}
		// bs for making into a dank array
		int i=0;
		int j=0;
		for(String[] m : scc){
			if(!m[0].equals("")) i++;
			int s=0;
			for(String n:m){
				if(!m.equals("")) s++;
			}
			if(s>j)j=s;
		}
		String[][] kanye = new String[i][];
		int y=0;
		for(String[] m: kanye){
			
		}
		return scc;
	}
	/// DFS here messes up for graphs like FUN , fix this DFS 
    private void DFS3(vertex v,Set<String> visited,String[][] scc, int x,int y){
    	for(vertex u:v.adjacents){
    		if(!visited.contains(u.url)){
    			if(scc[x][y].equals("")){
    			visited.add(u.url);
    			scc[x][y]=u.url;
    			DFS3(u,visited,scc,x,y);
    			}else{
    			for(int i=0;i<vertices.size()-y;i++){
    				if(scc[x][i+y].equals("")){
    					y=i+y;
    					break;
    				}
    			}
    			visited.add(u.url);
        		scc[x][y]=u.url;
        		DFS3(u,visited,scc,x,y+1);
    			}
    		}
    	}
    }
	
	private void DFS (LinkedList<vertex> graph, Stack<vertex> s){
		for (vertex v : graph ){
			v.discoverytime=-1;
		}
		time=1;
		for (vertex v: graph){
			if(v.discoverytime<0)DFS2(v,s);
		}
	}
	
	private void DFS2(vertex v, Stack<vertex> s){
		v.discoverytime=time++;
		for(vertex u: v.adjacents){
			if(u.discoverytime<0){
				DFS2(u,s);
			}
		}
		s.add(v);
		time++;
	}

	
	// TRANSPOSED part 1 
	// not sure if this works 
	private LinkedList<vertex> Transposedgraph(){
		LinkedList<vertex> copy= new LinkedList<vertex>();
		for(vertex v: vertices){
			copy.add(new vertex(v.url));
		}
		for (vertex v: vertices){
			Iterator<vertex> it= v.adjacents.iterator();
			while(it.hasNext()){
				vertex u=it.next();
				ATT(v,u,copy);
			}
		}
		return copy; 
	}
	// TRANSPOSED part 2 
	private void ATT(vertex adj, vertex parent, LinkedList<vertex> T ){
		vertex m=null; 
		for(vertex v:T){
			if((v.url).equals(adj.url)){
				m=v;
			}
		}
		for(vertex v:T){
			if((v.url).equals(parent.url)){
				v.adjacents.add(m);
			}
		}
	}
	
	
	/* for testing and shit*/
	//here 
	public void printV(){
		for(vertex v:vertices){
			System.out.println(v.url);
		}
	}
	
	public boolean containsAll(){
		int k=0;
		Set<String> a= new HashSet<String>();
		for(vertex v: vertices){
			a.add(v.url);
		}
		String[][] b= getStronglyConnectedComponents();
		for(String[] s: b){
			for(String w: s){
				if(a.contains(w)&& !w.equals("")){
					k++;
				}
			}
		}
		System.out.println("the edges are " + " " +k);
		System.out.println("there are " + vertices.size() + " vertices");
		if(k==vertices.size()){
			return true;
		}
		return false;
	}
	// to here 
	
	
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
		if(!found) return null;
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
		LinkedList<vertex> adjacents;
		int noOfTouchableVertices;
		int key; 
		int maxGst;
		int discoverytime; 
		private vertex(String url){
			this.url = url;
			adjacents = new LinkedList<vertex>();
		}

	}
}
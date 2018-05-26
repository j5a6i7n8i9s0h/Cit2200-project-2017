import java.util.*;

public class ReferenceCITS2200Project implements CITS2200Project {
	private ArrayList<String> pages = new ArrayList<String>();	// Allows us to lookup a page URL by id
	private Map<String, Integer> urlids = new TreeMap<String, Integer>();	// Allows us to lookup a page id by URL
	private ArrayList<List<Integer>> adjList = new ArrayList<List<Integer>>();	// Adjacency list for the graph
	private ArrayList<List<Integer>> revList = new ArrayList<List<Integer>>();	// Adjacency list for the transpose graph	
	public void addVert(String url) {
		if (!urlids.containsKey(url)) {	// Ignore duplicate vertices
			pages.add(url);
			urlids.put(url, urlids.size());
			adjList.add(new ArrayList<Integer>());
			revList.add(new ArrayList<Integer>());
		}
	}

	public void addEdge(String urlFrom, String urlTo) {
		addVert(urlFrom);	// Add vertices if necessary
		addVert(urlTo);
		int i = urlids.get(urlFrom), j = urlids.get(urlTo);
		adjList.get(i).add(j);	// Add edge to adjacency list
		revList.get(j).add(i);	// Add transpose edge
	}

	private int[] bfs(int root) {
		int[] d = new int[pages.size()];	// Distances from root to each vertex
		Arrays.fill(d, -1);
		Queue<Integer> q = new ArrayDeque<Integer>();
		d[root] = 0;
		q.add(root);
		while (!q.isEmpty()) {	// BFS order from root
			int curr = q.poll();
			for (int next : adjList.get(curr)) {	// Consider all neighbours
				if (d[next] != -1) continue;	// Ignore if we have already found a path
				d[next] = d[curr] + 1;	// Set distance using shortest path through curr
				q.add(next);	// Add next to BFS order
			}
		}
		return d;
	}

	public int getShortestPath(String urlFrom, String urlTo) {
		return bfs(urlids.get(urlFrom))[urlids.get(urlTo)];	// BFS out from urlFrom and return distance to urlTo
	}

	public String[] getCenters() {
		ArrayList<String> centers = new ArrayList<String>();
		int radius = pages.size();	// Minimum eccentricity
		for (int i = 0; i < pages.size(); i++) {
			int ecc = -1;
			for (int d : bfs(i)) if (d != -1 && ecc < d) ecc = d;	// BFS to find most distant vertex from i
			if (ecc != -1 && ecc < radius) {
				radius = ecc;	// Found new minimum eccentricity
				centers.clear();	// Clear old centers
			}
			if (ecc == radius) centers.add(pages.get(i));	// Add this vertex to centers if its eccentricity is right
		}
		return centers.toArray(new String[0]);
	}

	public String[][] getStronglyConnectedComponents() {	// SCCs by Kosaraju's algorithm
		boolean[] visited = new boolean[pages.size()];
		Arrays.fill(visited, false);
		LinkedList<Integer> po = new LinkedList<Integer>();	// Post Order
		for (int i = 0; i < pages.size(); i++) if (!visited[i]) dfs_scc(adjList, i, visited, po);	// Fill PO
		Arrays.fill(visited, false);
		ArrayList<ArrayList<Integer>> sccs = new ArrayList<ArrayList<Integer>>();
		while (!po.isEmpty()) {	// Iterate through in Reverse Post Order
			int curr = po.removeLast();
			if (visited[curr]) continue;
			ArrayList<Integer> scc = new ArrayList<Integer>();
			dfs_scc(revList, curr, visited, scc);	// DFS to find this SCC
			sccs.add(scc);
		}
		String[][] result = new String[sccs.size()][];
		for (int i = 0; i < sccs.size(); ++i) {
			result[i] = new String[sccs.get(i).size()];
			for (int j = 0; j < sccs.get(i).size(); ++j) result[i][j] = pages.get(sccs.get(i).get(j));
		}
		return result;
	}

	private void dfs_scc(ArrayList<List<Integer>> adj, int curr, boolean[] visited, List<Integer> lst) {
		visited[curr] = true;
		for (int next : adj.get(curr)) if (!visited[next]) dfs_scc(adj, next, visited, lst);
		lst.add(curr);
	}

	public String[] getHamiltonianPath() { // Held-Karp DP.
		int v = pages.size();
		boolean[][] dpt = new boolean[1 << v][v]; // Get used to seeing lots of bit hacks to represent a set.
		Arrays.fill(dpt[(1<<v)-1], true); // Base case for DP.
		for (int bs = (1<<v)-2; bs > 0; --bs) { // Go through subsets of visited vertices.
			for (int at = 0; at < v; ++at) if ((bs & (1<<at)) > 0) { // Last vertex in path.
				for (int next : adjList.get(at)) if ((bs & (1<<next)) == 0 && dpt[bs|(1<<next)][next]) {
					dpt[bs][at] = true; // If the rest of the path is soluble, then we have a valid prefix+suffix.
					break; // So we found a path, and can break.
				}
			}
		}
		for (int i = 0; i < v; ++i) if (dpt[1<<i][i]) { // Find a valid start vertex.
			ArrayList<String> path = new ArrayList<String>();
			int bs = 1<<i, at = i; // Pointers into DP table.
			path.add(pages.get(at)); // Go through the path in the DP table that led to "i" being a valid start vertex.
			while (bs != (1<<v)-1) { // Basically the same logic as the DP, but we keep pointers into the table.
				for (int next : adjList.get(at)) if ((bs & (1<<next)) == 0 && dpt[bs|(1<<next)][next]) {
					bs = bs|(1<<next);
					at = next;
					break;
				}
				path.add(pages.get(at));
			}
			return path.toArray(new String[0]);
		}
		return new String[0];
	}
}
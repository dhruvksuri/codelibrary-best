package graphs.dfs;

import java.util.*;
import java.util.stream.Stream;

// https://www.geeksforgeeks.org/biconnected-components/
public class Biconnectivity {
    static class Edge {
        int u, v;

        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public String toString() {
            return "(" + u + "," + v + ')';
        }
    }

    List<Integer>[] graph;
    boolean[] visited;
    Stack<Integer> stack;
    Stack<Edge> stackEdges;
    int time;
    int[] tin;
    int[] up;
    List<List<Integer>> edgeBiconnectedComponents;
    List<List<Edge>> vertexBiconnectedComponents;
    List<Integer> cutPoints;
    List<Edge> bridges;

    public void biconnectivity(List<Integer>[] graph) {
        int n = graph.length;
        this.graph = graph;
        visited = new boolean[n];
        stack = new Stack<>();
        stackEdges = new Stack<>();
        time = 0;
        tin = new int[n];
        up = new int[n];
        edgeBiconnectedComponents = new ArrayList<>();
        vertexBiconnectedComponents = new ArrayList<>();
        cutPoints = new ArrayList<>();
        bridges = new ArrayList<>();

        for (int u = 0; u < n; u++)
            if (!visited[u])
                dfs(u, -1);
    }

    void dfs(int u, int p) {
        visited[u] = true;
        up[u] = tin[u] = time++;
        stack.add(u);
        int children = 0;
        boolean cutPoint = false;

        // Idea is to identify FWD & BACK edges
        for (int v : graph[u]) {
            if (v == p)
                continue;
            // Have we visited v ?
            if (visited[v]) { //  Yes
                // Is it a BACK edge?
                if (tin[u] > tin[v]) { //  Yes
                    stackEdges.add(new Edge(u, v));
                }
                // u-v -> go back as much when we visited v
                up[u] = Math.min(up[u], tin[v]);
            } else {
                // Tree Edge -  if  v  is visited for the first time and  u  is currently being visited 
                stackEdges.add(new Edge(u, v));
                
                dfs(v, u);

                // compare up of u vs up of v
                up[u] = Math.min(up[u], up[v]);

                // if (tin[u] > up[v]) - we have exited v before, its not part of SCC 
                if (tin[u] <= up[v]) {
                    // FWD edge
                    cutPoint = true;
                    List<Edge> component = new ArrayList<>();
                    while (true) {
                        Edge e = stackEdges.pop();
                        component.add(e);
                        if (e.u == u && e.v == v)
                            break;
                    }
                    vertexBiconnectedComponents.add(component);
                }

                // Imp  - v is not visited before 
                if (tin[u] < up[v]) // or (up[v] == tin[v])
                    bridges.add(new Edge(u, v));

                //Imp
                ++children;
            }
        }
        
        // If root and more then one child, then its cutpoint
        if (p == -1)
            cutPoint = children >= 2;
        if (cutPoint)
            cutPoints.add(u);

        // M Imp - if u is not visited before then its new SCC
        if (tin[u] == up[u]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int x = stack.pop();
                component.add(x);
                if (x == u)
                    break;
            }
            edgeBiconnectedComponents.add(component);
        }
    }

    // tree of edge-biconnected components
    public static List<Integer>[] ebcTree(List<Integer>[] graph, List<List<Integer>> components) {
        int[] comp = new int[graph.length];
        for (int i = 0; i < components.size(); i++)
            for (int u : components.get(i)) comp[u] = i;
        List<Integer>[] g = Stream.generate(ArrayList::new).limit(components.size()).toArray(List[] ::new);
        for (int u = 0; u < graph.length; u++)
            for (int v : graph[u])
                if (comp[u] != comp[v])
                    g[comp[u]].add(comp[v]);
        return g;
    }

    // Usage example
    public static void main(String[] args) {
        int[][] edges = {{0, 1}, {1, 2}, {0, 2}, {2, 3}, {1, 4}, {4, 5}, {5, 1}};
        // int[][] edges = {{0, 1}};

        int n = Arrays.stream(edges).mapToInt(e -> Math.max(e[0], e[1])).max().getAsInt() + 1;
        List<Integer>[] graph = Stream.generate(ArrayList::new).limit(n).toArray(List[] ::new);

        for (int[] edge : edges) {
            graph[edge[0]].add(edge[1]);
            graph[edge[1]].add(edge[0]);
        }

        Biconnectivity bc = new Biconnectivity();
        bc.biconnectivity(graph);

        System.out.println("edge-biconnected components:" + bc.edgeBiconnectedComponents);
        System.out.println("vertex-biconnected components:" + bc.vertexBiconnectedComponents);
        System.out.println("cut points: " + bc.cutPoints);
        System.out.println("bridges:" + bc.bridges);
        System.out.println(
            "edge-biconnected condensation tree:" + Arrays.toString(ebcTree(graph, bc.edgeBiconnectedComponents)));
    }
}

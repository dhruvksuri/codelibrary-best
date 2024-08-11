package Graphs;
import java.util.*;
import java.util.stream.Stream;

/**

Best - https://www.baeldung.com/cs/graph-coloring-constructive-methods

Graph colouring is the task of assigning colours to the vertices of a graph so that:
1. pairs of adjacent vertices are assigned different colours, and
2. the number of different colours used across the graph is minimal.

At each step, the calculation chooses the vertex with the most no of in-degrees (i.e., the number of as-of-now colored adjoining vertices) 
and allocates it to a color that's not as of now utilized by its neighbors. 
This approach leads to optimized coloring, maximizing color differing qualities, and minimizing color utilization.
*/
public class GraphColoringGreedy2 {

	public static int[] color(List<Integer>[] graph) {
		int n = graph.length;
		int[] used = new int[n];
		int[] colors = new int[n];
		Arrays.fill(colors, -1);

		// Non SCC components would start re-using the same color
		for (int i = 0; i < n; i++) {
			int best_cnt = -1;
			int bestu = -1;
			for (int u = 0; u < n; u++) {
				// If u is not colored
				if (colors[u] == -1) {
					// No of in-degrees is bitCount
					// 000111 - 3 bit count means 2 in-coming edges so give color 3
					int cnt = Integer.bitCount(used[u]);
					if (best_cnt < cnt) {
						best_cnt = cnt;
						bestu = u;
					}
				}
			}
			// ~used[bestu] (000111) -> 111000 -> 3 
			int c = Integer.numberOfTrailingZeros(~used[bestu]);
			colors[bestu] = c; // Assign 3
			for (int v : graph[bestu]) {
				used[v] |= 1 << c;  // 001000
			}
		}
		return colors;
	}

	// Usage example
	public static void main(String[] args) {
		int n = 5;
		List<Integer>[] g = Stream.generate(ArrayList::new).limit(n).toArray(List[]::new);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				g[i].add((i + 1) % n);
				g[(i + 1) % n].add(i);
			}
		}
		System.out.println(Arrays.toString(color(g)));
	}
}

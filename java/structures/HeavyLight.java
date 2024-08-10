package structures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

// Heavy-light decomposition with path queries. Query complexity is O(log^2(n)).
// Based on the code from http://codeforces.com/blog/entry/22072

/**
Explanation from https://codeforces.com/blog/entry/81317

How this works?
    1. You are asked to update some values in the path from point a to point b and query the path sum.
        Soln:-
            1. Brute force
                Best explan - https://www.geeksforgeeks.org/introduction-to-heavy-light-decomposition/
            2. HeavyLight
                - You label heavy childs first & create segment tree across consecutive labels
                    a. ST1 - 0,1,2
                    b. ST2 - 5,6 & ST3 - 7,8,9,10
               

*/
public class HeavyLight {
    List<Integer>[] tree;
    boolean valuesOnVertices; // true - values on vertices, false - values on edges
    public SegmentTree segmentTree;
    int[] parent;
    int[] depth;
    int[] pathRoot;
    int[] in;
    int time;

    public HeavyLight(List<Integer>[] tree, boolean valuesOnVertices) {
        this.tree = tree;
        this.valuesOnVertices = valuesOnVertices;
        int n = tree.length;
        segmentTree = new SegmentTree(n);

        parent = new int[n];
        depth = new int[n];
        pathRoot = new int[n];
        in = new int[n];

        parent[0] = -1;
        dfs1(0);
        dfs2(0);
    }

    // Below method takes care of identifying heavy subtree of child
    // and move it to 0th index
    int dfs1(int u) {
        int size = 1;
        int maxSubtree = 0;
        for (int i = 0; i < tree[u].size(); i++) {
            int v = tree[u].get(i);
            if (v == parent[u])
                continue;
            parent[v] = u;
            depth[v] = depth[u] + 1;
            int subtree = dfs1(v);
            if (maxSubtree < subtree) {
                maxSubtree = subtree;
                
                // Swap ith child & 0th child
                
                tree[u].set(i, tree[u].set(0, v));  
                
                // tree[u].set(0, v) -> This method returns the element previously at the specified position.
            }
            size += subtree;
        }
        return size;
    }

    // If node is on the heavy path, link it's pathRoot to root parent (i.e 0)
    // If node is not on the heavy path, link it's pathRoot to itself
    void dfs2(int u) {
        in[u] = time++;
        // Parent pathRoot[0] = 0
        // Parent can have only one child point to it as pathRoot
        for (int v : tree[u]) {
            if (v == parent[u])
                continue;
            pathRoot[v] = v == tree[u].get(0) ? pathRoot[u] : v;
            dfs2(v);
        }
    }

    public SegmentTree.Node get(int u, int v) {
        SegmentTree.Node[] res = {new SegmentTree.Node()};
        processPath(u, v, (a, b) -> res[0] = SegmentTree.unite(res[0], segmentTree.get(a, b)));
        return res[0];
    }

    public void modify(int u, int v, long delta) {
        processPath(u, v, (a, b) -> segmentTree.modify(a, b, delta));
    }

    // We update node from u to LCA & v to LCA alternatively
    // Whoover has higher depth gets chosen
    // if u or v is part of heavy chain then entire chain updated till its parent using ST
    // if u or v is part of light chain then we update till its parent and move up 
    void processPath(int u, int v, BiConsumer<Integer, Integer> op) {
        for (; pathRoot[u] != pathRoot[v]; v = parent[pathRoot[v]]) {
            if (depth[pathRoot[u]] > depth[pathRoot[v]]) {
                int t = u;
                u = v;
                v = t;
            }
            // v has higher depth
            op.accept(in[pathRoot[v]], in[v]);
        }
        if (u != v || valuesOnVertices)
            op.accept(Math.min(in[u], in[v]) + (valuesOnVertices ? 0 : 1), Math.max(in[u], in[v]));
    }

    // Usage example
    public static void main(String[] args) {
        List<Integer>[] tree = Stream.generate(ArrayList::new).limit(5).toArray(List[] ::new);
        tree[0].add(1);
        tree[1].add(0);

        tree[0].add(2);
        tree[2].add(0);

        tree[1].add(3);
        tree[3].add(1);

        tree[1].add(4);
        tree[4].add(1);

        HeavyLight hlV = new HeavyLight(tree, true);
        hlV.modify(3, 2, 1);
        hlV.modify(1, 0, -1);
        System.out.println(1 == hlV.get(4, 2).sum);

        HeavyLight hlE = new HeavyLight(tree, false);
        hlE.modify(3, 2, 1);
        hlE.modify(1, 0, -1);
        System.out.println(1 == hlE.get(4, 2).sum);
    }
}

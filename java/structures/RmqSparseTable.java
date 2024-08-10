package structures;

/**
Best Explanation - https://www.algotree.org/algorithms/sparse_table/
void Build_SparseTable (vector<int>& vec, vector<vector<int>>& sparse_table) {

    int rows = vec.size();
    int cols = sparse_table[0].size();

    // Fill base case values
    for (int r = 0; r < rows; r++)
        sparse_table[r][0] = vec[r];

    for (int c = 1; c <= cols; c++) {
        int range = (1 << c);
        /* For  c    Range     c-1  Previous Range
                1   2^1 = 2     0      2^0 = 1
                2   2^2 = 4     1      2^1 = 2
                3   2^3 = 8     2      2^2 = 4
                ...         */
        for (int r = 0; r + range <= rows; r++) {
            // Values in the current column are derived from the
            // values in the previous column.
            sparse_table[r][c] = min (sparse_table[r][c-1],
                                      sparse_table[r+(1<<(c-1))][c-1]);
        }
    }

    Print_SparseTable(sparse_table);
}

Index : 0 1 2 3 4 5 6 7 8 
Array : 4 6 8 7 3 2 9 5 1 
Sparse table...
---------------
4 4 4 2 0 
6 6 3 1 0 
8 7 2 0 0 
7 3 2 0 0 
3 2 2 0 0 
2 2 1 0 0 
9 5 0 0 0 
5 1 0 0 0 
1 0 0 0 0 
---------------
Range Minium Queries (2, 7) : Left : 2 Right : 7
Part 1: ( 2...5 ) Part 2: ( 4...7 )
Smallest number : 2

Range Minium Queries (0, 2) : Left : 0 Right : 2
Part 1: ( 0...1 ) Part 2: ( 1...2 )
Smallest number : 4

Range Minium Queries (0, 8) : Left : 0 Right : 8
Part 1: ( 0...7 ) Part 2: ( 1...8 )
Smallest number : 1

Range Minium Queries (4, 5) : Left : 4 Right : 5
Part 1: ( 4...5 ) Part 2: ( 4...5 )
Smallest number : 2

Range Minium Queries (7, 8) : Left : 7 Right : 8
Part 1: ( 7...8 ) Part 2: ( 7...8 )
Smallest number : 1

Range Minium Queries (1, 4) : Left : 1 Right : 4
Part 1: ( 1...4 ) Part 2: ( 1...4 )
Smallest number : 3
                           
*/
public class RmqSparseTable {
    int[][] rmq;

    public RmqSparseTable(int[] a) {
        int n = a.length;
        rmq = new int[32 - Integer.numberOfLeadingZeros(n)][];
        rmq[0] = a.clone();
        for (int i = 1; i < rmq.length; i++) {
            rmq[i] = new int[n - (1 << i) + 1];
            for (int j = 0; j < rmq[i].length; j++) {
                rmq[i][j] = Math.min(rmq[i - 1][j];
                rmq[i - 1][j + (1 << (i - 1))]);
            }
        }
    }

    public int min(int i, int j) {
        int k = 31 - Integer.numberOfLeadingZeros(j - i + 1);
        return Math.min(rmq[k][i], rmq[k][j - (1 << k) + 1]);
    }

    public static void main(String[] args) {
        {
            RmqSparseTable st = new RmqSparseTable(new int[] {1, 5, -2, 3});
            System.out.println(1 == st.min(0, 0));
            System.out.println(-2 == st.min(1, 2));
            System.out.println(-2 == st.min(0, 2));
            System.out.println(-2 == st.min(0, 3));
        }
        {
            RmqSparseTable st = new RmqSparseTable(new int[] {1, 5, -2});
            System.out.println(1 == st.min(0, 0));
            System.out.println(-2 == st.min(1, 2));
            System.out.println(-2 == st.min(0, 2));
        }
    }
}

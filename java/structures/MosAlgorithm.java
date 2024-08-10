package structures;

import java.util.*;

// https://www.hackerearth.com/notes/mos-algorithm/
// https://www.geeksforgeeks.org/mos-algorithm-query-square-root-decomposition-set-1-introduction/
// Solution of http://www.spoj.com/problems/DQUERY/en/
/**
The idea of MO’s algorithm is to pre-process all queries so that result of one query can be used in next query. Below are steps.

Let a[0…n-1] be input array and q[0..m-1] be array of queries. 

Sort all queries in a way that queries with L values from 0 to ?n – 1 are put together, then all queries from ?n to 2*?n – 1, and so on. All queries within a block are sorted in increasing order of R values.
Process all queries one by one in a way that every query uses sum computed in the previous query.
Let ‘sum’ be sum of previous query.
Remove extra elements of previous query. For example if previous query is [0, 8] and current query is [3, 9], then we subtract a[0],a[1] and a[2] from sum
Add new elements of current query. In the same example as above, we add a[9] to sum.
*/

class MO {
 
    // Prints sum of all query ranges. m is number of queries 
    // n is size of array a[]. 
    static void queryResults(int a[], int n, ArrayList<Query> q, int m){
         
        // Find block size 
        int block = (int) Math.sqrt(n); 
     
        // Sort all queries so that queries of same blocks 
        // are arranged together.
        Collections.sort(q, new Comparator<Query>(){
             
            // Function used to sort all queries so that all queries  
            // of the same block are arranged together and within a block, 
            // queries are sorted in increasing order of R values. 
            public int compare(Query x, Query y){
 
                // Different blocks, sort by block. 
                if (x.L/block != y.L/block) 
                    return (x.L < y.L ? -1 : 1); 
 
                // Same block, sort by R value 
                return (x.R < y.R ? -1 : 1);
            }
        });
 
        // Initialize current L, current R and current sum 
        int currL = 0, currR = 0; 
        int currSum = 0; 
     
        // Traverse through all queries 
        for (int i=0; i<m; i++) 
        { 
            // L and R values of current range
            int L = q.get(i).L, R = q.get(i).R; 
 
            // Remove extra elements of previous range. For 
            // example if previous range is [0, 3] and current 
            // range is [2, 5], then a[0] and a[1] are subtracted 
            while (currL < L) 
            { 
                currSum -= a[currL]; 
                currL++; 
            } 
 
            // Add Elements of current Range 
            while (currL > L) 
            { 
                currSum += a[currL-1]; 
                currL--; 
            } 
            while (currR <= R) 
            { 
                currSum += a[currR]; 
                currR++; 
            } 
 
            // Remove elements of previous range.  For example 
            // when previous range is [0, 10] and current range 
            // is [3, 8], then a[9] and a[10] are subtracted 
            while (currR > R+1) 
            { 
                currSum -= a[currR-1]; 
                currR--; 
            } 
 
            // Print sum of current range 
            System.out.println("Sum of [" + L +
                           ", " + R + "] is "  + currSum); 
        } 
    }
} 
public class MosAlgorithm {
    public static class Query {
        int index;
        int a;
        int b;

        public Query(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    static int add(int[] a, int[] cnt, int i) {
        return ++cnt[a[i]] == 1 ? 1 : 0;
    }

    static int remove(int[] a, int[] cnt, int i) {
        return --cnt[a[i]] == 0 ? -1 : 0;
    }

    public static int[] processQueries(int[] a, Query[] queries) {
        for (int i = 0; i < queries.length; i++) queries[i].index = i;
        int sqrtn = (int) Math.sqrt(a.length);
        Arrays.sort(queries, Comparator.<Query>comparingInt(q -> q.a / sqrtn).thenComparingInt(q -> q.b));
        int[] cnt = new int[1000_002];
        int[] res = new int[queries.length];
        int L = 1;
        int R = 0;
        int cur = 0;
        for (Query query : queries) {
            while (L < query.a) cur += remove(a, cnt, L++);
            while (L > query.a) cur += add(a, cnt, --L);
            while (R < query.b) cur += add(a, cnt, ++R);
            while (R > query.b) cur += remove(a, cnt, R--);
            res[query.index] = cur;
        }
        return res;
    }

    public static void main(String[] args) {
        int[] a = {1, 3, 3, 4};
        Query[] queries = {new Query(0, 3), new Query(1, 3), new Query(2, 3), new Query(3, 3)};
        int[] res = processQueries(a, queries);
        System.out.println(Arrays.toString(res));
    }
}

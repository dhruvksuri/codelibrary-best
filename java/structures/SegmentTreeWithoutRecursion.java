package structures;

// Best Explanation - https://codeforces.com/blog/entry/18051
// Parent starts with 1 based index & its child indices are (2 * i, 2 * i + 1) 

/**
Explanation is much more complex than before, so let's focus first on the advantages it gives us.
  1. Segment tree uses exactly 2 * n memory, not 4 * n like some other implementations offer.
  2. Array elements are stored in continuous manner starting with index n.
  3. All operations are very efficient and easy to write.
*/
// Point update - Range Queries (Maximum)
public class SegmentTreeWithoutRecursion {
    public static int get(int[] t, int i) {
        return t[i + t.length / 2]; // since 1 based start index
    }

    public static void add(int[] t, int i, int value) {
        i += t.length / 2; // since 1 based start index
        
        t[i] += value;
        
        for (; i > 1; i >>= 1) {
            // i^1 turns 2 * i into 2 * i + 1 and vice versa,
            t[i >> 1] = Math.max(t[i], t[i ^ 1]);
        }
    }

    public static int max(int[] t, int a, int b) {
        int res = Integer.MIN_VALUE;
        
        a += t.length / 2;
        b += t.length / 2;

        // sum on interval [l, r] -> use a <= b && t[b]
        // sum on interval [l, r) -> use a < b && t[--b]
        for (; a <= b; a = (a + 1) >> 1, b = (b - 1) >> 1) {
            // If l, the left interval border, is odd (which is equivalent to l&1) then l is the right child of its parent.
            // Then our interval includes node l but doesn't include it's parent. 
            if ((a & 1) != 0)
                res = Math.max(res, t[a]);
            if ((b & 1) == 0)
                res = Math.max(res, t[b]);
        }
        return res;
    }

    // Usage example
    public static void main(String[] args) {
        int n = 10;
        int[] t = new int[n + n];
        add(t, 0, 1);
        add(t, 9, 2);
        System.out.println(2 == max(t, 0, 9));
    }
}

// Point update - Range Queries (Sum)
class SegmentTreeWithoutRecursion2 {
    int n;  // array size
    int t[2 * N];  // --------------> Imp
    
    void build() {  // build the tree
      for (int i = n - 1; i > 0; --i) {
          t[i] = t[i<<1] + t[i<<1|1];
      }
    }
    
    void modify(int p, int value) {  // set value at position p
        t[p += n] = value;
        // i^1 turns 2 * i into 2 * i + 1 and vice versa,
        for (; p > 1; p >>= 1) t[p>>1] = t[p] + t[p^1];
    }

    int query(int l, int r) {  // sum on interval [l, r)
      int res = 0;
      l += n;
      r += n;
      
      // sum on interval [l, r] -> use a <= b && t[b]
      // sum on interval [l, r) -> use a < b && t[--b]
      for (; l < r; l >>= 1, r >>= 1) {
        if (l&1) res += t[l++];
        if (r&1) res += t[--r];
      }
      return res;
    }

    // Range update - Point Queries (Sum)

    // [l, r)
    void modify(int l, int r, int value) {
      l += n;
      r += n;
      for (; l < r; l >>= 1, r >>= 1) {
        if (l&1) t[l++] += value;
        if (r&1) t[--r] += value;
      }
    }

    int query(int p) {
      int res = 0;
      p += n;  
      for (; p > 0; p >>= 1) {
          res += t[p];
      }
      return res;
    }

    // If at some point after modifications we need to inspect all the elements in the array, we can push all the modifications to the leaves using the following code. 
    void push() {
      for (int i = 1; i < n; ++i) {
        t[i<<1] += t[i];
        t[i<<1|1] += t[i];
        t[i] = 0;
      }
    }
    // TODO - Non-commutative combiner functions
}


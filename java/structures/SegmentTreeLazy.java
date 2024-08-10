package structures;

/**
https://www.geeksforgeeks.org/lazy-propagation-in-segment-tree/
Lazy propagation is required to perform both range queries and range modifications
First, we need more variables:

int h = sizeof(int) * 8 - __builtin_clz(n);
int d[N];  

d[i] is a delayed operation to be propagated to the children of node i when necessary

What if there are updates on a range of array indexes? 
For example add 10 to all values at indexes from 2 to 7 in array. The above update has to be called for every index from 2 to 7. 

Lazy Propagation – An optimization to make range updates faster
When there are many updates and updates are done on a range, we can postpone some updates (avoid recursive calls in update) and do those updates only when required.

A value 0 in lazy[i] indicates that there are no pending updates on node i in segment tree. A non-zero value of lazy[i] means that this amount needs to be added to node i in segment tree before making any query to the node.

*/
// Point update - Range Queries (Maximum)
public class SegmentTreeLazy {

  void apply(int p, int value) {
    // You apply update on leaf & parent node
    t[p] += value;
    if (p < n) {
      //  if p is not leaf then apply to d[p] - NonZero value indicates that updates haven't been propogated to child
      d[p] += value;
    }
  }

  // This is to build max/min, wont apply for sum
  void build(int p) {
    while (p > 1) {
      p >>= 1;
      t[p] = max(t[p<<1], t[p<<1|1]) + d[p];
    }
  }

  // h is a height of the tree, the highest significant bit in n.
  void push(int p) {
    for (int s = h; s > 0; --s) {
      int i = p >> s;
      if (d[i] != 0) {
        apply(i<<1, d[i]);
        apply(i<<1|1, d[i]);
        d[i] = 0;
      }
    }
  }

  void inc(int l, int r, int value) {
    l += n, r += n;
    int l0 = l, r0 = r;
    for (; l < r; l >>= 1, r >>= 1) {
      if (l&1) apply(l++, value);
      if (r&1) apply(--r, value);
    }
    build(l0);
    build(r0 - 1);
  }

  int query(int l, int r) {
    l += n, r += n;
    push(l);
    push(r - 1);
    int res = -2e9;
    for (; l < r; l >>= 1, r >>= 1) {
      if (l&1) res = max(res, t[l++]);
      if (r&1) res = max(t[--r], res);
    }
    return res;
  }
}

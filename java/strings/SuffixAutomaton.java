package strings;

import java.util.*;

// https://en.wikipedia.org/wiki/Suffix_automaton
// Intuitively a suffix automaton can be understood as a compressed form of all substrings of a given string. 
// An impressive fact is, that the suffix automaton contains all this information in a highly compressed form.
// https://codeforces.com/blog/entry/20861
public class SuffixAutomaton {
    public static class State {
        public int length;
        public int suffLink;
        public List<Integer> invSuffLinks = new ArrayList<>(0);
        public int firstPos = -1;
        public int[] next = new int[128];

        { Arrays.fill(next, -1); }
    }

    public static State[] buildSuffixAutomaton(CharSequence s) {
        int n = s.length();
        State[] st = new State[Math.max(2, 2 * n - 1)];
        st[0] = new State();
        st[0].suffLink = -1;
        int last = 0;
        int size = 1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int cur = size++;
            st[cur] = new State();
            st[cur].length = i + 1;
            st[cur].firstPos = i;
            
            // Parent of newly added child
            int p = last;

            // If Parent doesn't have child then add it
            // Do this recursively by traversing to the parent
            for (; p != -1 && st[p].next[c] == -1; p = st[p].suffLink) {
                // Add the index to the current character
                st[p].next[c] = cur;
            }

            // If parent is root then make suffLink of current to 0
            // else If parent is not root then c (character) already exists for parent
            if (p == -1) {
                st[cur].suffLink = 0;
            } else {
                // Index of the char
                int q = st[p].next[c];

                
                if (st[p].length + 1 == st[q].length) {
                    st[cur].suffLink = q;
                } else {
                    // abb is an example satisfying below
                    int clone = size++;
                    st[clone] = new State();
                    st[clone].length = st[p].length + 1;

                    /**
                      public static void arraycopy(Object source_arr, int sourcePos,
                            Object dest_arr, int destPos, int len)
                        Parameters : 
                        source_arr : array to be copied from
                        sourcePos : starting position in source array from where to copy
                        dest_arr : array to be copied in
                        destPos : starting position in destination array, where to copy in
                        len : total no. of components to be copied.  
                    */
                    
                    System.arraycopy(st[q].next, 0, st[clone].next, 0, st[q].next.length);
                    st[clone].suffLink = st[q].suffLink;
                    for (; p != -1 && st[p].next[c] == q; p = st[p].suffLink) {
                        st[p].next[c] = clone;
                    }
                    st[q].suffLink = clone;
                    st[cur].suffLink = clone;
                }
            }
            last = cur;
        }
        for (int i = 1; i < size; i++) {
            st[st[i].suffLink].invSuffLinks.add(i);
        }
        return Arrays.copyOf(st, size);
    }

    // random tests
    public static void main(String[] args) {
        int[] occurrences = occurrences("abaabbab", "ab");
        System.out.println(Arrays.toString(occurrences));

        String lcs = lcs("abaabbab", "abb");
        System.out.println(lcs);
    }

    public static int[] occurrences(String haystack, String needle) {
        SuffixAutomaton.State[] automaton = SuffixAutomaton.buildSuffixAutomaton(haystack);
        int node = 0;
        for (char c : needle.toCharArray()) {
            int next = automaton[node].next[c];
            if (next == -1) {
                return new int[0];
            }
            node = next;
        }
        List<Integer> occurrences = new ArrayList<>();
        Queue<Integer> q = new ArrayDeque<>();
        q.add(node);
        while (!q.isEmpty()) {
            int curNode = q.remove();
            if (automaton[curNode].firstPos != -1) {
                occurrences.add(automaton[curNode].firstPos - needle.length() + 1);
            }
            q.addAll(automaton[curNode].invSuffLinks);
        }
        return occurrences.stream().sorted().mapToInt(Integer::intValue).toArray();
    }

    public static String lcs(String a, String b) {
        SuffixAutomaton.State[] st = SuffixAutomaton.buildSuffixAutomaton(a);
        int len = 0;
        int bestLen = 0;
        int bestPos = -1;
        for (int i = 0, cur = 0; i < b.length(); ++i) {
            char c = b.charAt(i);
            if (st[cur].next[c] == -1) {
                for (; cur != -1 && st[cur].next[c] == -1; cur = st[cur].suffLink) {
                }
                if (cur == -1) {
                    cur = 0;
                    len = 0;
                    continue;
                }
                len = st[cur].length;
            }
            ++len;
            cur = st[cur].next[c];
            if (bestLen < len) {
                bestLen = len;
                bestPos = i;
            }
        }
        return b.substring(bestPos - bestLen + 1, bestPos + 1);
    }
}

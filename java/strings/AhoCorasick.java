package strings;

// https://en.wikipedia.org/wiki/Aho–Corasick_algorithm
// https://codeforces.com/blog/entry/100298
public class AhoCorasick {
    final int ALPHABET_SIZE = 26;
    final int MAX_STATES = 200_000;

    int[][] transitions = new int[MAX_STATES][ALPHABET_SIZE];
    int[] sufflink = new int[MAX_STATES];
    int[] escape = new int[MAX_STATES];
    int states = 1;

    public int addString(String s) {
        int v = 0;
        for (char c : s.toCharArray()) {
            c -= 'a';
            if (transitions[v][c] == 0) {
                transitions[v][c] = states++;
            }
            v = transitions[v][c];
        }
        escape[v] = v;
        return v;
    }

    public void buildLinks() {
        int[] q = new int[MAX_STATES];
        
        for (int s = 0, t = 1; s < t;) {
            int v = q[s++];
            
            // u -> v ...... u is parent of v
            int u = sufflink[v];
            
            // Node is broken but can we reach via Parent by skipping prefix
            if (escape[v] == 0) {
                escape[v] = escape[u];
            }
            for (int c = 0; c < ALPHABET_SIZE; c++) {
                if (transitions[v][c] != 0) {
                    
                    // we are storing transition node below that is used by s in next iteration
                    q[t++] = transitions[v][c];

                    // transition node link if we skip parent and still reach suffix
                    sufflink[transitions[v][c]] = v != 0 ? transitions[u][c] : 0;
                } else {
                    transitions[v][c] = transitions[u][c];
                }
            }
        }
    }

    // Usage example
    public static void main(String[] args) {
        AhoCorasick ahoCorasick = new AhoCorasick();
        ahoCorasick.addString("a");
        ahoCorasick.addString("aa");
        ahoCorasick.addString("abaaa");
        ahoCorasick.buildLinks();

        int[][] t = ahoCorasick.transitions;
        int[] e = ahoCorasick.escape;

        String s = "abaa";

        int state = 0;
        for (int i = 0; i < s.length(); i++) {
            state = t[state][s.charAt(i) - 'a'];
            if (e[state] != 0)
                System.out.println(i);
        }
    }
}

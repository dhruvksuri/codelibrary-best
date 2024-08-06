package strings;

import java.util.*;
import java.util.stream.IntStream;

// https://en.wikipedia.org/wiki/Suffix_array
// https://medium.com/@florian_algo/introduction-to-suffix-array-9080f9f359ce -> V.IMPPPPPPPPPPPPPPPPPPPPPPPPPPPP
public class SuffixArray2 {
    // suffix array in O(n*log^2(n))
    public static int[] suffixArray(CharSequence s) {
        int n = s.length();
        Integer[] sa = IntStream.range(0, n).boxed().toArray(Integer[] ::new);
        int[] rank = s.chars().toArray();

        for (int len = 1; len < n; len *= 2) {
            long[] rank2 = new long[n];
            
            for (int i = 0; i < n; i++) {
                rank2[i] = ((long) rank[i] << 32) + (i + len < n ? rank[i + len] + 1 : 0);
            }
            
            Arrays.sort(sa, Comparator.comparingLong(a -> rank2[a]));

            for (int i = 0; i < n; i++) {
                rank[sa[i]] = i > 0 && rank2[sa[i - 1]] == rank2[sa[i]] ? rank[sa[i - 1]] : i;
            }
        }
        return Arrays.stream(sa).mapToInt(Integer::intValue).toArray();
    }

    // random test
    public static void main(String[] args) {
        Random rnd = new Random(1);
        for (int step = 0; step < 100000; step++) {
            int n = rnd.nextInt(100);
            StringBuilder s = rnd.ints(n, 0, 10).collect(
                StringBuilder::new, (sb, i) -> sb.append((char) ('a' + i)), StringBuilder::append);
            int[] sa = suffixArray(s);
            for (int i = 0; i + 1 < n; i++)
                if (s.substring(sa[i]).compareTo(s.substring(sa[i + 1])) >= 0)
                    throw new RuntimeException();
        }
        System.out.println("Test passed");
    }

    /**
    Below is the suffix rank for "abaab"
    sa[0] = 2:  aab
    sa[1] = 3:  ab
    sa[2] = 0:  abaab
    sa[3] = 4:  b
    sa[4] = 1:  baab

    rk[0] = 2
    rk[1] = 4
    rk[2] = 0
    rk[3] = 1
    rk[4] = 3

    Definition: lcp[i], which means the length of the longest common prefix between the suffix ranked i and the suffix ranked i-1,
                lcp[0] can be considered as 0.

            For example, if sa is a suffix array of “abaab”, the longest common prefix between sa[0] = aab and sa[1] = ab is ‘a’ which has length 1,
            so lcp[1] = 1. Likewise, the LCP of sa[1] = ab and sa[2] = abaab is ‘ab’, so lcp[2] = 2.

  Kasai's algorithm is pretty easy and works in O(n).

    Let's look at the two continuous suffixes in the suffix array. Let their indexes in suffix array be i1 and i1 + 1. If their lcp > 0, then if we delete first letter from both of them. We can easily see that new strings will have the same relative order. Also we can see that lcp of new strings will be exactly lcp - 1.
    
    Let's now look at the string wich we have got from the i suffix by deleting its first character. Obviously it is some suffix of the string too. Let its index be i2. Let's look at the lcp of suffixes i2 and i2 + 1. We can see that it's lcp will be at least already mentioned lcp - 1. This is associated with certain properties of lcp array, in particular, that lcp(i, j) = min(lcpi, lcpi + 1, ..., lcpj - 1).
    
    And finally let's make the algorithm based on the mentioned above. We will need an additional array rank[n], wich will contain the index in the suffix array of the suffix starting in index i. Firstly we should calculate the lcp of the suffix with index rank[0]. Then let's iterate through all suffixes in order in which we meet them in the string and calculate lcp[rank[i]] in naive way, BUT starting it from lcp[rank[i - 1]] - 1. Easy to see that now we have O(n) algorithm because on the each step our lcp decreasing not more than by 1 (except the case when rank[i] = n - 1).
    
    The string S = "banana" has the following Suffix Array:

    Suffix Array (sa):          [5, 3, 1, 0, 4, 2]
    Inverse Suffix Array (inv): [3, 2, 5, 1, 4, 0]

    Given Text: 'banana'
    Sorted Suffixes: ['a', 'ana, 'anana', 'banana', na , 'nana 'J 

    LCP Array (lcp):  [0, 0, 0, 0, 0, 0]
    
    For i = 0:  sa[0] = 5, inv[5] = 0, k = 0.
        Update lcp[0] = 0.

    For i = 1:
        sa[1] = 3, inv[3] = 1, k = 0.
        Compare suffixes "na" (starting at index 3) and "ana" (starting at index 1). No common prefix was found.
        Update lcp[1] = 0.

    For i = 2:
        sa[2] = 1, inv[1] = 2, k = 0.
        Compare suffixes "ana" (starting at index 1) and "banana" (starting at index 0). No common prefix was found.
        Update lcp[2] = 0.

    For i = 3:
        sa[3] = 0, inv[0] = 3, k = 0.
        Compare suffixes "banana" (starting at index 0) and "nana" (starting at index 4). A common prefix of length 1 was found ("n").
        Update lcp[3] = 1.
    
    For i = 4:
        sa[4] = 4, inv[4] = 4, k = 0.
        Compare suffixes "nana" (starting at index 4) and "a" (starting at index 2). No common prefix was found.
        Update lcp[4] = 0.

    For i = 5:
        sa[5] = 2, inv[2] = 5, k = 0.
        Compare suffixes "a" (starting at index 2) and "banana" (starting at index 5). No common prefix was found.
        Update lcp[5] = 0.

    The final LCP array is [0, 0, 0, 1, 0, 0].
    */

    // Use Kasai algorithm to build LCP array
    // applying Kasai's algorithm to build LCP array
    static int[] kasaiAlgorithm(String orgnlString, int[] suffixVector) {
        int n = suffixVector.length;
        // To store lcp array
        int[] longPrefix = new int[n];
        // To store inverse of suffix array elements
        int[] suffixInverse = new int[n];
        // to fill values in suffixInverse[] array
        for (int i = 0; i < n; i++)
            suffixInverse[suffixVector[i]] = i;
        int k = 0;
        for (int i = 0; i < n; i++) {
            if (suffixInverse[i] == n - 1) {
                k = 0;
                continue;
            }
            int j = suffixVector[suffixInverse[i] + 1];
            while (i + k < n && j + k < n && orgnlString.charAt(i + k) == orgnlString.charAt(j + k))
                k++;
            longPrefix[suffixInverse[i]] = k;
            if (k > 0)
                k--;
        }
        return longPrefix;
    }
}

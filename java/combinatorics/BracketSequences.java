package combinatorics;

import java.util.Arrays;

public class BracketSequences {
    /**
        bool next_balanced_sequence(string & s) {
            int n = s.size();
            int depth = 0;
            for (int i = n - 1; i >= 0; i--) {
                if (s[i] == '(')
                    depth--;
                else
                    depth++;
        
                if (s[i] == '(' && depth > 0) {
                    depth--;
                    int open = (n - i - 1 - depth) / 2;
                    int close = n - i - 1 - open;
                    string next = s.substr(0, i) + ')' + string(open, '(') + string(close, ')');
                    s.swap(next);
                    return true;
                }
            }
            return false;
        }
    */
    public static boolean nextBracketSequence(char[] s) {
        int n = s.length;
        // right to left
        for (int i = n - 1, balance = 0; i >= 0; i--) {
            // Reduce count if open bracket & vice versa
            balance += s[i] == '(' ? -1 : 1;

            // ((()))   converted to (()()) 
            //   i                     i 
            if (s[i] == '(' && balance > 0) { // we have more number of close brackets
                --balance;  // Why? => if you see after conversion, the after balance is 1 (before balance is 2) 
                int open = (n - i - 1 - balance) / 2;
                int close = n - i - 1 - open;
                s[i] = ')';
                Arrays.fill(s, i + 1, i + 1 + open, '(');
                Arrays.fill(s, i + 1 + open, i + open + close, ')');
                return true;
            }
        }
        return false;
    }

    // Usage example
    public static void main(String[] args) {
        char[] s = "((()))".toCharArray();
        do {
            System.out.println(new String(s));
        } while (nextBracketSequence(s));
    }
}

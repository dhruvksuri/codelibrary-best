/**
Similar to N-Queen
Find the number of ways to place K bishops on an N×N chessboard so that no two bishops attack each other.
https://github.com/cp-algorithms/cp-algorithms/blob/master/src/combinatorics/bishops-on-chessboard.md
*/
public class BishopOnChessboard {

    int squares (int i) {
        if (i & 1)  // odd
            return i / 4 * 2 + 1;
        else   // even 2 has 2 sq, 6 has 4 sq
            return (i - 1) / 4 * 2 + 2;
    }

    int bishop_placements(int N, int K) {
        if (K > 2 * N - 1)
            return 0;
    
      int[][] D = new int[N * 2][K+1];

        for (int i = 0; i < N * 2; ++i)
            D[i][0] = 1;
      
      D[1][1] = 1;
        
      for (int i = 2; i < N * 2; ++i)
            for (int j = 1; j <= K; ++j)
                D[i][j] = D[i-2][j] + D[i-2][j-1] * (squares(i) - (j - 1));
    
        int ans = 0;

        for (int i = 0; i <= K; ++i)
            ans += D[N*2-1][i] * D[N*2-2][K-i];
        return ans;
    }
}

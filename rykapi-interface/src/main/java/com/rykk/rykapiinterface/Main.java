package com.rykk.rykapiinterface;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String accessKey = "aa";
        String secretKey = "ab";
//        RykApiClient rykApiClient = new RykApiClient(accessKey, secretKey);
        solveNQueens(4);
//        String rykk = "rykk";
//        User user = new User();
//        user.setName(rykk);
//        rykApiClient.getNameByGet(rykk);
//        rykApiClient.getNameByPost(rykk);
//        rykApiClient.getUserNameByPost(user);
    }

        static List<List<String>> res = new LinkedList<>();;
        public static List<List<String>> solveNQueens(int n) {
            char[][] temp = new char[n][n];
            for (int i = 0; i < n; i++) {
                Arrays.fill(temp[i], '.');
            }
            backtrack(temp, 0);
            return res;
        }

        private static void backtrack(char[][] temp, int row) {
            int n = temp.length;
            if (row == n) {
                List<String> tempRes = new LinkedList<>();
                for (int i = 0; i < n; i++) {
                    tempRes.add(new String(temp[i]));
                }
                res.add(tempRes);
            }

            for (int col = 0; col < n; col++) {
                if(!isValid(temp, row, col)) {
                    continue;
                }
                temp[row][col] = 'Q';
                backtrack(temp, row + 1);
                temp[row][col] = '.';
            }
        }
        private static boolean isValid(char[][] temp, int row, int col) {
            for (int i = 0; i < row; i++) {
                if (temp[i][col] == 'Q') {
                    return false;
                }
            }
            for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
                if (temp[i][col] == 'Q') {
                    return false;
                }
            }
            for (int i = row - 1, j = col + 1; i >= 0 && j < temp.length ; i--, j++) {
                if (temp[i][col] == 'Q') {
                    return false;
                }
            }
            return true;
        }


}

package com.usercenter.utils;

import java.util.List;

/**
 * Date 2023/6/8 10:33
 * author:wyf
 * 算法工具类
 */
public class AlgorithmUtils {

    /**
     * 编辑距离算法
     * https://blog.csdn.net/DBC_121/article/details/104198838?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522168619152616800211527219%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=168619152616800211527219&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-104198838-null-null.142^v88^control_2,239^v2^insert_chatgpt&utm_term=%E7%BC%96%E8%BE%91%E8%B7%9D%E7%A6%BB%E7%AE%97%E6%B3%95&spm=1018.2226.3001.4187
     * @param word1
     * @param word2
     * @return
     */
    public static int minDistance(List<String> word1, List<String> word2){
        int n = word1.size();
        int m = word2.size();

        if(n * m == 0)
            return n + m;

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++){
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++){
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++){
            for (int j = 1; j < m + 1; j++){
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!word1.get(i - 1).equals(word2.get(j - 1)))
                    left_down += 1;
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}

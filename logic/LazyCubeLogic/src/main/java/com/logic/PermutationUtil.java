package com.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alfred Roberts
 * @description Helper functions for generating integer permutations
 * Permutation code from: https://java2blog.com/permutations-array-java/
 */
public class PermutationUtil {

    /**
     * Generate all possible permutations for a given integer array
     * @param arr Array of ints to generate permutations for
     * @return List of int lists containing all possible permutations
     */
    static public List<List<Integer>> permute(int[] arr) {
        List<List<Integer>> list = new ArrayList<>();
        permuteHelper(list, new ArrayList<>(), arr);
        return list;
    }

    /**
     * Permutation helper method for generating permutations
     * @param list List of values to store permutations in
     * @param resultList List used within recursive building of permutations
     * @param arr Array to generate permutations from
     */
    static private void permuteHelper(List<List<Integer>> list, List<Integer> resultList, int [] arr){

        // Base case
        if(resultList.size() == arr.length){
            list.add(new ArrayList<>(resultList));
        }
        else{
            for (int element : arr) {

                if (resultList.contains(element)) {
                    // If element already exists in the list then skip
                    continue;
                }
                // Choose element
                resultList.add(element);
                // Explore
                permuteHelper(list, resultList, arr);
                // Unchoose element
                resultList.remove(resultList.size() - 1);
            }
        }
    }
}

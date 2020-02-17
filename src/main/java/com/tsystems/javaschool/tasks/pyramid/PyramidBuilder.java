package com.tsystems.javaschool.tasks.pyramid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PyramidBuilder {

    /**
     * Builds a pyramid with sorted values (with minumum value at the top line and maximum at the bottom,
     * from left to right). All vacant positions in the array are zeros.
     *
     * @param inputNumbers to be used in the pyramid
     * @return 2d array with pyramid inside
     * @throws {@link CannotBuildPyramidException} if the pyramid cannot be build with given input
     */
    public int[][] buildPyramid(List<Integer> inputNumbers) {
        preCheck(inputNumbers);
        Collections.sort(inputNumbers);

        int[][] pyramid = getEmptyPyramid(inputNumbers.size());
        List<int []> numbersForEachRowInPyramid = getNumbersForEachRowInPyramid(inputNumbers);

        fillPyramid(numbersForEachRowInPyramid, pyramid);

        return pyramid;
    }




    //preCheck
    private void preCheck(List<Integer> inputNumbers) {
        if (inputNumbers == null ||
                inputNumbers.size() == Integer.MAX_VALUE - 1 ||
                inputNumbers.contains(null) ||
                !canBuildWithInputSize(inputNumbers.size()))
        {
            throw new CannotBuildPyramidException();
        }
    }

    private boolean canBuildWithInputSize(final int size){
        int counter = 0;
        int baseWidth = 0;
        for (;;++counter){
            baseWidth += counter;
            if (baseWidth == size) return true;
            if (baseWidth > size) return false;
        }
    }




    //getEmptyPyramid
    private int[][] getEmptyPyramid(final int SIZE) {
        int [] scale = getScale(SIZE);
        return new int[scale[1]][scale[0]];
    }

    private int [] getScale(final int collectionSize){
        int baseWidth = 0;
        int rowCount = 0;
        while ((float) collectionSize / rowCount - 1 != (float) rowCount / 2 - 0.5){
            ++rowCount;
            baseWidth = rowCount + rowCount - 1;
        }
        return new int[]{baseWidth, rowCount};
    }




    //getNumbersForEachRowInPyramid
    private List<int []> getNumbersForEachRowInPyramid(List<Integer> inputNumbers){
        List<int []> numberStartEndPairsForEachRow = getStartEndPairsForEachRow(inputNumbers.size());
        List<int []> numbersForPyramid = new ArrayList<>();

        for (int [] startEndPair : numberStartEndPairsForEachRow){
            numbersForPyramid.add(getNumbersForRow(inputNumbers, startEndPair[0], startEndPair[1]));
        }

        return numbersForPyramid;
    }

    private List<int[]> getStartEndPairsForEachRow(int collectionSize){
        List<int[]> numberCounts = new ArrayList<>();
        int start;
        int end = 0;
        int counter = 0;
        while (end != collectionSize){
            start = end + 1;
            end += counter + 1;
            ++counter;
            numberCounts.add(new int[]{start - 1, end - 1});
        }
        return numberCounts;
    }

    private int [] getNumbersForRow(List<Integer> inputNumbers, int start, int end){
        final int LENGTH = end - start + 1;

        int [] numbersForRow = new int[LENGTH];
        for (int i = 0;i < LENGTH; ++i){
            numbersForRow[i] = inputNumbers.get(start);
            ++start;
        }
        return numbersForRow;
    }




    //fillPyramid
    private void fillPyramid(List<int[]> inNumbersForEachRowInPyramid, int[][] outPyramid) {
        int i = 0;
        for (int [] row : outPyramid){
            distributeOnRow(inNumbersForEachRowInPyramid.get(i), row, row.length / 2, i);
            ++i;
        }
    }

    private void distributeOnRow(int [] inNumbersForCurrentRow, int [] outRow, int middlePosition, int currentLevel){
        int numberPos = 0;
        for (int num : inNumbersForCurrentRow){
            outRow[(middlePosition - currentLevel) + 2 * numberPos] = num;
            ++numberPos;
        }
    }
}

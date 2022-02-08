package org.snax.supersnax.util;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author maoth
 * @date 2022/1/21 22:28
 * @description
 */

@AllArgsConstructor
public class StockWLS {
    private static final double PARTITION = 0.5d;

    public double[][] minutePriceSequence;

    public double[][] minuteVolumeSequence;

    public double lastDayClosePrice;

    public static void main(String[] args) {
        double[][] x = {{73.43034408f, 96.0499592f}, {94.86454387f, 60.00290113f}, {38.58709412f, 91.84033576f},
            {29.82591733f, 10.93151681f}};
        //        double[][] x = {{73.43034408}, {96.0499592}, {94.86454387},
        //            {60.00290113}};
        double[][] y = {{27.0193589f, 10.8395823f}, {48.58716953f, 3.73887387f}, {24.50615027f, 27.54632465f},
            {13.01549582f, 43.75321252f}};
        StockWLS test = new StockWLS(x, y, 0.2);

        System.out.println(test.getMinuteFactor());
    }

    private static <T> int[] sortToIndex(T[] v) {
        Map<T, Queue<Integer>> indexMap = new HashMap<>();
        for (int i = 0; i < v.length; i++) {
            Queue<Integer> indexes = indexMap.get(v[i]);
            if (indexes == null)
                indexes = new ArrayDeque<>();
            indexes.add(i);
            indexMap.put(v[i], indexes);
        }
        List<T> vSorted = Arrays.stream(v).sorted().collect(Collectors.toList());
        int[] indexes = new int[vSorted.size()];
        for (int i = vSorted.size() - 1; i >= 0; i--) {
            Queue<Integer> itemIndexes = indexMap.get(vSorted.get(i));
            Integer index = itemIndexes.poll();
            if (index == null)
                throw new RuntimeException("itemIndexes为空");
            indexes[i] = index;
        }
        return reverse(indexes, indexes.length);
    }

    static int[] reverse(int a[], int n) {

        int[] result = new int[n];
        int i, k, t;

        for (i = 0; i < n / 2; i++) {

            t = a[i];

            a[i] = a[n - i - 1];

            a[n - i - 1] = t;

        }



        /*printing the reversed array*/

        for (k = 0; k < n; k++) {

            result[k] = a[k];
        }
        return result;
    }

    private double getMinuteFactor() {
        double[] changeList = new double[minutePriceSequence.length];
        Double[] amountList = new Double[minutePriceSequence.length];
        double[] amplitudeList = new double[minutePriceSequence.length];
        for (int i = 0; i < minutePriceSequence.length; i++) {
            double[] priceBlocks = minutePriceSequence[i];
            double[] volumeBlock = minuteVolumeSequence[i];
            double closePrice = priceBlocks[priceBlocks.length - 1];
            double closeVolume = volumeBlock[volumeBlock.length - 1];
            double maxPrice = Arrays.stream(priceBlocks).max().getAsDouble();
            double minPrice = Arrays.stream(priceBlocks).min().getAsDouble();
            FactorValue factor = new FactorValue(lastDayClosePrice, closePrice, maxPrice, minPrice, closeVolume);
            double change = factor.change();
            double amount = factor.amount();
            double amplitude = factor.amplitude();
            lastDayClosePrice = closePrice;
            changeList[i] = change;
            amountList[i] = amount;
            amplitudeList[i] = amplitude;
        }
        int[] sortIndex = Arrays.copyOfRange(sortToIndex(amountList), 0, (int)((double)amountList.length * PARTITION));
        double[] changeListSorted = new double[sortIndex.length];
        double[] amountListSorted = new double[sortIndex.length];
        double[] amplitudeListSorted = new double[sortIndex.length];
        for (int p = 0; p < sortIndex.length; p++) {
            changeListSorted[p] = changeList[sortIndex[p]];
            amountListSorted[p] = amountList[sortIndex[p]];
            amplitudeListSorted[p] = amplitudeList[sortIndex[p]];
        }
        System.out.println(Arrays.toString(changeListSorted));
        System.out.println(Arrays.toString(amplitudeListSorted));
        System.out.println(Arrays.toString(amountListSorted));

        //        Map.Entry<Double, Double> entry =
        //            WeightedLinearRegression.regress1d(changeListSorted, amplitudeListSorted, amountListSorted);
        //        System.out.println("key :"+ entry.getKey() + "value " + entry.getValue());
        WLS wls = new WLS(changeListSorted, amplitudeListSorted, amountListSorted);
        Point point = wls.fitLinearRegression();
        System.out.println("point is :" + point.getSlope() + "," + "pei : " + point.getIntercept());
        double[] residList = new double[amplitudeListSorted.length];
        double count = 0D;
        double countAmount = 0d;
        double[] normalized = new double[amountListSorted.length];
        for (int i = 0; i < amplitudeListSorted.length; i++) {
            countAmount += amountListSorted[i];
        }
        for (int i = 0; i < amountListSorted.length; i++) {
            normalized[i] = amplitudeListSorted[i] / countAmount;
        }

        for (int i = 0; i < amplitudeListSorted.length; i++) {
            //            double resid = normalized[i] * (changeListSorted[i] - (amplitudeListSorted[i] * point
            //            .getSlope()
            //                + point.getIntercept())) * (changeListSorted[i] - (amplitudeListSorted[i] * point
            //                .getSlope()
            //                + point.getIntercept()));
            System.out.println("predict" + (amplitudeListSorted[i] * point.getSlope() + point.getIntercept()));
            System.out.println("real" + changeListSorted[i]);
            double resid = changeListSorted[i] - (amplitudeListSorted[i] * point.getSlope() + point.getIntercept());
            System.out.println("---------------" + resid);
            residList[i] = resid;
            count += resid;
        }

        System.out.println(Arrays.toString(residList));
        StandardDeviation sd = new StandardDeviation(false);
        double sdt = sd.evaluate(residList);
        double mean = count / residList.length;
        System.out.println(mean);
        return mean / sdt;
    }

}

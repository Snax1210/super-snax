package org.snax.supersnax.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.snax.supersnax.entity.Stock;
import org.snax.supersnax.util.Point;
import org.snax.supersnax.util.WLS;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author maoth
 * @date 2022/1/24 3:18
 * @description
 */
@Service
public class StockService {

    public static final String TRAD_PRICE = "TradPrice";

    public Stock getAmountAndOpeningAndClosingMaxAndMin(List<JSONObject> jsonList) {
        double opening = jsonList.get(0).getDouble(TRAD_PRICE);
        String startTimeDate = jsonList.get(0).getString("TradTime");
        double closing = jsonList.get(jsonList.size() - 1).getDouble(TRAD_PRICE);
        double max =
            jsonList.stream().max(Comparator.comparing(json -> json.getDouble(TRAD_PRICE))).get().getDouble(TRAD_PRICE);
        double min =
            jsonList.stream().min(Comparator.comparing(json -> json.getDouble(TRAD_PRICE))).get().getDouble(TRAD_PRICE);
        double amount = jsonList.stream().mapToDouble(json -> json.getDouble(TRAD_PRICE)).sum();
        Stock stock = new Stock();
        stock.setAmount(amount);
        stock.setOpening(opening);
        stock.setClosing(closing);
        stock.setMax(max);
        stock.setMin(min);
        stock.setFirstDate(startTimeDate);
        return stock;
    }

    public void getAmplitudeAndApplies(Stock lastStock, Stock stock) {
        if (lastStock == null || lastStock.getClosing() == 0) {
            stock.setAmplitude(0);
            stock.setApplies(0);
        } else {
            //(最高价-最低价)/上一个收盘价x100% 振幅
            stock.setAmplitude(Math.abs(stock.getMax() - stock.getMin()) / lastStock.getClosing());
            //(收盘价-上一个收盘价)/上一个收盘价x100% 涨跌幅
            stock.setApplies((stock.getClosing() - lastStock.getClosing()) / lastStock.getClosing());
        }
    }

    public List<Stock> get50PercentData(List<Stock> stockList) {
        stockList.sort(Comparator.comparingDouble(Stock::getAmount).reversed());
        if (stockList.size() >= 2) {
            stockList = stockList.subList(0, stockList.size() / 2);
        }
        return stockList;
    }

    public double getMeanAndStd(List<Stock> stockList) {
        int size = stockList.size();
        if (size > 2) {
            double[] arrayAmplitude = new double[size];
            double[] arrayApplies = new double[size];
            double[] arrayAmount = new double[size];
            for (int i = 0; i < size; i++) {
                Stock stock = stockList.get(i);
                arrayApplies[i] = stock.getApplies();
                arrayAmplitude[i] = stock.getAmplitude();
                arrayAmount[i] = stock.getAmount();
            }
            WLS wls = new WLS(arrayApplies, arrayAmplitude, arrayAmount);
            Point point = wls.fitLinearRegression();
            double[] residArray = new double[size];
            double count = 0d;
            for (int i = 0; i < size; i++) {
                double resid = arrayApplies[i] - (arrayAmplitude[i] * point.getSlope() + point.getIntercept());
                count += resid;
                residArray[i] = resid;
            }
            StandardDeviation sd = new StandardDeviation(false);
            double std = sd.evaluate(residArray);
            double mean = count / size;
            return mean / std;
        } else {
            return 0;
        }
    }
}

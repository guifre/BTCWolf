package org.btcwolf.strategy;

import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public abstract class AbstractStrategy implements Strategy {


    boolean haveToBuy = false;
    int totalNumberOfTransactions = 0;
    Double totalProfit = 0d;

    private int transactionFee;
    Double cBitcoints;
    Double opThreshold;

    public AbstractStrategy(int transactionFee, Double cBitcoints, Double opThreshold) {
        this.transactionFee = transactionFee;
        this.cBitcoints = cBitcoints;
        this.opThreshold = opThreshold;
        this.totalProfit =0d;
        this.haveToBuy = true;
    }

    abstract void process(double newPrice);

    public double run(List<Double> a) {
        for (double e : a) {
            process(e);
        }
        return totalProfit;
    }

    void sell(double newPrice) {
        double prof = (newPrice - cBitcoints - transactionFee);
        //System.out.println("Selling for [" + value + "] bought for [" + cBitcoints + "] got [+" + prof + "]");
        cBitcoints = newPrice;
        haveToBuy = false;
        totalProfit = totalProfit + prof;
    }

    void buy(double newPrice) {
        double prof = (cBitcoints - newPrice - transactionFee);
        //System.out.println("Buying for [" + value + "] sold for [" + cBitcoints + "] got [+" + prof + "]");
        cBitcoints = newPrice;
        totalProfit = totalProfit + prof;
        haveToBuy = true;
    }
}

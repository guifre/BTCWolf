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
    Double currentBitcoins;

    public AbstractStrategy(int transactionFee, Double cBitcoints) {
        this.transactionFee = transactionFee;
        this.currentBitcoins = cBitcoints;
        this.totalProfit =0d;
        this.haveToBuy = true;
    }

    abstract boolean isWorthBuying(double newPrice);

    abstract boolean isWorthSelling(double newPrice);

    void process(double newPrice) {
        if (isWorthBuying(newPrice)) {
            sell(newPrice);
        } else if (isWorthSelling(newPrice)) {
            buy(newPrice);
        }
    }
    public double run(List<Double> a) {
        for (double e : a) {
            process(e);
        }
        return totalProfit;
    }

    void sell(double newPrice) {
        if (haveToBuy) {
            return;
        }
        double prof = (newPrice - currentBitcoins - transactionFee);
        //System.out.println("Selling for [" + value + "] bought for [" + currentBitcoins + "] got [+" + prof + "]");
        currentBitcoins = newPrice;
        haveToBuy = false;
        totalProfit = totalProfit + prof;
        totalNumberOfTransactions++;
    }

    void buy(double newPrice) {
        if (!haveToBuy) {
            return;
        }
        double prof = (currentBitcoins - newPrice - transactionFee);
        //System.out.println("Buying for [" + value + "] sold for [" + currentBitcoins + "] got [+" + prof + "]");
        currentBitcoins = newPrice;
        totalProfit = totalProfit + prof;
        haveToBuy = true;
        totalNumberOfTransactions++;
    }
}

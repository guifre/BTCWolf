package org.btcwolf.strategy;

import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public abstract class AbstractStrategy implements Strategy {


    int totalNumberOfTransactions = 0;
    Double totalProfit = 0d;

    int transactionFee;
    Double mDollars;
    Double mBitcoins;

    public AbstractStrategy(int transactionFee, Double startDollars) {
        this.transactionFee = transactionFee;
        this.mDollars = startDollars;
        this.mBitcoins = 0d;
        this.totalProfit =0d;
    }

    abstract boolean isWorthGettingBitCoins(double newPrice);
    abstract boolean isWorthGettingDollars(double newPrice);

    abstract void onReceiveNewPrice(double newPrice);

    void process(double newPrice) {
        onReceiveNewPrice(newPrice);
        if (isWorthGettingBitCoins(newPrice)) {
            getBitCoins(newPrice);
        } else if (isWorthGettingDollars(newPrice)) {
            getDollars(newPrice);
        }
    }
    public double run(List<Double> a) {
        for (double e : a) {
            process(e);
        }
        return totalProfit;
    }

    void getBitCoins(double newPrice) {
        if (this.mDollars == 0d) {
            return;
        }
        double newBitCoins = (this.mDollars / newPrice) - this.transactionFee/newPrice;
        double prof = newBitCoins * newPrice - this.mDollars;
        //System.out.println("getting bitcoins for [" + newPrice + "] current [" + newBitCoins + "] profit [+" + prof + "]");
        this.mBitcoins = newBitCoins;
        this.mDollars = 0d;
        this.totalProfit = this.totalProfit + prof;
        this.totalNumberOfTransactions++;
    }

    void getDollars(double newPrice) {
        if (this.mBitcoins == 0d) {
            return;
        }
        double newDollars = (this.mBitcoins * newPrice) - this.transactionFee;
        double prof = newDollars / newPrice - this.mDollars;
        //System.out.println("getting dollars for [" + newPrice + "] current [" + newDollars + "] profit [+" + prof + "]");
        this.mDollars = newDollars;
        this.mBitcoins = 0d;
        this.totalProfit = this.totalProfit + prof;
        this.totalNumberOfTransactions++;
    }
}

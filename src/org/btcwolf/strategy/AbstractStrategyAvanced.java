package org.btcwolf.strategy;

import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public abstract class AbstractStrategyAvanced implements Strategy {


    int totalNumberOfTransactions = 0;
    Double totalProfit = 0d;

    double transactionFee;
    Double mDollars;
    Double mBitCoins;

    public AbstractStrategyAvanced(double transactionFee, Double startDollars) {
        this.transactionFee = transactionFee;
        this.mDollars = startDollars;
        this.mBitCoins = 0d;
        this.totalProfit =0d;
    }

    abstract double getDollarsToSell();
    abstract double getBitCoinsToSell();

    abstract void onReceiveNewPrice(double[] ticker);


    void process(double newPrice) { //main method that triggers the logic we apply fee

        double[] ticker = {newPrice-this.transactionFee,newPrice+this.transactionFee}; //ask bet BTC dollar
        onReceiveNewPrice(ticker);

        double dollarsToSell = getDollarsToSell();
        if (dollarsToSell > 0) {
            getBitCoins(dollarsToSell, ticker);
        }

        double bitCoinsToSell = getBitCoinsToSell();
        if (bitCoinsToSell > 0) {
            getDollars(bitCoinsToSell, ticker);
        }
    }

    public double run(List<Double> a) {
        for (double e : a) {
            process(e);
        }
        return this.totalProfit;
    }

    void getBitCoins(double dollarsToSell, double[] ticker) {
        if (this.mDollars == 0d) {
            return;
        }
        double newBitCoins = (dollarsToSell / ticker[1]);
        double prof = newBitCoins * ticker[1] - dollarsToSell;
        //System.out.println("getting bitcoins for [" + ticker[0] + "] current [" + newBitCoins + "] profit [+" + prof + "]");
        this.mBitCoins += newBitCoins;
        this.mDollars -= dollarsToSell;
        this.totalProfit = this.totalProfit + prof;
        this.totalNumberOfTransactions++;
    }

    void getDollars(double bitCoinsToSell, double[] ticker) {
        if (this.mBitCoins == 0d) {
            return;
        }
        double newDollars = (bitCoinsToSell * ticker[0]);
        double prof = newDollars / ticker[0] - bitCoinsToSell;
        //System.out.println("getting dollars for [" + ticker[1] + "] current [" + newDollars + "] profit [+" + prof + "]");
        this.mDollars += newDollars;
        this.mBitCoins -= bitCoinsToSell;
        this.totalProfit = this.totalProfit + prof;
        this.totalNumberOfTransactions++;
    }
}

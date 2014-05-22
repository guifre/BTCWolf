package org.btcwolf.strategy;

public class AveragePrice extends AbstractStrategyAvanced {

    private double percentageToSell = 0.2;

    private double dollarsToSell = 0d;
    private double bitCoinsToSell = 0d;

    private double averageCostPerBitCoin = 0d;
    private double averageCostPerDollar = 0d;
    boolean firstDataReceived;


    public AveragePrice(double fee, double startDollars) {
        super(0, startDollars);
        this.firstDataReceived = true;
    }

    double getBitCoinsToSell() {
        return this.bitCoinsToSell;
    }

    double getDollarsToSell() {
        return this.dollarsToSell;
    }

    @Override
    void onReceiveNewPrice(double[] ticker) {

        //first we buy 50% of each currency
        if(firstDataReceived) {
            this.dollarsToSell = this.mDollars / 2;
            this.averageCostPerDollar = ticker[0];
            this.averageCostPerBitCoin =ticker[1];
            this.firstDataReceived = false;
            return;
        }

        //sell dollars?
        if (ticker[0] > this.averageCostPerDollar && this.mDollars > 0) { //average lower than cost? sell percentage
            this.dollarsToSell = this.percentageToSell * this.mDollars;
            //this.averageCostPerDollar = ((this.averageCostPerDollar * (this.mDollars - this.dollarsToSell)) + (ticker[0] * this.dollarsToSell)) / (this.mDollars + this.dollarsToSell);
            this.averageCostPerDollar = ((1.0 - percentageToSell) * averageCostPerDollar) + (percentageToSell * ticker[0]);
            System.out.println("D " + this.dollarsToSell + " av cost " + this.averageCostPerDollar + " current cost " + ticker[0]);
        } else {
            this.dollarsToSell = 0;
        }

        //sell bitCoins?
        if (ticker[1] > this.averageCostPerBitCoin && this.mBitCoins > 0) {

            this.bitCoinsToSell = this.percentageToSell * this.mBitCoins;
            this.averageCostPerBitCoin = (1.0 -percentageToSell) * averageCostPerBitCoin + percentageToSell*ticker[1];
            //   this.averageCostPerBitCoin = (this.averageCostPerBitCoin * (this.mBitCoins - this.bitCoinsToSell)+ (ticker[1] * this.bitCoinsToSell)) / (this.mBitCoins + this.bitCoinsToSell);
            System.out.println("Bit " + this.bitCoinsToSell + " av cost " + this.averageCostPerBitCoin + " current cost " + ticker[1]);

        } else {
            this.bitCoinsToSell = 0;
        }

    }
}

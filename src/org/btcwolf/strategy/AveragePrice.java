//package org.btcwolf.strategy;
//
//import com.xeiam.xchange.dto.marketdata.Ticker;
//
//import java.math.BigDecimal;
//
//public class AveragePrice extends AbstractStrategy {
//
//    private BigDecimal percentageToSell = BigDecimal.valueOf(0.2);
//
//    private BigDecimal dollarsToSell = BigDecimal.valueOf(0d);
//    private BigDecimal bitCoinsToSell = BigDecimal.valueOf(0d);
//
//    private BigDecimal averageCostPerBitCoin = BigDecimal.valueOf(0d);
//    private BigDecimal averageCostPerDollar = BigDecimal.valueOf(0d);
//    boolean firstDataReceived;
//
//
//    public AveragePrice(BigDecimal fee, BigDecimal startDollars) {
//        super(fee, startDollars);
//        this.firstDataReceived = true;
//    }
//
//    BigDecimal getBitCoinsToSell() {
//        return this.bitCoinsToSell;
//    }
//
//    BigDecimal getDollarsToSell() {
//        return this.dollarsToSell;
//    }
//
//
//    @Override
//    void onReceiveTicker(Ticker ticker) {
//
//
//        //first we buy 50% of each currency
//        if(firstDataReceived) {
//            this.dollarsToSell = this.mCurrency.divide(BigDecimal.valueOf(2));
//            this.averageCostPerDollar = ticker[0];
//            this.averageCostPerBitCoin =ticker[1];
//            this.firstDataReceived = false;
//            return;
//        }
//
//        //sell dollars?
//        if (ticker[0] > this.averageCostPerDollar && this.mCurrency.doubleValue() > 0) { //average lower than cost? sell percentage
//            this.dollarsToSell = this.percentageToSell * this.mCurrency;
//            //this.averageCostPerDollar = ((this.averageCostPerDollar * (this.mCurrency - this.dollarsToSell)) + (ticker[0] * this.dollarsToSell)) / (this.mCurrency + this.dollarsToSell);
//            this.averageCostPerDollar = ((1.0 - percentageToSell) * averageCostPerDollar) + (percentageToSell * ticker[0]);
//            System.out.println("D " + this.dollarsToSell + " av cost " + this.averageCostPerDollar + " current cost " + ticker[0]);
//        } else {
//            this.dollarsToSell = 0;
//        }
//
//        //sell bitCoins?
//        if (ticker[1] > this.averageCostPerBitCoin && this.mBitCoins > 0) {
//
//            this.bitCoinsToSell = this.percentageToSell * this.mBitCoins;
//            this.averageCostPerBitCoin = (1.0 -percentageToSell) * averageCostPerBitCoin + percentageToSell*ticker[1];
//            //   this.averageCostPerBitCoin = (this.averageCostPerBitCoin * (this.mBitCoins - this.bitCoinsToSell)+ (ticker[1] * this.bitCoinsToSell)) / (this.mBitCoins + this.bitCoinsToSell);
//            System.out.println("Bit " + this.bitCoinsToSell + " av cost " + this.averageCostPerBitCoin + " current cost " + ticker[1]);
//
//        } else {
//            this.bitCoinsToSell = 0;
//        }
//
//    }
//}

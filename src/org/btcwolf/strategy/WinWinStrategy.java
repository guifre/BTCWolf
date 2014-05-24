package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_DOWN;

/**
 * WinWinStrategy uses a simple approach
 *  It sells if the current price is higher than the one of buying time + fees + threshold.
 *  It buys if the current price is lower than the one of selling time + fees + threshold.
 *
 *  Made millions of tests with different data and thresholds, it appears not not be a
 *  best threshold, the optimal one varies from 0.1 to 9.x
 *
 * Created by guifre on 20/05/14.
 */
public class WinWinStrategy extends AbstractStrategy {

    private BigDecimal currencyToBuy= BigDecimal.valueOf(0d);
    private BigDecimal bitCoinsToBuy = BigDecimal.valueOf(0d);

    private BigDecimal lastPriceUsedToSell = BigDecimal.ZERO;;
    private BigDecimal lastPriceUsedToBuy = BigDecimal.ZERO;;

    private BigDecimal opThreshold = BigDecimal.ZERO;

    public WinWinStrategy(BigDecimal fee, BigDecimal startCurrency, BigDecimal opThreshold) {
        super(fee, startCurrency);
        this.opThreshold = opThreshold;
    }


    @Override
    BigDecimal getBitCoinsToSell() {
       return this.currencyToBuy;
    }

    @Override
    BigDecimal getBitCoinsToBuy() {
        return this.bitCoinsToBuy;
    }

    @Override
    void onReceiveTicker(Ticker ticker) {
        this.bitCoinsToBuy = BigDecimal.valueOf(0);
        this.currencyToBuy = BigDecimal.valueOf(0);
        if (lastPriceUsedToBuy.doubleValue() == 0d && lastPriceUsedToSell.doubleValue() == 0d) {
            //buying for first time
            this.bitCoinsToBuy = this.mCurrency.divide(ticker.getBid(), ROUND_DOWN);
            this.lastPriceUsedToBuy = ticker.getBid();
            this.lastPriceUsedToSell = ticker.getAsk();
        } else {
            computeWorthnessBuyingBitcoins(ticker);
            computeWorthnessSellingBitcoins(ticker);
        }

    }

    private void computeWorthnessSellingBitcoins(Ticker ticker) {
        if (ticker.getAsk().doubleValue() > this.lastPriceUsedToSell.doubleValue()&& this.mBitCoins.doubleValue() > 0) {
            this.currencyToBuy = this.mBitCoins.multiply(ticker.getAsk());
            BigDecimal profitAfterTheOperation = this.mBitCoins.multiply(ticker.getAsk().subtract(this.lastPriceUsedToSell));
            this.totalProfit = this.totalProfit.add(profitAfterTheOperation);
            System.out.println("Ask [" +ticker.getAsk()+"] previous [" + this.lastPriceUsedToSell +"] profit of [" +String.format("%.4f", profitAfterTheOperation)+"] current profit [" + String.format("%.4f", totalProfit)+"]");
            lastPriceUsedToSell = ticker.getAsk();
            lastPriceUsedToBuy=ticker.getBid();
        }
    }

    private void computeWorthnessBuyingBitcoins(Ticker ticker) {
        if (ticker.getBid().doubleValue() < this.lastPriceUsedToBuy.doubleValue()&& this.mCurrency.doubleValue() > 0) {
            this.bitCoinsToBuy = this.mCurrency.divide(ticker.getBid(),20, ROUND_DOWN);
            BigDecimal profitAfterTheOperation =  this.bitCoinsToBuy.multiply(lastPriceUsedToBuy.subtract(ticker.getBid()));
            this.totalProfit = this.totalProfit.add(profitAfterTheOperation);
            System.out.println("Bid [" +ticker.getBid()+"] previous [" + this.lastPriceUsedToBuy +"] profit of [" +String.format("%.4f", profitAfterTheOperation)+"] current profit [" +  String.format("%.4f",totalProfit)+"]");
            this.lastPriceUsedToBuy = ticker.getBid();
            this.lastPriceUsedToSell=ticker.getAsk();

        }
    }

}

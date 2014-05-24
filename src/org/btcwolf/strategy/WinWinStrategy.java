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

    private final BigDecimal opBitCoinThreshold;
    private final BigDecimal opCurrencyThreshold;
    private BigDecimal currencyToBuy= BigDecimal.valueOf(0d);
    private BigDecimal bitCoinsToBuy = BigDecimal.valueOf(0d);

    private BigDecimal lastPriceUsedToSell = BigDecimal.ZERO;;
    private BigDecimal lastPriceUsedToBuy = BigDecimal.ZERO;;

    public WinWinStrategy(BigDecimal fee, BigDecimal startCurrency, BigDecimal opBitCoinThreshold, BigDecimal opCurrencyThreshold) {
        super(fee, startCurrency);
        this.opBitCoinThreshold = opBitCoinThreshold;
        this.opCurrencyThreshold = opCurrencyThreshold;
    }


    @Override
    BigDecimal getBitCoinsToSell() {
       return currencyToBuy;
    }

    @Override
    BigDecimal getBitCoinsToBuy() {
        return bitCoinsToBuy;
    }

    @Override
    void analyzeTicker(Ticker ticker) {
        bitCoinsToBuy = BigDecimal.valueOf(0);
        currencyToBuy = BigDecimal.valueOf(0);
        if (lastPriceUsedToBuy.doubleValue() == 0d && lastPriceUsedToSell.doubleValue() == 0d) {
            //buying for first time
            bitCoinsToBuy = mCurrency.divide(ticker.getBid(), ROUND_DOWN);
            lastPriceUsedToBuy = ticker.getBid();
            lastPriceUsedToSell = ticker.getAsk();
        } else {
            computeWorthnessBuyingBitcoins(ticker);
            computeWorthnessSellingBitcoins(ticker);
        }

    }

    private void computeWorthnessSellingBitcoins(Ticker ticker) {
        if (ticker.getAsk().doubleValue() > lastPriceUsedToSell.doubleValue()+opCurrencyThreshold.doubleValue()&& mBitCoins.doubleValue() > 0) {
            currencyToBuy = mBitCoins.multiply(ticker.getAsk());
            BigDecimal profitAfterTheOperation = mBitCoins.multiply(ticker.getAsk().subtract(lastPriceUsedToSell));
            totalProfit = totalProfit.add(profitAfterTheOperation);
            logger.config("Ask [" + ticker.getAsk() + "] previous [" + lastPriceUsedToSell + "] profit of [" + String.format("%.4f", profitAfterTheOperation) + "] current profit [" + String.format("%.4f", totalProfit) + "]");
            lastPriceUsedToSell = ticker.getAsk();
            lastPriceUsedToBuy=ticker.getBid();
        }
    }

    private void computeWorthnessBuyingBitcoins(Ticker ticker) {
        if (ticker.getBid().doubleValue() < lastPriceUsedToBuy.doubleValue()- opBitCoinThreshold.doubleValue() && mCurrency.doubleValue() > 0) {
            bitCoinsToBuy = mCurrency.divide(ticker.getBid(),20, ROUND_DOWN);
            BigDecimal profitAfterTheOperation =  bitCoinsToBuy.multiply(lastPriceUsedToBuy.subtract(ticker.getBid()));
            totalProfit = totalProfit.add(profitAfterTheOperation);
            logger.config("Bid [" + ticker.getBid() + "] previous [" + lastPriceUsedToBuy + "] profit of [" + String.format("%.4f", profitAfterTheOperation) + "] current profit [" + String.format("%.4f", totalProfit) + "]");
            lastPriceUsedToBuy = ticker.getBid();
            lastPriceUsedToSell=ticker.getAsk();

        }
    }

}

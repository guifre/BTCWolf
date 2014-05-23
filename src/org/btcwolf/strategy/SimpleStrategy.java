package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;

import java.math.BigDecimal;
import java.util.List;

/**
 * SimpleStrategy uses a simple approach
 *  It sells if the current price is higher than the one of buying time + fees + threshold.
 *  It buys if the current price is lower than the one of selling time + fees + threshold.
 *
 *  Made millions of tests with different data and thresholds, it appears not not be a
 *  best threshold, the optimal one varies from 0.1 to 9.x
 *
 * Created by guifre on 20/05/14.
 */
public class SimpleStrategy extends AbstractStrategy {

    private BigDecimal opThreshold;
    private BigDecimal lastPriceUsed = BigDecimal.ZERO;

    private BigDecimal bitCoinsToSell;
    private BigDecimal bitCoinsToBuy;

    public SimpleStrategy(BigDecimal fee, BigDecimal startDollars, BigDecimal opThreshold) {
        super(fee, startDollars);
        this.opThreshold = opThreshold;
    }

    boolean isWorthGettingBitCoins(double newPrice) {
        if (newPrice < (this.lastPriceUsed.add(this.opThreshold).doubleValue())) {
            lastPriceUsed = newPrice;
            return true;
        }
        return false;
    }


    @Override
    BigDecimal getBitCoinsToSell() {
       return this.bitCoinsToSell;
    }

    @Override
    BigDecimal getBitCoinsToBuy() {
        return this.bitCoinsToBuy;
    }

    @Override
    void onReceiveTicker(Ticker ticker) {

    }

    @Override
    public double run(List<Double> a) {
        return 0;
    }
}

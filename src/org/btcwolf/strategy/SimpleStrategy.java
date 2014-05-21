package org.btcwolf.strategy;

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

    private Double opThreshold;

    public SimpleStrategy(int fee, double cBitcoins, double opThreshold) {
        super(fee, cBitcoins);
        this.opThreshold = opThreshold;
    }

    boolean isWorthBuying(double newPrice) {
        return newPrice >= (currentBitcoins + opThreshold);
    }

    boolean isWorthSelling(double newPrice) {
        return  newPrice <= (currentBitcoins - opThreshold);
    }
}

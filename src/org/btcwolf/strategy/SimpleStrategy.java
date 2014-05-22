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
    private double lastPriceUsed =0d;


    public SimpleStrategy(int fee, double startDollars, double opThreshold) {
        super(fee, startDollars);
        this.opThreshold = opThreshold + fee;
    }

    boolean isWorthGettingBitCoins(double newPrice) {
        if (newPrice < (this.lastPriceUsed + this.opThreshold)) {
            lastPriceUsed = newPrice;
            return true;
        }
        return false;
    }

    boolean isWorthGettingDollars(double newPrice) {
        if (newPrice > (this.lastPriceUsed + this.opThreshold)) {
            lastPriceUsed = newPrice;
            return true;
        }
        return false;
    }

    @Override
    void onReceiveNewPrice(double newPrice) {
    }
}

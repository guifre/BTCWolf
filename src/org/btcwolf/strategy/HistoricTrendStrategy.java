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
public class HistoricTrendStrategy extends AbstractStrategy {

    private int consecutiveIncresesToOperate;
    private int count;
    private double prevPrice = 0;
    private double lastUsedPrice = 0d;
    public HistoricTrendStrategy(int fee, double startDollars, int consecutiveIncresesToOperate) {
        super(fee, startDollars);
        this.consecutiveIncresesToOperate = consecutiveIncresesToOperate;
        this.count = 0;
    }

    boolean isWorthGettingBitCoins(double newPrice) {
        if(lastUsedPrice > (newPrice+this.transactionFee) && count >= this.consecutiveIncresesToOperate){
            count=0;
            lastUsedPrice=newPrice;
            return true;
        }
        return false;
    }

    boolean isWorthGettingDollars(double newPrice) {
        if (lastUsedPrice < (newPrice+this.transactionFee) && count <= (this.consecutiveIncresesToOperate-this.consecutiveIncresesToOperate-this.consecutiveIncresesToOperate)){
            count=0;
            lastUsedPrice=newPrice;
            return true;
        }
        return false;
    }

    @Override
    void onReceiveNewPrice(double newPrice) {
        if (newPrice < (this.prevPrice)) {
            if (count > 0){
                count = 0;
            } else {
                count--;
            }
        } else if(newPrice > (this.prevPrice)) {
            if (count < 0){
                count = 0;
            } else {
                count++;
            }
        }
        this.prevPrice = newPrice;
    }
}

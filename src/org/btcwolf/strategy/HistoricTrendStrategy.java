
package org.btcwolf.strategy;

/**
 * Created by guifre on 20/05/14.
 */
public class HistoricTrendStrategy extends AbstractStrategy {

    public HistoricTrendStrategy(int transactionFee, double cBitcoins) {
        super(transactionFee, cBitcoins);
    }

    boolean isWorthBuying(double newPrice) {
       return false;
    }

    boolean isWorthSelling(double newPrice) {
       return  false;
    }
}

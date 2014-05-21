
package org.btcwolf.strategy;

/**
 * Created by guifre on 20/05/14.
 */
public class HistoricTrendStrategy extends AbstractStrategy {

    public HistoricTrendStrategy(int fee, double cBitcoins, double treshold) {
        super(fee, cBitcoins, treshold);
    }

    void process(double newPrice) {
        if (haveToBuy && newPrice >= (cBitcoints + opThreshold)) {
            sell(newPrice);
        } else if (!haveToBuy && newPrice <= (cBitcoints - opThreshold)) {
            buy(newPrice);
        }
    }
}

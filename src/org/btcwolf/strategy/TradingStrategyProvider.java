package org.btcwolf.strategy;

import java.math.BigDecimal;

/**
 * Created by guifre on 24/05/14.
 */
public class TradingStrategyProvider {

    public static final Strategy getDefaultStrategy() {
        return getAgent(BigDecimal.ZERO, BigDecimal.valueOf(5000), BigDecimal.valueOf(10), BigDecimal.valueOf(10));
    }
    public static final Strategy getAgent(BigDecimal fee, BigDecimal startCurrency, BigDecimal opBitCoinThreshold, BigDecimal opCurrencyThreshold) {
        return new WinWinStrategy(fee, startCurrency, opBitCoinThreshold, opCurrencyThreshold);
    }
}

/*
 * BTCWolf Copyright (C) 2014 Guifre Ruiz <guifre.ruiz@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_DOWN;

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
            placeFirstOrder(ticker);
        } else {
            computeWorthinessBuyingBitCoins(ticker);
            computeWorthinessSellingBitCoins(ticker);
        }
    }

    private void placeFirstOrder(Ticker ticker) {
        bitCoinsToBuy = mCurrency.divide(ticker.getBid(), ROUND_DOWN);
        lastPriceUsedToBuy = ticker.getBid();
        lastPriceUsedToSell = ticker.getAsk();
    }

    private void computeWorthinessSellingBitCoins(Ticker ticker) {
        if (ticker.getAsk().doubleValue() > lastPriceUsedToSell.doubleValue()+opCurrencyThreshold.doubleValue()&& mBitCoins.doubleValue() > 0) {
            currencyToBuy = mBitCoins.multiply(ticker.getAsk());
            BigDecimal profitAfterTheOperation = mBitCoins.multiply(ticker.getAsk().subtract(lastPriceUsedToSell));
            totalProfit = totalProfit.add(profitAfterTheOperation);
            logger.debug("Ask [" + ticker.getAsk() + "] previous [" + lastPriceUsedToSell + "] profit of [" + String.format("%.4f", profitAfterTheOperation) + "] current profit [" + String.format("%.4f", totalProfit) + "]");
            lastPriceUsedToSell = ticker.getAsk();
            lastPriceUsedToBuy=ticker.getBid();
        }
    }

    private void computeWorthinessBuyingBitCoins(Ticker ticker) {
        if (ticker.getBid().doubleValue() < lastPriceUsedToBuy.doubleValue()- opBitCoinThreshold.doubleValue() && mCurrency.doubleValue() > 0) {
            bitCoinsToBuy = mCurrency.divide(ticker.getBid(),20, ROUND_DOWN);
            BigDecimal profitAfterTheOperation =  bitCoinsToBuy.multiply(lastPriceUsedToBuy.subtract(ticker.getBid()));
            totalProfit = totalProfit.add(profitAfterTheOperation);
            logger.debug("Bid [" + ticker.getBid() + "] previous [" + lastPriceUsedToBuy + "] profit of [" + String.format("%.4f", profitAfterTheOperation) + "] current profit [" + String.format("%.4f", totalProfit) + "]");
            lastPriceUsedToBuy = ticker.getBid();
            lastPriceUsedToSell=ticker.getAsk();

        }
    }

}

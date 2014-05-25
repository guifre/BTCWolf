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

import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.log4j.Logger;
import org.btcwolf.agent.TraderAgent;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.valueOf;

public abstract class AbstractTradingStrategy implements TradingStrategy {

    static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class.getSimpleName());

    protected static final int DIVISION_LEVELS_ACCURACY = 20;

    private final TraderAgent traderAgent;
    protected BigDecimal mCurrency;
    protected BigDecimal mBitCoins;
    protected BigDecimal totalProfit;

    public AbstractTradingStrategy(TraderAgent traderAgent) {
        this.traderAgent = traderAgent;
        this.totalProfit = valueOf(0);
        this.totalProfit = valueOf(0);
        getAccountInfo();
    }

    private void getAccountInfo() {
        this.mBitCoins = traderAgent.getBitCoinBalance();
        this.mCurrency = traderAgent.getCurrencyBalance();
    }

    abstract BigDecimal getBitCoinsToSell();
    abstract BigDecimal getBitCoinsToBuy();

    abstract void analyzeTicker(Ticker ticker);


    public void onTickerReceived(Ticker ticker) { //main method that triggers the logic we apply fee
        logger.info("received ticker " + ticker);
        analyzeTicker(ticker);

        BigDecimal bitCoinsToBuy = getBitCoinsToBuy();

        if (bitCoinsToBuy.doubleValue() > 0d) {
            buyBitCoins(bitCoinsToBuy, ticker);
        }

        BigDecimal bitCoinsToSell = getBitCoinsToSell();
        if (bitCoinsToSell.doubleValue() > 0d) {
            sellBitCoins(bitCoinsToSell, ticker);
        }
    }

    BigDecimal run(List<Ticker> list) {
        for (Ticker ticker : list) {
            analyzeTicker(ticker);
        }
        return this.totalProfit;
    }

    void buyBitCoins(BigDecimal bitCoinsToBuy, Ticker ticker) {
        if (this.mCurrency.doubleValue() == 0d) {
            return;
        }
        BigDecimal bitCoinsAboutToBuy = bitCoinsToBuy.divide(ticker.getBid(), DIVISION_LEVELS_ACCURACY, ROUND_DOWN);
        this.mBitCoins = this.mBitCoins.add(bitCoinsAboutToBuy);
        this.mCurrency = BigDecimal.valueOf(0);
        logger.info("BTC [" + this.mBitCoins + "] Yu[" + this.mCurrency + "]\n");
        String orderResult = traderAgent.placeOrder(Order.OrderType.BID, bitCoinsAboutToBuy);
        logger.info("Order of buying [ " + bitCoinsAboutToBuy + "] currency placed, result [" + orderResult + "]");
    }

    void sellBitCoins(BigDecimal bitCoinsToSell, Ticker ticker) {
        if (this.mBitCoins.doubleValue() == 0d) {
            return;
        }
        BigDecimal currencyAboutToBuy = bitCoinsToSell.multiply(ticker.getAsk());
        this.mCurrency = currencyAboutToBuy;
        this.mBitCoins = BigDecimal.valueOf(0);
        logger.info("BTC[" + this.mBitCoins + "] Yu[" + this.mCurrency + "]\n");
        String orderResult = traderAgent.placeOrder(Order.OrderType.ASK, currencyAboutToBuy);
        logger.info("Order of buying [ " + currencyAboutToBuy + "] currency placed, result [" + orderResult + "]");
    }
}

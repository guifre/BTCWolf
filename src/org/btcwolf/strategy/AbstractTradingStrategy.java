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
import org.apache.log4j.Logger;
import org.btcwolf.agent.TraderAgent;

import java.math.BigDecimal;

import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static java.math.BigDecimal.ZERO;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.logAfterASK;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.logAfterBID;

public abstract class AbstractTradingStrategy implements TradingStrategy {

    static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class);

    final TraderAgent traderAgent;

    public AbstractTradingStrategy(TraderAgent traderAgent) {
        this.traderAgent = traderAgent;
    }

    abstract BigDecimal getBitCoinsToSell();

    abstract BigDecimal getBitCoinsToBuy();

    abstract void analyzeTicker(Ticker ticker);

    public void onTickerReceived(Ticker ticker) {

        analyzeTicker(ticker);

        BigDecimal bitCoinsToBuy = getBitCoinsToBuy();

        if (bitCoinsToBuy.compareTo(ZERO) == 1) {
            buyBitCoins(bitCoinsToBuy, ticker);
        }

        BigDecimal currencyToBuy = getBitCoinsToSell();
        if (currencyToBuy.compareTo(ZERO) == 1) {
            buyCurrency(currencyToBuy, ticker);
        }
    }

    void buyBitCoins(BigDecimal bitCoinsToBuy, Ticker ticker) {
        BigDecimal myCurrency = traderAgent.getCurrencyBalance();
        if (myCurrency.compareTo(ZERO) == 0) {
            return;
        }
        String orderResult = traderAgent.placeOrder(BID, bitCoinsToBuy, ticker);
        if (!"KO".equals(orderResult)) {
            logAfterBID(myCurrency, orderResult);
        }
    }

    void buyCurrency(BigDecimal bitCoinsToSell, Ticker ticker) {
        BigDecimal myBitCoins = traderAgent.getBitCoinBalance();
        if (myBitCoins.compareTo(ZERO) == 0) {
            return;
        }
        String orderResult = traderAgent.placeOrder(ASK, bitCoinsToSell, ticker);
        if (!"KO".equals(orderResult)) {
            logAfterASK(bitCoinsToSell, orderResult);
        }
    }
}
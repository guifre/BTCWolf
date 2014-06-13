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

import static java.math.BigDecimal.ZERO;

public abstract class AbstractTradingStrategy implements TradingStrategy {

    static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class);

    final TraderAgent traderAgent;

    public AbstractTradingStrategy(TraderAgent traderAgent) {
        this.traderAgent = traderAgent;
    }

    abstract void onOrdered(Ticker ticker, BigDecimal bitCoinsToBuy, Order.OrderType orderType, String orderResult);

    void placeOrder(Order.OrderType orderType, BigDecimal amount, Ticker ticker) {
        if (amount.compareTo(ZERO) == 0) {
            return;
        }
        onOrdered(ticker, amount, orderType, traderAgent.placeOrder(orderType, amount, ticker));
    }
}
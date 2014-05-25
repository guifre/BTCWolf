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
import com.xeiam.xchange.dto.trade.OpenOrders;

public class ExchangeMonitorDecorator implements TradingStrategy {

    private static final int POLLING_FREQ = 8;

    private AbstractTradingStrategy tradingStrategy;
    private int pollingCounter;

    public ExchangeMonitorDecorator(TradingStrategy tradingStrategy) {
        this.tradingStrategy = (AbstractTradingStrategy) tradingStrategy;
        this.pollingCounter = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        logStatus();
        tradingStrategy.onTickerReceived(ticker);
    }

    private void pollExchangeStatus() {
        if (pollingCounter < POLLING_FREQ) {
            pollingCounter++;
            return;
        }
        pollingCounter = 0;
        logStatus();
    }
    private void logStatus() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BitCoin Balance [" + tradingStrategy.traderAgent.getBitCoinBalance() +
                "] Currency Balance [" +tradingStrategy.traderAgent.getCurrencyBalance()+"]");
        OpenOrders openOrders = tradingStrategy.traderAgent.getOpenOrders();
        if (openOrders != null) {
            stringBuilder.append("Open Orders [" + openOrders.toString());
        }
        tradingStrategy.logger.info(stringBuilder.toString());
    }
}

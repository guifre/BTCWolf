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

package org.btcwolf.strategy.impl.decorators;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.log4j.Logger;
import org.btcwolf.strategy.TradingStrategy;
import org.btcwolf.strategy.TradingStrategyProvider;
import org.btcwolf.strategy.impl.AbstractTradingStrategy;
import org.btcwolf.twitter.TwitterAgent;

public class StrategyOrchestratorDecorator implements TradingStrategy {

    private static final int POLLING_FREQ = 8;
    private static final Logger logger = Logger.getLogger(StrategyOrchestratorDecorator.class);

    private AbstractTradingStrategy tradingStrategy;
    private TradingStrategyProvider tradingStrategyProvider;
    private int pollingCounter;

    public StrategyOrchestratorDecorator(TradingStrategyProvider tradingStrategyProvider, TradingStrategy tradingStrategy) {
        this.tradingStrategy = (AbstractTradingStrategy) tradingStrategy;
        this.tradingStrategyProvider = tradingStrategyProvider;
        this.pollingCounter = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        pollExchangeStatus(ticker);
        checkAppropiateStrategy(ticker);
        tradingStrategy.onTickerReceived(ticker);
    }

    private void checkAppropiateStrategy(Ticker ticker) {
        if (false) {
            this.tradingStrategyProvider.switchToDefaultWinWInStrategy();
        }
    }

    void pollExchangeStatus(Ticker ticker) {
        logger.debug("\n\n New " + ticker);
        if (pollingCounter > POLLING_FREQ) {
            pollingCounter = 0;
            logStatus();
        }
        pollingCounter++;
    }

    void logStatus() {
        logger.debug(
                "BTC Balance[" + tradingStrategy.traderAgent.getBitCoinBalance() +
                        "] CNY Balance[" + tradingStrategy.traderAgent.getCurrencyBalance() +
                        "]" + " Open Orders[" + tradingStrategy.traderAgent.getOpenOrders().toString() + "].");
    }


}
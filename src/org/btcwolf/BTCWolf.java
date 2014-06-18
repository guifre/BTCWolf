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

package org.btcwolf;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.agent.AgentsFactory;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.TradingStrategy;
import org.btcwolf.strategy.TradingStrategyProvider;

public class BTCWolf {

    private static final Logger LOGGER =Logger.getLogger(BTCWolf.class);
    private static final String LOG4J_PATH = "./resources/log4j.properties";
    private static final long POLLING_TIME = 30000;

    public static void main(String[] args) {

        PropertyConfigurator.configure(LOG4J_PATH);

        TraderAgent traderAgent = AgentsFactory.buildTraderAgent();
        TradingStrategyProvider tradingStrategyProvider = new TradingStrategyProvider(traderAgent);

        Ticker previousTicker = traderAgent.pollTicker();
        while(true) {
            Ticker ticker = traderAgent.pollTicker();
            if (!isSameTicker(previousTicker, ticker)) {
                tradingStrategyProvider.getStrategy().onTickerReceived(ticker);
                previousTicker = ticker;
            }
            makeSomeCoffee();
        }
    }

    private static boolean isSameTicker(Ticker previousTicker, Ticker ticker) {
        return previousTicker.getBid().compareTo(ticker.getBid()) != 0 ||
                previousTicker.getVolume().compareTo(ticker.getVolume()) != 0 ||
                previousTicker.getLast().compareTo(ticker.getLast()) != 0 ||
                previousTicker.getAsk().compareTo(ticker.getAsk()) != 0;
    }

    public static void makeSomeCoffee() {
        try {
            Thread.sleep(POLLING_TIME);
        } catch (InterruptedException e) {
            LOGGER.warn("thread interrupted, ignoring, this is bad." + e);
            makeSomeCoffee();
        }
    }
}

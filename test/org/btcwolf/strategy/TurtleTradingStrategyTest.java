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
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.agent.TraderAgent;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

public class TurtleTradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";
    private static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class);
    private BigDecimal lastAsk;

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
    }

    @Test
    public void TestTourtle() {

        //setup
        BigDecimal cny = BigDecimal.valueOf(0);
        BigDecimal btc = BigDecimal.valueOf(0.01);

        for (int turtleSpeed = 1; turtleSpeed < 50; turtleSpeed++) {
            TraderAgent testerAgent = new MarketExchangeAgent(btc, cny);

            TradingStrategy testedStrategy = new TurtleTradingStrategy(testerAgent, turtleSpeed);

            //run
            runTest(testerAgent, testedStrategy);

            //validation
            System.out.println("[" + turtleSpeed + "] [" +
                    testerAgent.getBitCoinBalance()
                            .subtract(btc)
                            .add(testerAgent.getCurrencyBalance().divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN))
                            .subtract(cny.divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN)));
        }
    }

    private void runTest(TraderAgent testerAgent, TradingStrategy testedStrategy) {
        //run
        Ticker ticker = testerAgent.pollTicker();
        while(ticker != null) {
            testedStrategy.onTickerReceived(ticker);
            lastAsk = ticker.getAsk();
            ticker = testerAgent.pollTicker();
        }
    }
}

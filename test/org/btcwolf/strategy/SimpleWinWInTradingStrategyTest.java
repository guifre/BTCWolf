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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.helpers.MarketExchangeAgent;
import org.btcwolf.helpers.StrategyTestHelper;
import org.btcwolf.helpers.TestStrategyFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

public class SimpleWinWInTradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
        Logger.getRootLogger().removeAllAppenders();
    }

    @Test
    public void testWinWinStrategy() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        for (int opThreshold = 1; opThreshold < 5; opThreshold++) {
            for (int l = 0; l < 10; l++) {
                int[] indexes = StrategyTestHelper.getIndexes(maxIndex);

                TestStrategyFactory strategyProviderWithSwitch = new TestStrategyFactory(testerAgent, true);
                strategyProviderWithSwitch.buildTestingWinWinStrategy(BigDecimal.valueOf(opThreshold));

                TestStrategyFactory strategyProvider = new TestStrategyFactory(testerAgent, false);
                strategyProviderWithSwitch.buildTestingWinWinStrategy(BigDecimal.valueOf(opThreshold));

                StrategyTestHelper.runWinWinTest(opThreshold, indexes, testerAgent, strategyProviderWithSwitch);
                StrategyTestHelper.runWinWinTest(opThreshold, indexes, testerAgent, strategyProvider);
            }
        }
    }
}

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
import org.btcwolf.helpers.TestStrategyProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

public class TurtleTradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
        Logger.getRootLogger().removeAllAppenders();
    }

    @Test
    public void testTurtleStrategy() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        for (int turtleSpeed = 2; turtleSpeed < 5; turtleSpeed++) {
            for (int amount = 1; amount < 4; amount++) {
                for (int l = 0; l < 10; l++) {
                    int[] indexes = StrategyTestHelper.getIndexes(maxIndex);

                    TestStrategyProvider strategyProviderWithSwitch = new TestStrategyProvider(testerAgent, true);
                    strategyProviderWithSwitch.geTurtleStrategy(testerAgent, turtleSpeed, amount);

                    TestStrategyProvider strategyProvider = new TestStrategyProvider(testerAgent, false);
                    strategyProvider.geTurtleStrategy(testerAgent, 1, 1);

                    StrategyTestHelper.runTurtleTest(turtleSpeed, indexes, amount, testerAgent, strategyProviderWithSwitch);
                    StrategyTestHelper.runTurtleTest(turtleSpeed, indexes, amount, testerAgent, strategyProvider);
                }
            }
        }
    }

    @Test
    public void testDefaultTurtleTradingStrategy() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        for (int l = 0; l < 20; l++) {
            int[] indexes = StrategyTestHelper.getIndexes(maxIndex);

            TestStrategyProvider strategyProviderWithSwitch = new TestStrategyProvider(testerAgent, true);
            strategyProviderWithSwitch.getDefaultTurtleStrategy(testerAgent);

            TestStrategyProvider strategyProvider = new TestStrategyProvider(testerAgent, false);
            strategyProvider.getDefaultTurtleStrategy(testerAgent);

            StrategyTestHelper.runTurtleTest(4, indexes, 2, testerAgent, strategyProviderWithSwitch);
            StrategyTestHelper.runTurtleTest(4, indexes, 2, testerAgent, strategyProvider);
        }

    }
}

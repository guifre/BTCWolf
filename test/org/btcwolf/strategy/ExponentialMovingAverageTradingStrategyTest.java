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

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.helpers.MarketExchangeAgent;
import org.btcwolf.helpers.StrategyTestHelper;
import org.btcwolf.helpers.TestStrategyFactory;
import org.btcwolf.persistance.plot.Plotting;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

public class ExponentialMovingAverageTradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
        Logger.getRootLogger().removeAllAppenders();
    }

    @Test
    public void testExponentialMovingAverageStrategy() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));

        for (int i = 0; i < 20; i++) {
            int[] indexes = StrategyTestHelper.getIndexes(500);
            TestStrategyFactory strategyProvider = new TestStrategyFactory(testerAgent, false);
            strategyProvider.switchStrategy(strategyProvider.buildExponentialMovingAverageStrategy());
            StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);
        }
    }

    @Test
    public void findBestExponentialMovingAverageSettings() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        int[] indexes = StrategyTestHelper.getIndexes(1000, maxIndex);
        int[][] values = {
                {10,10},
                {10, 110},
                {60, 10},
                {160,160},
                {210,210},
        };
        for (int i = 1; i < 50; i+=10) {
            for (int j = 1; j < 100; j+=10) {
                if (i> j) {
                    continue;
                }
                System.out.println("min " + i + " max " + j);
                TestStrategyFactory strategyProvider = new TestStrategyFactory(testerAgent, false);
                strategyProvider.switchStrategy(strategyProvider.buildExponentialMovingAverageStrategy(i, j, false));
                StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);
            }
        }
    }

    @Test
    public void compareStrategies() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        int[] indexes = StrategyTestHelper.getIndexes(1000, maxIndex);

        TestStrategyFactory strategyProvider = new TestStrategyFactory(testerAgent, false);
        strategyProvider.switchStrategy(strategyProvider.buildExponentialMovingAverageStrategy(180, 30, false));
        System.out.println("testing advanced strategy best 11 11 ");
        StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);

        strategyProvider = new TestStrategyFactory(testerAgent, false);
        strategyProvider.switchStrategy(strategyProvider.buildWinWinStrategy());
        System.out.println("testing winwin strategy");
        StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);

        strategyProvider = new TestStrategyFactory(testerAgent, false);
        strategyProvider.switchStrategy(strategyProvider.buildTurtleStrategy(testerAgent));
        System.out.println("testing turtle strategy");
        StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);
        for (int i = 1; i < 50; i+=40) {
            for (int j = 1; j < 100; j+=40) {
                if (i > j) {
                    continue;
                }
                indexes = StrategyTestHelper.getIndexes(1000, maxIndex);

                System.out.println("testing ExponentialMovingAverage strategy min " + i + " max " + j);
                 strategyProvider = new TestStrategyFactory(testerAgent, false);
//                strategyProvider.buildExponentialMovingAverageStrategy(i,j, false);
                strategyProvider.switchStrategy(strategyProvider.buildExponentialMovingAverageStrategy());
                StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);
            }
        }
    }

    @Test
    public void compareOnlyWinExponentialMovingAverageWithNormalExponentialMovingAverage() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        boolean onlyWin;
        int[] indexes = StrategyTestHelper.getIndexes(500, maxIndex);
        for (int i = 1; i < 50; i++) {
            String message;
            if (i % 2 == 0) {
                indexes = StrategyTestHelper.getIndexes(500, maxIndex);
                onlyWin = true;
                message = "ExponentialMovingAverage str OnlyWin " + onlyWin + "  indexes from " + indexes[0] + " to " + indexes[1] + " ";
            } else {
                onlyWin = false;
                message = "ExponentialMovingAverage str OnlyWin " + onlyWin + " indexes from " + indexes[0] + " to " + indexes[1] + " ";
            }
            System.out.print(message);
            TestStrategyFactory strategyProvider = new TestStrategyFactory(testerAgent, false);
            strategyProvider.switchStrategy(strategyProvider.buildExponentialMovingAverageStrategy(30, 160, onlyWin));
            StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);
        }
    }

    @Test
    public void testExponentialMovingAverageStrategyWithPlot() throws InterruptedException {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        final Plotting plotting = new Plotting();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0), plotting);
        int[] indexes = StrategyTestHelper.getIndexes(4000, maxIndex); //{199, 699 }, {10557, 11057};// {9389, 9689};{12202 , 12502};//
        System.out.println("init " + indexes[0] + " end "  + indexes[1] + " total " + maxIndex);
        TestStrategyFactory strategyProvider = new TestStrategyFactory(testerAgent, false);
        strategyProvider.switchStrategy(strategyProvider.buildExponentialMovingAverageStrategy());
        StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider, plotting);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                new JFXPanel();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            plotting.start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        thread.start();
        Thread.sleep(1000000);
    }
}

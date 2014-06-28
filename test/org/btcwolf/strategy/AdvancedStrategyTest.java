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
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.helpers.MarketExchangeAgent;
import org.btcwolf.helpers.StrategyTestHelper;
import org.btcwolf.helpers.TestStrategyProvider;
import org.btcwolf.persistance.plot.Plotting;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

public class AdvancedStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
       //Logger.getRootLogger().removeAllAppenders();
    }

    @Test
    public void testAdvancedStrategy() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));

        for (int i = 0; i < 20; i++) {
            int[] indexes = StrategyTestHelper.getIndexes(maxIndex);
            TestStrategyProvider strategyProvider = new TestStrategyProvider(testerAgent, false);
            strategyProvider.switchToAdvancedStrategy();
            StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider);
        }
    }

    @Test
    public void testA() throws InterruptedException {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
        final Plotting plotting = new Plotting();
        int[] indexes = StrategyTestHelper.getIndexes(50, 1000);
        System.out.println("init " + indexes[0] + " end "  + indexes[1]);
        TestStrategyProvider strategyProvider = new TestStrategyProvider(testerAgent, false);
        strategyProvider.switchToAdvancedStrategy();
        StrategyTestHelper.runAdvancedStrategyTest(indexes, testerAgent, strategyProvider, plotting);


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                new JFXPanel(); // Initializes the JavaFx Platform

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            plotting.start(new Stage()); // Create and
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        thread.start();// Initialize the thread
        Thread.sleep(1000000); // Time to use the app, with out this, the thread
        // will be killed before you can tell.
    }



}

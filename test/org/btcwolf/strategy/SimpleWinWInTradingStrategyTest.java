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
import org.btcwolf.strategy.impl.SimpleWinWinTradingStrategy;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class SimpleWinWInTradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";
    private static final Logger logger = Logger.getLogger(SimpleWinWInTradingStrategyTest.class);
    private BigDecimal lastAsk;

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
        Logger.getRootLogger().removeAllAppenders();

    }

    @Test
    public void testWinWinStrategy() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();
        for (int turtleSpeed = 1; turtleSpeed < 30; turtleSpeed++) {
            int[] indexes = getIndexes(maxIndex);
            runSimpleWinWinTest(turtleSpeed, indexes);
        }
    }

    public void runSimpleWinWinTest(int turtleSpeed, int[] indexes) {

        //setup
        BigDecimal cny = BigDecimal.valueOf(0);
        BigDecimal btc = BigDecimal.valueOf(0.01);
        MarketExchangeAgent testerAgent = new MarketExchangeAgent(btc, cny);
        testerAgent.setDataRange(indexes);
        TradingStrategy testedStrategy = new SimpleWinWinTradingStrategy(testerAgent, BigDecimal.valueOf(turtleSpeed), false);

        //run
        runTest(testerAgent, testedStrategy);

        //validation
        BigDecimal finalMoney = testerAgent.getBitCoinBalance()
                .add(testerAgent.getCurrencyBalance().divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN));
        BigDecimal profit = finalMoney.subtract(btc);
        if (profit.compareTo(BigDecimal.ZERO) == 1) {
            System.out.print("OK ");
        } else {
            System.out.print("KOO ");
        }
        System.out.println("speed ["+ turtleSpeed +"] start money [" + String.format("%f.4", btc.doubleValue()) + "]" +
                " end money [" + String.format("%f.4", finalMoney.doubleValue()) + "][" +
                " profit [" + String.format("%f.4", profit) + "] [" +
                String.format("%f.1", finalMoney.divide(btc, 80, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100))) + "]%]" +
                " index [" + indexes[0] + "-" + indexes[1] + "]");
    }

    public int[] getIndexes(int max) {
        int amount = 1000;
        Random rand = new Random(System.nanoTime());
        int s = rand.nextInt(max);
        int f = rand.nextInt(max);
        while (s > f || f - s < amount ) {
            s = rand.nextInt(max);
            f = rand.nextInt(max);
        }
        return new int[]{s, f};
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

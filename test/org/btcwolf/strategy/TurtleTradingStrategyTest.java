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
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.agent.TraderAgent;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class TurtleTradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";
    private static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class);
    private BigDecimal lastAsk;

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
    }

    @Test
    public void testOptimalTurtleSpeed() {

        //setup
        BigDecimal cny = BigDecimal.valueOf(0);
        BigDecimal btc = BigDecimal.valueOf(0.01);
        int turtleSpeed = (4);
        TraderAgent testerAgent = new MarketExchangeAgent(btc, cny);

        TradingStrategy testedStrategy = new SimpleWinWinTradingStrategy(testerAgent);

        //run
        runTest(testerAgent, testedStrategy);

        //validation
        BigDecimal finalMoney = testerAgent.getBitCoinBalance()
                .add(testerAgent.getCurrencyBalance().divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN))
                .subtract(cny.divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN));
            System.out.println("start money [" + String.format("%f.4", btc.doubleValue()) + "]" +
                    " end money [" + String.format("%f.4", finalMoney.doubleValue()) + "][" +
                    " profit [" + String.format("%f.4", finalMoney.subtract(btc).doubleValue()) + "] [" +
                    String.format("%f.1", finalMoney.divide(btc, 40, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100))) + "]%]");
    }

    @Test
    public void testTurtleStrategy() {
        int maxIndex = new MarketExchangeAgent(BigDecimal.ZERO, BigDecimal.ZERO).getTickers();

        for (int turtleSpeed = 10; turtleSpeed < 30; turtleSpeed++) {
            int[] indexes = getIndexes(maxIndex);
            runTurtleTest(turtleSpeed, 1, indexes);
        }
    }
    public void runTurtleTest(int turtleSpeed, int percentageToSell, int[] indexes) {

        //setup
        BigDecimal cny = BigDecimal.valueOf(0);
        BigDecimal btc = BigDecimal.valueOf(0.01);
        TraderAgent testerAgent = new MarketExchangeAgent(btc, cny);

        TradingStrategy testedStrategy = new TurtleTradingStrategy(testerAgent, turtleSpeed);

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
        Assert.fail();
    }


    @Test
    public void TestTurtle() {

        for (int i = 4; i < 5; i++) {
            int turtleSpeed = i;
            System.out.println("tourtle " + i);
            System.out.println("===========================================================");
            int won = 0;
            for (int j = 0; j < 10; j++) {
                BigDecimal cny = BigDecimal.valueOf(0);
                BigDecimal btc = BigDecimal.valueOf(0.01);
                MarketExchangeAgent testerAgent = new MarketExchangeAgent(btc, cny);
                TradingStrategy testedStrategy = new TurtleTradingStrategy(testerAgent, turtleSpeed);
                int[] indexes = getIndexes(testerAgent.getTickers());
                testerAgent.setDataRange(indexes);
                //run
                runTest(testerAgent, testedStrategy);
                //validation
                BigDecimal finalMoney = testerAgent.getBitCoinBalance()
                        .subtract(btc)
                        .add(testerAgent.getCurrencyBalance().divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN))
                        .subtract(cny.divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN));
                if (finalMoney.compareTo(btc) == 1) {
                    System.out.println("OK range [" + turtleSpeed +
                                    "] profit [" + String.format("%f.4", finalMoney) + "]" + " [" +
                                    String.format("%f.1", finalMoney.divide(btc, 40, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100))) + "]%" +
                                    " index [" + indexes[0] + "-" + indexes[1] + "]"
                    );
                    won++;
                } else {
                    System.out.println("KOOOO range [" + turtleSpeed +
                            "] profit [" + String.format("%f.4", finalMoney) + "]" + " [" +
                            String.format("%f.1", finalMoney.divide(btc, 40, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100))) + "]%" +
                            " index [" + indexes[0] + "-" + indexes[1] + "]"
                    );
                }
            }
            System.out.println(" Won " + won + " out of " + i );
            System.out.println("===========================================================");
        }

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

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

import static java.math.BigDecimal.valueOf;

public class SliceWinWinTradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";
    private static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class);
    private BigDecimal firstBid;

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
    }

    @Test
    public void testSliceWinWinStrategy() {

        //data
        BigDecimal cnz = valueOf(40);
        BigDecimal btc = valueOf(0.01);
        BigDecimal maxProfit = valueOf(-1);
        BigDecimal bestOpAmount = valueOf(-1);
        BigDecimal bestOpThreshold = valueOf(-1);
        for (double i = 0.1; i < 20; i = i + 0.5) {
            BigDecimal current = null;
            for (double j = 0.001; j < 0.01; j = j + 0.001) {
                BigDecimal threshold = valueOf(i);
                BigDecimal opAmount = valueOf(j);
                current = runTest(threshold, cnz, btc, opAmount);
                if (current.compareTo(maxProfit) == 1) {
                    maxProfit = current;
                    bestOpAmount = opAmount;
                    bestOpThreshold = threshold;
                    System.out.println("profit " + current + " with opamount " + opAmount + " and th " + threshold + " is the new max " +maxProfit);

                } else {
                    System.out.println("profit " + current + " with opamount " + opAmount + " and th " + threshold + " smaller than " +maxProfit);
                }
            }
            System.out.println("best th " + bestOpThreshold + " best amount " + bestOpAmount + " profit " + current);
        }
        System.out.println("best th " + bestOpThreshold + " best amount " + bestOpAmount);
    }

    private BigDecimal runTest(BigDecimal threshold, BigDecimal cnz, BigDecimal btc, BigDecimal opAmount) {

        //setup
        TraderAgent testerAgent = new MarketExchangeAgent(btc, cnz);
        TradingStrategy testedStrategy = new SliceWinWinTradingStrategy(testerAgent, threshold, opAmount);

        //run
        runTest(testerAgent, testedStrategy);

        //validation
        //System.out.println("Op threshold[" + String.format("%.1f", threshold) +
        //"] CNY start[" +cnz + "] end [" + String.format("%.4f", cnz.subtract(testerAgent.getCurrencyBalance())) + "]"+
        //"] BTC start[" + btc + "] end [" + String.format("%.4f", testerAgent.getBitCoinBalance()) + "]");
        return firstBid.multiply(testerAgent.getBitCoinBalance()).add(testerAgent.getCurrencyBalance());
    }

    private void runTest(TraderAgent testerAgent, TradingStrategy testedStrategy) {
        //run
        Ticker ticker = testerAgent.pollTicker();
        firstBid = ticker.getBid();
        while(ticker != null) {
            testedStrategy.onTickerReceived(ticker);
            ticker = testerAgent.pollTicker();
        }
    }
}

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
import org.btcwolf.persistance.Serializer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TradingStrategyTest {

    private static final String LOG4J_PATH = "./resources/log4j.properties";
    private static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class);

    public List<Ticker> getTicker() {
        List<Ticker> list2 = null;
        try {
            list2 = Serializer.read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list2;
    }

    @BeforeClass
    public static void setup() {
        PropertyConfigurator.configure(LOG4J_PATH);
    }

    @Test
    public void testSliceWinWinStrategy() {

        //data
        BigDecimal threshold = BigDecimal.valueOf(1);
        BigDecimal cnz = BigDecimal.valueOf(4001);
        BigDecimal btc = BigDecimal.valueOf(1);
        BigDecimal opAmount = BigDecimal.valueOf(0.2);

        //setup
        TraderAgent testerAgent = new MarketExchangeAgent(btc, cnz);
        TradingStrategy testedStrategy = new SliceWinWinTradingStrategy(testerAgent, threshold, opAmount);

        //run
        runTest(testerAgent, testedStrategy);

        //validation
        System.out.println("Op threshold[" + String.format("%.1f", threshold) +
                "] CNY start[" +cnz + "] end [" + String.format("%.4f", cnz.subtract(testerAgent.getCurrencyBalance())) + "]"+
                "] BTC start[" + btc + "] end [" + String.format("%.4f", testerAgent.getBitCoinBalance()) + "]");
    }

    @Test
    public void testSimpleWinWinStrategy() {

        //data
        BigDecimal threshold = BigDecimal.valueOf(1);
        BigDecimal cnz = BigDecimal.valueOf(4001);
        BigDecimal btc = BigDecimal.valueOf(1);

        //setup
        TraderAgent testerAgent = new MarketExchangeAgent(btc, cnz);
        TradingStrategy testedStrategy = new SimpleWinWinTradingStrategy(testerAgent);

        //run
        runTest(testerAgent, testedStrategy);

        //validation
        System.out.println(
                "Op threshold[" + String.format("%.1f", threshold) +
                "] CNY start[" +cnz + "] end [" + String.format("%.4f", cnz.subtract(testerAgent.getCurrencyBalance())) + "]"+
                "] BTC start[" + btc + "] end [" + String.format("%.4f", testerAgent.getBitCoinBalance()) + "]");
    }

    private void runTest(TraderAgent testerAgent, TradingStrategy testedStrategy) {
        //run
        Ticker ticker = testerAgent.pollTicker();
        while(ticker != null) {
            testedStrategy.onTickerReceived(ticker);
            ticker = testerAgent.pollTicker();
        }
    }
}

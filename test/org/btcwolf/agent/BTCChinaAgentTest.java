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

package org.btcwolf.agent;

import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import org.apache.log4j.Logger;
import org.btcwolf.agent.impl.BTCChinaAgent;
import org.btcwolf.persistance.Serializer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xeiam.xchange.dto.Order.OrderType.ASK;

public class BTCChinaAgentTest {

    private static final long POLLING_TIME = 30000l;
    private static final Logger logger = Logger.getLogger(BTCChinaAgentTest.class);

    @Test
    public void simpleTest() throws IOException {
        BTCChinaAgent agent = new BTCChinaAgent();
        System.out.println(agent.getCurrencyPair().baseSymbol + " " + agent.getBitCoinBalance());
        System.out.println(agent.getCurrencyPair().counterSymbol + " " + agent.getCurrencyBalance());
        System.out.println("Open Orders " + agent.getOpenOrders().getOpenOrders().size());
        for (Trade trade : agent.getTrades().getTrades()) {
            System.out.println(trade);
        }
    }

    @Test
    public void testBugTrade() throws IOException {
        BTCChinaAgent agent = new BTCChinaAgent();
        agent.placeOrder(ASK, BigDecimal.ZERO,agent.pollTicker());
    }

    @Test
    public void serializeMoreTickers() throws FileNotFoundException {

        BTCChinaAgent agent = new BTCChinaAgent();

        java.util.List<Ticker> tickers = new ArrayList<Ticker>();

        for (int i = 0; i < 700; i++) {
            tickers.add(agent.pollTicker());
            try {
                Thread.sleep(POLLING_TIME);
            } catch (InterruptedException e) {
                //
            }
        }
        Serializer.write(tickers);
    }

    @Test
    public void testTradingRetrieval() throws IOException {
        BTCChinaAgent agent = new BTCChinaAgent();
        List<Trade> trades = agent.getTradeHistory(200).getTrades();
        int bids = 0; int asks = 0;
        for (Trade trade : trades) {
            System.out.println(trade);
            if (ASK == trade.getType()) {
                asks++;
            } else {
                bids++;
            }
        }
        System.out.println("Bids [" + bids + "] Asks [" + asks + "]");
    }

    @Test
    public void testLastOperationDate() throws IOException {
        BTCChinaAgent agent = new BTCChinaAgent();
        List<Trade> trades = agent.getTrades().getTrades();
        Trade lastTrade = trades.get(trades.size() - 1);
        Date now = new Date();
        int diff = (int) (now.getTime() - lastTrade.getTimestamp().getTime());
        System.out.println("Diff betwen " + now + " and "  + lastTrade.getTimestamp() + " is " +diff/1000/60/60 + " hours");
    }
}

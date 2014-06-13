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

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.Wallet;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.persistance.Serializer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;

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

    @Test
    public void testSliceWinWinStrategy() {

        //data
        BigDecimal threshold = BigDecimal.valueOf(1);
        BigDecimal cnz = BigDecimal.valueOf(4001);
        BigDecimal btc = BigDecimal.valueOf(1);

        //setup
        PropertyConfigurator.configure(LOG4J_PATH);
        TraderAgent testerAgent = new MyAgent(btc, cnz);
        TradingStrategy testedStrategy = new SliceWinWinTradingStrategy(testerAgent);

        //run
        Ticker ticker = testerAgent.pollTicker();
        while(ticker != null) {
            testedStrategy.onTickerReceived(ticker);
            ticker = testerAgent.pollTicker();
        }

        //validation
        System.out.println("Op threshold[" + String.format("%.1f", threshold) +
                "] CNY [" + String.format("%.4f", testerAgent.getCurrencyBalance()) + "]"+
                "] BTC [" + String.format("%.4f", testerAgent.getBitCoinBalance()) + "]");
    }

    @Test
    public void testSimpleWinWinStrategy() {

        //data
        BigDecimal threshold = BigDecimal.valueOf(1);
        BigDecimal cnz = BigDecimal.valueOf(4001);
        BigDecimal btc = BigDecimal.valueOf(1);

        //setup
        PropertyConfigurator.configure(LOG4J_PATH);
        TraderAgent testerAgent = new MyAgent(btc, cnz);
        TradingStrategy testedStrategy = new SimpleWinWinTradingStrategy(testerAgent);

        //run
        Ticker ticker = testerAgent.pollTicker();
        while(ticker != null) {
            testedStrategy.onTickerReceived(ticker);
            ticker = testerAgent.pollTicker();
        }

        //validation
        System.out.println("Op threshold[" + String.format("%.1f", threshold) +
                "] CNY [" + String.format("%.4f", testerAgent.getCurrencyBalance()) + "]"+
                "] BTC [" + String.format("%.4f", testerAgent.getBitCoinBalance()) + "]");
    }

    class MyAgent implements TraderAgent {

        private  final Logger LOGGER = Logger.getLogger(MyAgent.class);

        private final List<Ticker> data;
        private int index = 0;

        private BigDecimal mBitCoins;
        private BigDecimal mCurrency;

        public MyAgent(BigDecimal bitcoins, BigDecimal currency) {
            mBitCoins = bitcoins;
            mCurrency = currency;
            logger.info("init wallet of [" + mBitCoins + "]BTC and [" + mCurrency + "]CNY");

            try {
                this.data = Serializer.read();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Can not read xml file");
            }
        }

        @Override
        public Ticker pollTicker() {
            if (index >= data.size()) {
                return null;
            }
            logger.info("new ticker [" + data.get(index).toString() + "]");
            return data.get(index++);
        }

        @Override
        public String placeOrder(Order.OrderType orderType, BigDecimal amount, Ticker ticker) {
            logger.info("placed order [" + orderType + "] of [" + amount + "]");
            if (orderType == ASK) {
                if (amount.compareTo(mBitCoins) == 1) {
                    logger.info("no money to  [" + orderType + "] of [" + amount + "]");
                    return "KO";
                }mCurrency = mCurrency.add(amount.multiply(ticker.getAsk()));
                mBitCoins = mBitCoins.subtract(amount);
            } else if (orderType == BID) {
                if (amount.multiply(ticker.getBid()).compareTo(mCurrency) == 1) {
                    logger.info("no money to  [" + orderType + "] of [" + amount + "]");
                    return "KO";
                }
                mBitCoins = mBitCoins.add(amount);
                mCurrency = mCurrency.subtract(amount.multiply(ticker.getBid()));
            }
            logger.info("wallet of [" + mBitCoins + "]BTC and [" + mCurrency + "]CNY");
            return "ok";
        }

        @Override
        public List<Wallet> getWallets() {
            return null;
        }

        @Override
        public BigDecimal getCurrencyBalance() {
            return this.mCurrency;
        }

        @Override
        public BigDecimal getBitCoinBalance() {
            return this.mBitCoins;
        }

        @Override
        public OpenOrders getOpenOrders() {
            List orderList = new ArrayList(1);
            return new OpenOrders(orderList);
        }

        @Override
        public Trades getTrades() {
            return null;
        }

        @Override
        public CurrencyPair getCurrencyPair() {
            return null;
        }
    }

}

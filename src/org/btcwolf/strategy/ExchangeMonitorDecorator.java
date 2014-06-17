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

import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.LimitOrder;
import org.apache.log4j.Logger;
import org.btcwolf.twitter.TwitterAgent;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeMonitorDecorator implements TradingStrategy {

    private static final int POLLING_FREQ = 8;
    private static final Logger logger = Logger.getLogger(ExchangeMonitorDecorator.class);

    private static final TwitterAgent twitterAgent = new TwitterAgent();
    private AbstractTradingStrategy tradingStrategy;

    private int pollingCounter;

    public ExchangeMonitorDecorator(TradingStrategy tradingStrategy) {
        this.tradingStrategy = (AbstractTradingStrategy) tradingStrategy;
        this.pollingCounter = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        pollExchangeStatus(ticker);
        tradingStrategy.onTickerReceived(ticker);
    }

    void pollExchangeStatus(Ticker ticker) {
        tradingStrategy.logger.debug("\n\n New " + ticker);
        if (pollingCounter > POLLING_FREQ) {
            pollingCounter = 0;
            logStatus();
        }
        pollingCounter++;
    }

    void logStatus() {
        logger.debug(
                "BTC Balance[" + tradingStrategy.traderAgent.getBitCoinBalance() +
                "] CNY Balance[" + tradingStrategy.traderAgent.getCurrencyBalance() +
                "]" + " Open Orders[" + tradingStrategy.traderAgent.getOpenOrders().toString() + "].");
    }

    static void logOrder(BigDecimal bitCoinsToBuy, Order.OrderType orderType, String orderResult) {
         logger.info("Order " + orderType.toString() +" [ " + bitCoinsToBuy + "]CNY placed, result [" + orderResult + "]");
    }

    static void logNotASK(Ticker ticker, BigDecimal previousAskUsed, BigDecimal opCurrencyThreshold) {
         logger.debug("Prev price [" +
                String.format("%.2f", previousAskUsed) + "] new ASK[" +
                String.format("%.2f", ticker.getAsk()) +
                "] th[" + opCurrencyThreshold + "] nothing to do.");
    }

    static void logNotBID(Ticker ticker, BigDecimal previousBidUsed, BigDecimal opBitCoinThreshold) {
         logger.debug("Prev price [" +
                String.format("%.2f", previousBidUsed) + "] new BID[" +
                String.format("%.2f", ticker.getBid()) +
                "] th[" + opBitCoinThreshold + "] nothing to do.");
    }

     static void logOpenOrders(List<LimitOrder> openOrders) {
        for (LimitOrder order : openOrders) {
            logger.info("Noting to do, open order [" + order + "]");
        }
    }

    static void logASK(Ticker ticker, BigDecimal myBitCoins, BigDecimal previousAskUsed, BigDecimal priceDifference, BigDecimal opProfit) {
        log("Ordered ASK [" +
                String.format("%.5f", myBitCoins) + "]BTC for [" +
                String.format("%.1f", ticker.getAsk()) + "]. Expected. [" +
                String.format("%.1f", (myBitCoins.multiply(ticker.getAsk()))) + "]CNY. Last used [" +
                String.format("%.1f", previousAskUsed) + "]. Profit Rel[" +
                String.format("%.1f", priceDifference)+"]. Abs[" +
                String.format("%.4f", opProfit)+ "]CNY");
    }

    void logBID(Ticker ticker, BigDecimal bitCoinsToBuy, BigDecimal previousBidUsed, BigDecimal priceDifference, BigDecimal opProfit) {
        log("Ordered BID [" +
                String.format("%.5f", bitCoinsToBuy) + "]BTC for [" +
                String.format("%.1f", ticker.getBid()) + "]. Expected [" +
                String.format("%.1f", (bitCoinsToBuy.multiply(ticker.getBid()))) + "]CNY. Last used [" +
                String.format("%.1f", previousBidUsed) + "]. Profit Rel[" +
                String.format("%.2f", priceDifference) + "]. Abs[" +
                String.format("%.4f", opProfit) + "]CNY");
    }
    static void logOrder(Ticker ticker, BigDecimal amount, Order.OrderType orderType) {
        BigDecimal price = null;
        if (orderType == Order.OrderType.ASK) {
            price = ticker.getAsk();
        } else {
            price = ticker.getBid();
        }
        log("Ordered "+orderType.toString() +" [" +
                String.format("%.5f", amount) + "]BTC for [" +
                String.format("%.1f", price) + "].");
    }

    static void log(String message) {
        logger.info(message);
        twitterAgent.publish(message);
    }
}
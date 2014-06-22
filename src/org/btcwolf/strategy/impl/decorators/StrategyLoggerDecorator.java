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

package org.btcwolf.strategy.impl.decorators;

import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.LimitOrder;
import org.apache.log4j.Logger;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.impl.AbstractTradingStrategy;
import org.btcwolf.twitter.TwitterAgent;

import java.math.BigDecimal;
import java.util.List;

public abstract class StrategyLoggerDecorator extends AbstractTradingStrategy {

    private static final int POLLING_FREQ = 8;
    protected static final Logger logger = Logger.getLogger(StrategyLoggerDecorator.class);


    private TwitterAgent twitterAgent;
    private int pollingCounter;

    public StrategyLoggerDecorator(TraderAgent traderAgent, boolean useTwitterAgent ) {
        super(traderAgent);
        this.pollingCounter = 0;
        if (useTwitterAgent) {
            twitterAgent = new TwitterAgent();
        } else {
            twitterAgent = null;
        }
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        pollExchangeStatus(ticker);
    }

    void pollExchangeStatus(Ticker ticker) {
        logger.debug("\n\n New " + ticker);
        if (pollingCounter > POLLING_FREQ) {
            pollingCounter = 0;
            logStatus();
        }
        pollingCounter++;
    }

    void logStatus() {
        logger.debug(
                "BTC Balance[" + traderAgent.getBitCoinBalance() +
                        "] CNY Balance[" + traderAgent.getCurrencyBalance() +
                        "]" + " Open Orders[" + traderAgent.getOpenOrders().toString() + "].");
    }


    protected void logOrder(BigDecimal bitCoinsToBuy, Order.OrderType orderType, String orderResult) {
        logger.info("Ordered " + orderType.toString() + " [ " + bitCoinsToBuy + "]CNY, result [" + orderResult + "]CNY");
    }

    protected void logNotASK(Ticker ticker, BigDecimal previousAskUsed, BigDecimal opCurrencyThreshold) {
        logger.debug("Prev price [" +
                String.format("%.2f", previousAskUsed) + "] new ASK[" +
                String.format("%.2f", ticker.getAsk()) +
                "] th[" + opCurrencyThreshold + "] nothing to do.");
    }

    protected void logNotBID(Ticker ticker, BigDecimal previousBidUsed, BigDecimal opBitCoinThreshold) {
        logger.debug("Prev price [" +
                String.format("%.2f", previousBidUsed) + "] new BID[" +
                String.format("%.2f", ticker.getBid()) +
                "] th[" + opBitCoinThreshold + "] nothing to do.");
    }

    protected void logOpenOrders(List<LimitOrder> openOrders) {
        for (LimitOrder order : openOrders) {
            logger.info("Noting to do, open order [" + order + "]");
        }
    }

    protected void logASK(Ticker ticker, BigDecimal myBitCoins, BigDecimal previousAskUsed, BigDecimal priceDifference, BigDecimal opProfit) {
        log("Ordered ASK [" +
                String.format("%.5f", myBitCoins) + "]BTC for [" +
                String.format("%.1f", ticker.getAsk()) + "]. Expected. [" +
                String.format("%.1f", (myBitCoins.multiply(ticker.getAsk()))) + "]CNY. Last used [" +
                String.format("%.1f", previousAskUsed) + "]. Profit Rel[" +
                String.format("%.1f", priceDifference)+"]. Abs[" +
                String.format("%.4f", opProfit)+ "]CNY");
    }

    protected void logBID(Ticker ticker, BigDecimal bitCoinsToBuy, BigDecimal previousBidUsed, BigDecimal priceDifference, BigDecimal opProfit) {
        log("Ordered BID [" +
                String.format("%.5f", bitCoinsToBuy) + "]BTC for [" +
                String.format("%.1f", ticker.getBid()) + "]. Expected [" +
                String.format("%.1f", (bitCoinsToBuy.multiply(ticker.getBid()))) + "]CNY. Last used [" +
                String.format("%.1f", previousBidUsed) + "]. Profit Rel[" +
                String.format("%.2f", priceDifference) + "]. Abs[" +
                String.format("%.4f", opProfit) + "]CNY");
    }

    public  void logOrder(Ticker ticker, BigDecimal amount, Order.OrderType orderType) {
        BigDecimal price = null;
        if (orderType == Order.OrderType.ASK) {
            price = ticker.getAsk();
        } else {
            price = ticker.getBid();
        }
        log("Ordered "+orderType.toString() +" [" +
                String.format("%.5f", amount) + "]BTC for [" +
                String.format("%.1f", price) + "]CNY.");
    }

    void log(String message) {
        logger.info(message);
        if (twitterAgent != null) {
            twitterAgent.publish(message);
        }
    }
}

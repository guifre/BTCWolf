package org.btcwolf.strategy.impl.decorators;

import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.LimitOrder;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.impl.AbstractTradingStrategy;
import org.btcwolf.twitter.TwitterAgent;

import java.math.BigDecimal;
import java.util.List;

public abstract class StrategyLoggerDecorator extends AbstractTradingStrategy {

    TwitterAgent twitterAgent;

    public StrategyLoggerDecorator(TraderAgent traderAgent, boolean useTwitterAgent ) {
        super(traderAgent);
        if (useTwitterAgent) {
            twitterAgent = new TwitterAgent();
        }
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

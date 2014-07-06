package org.btcwolf.strategy.impl.decorators;

import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.log4j.Logger;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.TradingStrategyFactory;
import org.btcwolf.strategy.impl.TurtleTradingStrategy;

import java.math.BigDecimal;

public abstract class TradingStrategyMonitorDecorator extends StrategyLoggerDecorator {

    protected static final Logger logger = Logger.getLogger(TradingStrategyMonitorDecorator.class);

    private final int MARKET_TREND_OP = 4;
    private final TradingStrategyFactory tradingStrategyFactory;

    private int marketTrend;
    private BigDecimal myMoney;
    private BigDecimal lastUsedBid = BigDecimal.valueOf(0);

    public TradingStrategyMonitorDecorator(TradingStrategyFactory tradingStrategyFactory,
                                           TraderAgent traderAgent, boolean useTwitterAgent) {
        super(traderAgent, useTwitterAgent);
        this.tradingStrategyFactory = tradingStrategyFactory;
        this.marketTrend = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        super.onTickerReceived(ticker);
        if (!(this.tradingStrategyFactory.getTradingStrategy() instanceof TurtleTradingStrategy)) {
            return;
        }
        BigDecimal money = traderAgent.getBitCoinBalance().multiply(lastUsedBid).add(traderAgent.getCurrencyBalance());
        if (myMoney == null) {
            myMoney = money;
        } else if (myMoney.compareTo(money) != 0) {
            if (myMoney.compareTo(money) == 1) {
                marketTrend--;
            } else {
                marketTrend++;
            }
            myMoney = money;
            if (Math.abs(marketTrend) == MARKET_TREND_OP) {
                changeStrategy();
                marketTrend = 0;
            }
        }
    }

    @Override
    protected String placeOrder(Order.OrderType orderType, BigDecimal amount, Ticker ticker) {
        //lastUsedBid = ticker.getBid();
        String orderResult = super.placeOrder(orderType, amount, ticker);
        onOrdered(ticker, amount, orderType, orderResult);
        return orderResult;
    }

    private void changeStrategy() {
        if (tradingStrategyFactory.getTradingStrategy() instanceof TurtleTradingStrategy) {
            TurtleTradingStrategy strategy = (TurtleTradingStrategy) tradingStrategyFactory.getTradingStrategy();
             int newTurtleSpeed;
            int newOpAmount;
            if (marketTrend > 0) { //doing bad
                newTurtleSpeed = strategy.getTurtleSpeed() - 1;
                newOpAmount = strategy.getOpAmount() + 1 ;
            } else { //doing good
                newTurtleSpeed = strategy.getTurtleSpeed() + 1;
                newOpAmount = strategy.getOpAmount() - 1;
            }
            if (newOpAmount > 4) {
                newOpAmount = 4;
            }
            if (newOpAmount <= 0 ) {
                newOpAmount = 1;
            }
            if (newTurtleSpeed <= 1 ) {
                newTurtleSpeed = 2;
            }
            logger.info("Switching turtle strategy to speed [" + newTurtleSpeed + "] amount [" + newOpAmount);
            tradingStrategyFactory.switchStrategy(
                    tradingStrategyFactory.buildTurtleStrategy(
                            traderAgent,
                            newTurtleSpeed,
                            newOpAmount
                    ));
        }
    }
}

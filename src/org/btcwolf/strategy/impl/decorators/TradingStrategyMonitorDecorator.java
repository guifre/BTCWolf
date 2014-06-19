package org.btcwolf.strategy.impl.decorators;

import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.impl.decorators.StrategyLoggerDecorator;

import java.math.BigDecimal;

public abstract class TradingStrategyMonitorDecorator extends StrategyLoggerDecorator {


    public TradingStrategyMonitorDecorator(TraderAgent traderAgent, boolean useTwitterAgent) {
        super(traderAgent, useTwitterAgent);
    }

    @Override
    protected String placeOrder(Order.OrderType orderType, BigDecimal amount, Ticker ticker) {
        String orderResult = super.placeOrder(orderType, amount, ticker);
        onOrdered(ticker, amount, orderType, orderResult);
        return orderResult;
    }
}

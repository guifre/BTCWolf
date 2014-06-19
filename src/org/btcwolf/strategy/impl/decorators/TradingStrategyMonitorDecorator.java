package org.btcwolf.strategy.impl.decorators;

import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.TradingStrategyProvider;

import java.math.BigDecimal;

public abstract class TradingStrategyMonitorDecorator extends StrategyLoggerDecorator {

    private final int MAX_BAD_ORDERS = 4;
    private final TradingStrategyProvider tradingStrategyProvider;

    private int currentBadOrders;
    private BigDecimal myMoney;
    private BigDecimal lastUsedBid = BigDecimal.valueOf(0);

    public TradingStrategyMonitorDecorator(TradingStrategyProvider tradingStrategyProvider, TraderAgent traderAgent, boolean useTwitterAgent) {
        super(traderAgent, useTwitterAgent);
        this.tradingStrategyProvider = tradingStrategyProvider;
        this.currentBadOrders = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        super.onTickerReceived(ticker);
        BigDecimal money = traderAgent.getBitCoinBalance().multiply(lastUsedBid).add(traderAgent.getCurrencyBalance());
        if (myMoney == null) {
            myMoney = money;
        } else if (myMoney.compareTo(money) != 0) {
            //System.out.println("old [" + myMoney + "] is different to [" + money);
            if (myMoney.compareTo(money) == 1) {
                currentBadOrders++;
                if (currentBadOrders == MAX_BAD_ORDERS) {
                    //System.out.println("reached max bad orders switching strategy");
                    changeStrategy();
                }
            } else {
                currentBadOrders = 0;
            }
            myMoney = money;
        }
    }

    @Override
    protected String placeOrder(Order.OrderType orderType, BigDecimal amount, Ticker ticker) {
        lastUsedBid = ticker.getBid();
        String orderResult = super.placeOrder(orderType, amount, ticker);
        onOrdered(ticker, amount, orderType, orderResult);
        return orderResult;
    }

    private void changeStrategy() {
        tradingStrategyProvider.switchStrategy();
    }
}

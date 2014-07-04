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

package org.btcwolf.strategy.impl;

import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.TradingStrategyFactory;
import org.btcwolf.strategy.impl.decorators.TradingStrategyMonitorDecorator;

import java.math.BigDecimal;
import java.util.*;

import static com.xeiam.xchange.dto.Order.OrderType;
import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.valueOf;
import static org.btcwolf.agent.AbstractAgent.FAILED_ORDER;

public class ExponentialMovingAverageStrategy extends TradingStrategyMonitorDecorator {

    private static final int MAX_TICKERS = 60; //about 2h
    private static final int MIN_TICKERS = 30; //about 16 mins

    private static final int MIN_TICKERS_BETWEEN_ORDERS = 25;

    private static final BigDecimal MIN_DIFFERENCE_SHORT_LONG_EMA_TO_OP = valueOf(0.0002);

    private static final int CHECK_DEAD_ORDERS_FREQ = 10; // every 10 tickers check hard limits
    private static final int MAX_MINUTES_ORDER_TO_PROCESSED = 10;

    private static final double MIN_AVAILABLE_BTC_TO_OP = 0.001;
    private static final double MIN_AVAILABLE_CNY_TO_OP = 0.1;

    private static final int MAX_HISTORIC_SHORT_EMA = 5; // determines orderType
    private static final BigDecimal PLAIN_EMA_THRESHOLD = valueOf(0.05); // threshold for unchanged EMA

    private final Deque<Ticker> tickers;
    private final int minTickers;

    private final int maxTickers;
    private Ticker previousTicker;
    private int shortEMASize ; //;//elements to calculate the EMA short
    private BigDecimal shortEMA; //contains the value of the short EMA algorithm

    private BigDecimal expShortEMA;
    private final Deque<BigDecimal> historicShortEMA;

    private BigDecimal expLongEMA;
    private BigDecimal longEMA;

    private BigDecimal lastAsk;

    private BigDecimal lastBid;
    private boolean onlyWin; // enforces only orders that will generate profit

    private int time;
    private int lastOpTime;

    private int limitOrdersCount;

    public ExponentialMovingAverageStrategy(TradingStrategyFactory tradingStrategyFactory, TraderAgent traderAgent, boolean useTwitterAgent) {
        super(tradingStrategyFactory, traderAgent, useTwitterAgent);
        this.tickers = new ArrayDeque<Ticker>();
        this.historicShortEMA = new ArrayDeque<BigDecimal>();
        this.minTickers = MIN_TICKERS;
        this.maxTickers = MAX_TICKERS;
        this.shortEMASize = MIN_TICKERS + 1;
        this.onlyWin = false;
        this.time = 0;
        this.limitOrdersCount = 0;
    }

    public ExponentialMovingAverageStrategy(TradingStrategyFactory tradingStrategyFactory, TraderAgent traderAgent, boolean useTwitterAgent, int min, int max, boolean onlyWin) {
        super(tradingStrategyFactory, traderAgent, useTwitterAgent);
        this.tickers = new ArrayDeque<Ticker>();
        this.historicShortEMA = new ArrayDeque<BigDecimal>();
        this.minTickers = min;
        this.maxTickers = max;
        this.shortEMASize = minTickers + 1;
        this.onlyWin = onlyWin;
        this.time = 0;
        this.limitOrdersCount = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        super.onTickerReceived(ticker);
        checkDeadLimitOrders();
        lastOpTime++;
        time++;
        if (previousTicker == null) {
            initTickers(ticker);
        } else {
            addTicker(ticker);
            processTicker(ticker);
        }
        order(ticker);
    }

    public BigDecimal getShortEMA() {
        return shortEMA;
    }

    public BigDecimal getLongEMA() {
        return longEMA;
    }

    public int getTime() {
        return time;
    }

    private void order(Ticker ticker) {
        if (shortEMA == null || longEMA == null) {
            return;
        }
        if (shortEMA.subtract(longEMA).abs().compareTo(MIN_DIFFERENCE_SHORT_LONG_EMA_TO_OP) == 1
                && lastOpTime > MIN_TICKERS_BETWEEN_ORDERS) {

            lastOpTime = 0;
            OrderType type = getOrderType();
            BigDecimal amount;

            if (type == OrderType.BID
                    && (lastAsk == null || !onlyWin || lastAsk.compareTo(ticker.getBid()) == 1)
                    && traderAgent.getCurrencyBalance().compareTo(valueOf(MIN_AVAILABLE_CNY_TO_OP)) == 1) {

                amount = traderAgent.getCurrencyBalance().divide(ticker.getBid(), 40, ROUND_DOWN);
                logger.info("About to order BID price [" + ticker.getBid() + "] last ask was [" + lastBid + "] amount [" + amount + "]");
                lastBid = ticker.getBid();

            } else if (type == OrderType.ASK
                    && (lastBid == null || !onlyWin || lastBid.compareTo(ticker.getAsk()) == -1)
                    && traderAgent.getBitCoinBalance().compareTo(valueOf(MIN_AVAILABLE_BTC_TO_OP)) == 1) {

                amount = traderAgent.getBitCoinBalance();
                logger.info("About to order ASK price [" + ticker.getAsk() + "] last ask was [" + lastAsk + "] amount [" + amount + "]");
                lastAsk = ticker.getAsk();

            } else {
                return;
            }

            placeOrder(type, amount, ticker);
        }
    }

    private OrderType getOrderType() {
        int highers = 0;
        int lowers = 0;
        for (BigDecimal bigDecimal : historicShortEMA) {
            if (shortEMA.compareTo(bigDecimal) == 1) {
                lowers++;
            } else if (shortEMA.compareTo(bigDecimal) == -1) {
                highers++;
            }
        }
        if (isLinear() || isFakeTrend()) {
            return null;
        }
        if (highers > lowers) {
            return OrderType.ASK;
        } else if (lowers > highers){
            return OrderType.BID;
        }
        return null;
    }

    private void addTicker(Ticker ticker) {
        if (tickers.size() == maxTickers) {
            tickers.removeLast();
        }
        tickers.addFirst(ticker);
    }

    private void initTickers(Ticker ticker) {
        previousTicker = ticker;

        expShortEMA = valueOf((double) 2 / (shortEMASize + 1));

        expLongEMA = valueOf((double) 2 / (maxTickers + 1));

        longEMA = ticker.getLast();

        tickers.addFirst(ticker);
    }

    private void processTicker(Ticker ticker) {
        if (shortEMA == null) {
            shortEMA = ticker.getLast();
        } else {
            shortEMA = ticker.getLast().multiply(expShortEMA).add(shortEMA.multiply(valueOf(1).subtract(expShortEMA)));
            if (historicShortEMA.size() == MAX_HISTORIC_SHORT_EMA) {
                historicShortEMA.removeLast();
            }
            historicShortEMA.addFirst(shortEMA);
        }
        longEMA = ticker.getLast().multiply(expLongEMA).add(longEMA.multiply(valueOf(1).subtract(expLongEMA)));
        previousTicker = ticker;
    }

    @Override
    protected void onOrdered(Ticker ticker, BigDecimal bitCoinsToBuy, OrderType orderType, String orderResult) {
        if (!FAILED_ORDER.equals(orderResult)) {
            logOrder(ticker, bitCoinsToBuy, orderType);
        }
    }

    private void checkDeadLimitOrders() {
        if (limitOrdersCount < CHECK_DEAD_ORDERS_FREQ) {
            limitOrdersCount++;
            return;
        }
        limitOrdersCount = 0;
        OpenOrders openOrders = traderAgent.getOpenOrders();
        if (openOrders == null || openOrders.getOpenOrders() == null || openOrders.getOpenOrders().size() == 0) {
            return;
        }
        Date time = new Date();
        for (LimitOrder limitOrder : openOrders.getOpenOrders()) {
            int timeSincePlaced = (int) (time.getTime() - limitOrder.getTimestamp().getTime());
            int minutesSincePlacedLimit = timeSincePlaced / 60 / 1000;
            if (minutesSincePlacedLimit > MAX_MINUTES_ORDER_TO_PROCESSED) {
                boolean cancelled = traderAgent.cancelLimitOrder(limitOrder);
                logger.info("Limit placed [" + minutesSincePlacedLimit + "] mins ago, cancelled [" + cancelled + "] limit [" + limitOrder);
            } else {
                logger.debug("Limit placed [" + minutesSincePlacedLimit + "] mins ago, on time, limit [" + limitOrder);
            }
        }
    }

    private boolean isLinear() {
        int plain = 0;
        List<BigDecimal> list = new ArrayList<BigDecimal>(historicShortEMA);
        for (int i = 0; i < historicShortEMA.size() - 1; i++) {
            if (list.get(i).subtract(list.get(i + 1)).abs().compareTo(PLAIN_EMA_THRESHOLD) != 1) {
                plain++;
            }
        }
        if (plain > MAX_HISTORIC_SHORT_EMA / 2 + 1) {
            logger.info("plain price, not ordering");
            return true;
        }
        return false;
    }
    private boolean isFakeTrend() {
        //TODO I should test tickers from 12644 end 14144 total 31075  check that we dont op for fake trending changes
        return false;
    }
}
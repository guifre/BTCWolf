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
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.TradingStrategyProvider;
import org.btcwolf.strategy.impl.decorators.TradingStrategyMonitorDecorator;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.xeiam.xchange.dto.Order.OrderType;
import static java.math.BigDecimal.*;
import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.valueOf;
import static org.btcwolf.agent.AbstractAgent.FAILED_ORDER;

public class AdvancedStrategy extends TradingStrategyMonitorDecorator {

    private static final int MAX_TICKERS = 160; //about 2h
    private static final int MIN_TICKERS = 50; //about 16 mins

    private static final int MIN_TICKERS_BETWEEN_ORDERS = 1;
    private static final BigDecimal MIN_DIFFERENCE_SHORT_LONG_EMA_TO_OP = valueOf(0.2);

    private static final double MIN_AVAILABLE_BTC_TO_OP = 0.001;
    private static final double MIN_AVAILABLE_CNY_TO_OP = 0.1;

    private final Deque<Ticker> tickers;

    private final Deque<BigDecimal> historicShortEMA;
    private final int minTickers;
    private final int maxTickers;

    private Ticker previousTicker;

    private int shortEMASize ; //;//elements to calculate the EMA short
    private BigDecimal shortEMA; //contains the value of the short EMA algorithm
    private BigDecimal expShortEMA;

    private BigDecimal expLongEMA;
    private BigDecimal longEMA;

    private BigDecimal lastAsk;
    private BigDecimal lastBid;

    private boolean onlyWin = false;

    private int time;
    private int lastOpTime = 3;

    public AdvancedStrategy(TradingStrategyProvider tradingStrategyProvider, TraderAgent traderAgent, boolean useTwitterAgent) {
        super(tradingStrategyProvider, traderAgent, useTwitterAgent);
        this.tickers = new ArrayDeque<Ticker>();
        this.historicShortEMA = new ArrayDeque<BigDecimal>();
        this.time = 0;
        this.minTickers = MIN_TICKERS;
        this.maxTickers = MAX_TICKERS;
        this.shortEMASize = MIN_TICKERS + 1;
    }

    public AdvancedStrategy(TradingStrategyProvider tradingStrategyProvider, TraderAgent traderAgent, boolean useTwitterAgent, int min, int max, boolean onlyWin) {
        super(tradingStrategyProvider, traderAgent, useTwitterAgent);
        this.tickers = new ArrayDeque<Ticker>();
        this.historicShortEMA = new ArrayDeque<BigDecimal>();
        this.time = 0;
        this.minTickers = min;
        this.maxTickers = max;
        this.shortEMASize = minTickers + 1;
        this.onlyWin = onlyWin;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        super.onTickerReceived(ticker);
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

    private void order(Ticker ticker) {
        if (shortEMA == null || longEMA == null) {
            return;
        }
        if (shortEMA.subtract(longEMA).abs().compareTo(MIN_DIFFERENCE_SHORT_LONG_EMA_TO_OP) == 1
                &&lastOpTime > MIN_TICKERS_BETWEEN_ORDERS) {

            lastOpTime = 0;
            OrderType type = getOrderType();
            BigDecimal amount;

            if (type == OrderType.BID
                    && (lastAsk == null || !onlyWin || lastAsk.compareTo(ticker.getBid()) == 1)
                    && traderAgent.getCurrencyBalance().compareTo(valueOf(MIN_AVAILABLE_CNY_TO_OP)) == 1) {

                amount = traderAgent.getCurrencyBalance().divide(ticker.getBid(), 40, ROUND_DOWN);
                lastBid = ticker.getBid();

            } else if (type == OrderType.ASK
                    && (lastBid == null || !onlyWin || lastBid.compareTo(ticker.getAsk()) == -1)
                    && traderAgent.getBitCoinBalance().compareTo(valueOf(MIN_AVAILABLE_BTC_TO_OP)) == 1) {

                amount = traderAgent.getBitCoinBalance();
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
            if (historicShortEMA.size() == 5) {
                historicShortEMA.removeLast();
            }
            historicShortEMA.addFirst(shortEMA);
        }
        longEMA = ticker.getLast().multiply(expLongEMA).add(longEMA.multiply(valueOf(1).subtract(expLongEMA)));
        previousTicker = ticker;
    }

    public int getTime() {
        return time;
    }

    @Override
    protected void onOrdered(Ticker ticker, BigDecimal bitCoinsToBuy, OrderType orderType, String orderResult) {
        if (!FAILED_ORDER.equals(orderResult)) {
            logOrder(ticker, bitCoinsToBuy, orderType);
        }
    }
}
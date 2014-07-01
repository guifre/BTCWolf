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
import java.util.LinkedList;

import static com.xeiam.xchange.dto.Order.OrderType;
import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.valueOf;

public class AdvancedStrategy extends TradingStrategyMonitorDecorator {

    private static final int MAX_TICKERS = 400; //about 2h
    private static final int MIN_TICKERS = 82; //about 16 mins

    private final Deque<Ticker> tickers;

    private final Deque<BigDecimal> historicShortEMA;

    private Ticker previousTicker;

    private static final int shortEMASize = MIN_TICKERS + 1; //elements to calculate the EMA short
    private BigDecimal shortEMA; //contains the value of the short EMA algorithm
    private BigDecimal expShortEMA;

    private BigDecimal expLongEMA;
    private BigDecimal longEMA;

    private int time;

    private int lastOpTime = 15;

    public AdvancedStrategy(TradingStrategyProvider tradingStrategyProvider, TraderAgent traderAgent, boolean useTwitterAgent) {
        super(tradingStrategyProvider, traderAgent, useTwitterAgent);
        this.tickers = new ArrayDeque<Ticker>();
        this.historicShortEMA = new ArrayDeque<BigDecimal>();
        time = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        lastOpTime++;
        super.onTickerReceived(ticker);
        time++;
        if (previousTicker == null) {
            initTickers(ticker);
        } else {
            addTicker(ticker);
            processTicker(ticker);
        }
        printIndicators(ticker);
        newOrder(ticker);
    }

    private void newOrder(Ticker ticker) {
        if (shortEMA == null || longEMA == null) {
            return;
        }
        double res = shortEMA.subtract(longEMA).doubleValue();
        if (Math.abs(res) < 0.2) {
            if (lastOpTime > 15) {
                lastOpTime = 0;
                OrderType type = getOrderType();
//                System.out.println("about to order, price [" +ticker.getLast() + "] getOrderType["
//                        + getOrderType() + "] getOrderType[" + type + "] getVolDiff [" + getVolDiff() +
//                        "] getAskArrow[" + getAskArrow() + "] getBidArrow [" + getBidArrow() +
//                        "] getTrendArrow [" + getTrendArrow() + "]");
                BigDecimal amount;
                if (type == OrderType.BID) {
                    amount = traderAgent.getCurrencyBalance().divide(ticker.getBid(), 40, ROUND_DOWN).subtract(BigDecimal.valueOf(0.00001));
                } else if (type == OrderType.ASK) {
                    amount = traderAgent.getBitCoinBalance().subtract(BigDecimal.valueOf(0.00001));
                } else {
                    return;
                }
                placeOrder(type, amount, ticker);
            }
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
        if (tickers.size() == MAX_TICKERS) {
            Ticker oldTicker = tickers.removeLast();
            revertTickerInfo(oldTicker);
        }
        tickers.addFirst(ticker);
    }

    private void revertTickerInfo(Ticker oldTicker) {
        // new bid compare with previous determines bid arrow
//        if (oldTicker.getBid().compareTo(previousTicker.getBid()) == -1) {
//            bidArrow++;
//        } else if (oldTicker.getBid().compareTo(previousTicker.getBid()) == 1) {
//            bidArrow--;
//        }
//
//        // new ask compare with previous determines ask arrow
//        if (oldTicker.getAsk().compareTo(previousTicker.getAsk()) == -1) {
//            askArrow++;
//        } else if (oldTicker.getAsk().compareTo(previousTicker.getAsk()) == 1) {
//            askArrow--;
//        }
//
//        // new last compare with previous last trend arrow
//        if (oldTicker.getLast().compareTo(previousTicker.getLast()) == -1) {
//            trendArrow++;
//        } else if (oldTicker.getLast().compareTo(previousTicker.getLast()) ==1) {
//            trendArrow--;
//        }
        // new last compare with high and low updates it
    }

    public BigDecimal getShortEMA() {
        return shortEMA;
    }

    public BigDecimal getLongEMA() {
        return longEMA;
    }

    private void initTickers(Ticker ticker) {
        previousTicker = ticker;

        expShortEMA = BigDecimal.valueOf((double) 2 / (shortEMASize + 1));

        expLongEMA = BigDecimal.valueOf((double) 2 / (MAX_TICKERS + 1));

        longEMA = ticker.getLast();

        tickers.addFirst(ticker);
    }

    private void processTicker(Ticker ticker) {

        // new bid compare with previous determines bid arrow
//        if (ticker.getBid().compareTo(previousTicker.getBid()) == 1) {
//            bidArrow++;
//        } else if (ticker.getBid().compareTo(previousTicker.getBid()) == -1) {
//            bidArrow--;
//        }
//
//        // new ask compare with previous determines ask arrow
//        if (ticker.getAsk().compareTo(previousTicker.getAsk()) == 1) {
//            askArrow++;
//        } else if (ticker.getAsk().compareTo(previousTicker.getAsk()) == -1) {
//            askArrow--;
//        }
//
//        // new last compare with previous last trend arrow
//        if (ticker.getLast().compareTo(previousTicker.getLast()) == 1) {
//            trendArrow++;
//        } else if (ticker.getLast().compareTo(previousTicker.getLast()) == -1) {
//            trendArrow--;
//        }
//
//        // new last compare with high and low updates it
//        if (ticker.getLast().compareTo(highTicker.getLast()) == 1) {
//            highTicker = ticker;
//        } else if (ticker.getLast().compareTo(lowTicker.getLast()) == -1) {
//            lowTicker = ticker;
//        }

        //volDiff = ticker.getVolume().subtract(previousTicker.getVolume());
      //  vwap = vwap.add(ticker.getLast().multiply(volDiff));
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
        BigDecimal price;
        if (orderType == OrderType.BID) {
            price = ticker.getBid();
        } else {
            price = ticker.getAsk();
        }
        // System.out.println("order [" + orderType + "] price [" + price + "]  $[" + bitCoinsToBuy + "] result [" + orderResult + "]" );
    }

    private void printIndicators(Ticker ticker) {
//        System.out.println("New [" + ticker.toString() + "]");
//        System.out.println("bidA[" + bidArrow +  "] askA[" + askArrow + "] trendA[" +trendArrow +
//                "] h[" + String.format("%.2f", highTicker.getLast()) +
//                        "] l[" + String.format("%.2f", lowTicker.getLast()) +
//                        "] vDiff[" + String.format("%.2f", volDiff) +
//                        "] vwap[" + String.format("%.2f", vwap) +
//                        "] shortEMA[" + String.format("%.2f", shortEMA) +
//                        "] longEMA[" + String.format("%.2f", longEMA) +
//                "].\n"
//        );
    }
}

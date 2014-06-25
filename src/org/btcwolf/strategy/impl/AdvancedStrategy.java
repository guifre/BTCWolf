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
import java.util.LinkedList;

import static com.xeiam.xchange.dto.Order.OrderType;
import static java.math.BigDecimal.*;

public class AdvancedStrategy extends TradingStrategyMonitorDecorator {

    private static final int MAX_TICKERS = 240; //about 2h
    private static final int MIN_TICKERS = 32; //about 16 mins

    private final LinkedList<Ticker> tickers;

    private int trendArrow;
    private int bidArrow;
    private int askArrow;

    private BigDecimal volDiff;
    private BigDecimal vwap; //Volume Weighted Average Price

    private Ticker previousTicker;
    private Ticker highTicker;
    private Ticker lowTicker;

    private int asksInARow;
    private int bidsInARow;

    private static final int shortEMASize = MIN_TICKERS + 1; //elements to calculate the EMA short
    private BigDecimal shortEMA; //contains the value of the short EMA algorithm
    private BigDecimal expShortEMA;

    private BigDecimal expLongEMA;
    private BigDecimal longEMA;

    public AdvancedStrategy(TradingStrategyProvider tradingStrategyProvider, TraderAgent traderAgent, boolean useTwitterAgent) {
        super(tradingStrategyProvider, traderAgent, useTwitterAgent);
        this.tickers = new LinkedList<Ticker>();
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        super.onTickerReceived(ticker);
        if (previousTicker == null || highTicker == null || lowTicker == null) {
            initTickers(ticker);
        } else {
            addTicker(ticker);
            processTicker(ticker);
        }
//        System.out.println("New " + ticker.toString());
//        System.out.println("bidA[" + bidArrow +  "] askA[" + askArrow + "] trendA[" +trendArrow +
//                "] h[" + String.format("%.2f", highTicker.getLast()) +
//                        "] l[" + String.format("%.2f", lowTicker.getLast()) +
//                        "] vDiff[" + String.format("%.2f", volDiff) +
//                        "] vwap[" + String.format("%.2f", vwap) +
//                        "] shortEMA[" + String.format("%.2f", shortEMA) +
//                        "] longEMA[" + String.format("%.2f", longEMA) +
//                "].\n"
//        );
        if (tickers.size() >= MIN_TICKERS) {
            if (shouldAskEMA(ticker)) {
                placeOrder(OrderType.ASK, getAmountToAsk(), ticker);
            } else {
                askArrow = 0;
            }
            if (shouldBidEMA(ticker)) {
                placeOrder(OrderType.BID, getAmountToBid(), ticker);
            } else {
                bidsInARow = 0;
            }
        }
    }

    private void addTicker(Ticker ticker) {
        if (tickers.size() == MAX_TICKERS) {
            Ticker oldTicker = tickers.removeLast();
            revertTickerInfo(oldTicker);
        }
        tickers.addFirst(ticker);
    }

    private void revertTickerInfo(Ticker oldTicker) {
        // new bid compare with previous determines bidarrow
        if (oldTicker.getBid().compareTo(previousTicker.getBid()) == -1) {
            bidArrow++;
        } else if (oldTicker.getBid().compareTo(previousTicker.getBid()) == 1) {
            bidArrow--;
        }

        // new ask compare with previous determines askarrow
        if (oldTicker.getAsk().compareTo(previousTicker.getAsk()) == -1) {
            askArrow++;
        } else if (oldTicker.getAsk().compareTo(previousTicker.getAsk()) == 1) {
            askArrow--;
        }

        // new last compare with previous last trendarrow
        if (oldTicker.getLast().compareTo(previousTicker.getLast()) == -1) {
            trendArrow++;
        } else if (oldTicker.getLast().compareTo(previousTicker.getLast()) ==1) {
            trendArrow--;
        }

        // new last compare with high and low updates it
    }

    private void initTickers(Ticker ticker) {
        previousTicker = ticker;
        lowTicker = ticker;
        highTicker = ticker;
        trendArrow = 0;
        askArrow = 0;
        bidArrow = 0;
        asksInARow = 0;
        bidsInARow = 0;
        vwap = valueOf(0);

        expShortEMA = BigDecimal.valueOf((double) 2 / (shortEMASize + 1));

        expLongEMA = BigDecimal.valueOf((double) 2 / (MAX_TICKERS + 1));
        longEMA = ticker.getLast();

        tickers.addFirst(ticker);
    }

    private void processTicker(Ticker ticker) {

        // new bid compare with previous determines bidarrow
        if (ticker.getBid().compareTo(previousTicker.getBid()) == 1) {
            bidArrow++;
        } else if (ticker.getBid().compareTo(previousTicker.getBid()) == -1) {
            bidArrow--;
        }

        // new ask compare with previous determines askarrow
        if (ticker.getAsk().compareTo(previousTicker.getAsk()) == 1) {
            askArrow++;
        } else if (ticker.getAsk().compareTo(previousTicker.getAsk()) == -1) {
            askArrow--;
        }

        // new last compare with previous last trendarrow
        if (ticker.getLast().compareTo(previousTicker.getLast()) == 1) {
            trendArrow++;
        } else if (ticker.getLast().compareTo(previousTicker.getLast()) == -1) {
            trendArrow--;
        }

        // new last compare with high and low updates it
        if (ticker.getLast().compareTo(highTicker.getLast()) == 1) {
            highTicker = ticker;
        } else if (ticker.getLast().compareTo(lowTicker.getLast()) == -1) {
            lowTicker = ticker;
        }

        volDiff = ticker.getVolume().subtract(previousTicker.getVolume());
        vwap = vwap.add(ticker.getLast().multiply(volDiff));
        if (shortEMA == null) {
            shortEMA = ticker.getLast();
        } else {
            if (tickers.size() > shortEMASize) {

            }
            shortEMA = ticker.getLast().multiply(expShortEMA).add(shortEMA.multiply(valueOf(1).subtract(expShortEMA)));
        }

        longEMA = ticker.getLast().multiply(expLongEMA).add(longEMA.multiply(valueOf(1).subtract(expLongEMA)));

        previousTicker = ticker;
    }

    private OrderType getOrderType() { // Advance/Decline Spread to decide trade action
        if (trendArrow > 0 && bidArrow > 0) {
            return OrderType.BID;   // market trending down
        } else if(trendArrow < 0 && askArrow < 0 ){
            return OrderType.ASK;   // market trending up
        }
        return null;    //market trending flat
    }

    private int getVWAPCrossTrend(Ticker ticker) {
        if (ticker.getBid().compareTo(vwap) == 1) {
            return 1;   // Current bid prices above the VWAP
         } else if (ticker.getAsk().compareTo(vwap) == -1) {
            return -1;   // Current ask price is below the VWAP
        }
        return 0;       //flat
    }

    @Override
    protected void onOrdered(Ticker ticker, BigDecimal bitCoinsToBuy, OrderType orderType, String orderResult) {

    }

    private boolean shouldAskEMA(Ticker ticker) { // if short ema is lower than long ema, market trending down, buy btc
        return shortEMA.compareTo(longEMA) == -1 && ticker.getLast().compareTo(vwap) == 1;
    }

    private boolean shouldBidEMA(Ticker ticker) { // if short ema is higher than long ema, market trending up, sell btc
        return shortEMA.compareTo(longEMA) == 1 && ticker.getLast().compareTo(vwap) == -1;
    }
    private BigDecimal getAmountToAsk() {
        double weight = (double)(bidArrow + trendArrow) / tickers.size(); // low risk askArrow / tickerSize * (double)trendArrow / tickerSize
        double bigWeight = Math.abs(weight) / Math.pow(2, asksInARow);
        askArrow++;
        BigDecimal quantityToSell = traderAgent.getCurrencyBalance().multiply(valueOf(bigWeight));
        //System.out.println("ask ["+ bidArrow +"] trend [" + trendArrow + "] weight [" + weight + "] bigweight [" + bigWeight +"] balance [" + traderAgent.getCurrencyBalance() + "] About to ask [" + quantityToSell + "]");
        //System.out.println("About to ask [" + quantityToSell + "]");
        return quantityToSell;
    }

    private BigDecimal getAmountToBid() {
        double weight = (double)(askArrow + trendArrow) / tickers.size(); // low risk askArrow / tickerSize * (double)trendArrow / tickerSize
        double bigWeight = Math.abs(weight) / Math.pow(2, bidsInARow);
        bidsInARow++;
        BigDecimal quantityToBuy = traderAgent.getCurrencyBalance().multiply(valueOf(bigWeight));
        //System.out.println("weight [" + weight + "] bigweight [" + bigWeight +"] balance [" + traderAgent.getCurrencyBalance() + "] About to bid [" + quantityToBuy + "]");
        //System.out.println("About to ask [" + quantityToBuy + "]");
        return quantityToBuy;
    }

}

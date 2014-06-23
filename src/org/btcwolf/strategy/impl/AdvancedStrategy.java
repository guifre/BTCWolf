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

import static com.xeiam.xchange.dto.Order.OrderType;
import static java.math.BigDecimal.*;

public class AdvancedStrategy extends TradingStrategyMonitorDecorator {

    private static final int MAX_TICKERS = 100;

    private int currentTickers;

    private int trendArrow;
    private int bidArrow;
    private int askArrow;

    private BigDecimal volDiff;
    private BigDecimal vwap; //Volume Weighted Average Price

    private Ticker previousTicker;
    private Ticker highTicker;
    private Ticker lowTicker;


    public AdvancedStrategy(TradingStrategyProvider tradingStrategyProvider, TraderAgent traderAgent, boolean useTwitterAgent) {
        super(tradingStrategyProvider, traderAgent, useTwitterAgent);
    }


    @Override
    public void onTickerReceived(Ticker ticker) {
        super.onTickerReceived(ticker);
        if (previousTicker == null || highTicker == null || lowTicker == null || currentTickers == MAX_TICKERS) {
            initTickers(ticker);
        } else {
            processTicker(ticker);
        }
        System.out.println("New " + ticker.toString());
        System.out.println("bidA[" + bidArrow +
                "] askA[" + askArrow +
                "] trendA[" +trendArrow +
                "] h[" + String.format("%.2f", highTicker.getLast()) +
                "] l[" + String.format("%.2f", lowTicker.getLast()) +
                        "] vDiff[" + String.format("%.2f", volDiff) +
                        "] vwap[" + String.format("%.2f", vwap) +
                "].\n"
        );
    }

    private void initTickers(Ticker ticker) {
        previousTicker = ticker;
        lowTicker = ticker;
        highTicker = ticker;
        currentTickers = 0;
        trendArrow = 0;
        askArrow = 0;
        bidArrow = 0;
        vwap = valueOf(0);
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

        previousTicker = ticker;
        currentTickers++;
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
            return 1;   // Current bid priceis above the VWAP
         } else if (ticker.getAsk().compareTo(vwap) == -1) {
            return -1;   // Current ask price is below the VWAP
        }
        return 0;       //flat
    }

    @Override
    protected void onOrdered(Ticker ticker, BigDecimal bitCoinsToBuy, OrderType orderType, String orderResult) {

    }
}

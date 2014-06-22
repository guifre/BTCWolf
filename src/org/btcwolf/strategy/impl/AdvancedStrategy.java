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

public class AdvancedStrategy extends TradingStrategyMonitorDecorator {

    //read 1h of data (100 ticks)

    //60 were up and 40 were down the trendarrow will be +20 (the market is now trending up)
    // 75 instances where the bidArrow went up and 25 where it went down, the bidArrow is now +50
    // If the last trade was 10.25 and the VWAP is 10.20;
    // the Volume articipation algorithm (VWAP Cross) has determined that now is a good time to sell because there is enormous pressure in the market to buy.


    private int trendArrow;
    private int bidArrow;
    private int askArrow;
    private BigDecimal volDiff;

    // how much do we sell ? total/number of tickers monitoring,
    private Ticker previousTicker;
    private Ticker highTicker;
    private Ticker lowTicker;

    private final int minOP = 4;
    private final int maxOp = 8;
    private int curOp = 0;
    public AdvancedStrategy(TradingStrategyProvider tradingStrategyProvider, TraderAgent traderAgent, boolean useTwitterAgent) {
        super(tradingStrategyProvider, traderAgent, useTwitterAgent);
    }


    @Override
    public void onTickerReceived(Ticker ticker) {
        super.onTickerReceived(ticker);
        if (previousTicker == null || highTicker == null || lowTicker == null || curOp == maxOp) {
            initTickers(ticker);
            curOp = 0;
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
                "].\n"
        );

        if (curOp >= minOP) {
            if (askArrow >= 4) {
                //sell

                placeOrder(OrderType.ASK, traderAgent.getBitCoinBalance(), ticker);
            } else if (bidArrow <= -4) {
                //buy
                placeOrder(OrderType.BID, traderAgent.getBitCoinBalance(), ticker);
            }
        }
    }

    private void initTickers(Ticker ticker) {
        previousTicker = ticker;
        lowTicker = ticker;
        highTicker = ticker;
        trendArrow = 0;
        askArrow = 0;
        bidArrow = 0;
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
        previousTicker = ticker;
        curOp++;
    }

    @Override
    protected void onOrdered(Ticker ticker, BigDecimal bitCoinsToBuy, OrderType orderType, String orderResult) {

    }
}

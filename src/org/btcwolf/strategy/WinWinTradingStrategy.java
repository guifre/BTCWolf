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

package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import org.btcwolf.agent.TraderAgent;

import java.math.BigDecimal;
import java.util.Date;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.*;

public class WinWinTradingStrategy extends AbstractTradingStrategy {

    private static final int MAX_NON_OP_TIME = 5; //hours
    private static final BigDecimal opThreshold = BigDecimal.valueOf(2);

    private BigDecimal bitCoinsToSell = BigDecimal.valueOf(0d);
    private BigDecimal bitCoinsToBuy = BigDecimal.valueOf(0d);

    private BigDecimal previousPriceUsed;

    public WinWinTradingStrategy(TraderAgent traderAgent) {
        super(traderAgent);
        processHistoricOrders();
    }

    @Override
    BigDecimal getBitCoinsToSell() {
        return bitCoinsToSell;
    }

    @Override
    BigDecimal getBitCoinsToBuy() {
        return bitCoinsToBuy;
    }

    @Override
    void analyzeTicker(Ticker ticker) {
        if (previousPriceUsed == null) {
            previousPriceUsed = ticker.getAsk();
            logger.info("No older orders, setting prev Op[" + previousPriceUsed);
            return;
        }
        bitCoinsToBuy = ZERO;
        bitCoinsToSell = ZERO;
        if (traderAgent.getOpenOrders().getOpenOrders().size() > 0) {
            logOpenOrders(traderAgent.getOpenOrders().getOpenOrders());
        } else {
            computeWorthinessBuyingBitCoins(ticker);
            computeWorthinessSellingBitCoins(ticker);
        }
    }

    private void computeWorthinessSellingBitCoins(Ticker ticker) {

        BigDecimal myBitCoins = this.traderAgent.getBitCoinBalance();
        if (traderAgent.getCurrencyBalance().compareTo(myBitCoins.multiply(previousPriceUsed)) == 1) {
            return;
        }
        if ((ticker.getAsk().compareTo(previousPriceUsed.add(opThreshold)) == 1 && myBitCoins.compareTo(ZERO) == 1) ||
                lostTheTrend()) {
            // new ask higher than the last one plus the threshold and be have money

            BigDecimal priceDifference = ticker.getAsk().subtract(previousPriceUsed);
            BigDecimal opProfit = priceDifference.multiply(myBitCoins);
            bitCoinsToSell = myBitCoins;
            logASK(ticker, myBitCoins, previousPriceUsed, priceDifference, opProfit);
            previousPriceUsed = ticker.getAsk();
        } else {
            logNotASK(ticker, previousPriceUsed, opThreshold);
        }
    }


    private void computeWorthinessBuyingBitCoins(Ticker ticker) {

        BigDecimal myCurrency = traderAgent.getCurrencyBalance();
        if (traderAgent.getBitCoinBalance().multiply(previousPriceUsed).compareTo(myCurrency) == 1) {
            return;
        }

        BigDecimal priceDifference = previousPriceUsed.subtract(ticker.getBid());
        if (previousPriceUsed.add(opThreshold).compareTo(ticker.getBid()) == 1 && myCurrency.compareTo(ZERO) == 1 ||
                lostTheTrend()) {
             // old price plus threshold is higher than the bid one, and be have money

            BigDecimal opProfit = priceDifference.multiply(myCurrency);
            bitCoinsToBuy = myCurrency.divide(ticker.getBid(), 40, HALF_EVEN);

            logBID(ticker, myCurrency, bitCoinsToBuy, previousPriceUsed, priceDifference, opProfit);

            previousPriceUsed = ticker.getBid();
        } else {
            logNotBID(ticker, previousPriceUsed, opThreshold);
        }
    }

    private void processHistoricOrders() {
        Trades trades = traderAgent.getTrades();
        if (trades == null || trades.getTrades() == null || trades.getTrades().isEmpty()) {
            logger.info("empty historic, waiting for next ticker.");
        } else {
            Trade lastTrade = trades.getTrades().get(trades.getTrades().size() -1 );
            previousPriceUsed = lastTrade.getPrice();
            logger.info("Using last trade price [" + previousPriceUsed + "]");
        }
    }

    private boolean lostTheTrend() {

        Trades trades = traderAgent.getTrades();
        if (trades == null || trades.getTrades() == null || trades.getTrades().isEmpty()) {
            return false;
        }
        Trade lastTrade = trades.getTrades().get(trades.getTrades().size() -1);
        int timeSinceLastOp = (int) (new Date().getTime() - lastTrade.getTimestamp().getTime());
        int timeSinceLastOpInHours = timeSinceLastOp/60/60/1000;
        logger.info("Time since last order [" + timeSinceLastOpInHours + "] hours");
        return  timeSinceLastOpInHours > MAX_NON_OP_TIME;
    }


}

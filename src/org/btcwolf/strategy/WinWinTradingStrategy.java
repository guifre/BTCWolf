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
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.LimitOrder;
import org.btcwolf.agent.TraderAgent;

import java.math.BigDecimal;

import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static java.math.BigDecimal.ZERO;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.logASK;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.logBID;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.logNotBID;

public class WinWinTradingStrategy extends AbstractTradingStrategy {

    private final BigDecimal opBitCoinThreshold;
    private final BigDecimal opCurrencyThreshold;

    private BigDecimal bitCoinsToSell = BigDecimal.valueOf(0d);
    private BigDecimal bitCoinsToBuy = BigDecimal.valueOf(0d);

    private BigDecimal previousAskUsed;
    private BigDecimal previousBidUsed;

    public WinWinTradingStrategy(TraderAgent traderAgent, BigDecimal opBitCoinThreshold, BigDecimal opCurrencyThreshold) {
        super(traderAgent);
        this.opBitCoinThreshold = opBitCoinThreshold;
        this.opCurrencyThreshold = opCurrencyThreshold;
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
        if (previousAskUsed == null || previousBidUsed == null) {
            previousAskUsed = ticker.getAsk();
            previousBidUsed = ticker.getBid();
            logger.info("No older orders, setting prev BID[" + previousBidUsed + "] ASK [" + previousAskUsed + "]");
            return;
        }
        bitCoinsToBuy = ZERO;
        bitCoinsToSell = ZERO;
        if (traderAgent.getOpenOrders().getOpenOrders().size() > 0) {
            for (LimitOrder order : traderAgent.getOpenOrders().getOpenOrders()) {
                logger.info("Open Order " + order.getTradableAmount());
            }
        } else {
            computeWorthinessBuyingBitCoins(ticker);
            computeWorthinessSellingBitCoins(ticker);
        }
    }

    private void computeWorthinessSellingBitCoins(Ticker ticker) {

        BigDecimal myBitCoins = this.traderAgent.getBitCoinBalance();
        BigDecimal priceDifference = ticker.getAsk().subtract(previousAskUsed);
        if (ticker.getAsk().compareTo(previousAskUsed.add(opCurrencyThreshold)) == 1 &&
                myBitCoins.compareTo(ZERO) == 1) { // new ask higher than the last one plus the threshold and be have money

            BigDecimal opProfit = priceDifference.multiply(myBitCoins);
            bitCoinsToSell = myBitCoins.multiply(ticker.getAsk());

            logASK(ticker, myBitCoins, previousAskUsed, priceDifference, opProfit);

            previousAskUsed = ticker.getAsk();
            previousBidUsed = ticker.getBid();
        } else {
            logNotASK(ticker, previousAskUsed, opCurrencyThreshold);
        }
    }

    private void logNotASK(Ticker ticker, BigDecimal previousAskUsed, BigDecimal opCurrencyThreshold) {
    }

    private void computeWorthinessBuyingBitCoins(Ticker ticker) {

        BigDecimal myCurrency = this.traderAgent.getCurrencyBalance();
        BigDecimal priceDifference = ticker.getBid().subtract(previousBidUsed);

        if ( (previousBidUsed.add(opBitCoinThreshold)).compareTo(ticker.getBid()) == 1 &&
                myCurrency.compareTo(ZERO) == 1) { // new bid is lower than the last one plus the threshold and be have money

            BigDecimal opProfit = priceDifference.multiply(myCurrency);
            bitCoinsToBuy = myCurrency.multiply(ticker.getBid());

            logBID(ticker, myCurrency, bitCoinsToBuy, previousBidUsed, priceDifference, opProfit);

            previousAskUsed = ticker.getAsk();
            previousBidUsed = ticker.getBid();
        } else {
            logNotBID(ticker, previousBidUsed, opBitCoinThreshold);
        }
    }

    private void processHistoricOrders() {
        Trades trades = this.traderAgent.getTrades();
        if (trades.getTrades() == null || trades.getTrades().isEmpty()) {
            logger.info("empty historic, waiting for next ticker.");
        } else {
            int i = trades.getTrades().size() - 1;
            while (previousAskUsed == null && previousBidUsed == null && i >= 0) {

                if (ASK.equals(trades.getTrades().get(i).getType()) && previousAskUsed == null) {
                    previousAskUsed = trades.getTrades().get(i).getPrice();
                    logger.info("setting previous ask to [" + previousAskUsed + "]");
                } else if (BID.equals(trades.getTrades().get(i).getType()) && previousBidUsed == null) {
                    previousBidUsed = trades.getTrades().get(i).getPrice();
                    logger.info("setting previous bid to [" + previousBidUsed + "]");
                }
                i--;
            }
        }
    }


}

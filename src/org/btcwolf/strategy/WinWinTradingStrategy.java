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

import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.LimitOrder;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.twitter.TwitterAgent;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class WinWinTradingStrategy extends AbstractTradingStrategy {

    private final BigDecimal opBitCoinThreshold;
    private final BigDecimal opCurrencyThreshold;

    private BigDecimal bitCoinsToSell = BigDecimal.valueOf(0d);
    private BigDecimal bitCoinsToBuy = BigDecimal.valueOf(0d);

    private BigDecimal previousAskUsed;
    private BigDecimal previousBidUsed;

    public WinWinTradingStrategy(TraderAgent traderAgent, TwitterAgent twitterAgent, BigDecimal opBitCoinThreshold, BigDecimal opCurrencyThreshold) {
        super(traderAgent, twitterAgent);
        this.opBitCoinThreshold = opBitCoinThreshold;
        this.opCurrencyThreshold = opCurrencyThreshold;
        processOrders();
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
        BigDecimal priceDifference = previousAskUsed.subtract(ticker.getAsk());
        if (ticker.getAsk().compareTo(previousAskUsed.add(opCurrencyThreshold)) == 1 &&
                myBitCoins.compareTo(ZERO) == 1) { // new ask higher than the last one plus the threshold and be have money

            BigDecimal opProfit = priceDifference.multiply(myBitCoins);
            bitCoinsToSell = myBitCoins.multiply(ticker.getAsk());
            totalProfit = totalProfit.add(priceDifference);

            log("Placed order ASK [" + String.format("%.5f", myBitCoins) + "]BTC to CNY for [" + String.format("%.1f", ticker.getAsk()) +
                    "]. Last used [" + String.format("%.1f", previousAskUsed) + "]. Profit %[" +
                    String.format("%.1f", priceDifference)+"]. Net[" + String.format("%.4f", opProfit)+ "]");

            previousAskUsed = ticker.getAsk();
            previousBidUsed = ticker.getBid();
        }
    }

    private void computeWorthinessBuyingBitCoins(Ticker ticker) {

        BigDecimal myCurrency = this.traderAgent.getCurrencyBalance();
        BigDecimal priceDifference = previousBidUsed.subtract(ticker.getBid());

        if ( (previousBidUsed.add(opBitCoinThreshold)).compareTo(ticker.getBid()) == 1 &&
                myCurrency.compareTo(ZERO) == 1) { // new bid is lower than the last one plus the threshold and be have money

            BigDecimal opProfit = priceDifference.multiply(myCurrency);
            bitCoinsToBuy = myCurrency.multiply(ticker.getBid());
            totalProfit = totalProfit.add(priceDifference);

            log("Placed order of BID [" + String.format("%.1f",myCurrency) + "]CNY to [" + String.format("%.5f",bitCoinsToBuy) +
                    "BTC for [" + String.format("%.1f", ticker.getBid()) + "]. Last used [" + String.format("%.1f", previousBidUsed) +
                    "]. Profit %[" + String.format("%.2f", priceDifference) + "]. Net[" + String.format("%.4f", (opProfit)) + "]");

            previousAskUsed = ticker.getAsk();
            previousBidUsed = ticker.getBid();
        }
    }

    private void processOrders() {
        OrderBook orders = this.traderAgent.getOrderBook();
        if (!orders.getAsks().isEmpty()) {
            previousAskUsed = orders.getAsks().get(orders.getAsks().size()-1).getLimitPrice();
        } else {
            previousAskUsed = ZERO;
        }
        if (!orders.getAsks().isEmpty()) {
            previousBidUsed = orders.getBids().get(orders.getAsks().size()-1).getLimitPrice();
        } else {
            previousBidUsed = ZERO;
        }
    }

    private void log(String message) {
        logger.info(message);
        twitterAgent.publish(message);
    }
}

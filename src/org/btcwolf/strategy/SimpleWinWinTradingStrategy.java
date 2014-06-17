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
import org.btcwolf.persistance.SettingsProvider;

import java.math.BigDecimal;
import java.util.Date;

import static com.xeiam.xchange.dto.Order.OrderType;
import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.ZERO;
import static org.btcwolf.agent.AbstractAgent.FAILED_ORDER;

public class SimpleWinWinTradingStrategy extends AbstractTradingStrategy {

    private static final boolean MIN_OP_TIME = false;
    private static final int MAX_NON_OP_TIME = 5; //hours

    private static final BigDecimal DEFAULT_OP_THRESHOLD = BigDecimal.valueOf(2);
    private static final String OP_THRESHOLD_ENV = "OP_THRESHOLD";

    private BigDecimal opThreshold;
    private BigDecimal previousPriceUsed;

    public SimpleWinWinTradingStrategy(TraderAgent traderAgent) {
        super(traderAgent);
        getThreshold();
        processHistoricOrders();
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        if (previousPriceUsed == null) {
            previousPriceUsed = ticker.getAsk();
            logger.info("No older orders, setting prev Op[" + previousPriceUsed + "]");
            return;
        }
        if (traderAgent.getOpenOrders().getOpenOrders().size() > 0) {
            new ExchangeMonitorDecorator(this).logOpenOrders(traderAgent.getOpenOrders().getOpenOrders());
        } else {
            checkIfProfitableBIDAndCarryOn(ticker);
            checkIfProfitableASKAndCarryOn(ticker);
        }
    }

    void onOrdered(Ticker ticker, BigDecimal amount, OrderType orderType, String orderResult) {

        if (!FAILED_ORDER.equals(orderResult)) {
            new ExchangeMonitorDecorator(this).logOrder(amount, orderType, orderResult);

            if (BID == orderType) {
                BigDecimal priceDifference = previousPriceUsed.subtract(ticker.getBid());
                BigDecimal opProfit = priceDifference.multiply(amount);
                new ExchangeMonitorDecorator(this).logBID(ticker, amount, previousPriceUsed, priceDifference, opProfit);
                previousPriceUsed = ticker.getBid();

            } else if (ASK == orderType) {
                BigDecimal priceDifference = ticker.getAsk().subtract(previousPriceUsed);
                BigDecimal opProfit = priceDifference.multiply(amount);
                new ExchangeMonitorDecorator(this).logASK(ticker, amount, previousPriceUsed, priceDifference, opProfit);
                previousPriceUsed = ticker.getAsk();
            }
        }
    }

    private void checkIfProfitableASKAndCarryOn(Ticker ticker) {
        BigDecimal myBitCoins = traderAgent.getBitCoinBalance();
        if (traderAgent.getCurrencyBalance().compareTo(myBitCoins.multiply(previousPriceUsed)) == 1) {
            return;
        }
        if ((ticker.getAsk().compareTo(previousPriceUsed.add(opThreshold)) == 1 && myBitCoins.compareTo(ZERO) == 1) || lostTheTrend()) {
            placeOrder(ASK, myBitCoins, ticker); // new ask higher than the last one plus the threshold and be have money
        } else {
            new ExchangeMonitorDecorator(this).logNotASK(ticker, previousPriceUsed, opThreshold);
        }
    }

    private void checkIfProfitableBIDAndCarryOn(Ticker ticker) {
        BigDecimal myCurrency = traderAgent.getCurrencyBalance();
        if (traderAgent.getBitCoinBalance().multiply(previousPriceUsed).compareTo(myCurrency) == 1) {
            return;
        }
        if (previousPriceUsed.add(opThreshold).compareTo(ticker.getBid()) == 1 && myCurrency.compareTo(ZERO) == 1 || lostTheTrend()) {
             // old price plus threshold is higher than the bid one, and be have money
            BigDecimal bitCoinsToBuy = myCurrency.divide(ticker.getBid(), 80, ROUND_DOWN);
            placeOrder(BID, bitCoinsToBuy, ticker);
        } else {
            new ExchangeMonitorDecorator(this).logNotBID(ticker, previousPriceUsed, opThreshold);
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

    private void getThreshold() {
        if (SettingsProvider.getProperty(OP_THRESHOLD_ENV) == null) {
            opThreshold = DEFAULT_OP_THRESHOLD;
        } else {
            opThreshold = BigDecimal.valueOf(Integer.valueOf(SettingsProvider.getProperty(OP_THRESHOLD_ENV)));
        }
       logger.info("Using op threshold of " + opThreshold);
    }

    private boolean lostTheTrend() {
        if (!MIN_OP_TIME) {
            return false;
        }
        Trades trades = traderAgent.getTrades();
        if (trades == null || trades.getTrades() == null || trades.getTrades().isEmpty()) {
            return false;
        }
        Trade lastTrade = trades.getTrades().get(trades.getTrades().size() -1);
        int timeSinceLastOp = (int) (new Date().getTime() - lastTrade.getTimestamp().getTime());
        int timeSinceLastOpInHours = timeSinceLastOp / 60 / 60 / 1000;
        logger.info("Time since last order [" + timeSinceLastOpInHours + "] hours");
        return  timeSinceLastOpInHours > MAX_NON_OP_TIME;
    }
}

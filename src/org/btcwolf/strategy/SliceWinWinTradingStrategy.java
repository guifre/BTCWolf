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

import static com.xeiam.xchange.dto.Order.OrderType;
import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static java.math.RoundingMode.HALF_EVEN;
import static org.btcwolf.agent.AbstractAgent.FAILED_ORDER;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.*;

public class SliceWinWinTradingStrategy extends AbstractTradingStrategy {

    private static final BigDecimal DEFAULT_OP_THRESHOLD = BigDecimal.valueOf(5);
    private static final String OP_THRESHOLD_ENV = "OP_THRESHOLD";

    private BigDecimal opThreshold;
    private BigDecimal averagePrice;

    public SliceWinWinTradingStrategy(TraderAgent traderAgent) {
        super(traderAgent);
        initialize();
        initThreshold();
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
        if (averagePrice == null) {
            averagePrice = ticker.getAsk();
            logger.info("No older orders, setting av price [" + averagePrice + "]");
            return;
        }
        if (traderAgent.getOpenOrders().getOpenOrders().size() > 0) {
            logOpenOrders(traderAgent.getOpenOrders().getOpenOrders());
        }
        shouldBID(ticker);
        shouldASK(ticker);
        }

    void onOrdered(Ticker ticker, BigDecimal amount, OrderType orderType, String orderResult) {

        if (!FAILED_ORDER.equals(orderResult)) {
            BigDecimal btcBalance = traderAgent.getBitCoinBalance();
            BigDecimal orderCost = getCostByOp(ticker, orderType);
            logOrder(amount, orderType, orderResult);
            logger.info("previous average price [" + averagePrice + "] order cost [" + orderCost +
                    "current btc balance [" + btcBalance +"]");
            averagePrice = averagePrice.multiply(btcBalance)
                    .add(orderCost.multiply(amount))
                    .divide(btcBalance.add(amount), 40, ROUND_HALF_EVEN);
            logger.info("New  average cost [" + averagePrice + "]");

        }
    }

    private BigDecimal getCostByOp(Ticker ticker, OrderType orderType) {
        if (ASK == orderType) {
            return ticker.getAsk();
        } else {
            return  ticker.getBid();
        }
    }

    private void shouldASK(Ticker ticker) {
        if ((ticker.getAsk().compareTo(averagePrice.add(opThreshold)) == 1)) {
            BigDecimal amount = getOpAmount();
            if (traderAgent.getBitCoinBalance().compareTo(amount) == 1) {
                logger.info("Placing order ask amount["+amount+"] for["+ticker.getAsk()+"]");
                placeOrder(ASK, amount, ticker); // new ask higher than the last one plus the threshold and be have money
            }
        } else {
            logNotASK(ticker, averagePrice, opThreshold);
        }
    }

    private BigDecimal getOpAmount() {
        return BigDecimal.valueOf(0.002);
    }

    private void shouldBID(Ticker ticker) {
        if (averagePrice.add(opThreshold).compareTo(ticker.getBid()) == 1) {
            BigDecimal amount = getOpAmount();
            if (traderAgent.getCurrencyBalance().compareTo(amount) == 1) {
                logger.info("Placing order bid amount["+amount+"] for["+ticker.getBid()+"]");
                placeOrder(BID, amount, ticker);
            }
        } else {
            logNotBID(ticker, averagePrice, opThreshold);
        }
     }

    private void initialize() {
        Trades trades = traderAgent.getTrades();
        if (trades == null || trades.getTrades() == null || trades.getTrades().isEmpty()) {
            logger.info("empty historic, waiting for next ticker.");
        } else {
            int numberOfTrades = 0;
            for (Trade trade : trades.getTrades()) {
                averagePrice = averagePrice.add(trade.getPrice());
                numberOfTrades++;
            }
            averagePrice = averagePrice.divide(BigDecimal.valueOf(numberOfTrades), 40, HALF_EVEN);
            logger.info("Computed average BTC cost of[" + averagePrice + "]CNZ");
        }
    }

    private void initThreshold() {
        if (SettingsProvider.getProperty(OP_THRESHOLD_ENV) == null) {
            opThreshold = DEFAULT_OP_THRESHOLD;
        } else {
            opThreshold = BigDecimal.valueOf(Integer.valueOf(SettingsProvider.getProperty(OP_THRESHOLD_ENV)));
        }
        logger.info("Using op threshold of " + opThreshold);
    }
}

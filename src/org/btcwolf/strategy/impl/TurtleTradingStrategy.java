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
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import org.btcwolf.agent.TraderAgent;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;

import static com.xeiam.xchange.dto.Order.OrderType;
import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static java.math.BigDecimal.ZERO;
import static org.btcwolf.agent.AbstractAgent.FAILED_ORDER;
import static org.btcwolf.strategy.impl.StrategyOrchestratorDecorator.logOrder;

public class TurtleTradingStrategy extends AbstractTradingStrategy {

    private static final int MAX_MINS_ORDER_TO_PROCESSED = 20;
    private static final int CHECK_DEAD_ORDERS_FREQ = 10;

    private final LinkedList<Ticker> historicData;
    private final int turtleSpeed;
    private final int opAmount;

    private int limitOrdersCount;

    public TurtleTradingStrategy(TraderAgent traderAgent, int turtleSpeed, int opAmount) {
        super(traderAgent);
        this.turtleSpeed = turtleSpeed;
        this.opAmount = opAmount;
        this.historicData = new LinkedList<Ticker>();
        this.limitOrdersCount = 0;
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
       addToHistoric(ticker);
       checkDeadLimitOrders();
        if (historicData.size() == turtleSpeed) {
            checkIfProfitableASKAndCarryOn(ticker);
            checkIfProfitableBIDAndCarryOn(ticker);
        }
    }

    private void addToHistoric(Ticker ticker) {
        if (historicData.size() == turtleSpeed) {
            historicData.removeLast();
        }
        historicData.addFirst(ticker);
    }

    void onOrdered(Ticker ticker, BigDecimal amount, OrderType orderType, String orderResult) {
        if (!FAILED_ORDER.equals(orderResult)) {
            StrategyOrchestratorDecorator.logOrder(ticker, amount, orderType);
        }
    }

    private void checkIfProfitableASKAndCarryOn(Ticker ticker) {
        BigDecimal mBitcoins = traderAgent.getBitCoinBalance();
        if (mBitcoins.compareTo(ZERO) == 1 && shouldAsk(ticker)) {
            BigDecimal amountToSell = mBitcoins.divide(BigDecimal.valueOf(opAmount), 80, ROUND_HALF_EVEN);
            placeOrder(ASK, amountToSell, ticker);
            historicData.clear();
        }
    }

    private void checkIfProfitableBIDAndCarryOn(Ticker ticker) {
        BigDecimal mCurrency = traderAgent.getCurrencyBalance();
        if (mCurrency.compareTo(ZERO) == 1 && shouldBid(ticker)) {
            BigDecimal amountToBuy = mCurrency.divide(BigDecimal.valueOf(opAmount), 80, ROUND_HALF_EVEN);
            placeOrder(BID, amountToBuy.divide(ticker.getBid(), 80, ROUND_HALF_EVEN), ticker);
            historicData.clear();
        }
    }

    private boolean shouldAsk(Ticker ticker) {

        for (Ticker historicTicker : historicData) {
            BigDecimal previousValue = historicTicker.getAsk();
            if (ticker.getAsk().compareTo(previousValue) == 1) {
                logger.debug("Not ordering ASK, found [" + previousValue + "] higher than [" + ticker.getAsk() + "]");
                return false; //if a previous ask is higher we do not sell
            }
        }
        String oldD = "";
        for (Ticker oldData : historicData) {
            oldD = oldD.concat(String.format("%f.1", oldData.getAsk()) + ", ");
        }
        logger.info("ORDERING ASK, found [" + ticker.getAsk() + "] is higher than [" + oldD + "]");
        return true;
    }

    private boolean shouldBid(Ticker ticker) {
        for (Ticker historicTicker : historicData) {
            BigDecimal previousBid = historicTicker.getBid();
            if (previousBid.compareTo(ticker.getBid()) == 1) {
                logger.debug("Not ordering BID, found [" + previousBid + "] higher than [" + ticker.getBid() + "]");
                return false; //if a previous Bid is higher, we do not buy
            }
        }
        String oldD = "";
        for (Ticker oldData : historicData) {
            oldD = oldD.concat(String.format("%f.1", oldData.getBid()) + ", ");
        }
        logger.info("ORDERING BID, found [" + ticker.getBid() + "] is lower than [" + oldD + "]");
        return true;
    }

    private void checkDeadLimitOrders() {
        if (limitOrdersCount < CHECK_DEAD_ORDERS_FREQ) {
            limitOrdersCount++;
            return;
        }
        limitOrdersCount = 0;
        OpenOrders openOrders = traderAgent.getOpenOrders();
        if (openOrders == null || openOrders.getOpenOrders() == null || openOrders.getOpenOrders().size() == 0) {
            return;
        }
        Date time = new Date();
        for (LimitOrder limitOrder : openOrders.getOpenOrders()) {
            int timeSincePlaced = (int) (time.getTime() - limitOrder.getTimestamp().getTime());
            int minutesSincePlacedLimit = timeSincePlaced / 60 / 1000;
            if (minutesSincePlacedLimit > MAX_MINS_ORDER_TO_PROCESSED) {
                boolean cancelled = traderAgent.cancelLimitOrder(limitOrder);
                logger.info("Limit placed [" + minutesSincePlacedLimit + "] mins ago, cancelled [" + cancelled + "] limit [" + limitOrder);
            } else {
                logger.debug("Limit placed [" + minutesSincePlacedLimit + "] mins ago, on time, limit [" + limitOrder);
            }
        }
    }
}
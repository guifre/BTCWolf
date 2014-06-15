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
import java.util.ArrayList;
import java.util.List;

import static com.xeiam.xchange.dto.Order.OrderType;
import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static java.math.BigDecimal.ZERO;
import static org.btcwolf.agent.AbstractAgent.FAILED_ORDER;
import static org.btcwolf.strategy.ExchangeMonitorDecorator.*;

public class TurtleTradingStrategy extends AbstractTradingStrategy {

    private static final boolean MIN_OP_TIME = false;
    private static final int MAX_NON_OP_TIME = 5; //hours

    private static final BigDecimal DEFAULT_OP_THRESHOLD = BigDecimal.valueOf(2);
    private static final String OP_THRESHOLD_ENV = "OP_THRESHOLD";

    private List<Ticker> lastTickers;
    private BigDecimal previousPriceUsed;
    private int tourtleSpeed;

    public TurtleTradingStrategy(TraderAgent traderAgent, int tourtleSpeed) {
        super(traderAgent);
        this.tourtleSpeed = tourtleSpeed;
        lastTickers = new ArrayList<Ticker>(tourtleSpeed);
    }

    @Override
    public void onTickerReceived(Ticker ticker) {
       addToHitoric(ticker);
        if(lastTickers.size() == tourtleSpeed) {
            checkIfProfitableASKAndCarryOn(ticker);
            checkIfProfitableBIDAndCarryOn(ticker);
        }
    }

    private void addToHitoric(Ticker ticker) {
        if (lastTickers.size() == tourtleSpeed) {
            lastTickers.remove(lastTickers.size() -1);
        }
        lastTickers.add(0, ticker);
    }

    void onOrdered(Ticker ticker, BigDecimal amount, OrderType orderType, String orderResult) {

        if (!FAILED_ORDER.equals(orderResult)) {
            logOrder(amount, orderType, orderResult);

        }
    }

    private void checkIfProfitableASKAndCarryOn(Ticker ticker) {
        BigDecimal mBitcoins = traderAgent.getBitCoinBalance();
        if (mBitcoins.compareTo(ZERO) == 1 && shouldAsk(ticker)) {
            placeOrder(ASK, mBitcoins.divide(BigDecimal.valueOf(10), 40, ROUND_HALF_EVEN), ticker);
        }
    }

    private void checkIfProfitableBIDAndCarryOn(Ticker ticker) {
        BigDecimal mCurrency = traderAgent.getCurrencyBalance();
        if (mCurrency.compareTo(ZERO) == 1 && shouldBid(ticker)) {
            placeOrder(BID, mCurrency.divide(ticker.getBid(), 80, ROUND_HALF_EVEN).divide(BigDecimal.valueOf(10), 40, ROUND_HALF_EVEN), ticker);
        }
    }

    private boolean shouldAsk(Ticker ticker) {
        for (Ticker lastTicker : lastTickers) {
            if (ticker.getAsk().compareTo(lastTicker.getAsk()) == 1) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldBid(Ticker ticker) {
        for (Ticker lastTicker : lastTickers) {
            if (lastTicker.getBid().compareTo(ticker.getBid()) == 1) {
                return false;
            }
        }
        return true;
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
}

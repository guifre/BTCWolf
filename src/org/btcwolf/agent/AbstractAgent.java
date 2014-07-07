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

package org.btcwolf.agent;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.Wallet;
import org.apache.log4j.Logger;
import org.btcwolf.BTCWolf;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.xeiam.xchange.dto.Order.OrderType;

public abstract class AbstractAgent implements TraderAgent {

    protected static final Logger logger = Logger.getLogger(AbstractAgent.class);
    public static final String FAILED_ORDER = "KO";

    private final Exchange exchange;
    private final CurrencyPair currencyPair;

    public AbstractAgent(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
        this.exchange = buildExchange();
    }

    protected abstract Exchange buildExchange();

    @Override
    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    @Override
    public Ticker pollTicker() {
        try {
            return exchange.getPollingMarketDataService().getTicker(currencyPair);
        } catch (Exception e) {
            logger.warn("oops when getting ticker " + e.getMessage());
            BTCWolf.makeSomeCoffee();
            return pollTicker();
        }
    }

    @Override
    public String placeOrder(OrderType orderType, BigDecimal amount, BigDecimal price) {
        try {
            if(exchange.getPollingAccountService().getAccountInfo().getTradingFee().compareTo(BigDecimal.ZERO) == 1) {
                throw new RuntimeException("Detected potential trading fee, bye" + exchange.getPollingAccountService().getAccountInfo().getTradingFee());
            }
        } catch (IOException e) {
            //
        }
        logger.info("Placing order [" + orderType + "] amount [" + amount + "] currency [" + currencyPair + "] limit price [" + price + "]");
        return attemptPlaceOrder(orderType, amount, price, 0);
    }

    @Override
    public boolean cancelLimitOrder(LimitOrder limitOrder) {
        return attemptCancelLimit(limitOrder, 0);
    }

    @Override
    public List<Wallet> getWallets() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getWallets();
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getWallets();
        }
    }

    @Override
    public BigDecimal getBitCoinBalance() {
        try {
            Wallet myWallet = null;
            for (Wallet wallet : this.exchange.getPollingAccountService().getAccountInfo().getWallets()) {
                if (currencyPair.baseSymbol.equals(wallet.getCurrency())) {
                    myWallet = wallet;
                }
            }
            if (myWallet != null) {
                return myWallet.getBalance();
            }
            throw new RuntimeException("could not find currency");
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getBitCoinBalance();
        }
    }

    @Override
    public BigDecimal getCurrencyBalance() {
        try {
            Wallet myWallet = null;
            for (Wallet wallet : this.exchange.getPollingAccountService().getAccountInfo().getWallets()) {
                if (currencyPair.counterSymbol.equals(wallet.getCurrency())) {
                    myWallet = wallet;
                }
            }
            if (myWallet != null) {
                return myWallet.getBalance();
            }
            throw new RuntimeException("could not find currency");
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getBitCoinBalance();
        }
    }

    @Override
    public OpenOrders getOpenOrders() {
        try {
            return this.exchange.getPollingTradeService().getOpenOrders();
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getOpenOrders();
        }
    }

    @Override
    public OrderBook getOrderBook() {
        try {
            return this.exchange.getPollingMarketDataService().getOrderBook(currencyPair);
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getOrderBook();
        }
    }

    public Trades getTradeHistory(int numberOfTrades) {
        try {
            return this.exchange.getPollingMarketDataService().getTrades(currencyPair, numberOfTrades);
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getTradeHistory(numberOfTrades);
        }
    }

    @Override
    public Trades getTrades() {
        try {
            return exchange.getPollingTradeService().getTradeHistory();
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getTrades();
        }
    }

    private String attemptPlaceOrder(OrderType orderType, BigDecimal amount, BigDecimal price, int attempt) {
        if (attempt >= 5) {
            return FAILED_ORDER;
        }
        try {
            return exchange.getPollingTradeService().placeLimitOrder(
                    new LimitOrder(orderType, amount, currencyPair, "0", new Date(), price));
        } catch (Exception e) {
            logger.warn("oops attempt "  + attempt + " " + e.getMessage() + " " + e.toString());
            return attemptPlaceOrder(orderType, amount, price, attempt + 1);
        }
    }

    private boolean attemptCancelLimit(LimitOrder limitOrder, int attempt) {
        if (attempt >= 5) {
            return false;
        }
        try {
            return exchange.getPollingTradeService().cancelOrder(limitOrder.getId());
        } catch (Exception e) {
            logger.warn("oops attempt "  + attempt + " " + e.getMessage() + " " + e.toString());
            return attemptCancelLimit(limitOrder, attempt + 1);
        }
    }
}
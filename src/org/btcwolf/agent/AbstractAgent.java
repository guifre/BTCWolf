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
import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;

public abstract class AbstractAgent implements TraderAgent {

    private static final Logger logger = Logger.getLogger(TraderAgent.class.getName());

    private final Exchange exchange;

    private final CurrencyPair currencyPair;

    public AbstractAgent(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
        this.exchange = buildExchange();
    }

    protected abstract Exchange buildExchange();

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public Ticker pollTicker() {
        try {
            return exchange.getPollingMarketDataService().getTicker(currencyPair);
        } catch (Exception e) {
            logger.warn("oops when getting ticker " + e.getMessage());
            BTCWolf.makeSomeCoffee();
            return pollTicker();
        }
    }

    public String placeOrder(OrderType orderType, BigDecimal amount, Ticker ticker) {
        try {
            if(exchange.getPollingAccountService().getAccountInfo().getTradingFee().compareTo(BigDecimal.ZERO) == 1) {
                throw new RuntimeException("found potential trading fee, bye" + exchange.getPollingAccountService().getAccountInfo().getTradingFee());
            }
        } catch (IOException e) {
            //
        }
        logger.info("placing order " + orderType + " amount " + amount + "currency" + currencyPair + "limitprace " + ticker.getAsk());
        if (ASK.equals(orderType)) {
                return attemptPlaceOrder(orderType, amount, ticker.getAsk(), 0);
        } else if (BID.equals(orderType)) {
            return attemptPlaceOrder(orderType, amount, ticker.getBid(), 0);
        } else {
            logger.error("Could not process " + orderType);
            return "Error. Could not process " + orderType;
        }
    }

    public List<Wallet> getWallets() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getWallets();
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getWallets();
        }
    }

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

    public OpenOrders getOpenOrders() {
        try {
            return this.exchange.getPollingTradeService().getOpenOrders();
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getOpenOrders();
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
            return "KO";
        }
        try {
            return exchange.getPollingTradeService().placeLimitOrder(
                    new LimitOrder(orderType, amount, currencyPair, "0", new Date(), price));
        } catch (Exception e) {
            logger.warn("oops attempt "  + attempt + " " + e.getMessage() + " " + e.toString());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logger.warn(stackTraceElement.toString());
            }
            return attemptPlaceOrder(orderType, amount, price, attempt + 1);
        }
    }
}
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
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.Wallet;
import org.apache.log4j.Logger;
import org.btcwolf.BTCWolf;

import java.io.IOException;
import java.math.BigDecimal;

public class BTCChinaAgent implements TraderAgent {

    private static final Logger logger = Logger.getLogger(TraderAgent.class.getName());

    private static final String SECRET_KEY_ENV = "SecretKey";
    private static final String API_KEY_ENV = "APIKey";
    private static final String PASSWORD_ENV = "Password";

    private static final CurrencyPair CURRENCY = CurrencyPair.BTC_CNY;

    private final Exchange exchange;

    public BTCChinaAgent() {
        this.exchange = buildExchange();
    }

    private Exchange buildExchange() {
        if (System.getProperty(SECRET_KEY_ENV) == null || System.getProperty(PASSWORD_ENV) == null || System.getProperty(API_KEY_ENV) == null) {
            String msg = "Could not find credential arguments " + SECRET_KEY_ENV + System.getProperty(SECRET_KEY_ENV) + ", " + PASSWORD_ENV+System.getProperty(PASSWORD_ENV) + ", " + API_KEY_ENV + System.getProperty(API_KEY_ENV);
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        ExchangeSpecification exSpec = new ExchangeSpecification(BTCChinaExchange.class);
        exSpec.setSecretKey(System.getProperty(SECRET_KEY_ENV));
        exSpec.setApiKey(System.getProperty(API_KEY_ENV));
        exSpec.setPassword(System.getProperty(PASSWORD_ENV));
        return ExchangeFactory.INSTANCE.createExchange(exSpec);
    }

    public Ticker pollTicker() {
        try {
            return exchange.getPollingMarketDataService().getTicker(CURRENCY);
        } catch (IOException e) {
            logger.warn("oops when getting ticker " + e.getMessage());
            BTCWolf.makeSomeCoffee();
            return pollTicker();
        }
    }

    public String placeOrder(Order.OrderType orderType, BigDecimal amount) {
        try {
            if(exchange.getPollingAccountService().getAccountInfo().getTradingFee().doubleValue() > 0) {
                throw new RuntimeException("found potential trading fee, bye" + exchange.getPollingAccountService().getAccountInfo().getTradingFee());
            }
            return exchange.getPollingTradeService().placeMarketOrder(new MarketOrder(Order.OrderType.ASK, amount, CURRENCY));
        } catch (Exception e) {
            return e.getMessage();
          //  throw new RuntimeException("something went wrong when polling" + e.getMessage() +e.getCause()+e.getStackTrace());
        }
    }
    
    public Wallet getWallet() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getWallets().get(this.exchange.getPollingAccountService().getAccountInfo().getWallets().size()-1);
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getWallet();
        }
    }   
    
    public BigDecimal getBitCoinBalance() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getBalance("BTC");
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getBitCoinBalance();
        }
    }

    public BigDecimal getCurrencyBalance() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getBalance("CNY");
        } catch (IOException e) {
            logger.warn("oops " + e.getMessage());
            return getCurrencyBalance();
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
}
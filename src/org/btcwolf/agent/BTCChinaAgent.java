package org.btcwolf.agent;

import org.btcwolf.strategy.Strategy;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.campbx.CampBX;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.trade.MarketOrder;
import com.xeiam.xchange.dto.trade.Wallet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Created by guifre on 21/05/14.
 */
public class BTCChinaAgent implements TraderAgent {


    private static final Logger logger = Logger.getLogger(TraderAgent.class.getName());
    private static final String SECRET_KEY_ENV = "SecretKey";
    private static final String API_KEY_ENV = "APIKey";
    private static final String PASSWORD_ENV = "Password";
    private static final long POLLING_TIME = 30000;
    private static final CurrencyPair CURRENCY = CurrencyPair.BTC_CNY;

    private final Strategy tradingStrategy;
    private Exchange exchange;


    public BTCChinaAgent(Strategy tradingStrategy) {
        this.exchange = buildExchange();
        this.tradingStrategy = tradingStrategy;
    }

    public void printAccountInfo() {
        try {
            AccountInfo accountInfo = null;
            System.out.println("AccountInfo as String: " + accountInfo.toString());
            System.out.println("Printing current open orders;");
            System.out.println("adre " + exchange.getPollingAccountService().requestDepositAddress(""));
            System.out.println(this.exchange.getPollingMarketDataService().getOrderBook(CURRENCY));
            System.out.println(this.exchange.getPollingMarketDataService().getTrades(CURRENCY));
            accountInfo = exchange.getPollingAccountService().getAccountInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private Exchange buildExchange() {
        ExchangeSpecification exSpec = new ExchangeSpecification(BTCChinaExchange.class);
        exSpec.setSecretKey(System.getProperty(SECRET_KEY_ENV));
        exSpec.setApiKey(System.getProperty(API_KEY_ENV));
        exSpec.setPassword(System.getProperty(PASSWORD_ENV));
        return ExchangeFactory.INSTANCE.createExchange(exSpec);
    }

    @Override
    public void run() {
        while(true) {
            Ticker ticker = pollTicker();
            this.tradingStrategy.onTickerReceived(ticker);
            try {
                Thread.sleep(POLLING_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException("interrupted bye");
            }
        }
    }

    Ticker pollTicker() {
        try {
            return exchange.getPollingMarketDataService().getTicker(CURRENCY);
        } catch (IOException e) {
            logger.warning("oops " + e.getMessage());
            return pollTicker();
        }
    }
    
    public String placeOrder(CampBX.OrderType orderType, BigDecimal amount) {
        try {
            if(exchange.getPollingAccountService().getAccountInfo().getTradingFee().doubleValue() > 0) {
                throw new RuntimeException("found potential trading fee, bye" + exchange.getPollingAccountService().getAccountInfo().getTradingFee());
            }
            return exchange.getPollingTradeService().placeMarketOrder(new MarketOrder(Order.OrderType.ASK, amount, CURRENCY));
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when polling" + e.getMessage() +e.getCause()+e.getStackTrace());
        }
    }
    
    public Wallet getWallet() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getWallets().get(this.exchange.getPollingAccountService().getAccountInfo().getWallets().size()-1);
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when polling" + e.getMessage() +e.getCause()+e.getStackTrace());
        }
    }   
    
    public BigDecimal getBitCoinBalance() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getBalance("BTC");
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when polling" + e.getMessage() +e.getCause()+e.getStackTrace());
        }
    }

    public BigDecimal getCurrencyBalance() {
        try {
            return this.exchange.getPollingAccountService().getAccountInfo().getBalance("CNY");
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when polling" + e.getMessage() +e.getCause()+e.getStackTrace());
        }
    }
}
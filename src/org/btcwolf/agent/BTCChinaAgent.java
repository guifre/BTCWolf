package org.btcwolf.agent;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.service.polling.PollingTradeService;
import org.btcwolf.persistance.MyTicker;
import org.btcwolf.persistance.Serializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guifre on 21/05/14.
 */
public class BTCChinaAgent implements TraderAgent {

    private static final String SECRET_KEY_ENV = "SecretKey";
    private static final String API_KEY_ENV = "APIKey";
    private static final String PASSWORD_ENV = "Password";

    public PollingTradeService tradeService;
    public PollingAccountService accountService;
    public PollingMarketDataService marketDataService;
    public Exchange exchange;


    public BTCChinaAgent() {
        this.exchange = buildExchange();
        this.accountService = exchange.getPollingAccountService();
        this.marketDataService = exchange.getPollingMarketDataService();
        this.tradeService = exchange.getPollingTradeService();
    }

    public void printAccountInfo() {
        try {
            AccountInfo accountInfo = null;
            System.out.println("AccountInfo as String: " + accountInfo.toString());
            System.out.println("Printing current open orders;");
            System.out.println("adre " + accountService.requestDepositAddress(""));
            System.out.println(this.exchange.getPollingMarketDataService().getOrderBook(CurrencyPair.BTC_CNY));
            System.out.println(this.exchange.getPollingMarketDataService().getTrades(CurrencyPair.BTC_CNY));
            accountInfo = accountService.getAccountInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getTricker() {
        List<MyTicker> list = new ArrayList<MyTicker>();

//        for (int i = 0; i < 1000; i++) {
//            Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_CNY);
//            list.add(TickerAdapter.adapt(ticker));
//            System.out.println(ticker.toString());
//            Thread.sleep(15000);
//        }
        List<MyTicker> list2 = null;
        try {
            list2 = Serializer.read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (MyTicker myTicker : list2) {
            System.out.println(myTicker);
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

    }
}
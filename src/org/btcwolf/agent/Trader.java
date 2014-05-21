package org.btcwolf.agent;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.coinbase.CoinbaseExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;

/**
 * Created by guifre on 20/05/14.
 */
public class Trader {

    Exchange mExchanger;
    PollingMarketDataService marketDataService;

    public Trader() {
        this.mExchanger = ExchangeFactory.INSTANCE.createExchange(CoinbaseExchange.class.getName());
        this.marketDataService = this.mExchanger.getPollingMarketDataService();
    }
    public Trader(String name) {
        this.mExchanger = ExchangeFactory.INSTANCE.createExchange(name);
        this.marketDataService = this.mExchanger.getPollingMarketDataService();
    }

    public Ticker getTicker() throws IOException {
        if(marketDataService==null)return null;
        return marketDataService.getTicker(CurrencyPair.BTC_USD);
    }




    }

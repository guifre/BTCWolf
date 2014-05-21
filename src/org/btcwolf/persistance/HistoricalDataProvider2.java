package org.btcwolf.persistance;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;

/**
 * Created by guifre on 20/05/14.
 */
public class HistoricalDataProvider2 {

    private static PollingMarketDataService marketDataService;

    public static void main(String[] args) throws IOException {

        // Use the factory to get the version 1 MtGox exchange API using default settings
        Exchange mtGox = ExchangeFactory.INSTANCE.createExchange("com.xeiam.xchange.mtgox.v1.MtGoxExchange");

        // Interested in the public polling market data feed (no authentication)
        marketDataService = mtGox.getPollingMarketDataService();

        // Get the latest ticker data showing BTC to USD
        Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_USD);
        double value = ticker.getLast().doubleValue();
        String currency = ticker.getLast().toString();
        System.out.println("Last: " + currency + "-" + value);

        System.out.println("Last: " + ticker.getLast().toString());
        System.out.println("Bid: " + ticker.getBid().toString());
        System.out.println("Ask: " + ticker.getAsk().toString());


    }

}

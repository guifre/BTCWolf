package org.btcwolf.persistance;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.coinbase.CoinbaseExchange;
import com.xeiam.xchange.coinbase.dto.marketdata.*;
import com.xeiam.xchange.coinbase.service.polling.CoinbaseMarketDataService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by guifre on 20/05/14.
 */
public class HistoricalDataProvider {

    public HistoricalDataProvider() throws IOException {
    }

    public void persistData() throws IOException {
        Exchange coinbaseExchange = ExchangeFactory.INSTANCE.createExchange(CoinbaseExchange.class.getName());
        PollingMarketDataService marketDataService = coinbaseExchange.getPollingMarketDataService();
       // Serializer.write(raw((MyTicker) marketDataService));
    }

    public List<Double> getData() throws IOException {
     //   return Serializer.read();
        return null;
    }

    public List<CoinbaseHistoricalSpotPrice> raw(CoinbaseMarketDataService marketDataService) throws IOException {

        List<CoinbaseCurrency> currencies = marketDataService.getCoinbaseCurrencies();
        System.out.println(currencies);

        Map<String, BigDecimal> exchangeRates = marketDataService.getCoinbaseCurrencyExchangeRates();
        System.out.println("Exchange Rates: " + exchangeRates);

        String amount = "1.57";
        CoinbasePrice buyPrice = marketDataService.getCoinbaseBuyPrice(new BigDecimal(amount));
        System.out.println("Buy Price for " + amount + " BTC: " + buyPrice);

        CoinbasePrice sellPrice = marketDataService.getCoinbaseSellPrice();
        System.out.println("Sell Price: " + sellPrice);

        CoinbaseMoney spotRate = marketDataService.getCoinbaseSpotRate("USD");
        System.out.println("Spot Rate: " + spotRate);

        int page = 50;
        List<CoinbaseHistoricalSpotPrice> spotPriceHistoryList = new ArrayList<CoinbaseHistoricalSpotPrice>();
        for (int i = 10; i > 0; i--) {
            CoinbaseSpotPriceHistory spotPriceHistory = marketDataService.getCoinbaseHistoricalSpotRates(page);
            spotPriceHistoryList.addAll(spotPriceHistory.getSpotPriceHistory());
        }

        return spotPriceHistoryList;

    }
}

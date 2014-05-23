package org.btcwolf.persistance;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;

/**
 * Created by guifre on 23/05/14.
 */
public class TickerAdapter {

    public static MyTicker adapt(Ticker ticker) {
        return new MyTicker(null, ticker.getBid(), ticker.getAsk(), ticker.getHigh(), ticker.getLow(), ticker.getVolume(), ticker.getTimestamp(), ticker.getLast());
    }
    public static Ticker adapt(MyTicker ticker) {
        return Ticker.TickerBuilder.newInstance().withAsk(ticker.getAsk()).withBid(ticker.getBid()).withCurrencyPair(CurrencyPair.BTC_CNY).withLast(ticker.getAsk()).withHigh(ticker.getHigh()).withTimestamp(ticker.getTimestamp()).withVolume(ticker.getVolume()).build();
    }
}

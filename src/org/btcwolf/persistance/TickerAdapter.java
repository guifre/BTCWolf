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

package org.btcwolf.persistance;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;

public class TickerAdapter {

    public static MyTicker adapt(Ticker ticker) {
        return new MyTicker(null, ticker.getBid(), ticker.getAsk(), ticker.getHigh(), ticker.getLow(), ticker.getVolume(), ticker.getTimestamp(), ticker.getLast());
    }
    public static Ticker adapt(MyTicker ticker) {
        return Ticker.TickerBuilder.newInstance().withAsk(ticker.getAsk()).withBid(ticker.getBid()).withCurrencyPair(CurrencyPair.BTC_CNY).withLast(ticker.getAsk()).withHigh(ticker.getHigh()).withTimestamp(ticker.getTimestamp()).withVolume(ticker.getVolume()).build();
    }
}

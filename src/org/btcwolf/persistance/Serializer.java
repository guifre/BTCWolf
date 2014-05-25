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

import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public class Serializer {

    private static final String FILE = "resources/historic";

    public static void  write(List<Ticker> tickerList) throws FileNotFoundException {
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(FILE)));
        PersistenceDelegate pd=encoder.getPersistenceDelegate(Integer.class);
        encoder.setPersistenceDelegate(BigDecimal.class,pd );
        encoder.setPersistenceDelegate(CurrencyPair.class, pd);
        List<MyTicker> myTickerList = new ArrayList<MyTicker>();
        for (Ticker ticker : tickerList) {
            myTickerList.add(TickerAdapter.adapt(ticker));
        }
        encoder.writeObject(myTickerList);
        encoder.close();
    }

    public static List<Ticker> read() throws FileNotFoundException {
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(FILE)));
        List<MyTicker> myTickerList = (List<MyTicker>) decoder.readObject();
        decoder.close();
        List<Ticker> tickerList = new ArrayList<Ticker>();
        for (MyTicker myTicker : myTickerList) {
            tickerList.add(TickerAdapter.adapt(myTicker));
        }
        return tickerList;
    }
}

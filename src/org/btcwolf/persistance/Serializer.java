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

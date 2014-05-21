package org.btcwolf.persistance;

import com.xeiam.xchange.coinbase.dto.marketdata.CoinbaseHistoricalSpotPrice;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public class Serializer {

    private static final String FILE = "resources/historic";

    public static void write(List<CoinbaseHistoricalSpotPrice> o) throws FileNotFoundException {
        XMLEncoder encoder = new XMLEncoder(
                new BufferedOutputStream(
                        new FileOutputStream(FILE)));
        List<Double> converted = new ArrayList<Double>();
        for (CoinbaseHistoricalSpotPrice e : o) {
            converted.add(e.getSpotRate().doubleValue());
        }
        encoder.writeObject(converted);
        encoder.close();
    }
    public static List<Double> read() throws FileNotFoundException {
        XMLDecoder decoder = new XMLDecoder(
                new BufferedInputStream(
                        new FileInputStream(FILE)));
        List<Double> o = (List<Double>) decoder.readObject();
        decoder.close();
        if (o instanceof List) {
            return (List<Double>)o;
        }
        throw new RuntimeException("did not get a list");
    }
}

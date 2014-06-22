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

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guifre on 13/06/14.
 */
public class TickerHistoricBuilder {

    @Test
    public void parseLogAndPersistTickers() throws IOException {
        List<Ticker> tickerList = new ArrayList<Ticker>();
        String file = "../t/log/BTCWolf_debug.log";
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String line = null;
        Pattern pattern = Pattern.compile("last=([0-9]*.[0-9]*), bid=([0-9]*.[0-9]*), ask=([0-9]*.[0-9]*), high=([0-9]*.[0-9]*), low=([0-9]*.[0-9]*), volume=([0-9]*.[0-9]*),");
        while ((line = br.readLine()) != null) {
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                tickerList.add(Ticker.TickerBuilder.newInstance()
                        .withLast(BigDecimal.valueOf(Double.parseDouble(m.group(1))))
                        .withBid(BigDecimal.valueOf(Double.parseDouble(m.group(2))))
                        .withAsk(BigDecimal.valueOf(Double.parseDouble(m.group(3))))
                        .withHigh(BigDecimal.valueOf(Double.parseDouble(m.group(4))))
                        .withLow(BigDecimal.valueOf(Double.parseDouble(m.group(5))))
                        .withVolume(BigDecimal.valueOf(Double.parseDouble(m.group(6))))
                        .build());
            }
        }
        br.close();
        Serializer.write(tickerList);
    }
}

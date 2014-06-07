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

package org.btcwolf.agent;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.log4j.Logger;
import org.btcwolf.persistance.Serializer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class BTCChinaAgentTest {

    private static final long POLLING_TIME = 30000l;
    private static final Logger logger = Logger.getLogger(BTCChinaAgentTest.class);

    @Test
    public void simpleTest() throws IOException {
        BTCChinaAgent agent = new BTCChinaAgent();
        logger.info("BTC " + agent.getBitCoinBalance());
        logger.info("CNY " + agent.getCurrencyBalance());
        logger.info("Open Orders " + agent.getOpenOrders().getOpenOrders().size());
    }

    @Ignore
    @Test
    public void serializeMoreTickers() throws FileNotFoundException {

        BTCChinaAgent agent = new BTCChinaAgent();

        java.util.List<Ticker> tickers = new ArrayList<Ticker>();

        for (int i = 0; i < 700; i++) {
            tickers.add(agent.pollTicker());
            try {
                Thread.sleep(POLLING_TIME);
            } catch (InterruptedException e) {
                //
            }
        }
        Serializer.write(tickers);
    }
}

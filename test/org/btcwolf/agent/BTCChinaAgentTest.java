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
        logger.info("Yu " + agent.getCurrencyBalance());
        logger.info("BTC " + agent.getBitCoinBalance());
        System.out.println("wallet " + agent.getWallet().getCurrency()  + agent.getWallet().getDescription() + agent.getWallet().getBalance());
    }

    @Ignore
    @Test
    public void getMoreSamples() throws FileNotFoundException {

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

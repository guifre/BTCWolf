package org.btcwolf.agent;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.btcwolf.persistance.Serializer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by guifre on 24/05/14.
 */
public class BTCChinaAgentTest {

    @Test
    public void simpleTest() throws IOException {
        BTCChinaAgent agent = new BTCChinaAgent();
        System.out.println("yuans" + agent.getCurrencyBalance());
        System.out.println("yuans" + agent.getBitCoinBalance());
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
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                //
            }
        }
        Serializer.write(tickers);
    }
}

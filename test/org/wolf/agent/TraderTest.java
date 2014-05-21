package org.wolf.agent;


import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.anx.v2.dto.ANXException;
import com.xeiam.xchange.bitcoinium.BitcoiniumExchange;
import com.xeiam.xchange.bitcurex.Bitcurex;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitfinex.v1.BitfinexExchange;
import com.xeiam.xchange.bitstamp.Bitstamp;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.blockchain.BlockchainExchange;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.v3.BTCEExchange;
import com.xeiam.xchange.bter.BTERExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.cexio.CexIOExchange;
import com.xeiam.xchange.coinbase.CoinbaseExchange;
import com.xeiam.xchange.coinfloor.CoinfloorExchange;
import com.xeiam.xchange.cryptotrade.CryptoTradeExchange;
import com.xeiam.xchange.itbit.v1.ItBitExchange;
import com.xeiam.xchange.kraken.KrakenExchange;

import java.io.IOException;

/**
 * Created by guifre on 20/05/14.
 */
public class TraderTest {

    public static void main(String[] args) throws IOException {

        new TraderTest().test(BTCChinaExchange.class);
        new TraderTest().test(BitstampExchange.class);
        new TraderTest().test(BTERExchange.class);
        new TraderTest().test(BitcoiniumExchange.class);
        new TraderTest().test(BitcurexExchange.class);
        new TraderTest().test(BitfinexExchange.class);
        new TraderTest().test(BlockchainExchange.class);
        new TraderTest().test(CoinfloorExchange.class);
        new TraderTest().test(BTCEExchange.class);
        new TraderTest().test(CampBXExchange.class);
        new TraderTest().test(CoinbaseExchange.class);
        new TraderTest().test(KrakenExchange.class);
        new TraderTest().test(CryptoTradeExchange.class);
    }

    private void test(Class c) {
        Trader trader = new Trader(c.getName());
        try {
            System.out.println( c.getName() +" "+trader.getTicker());
        } catch (Exception e){
        }
    }


}

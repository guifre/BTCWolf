package org.btcwolf.agent;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.service.polling.PollingTradeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guifre on 21/05/14.
 */
public class BTCChinaAgent {

    //Make it accesisable trough whole class instead of main void.
    static PollingTradeService tradeService;

    static List<Double> askList = new ArrayList<Double>();
    static List<Double> bidList = new ArrayList<Double>();

    public static void main (String[] args) throws Exception {
        //Bot variables;
        final int avarageOfHowmanyAsks = 10;
        final int avarageofHowmanyBids = avarageOfHowmanyAsks;

        final double minumumAskBidDifferenceToDoAnything = 0.25;

        final String bidPriceUnderCurrentBid = /*CNY*/"25";
        final String askPriceAboveCurrentAsk = /*CNY*/"50";
        //End variables.

        //The declaration stuff, nothing fancy just copy paste and CTRL+V.
        //Add some final stuff to look less copy paste and noob.
        //Do not edit under here if you have no clue what you do. Its for you're own good.
        final Exchange btcchina = BTCChinaExamplesUtils.getExchange();
        final PollingAccountService accountService = btcchina.getPollingAccountService();
        final Exchange btcchinaa = ExchangeFactory.INSTANCE.createExchange(BTCChinaExchange.class.getName());
        final PollingMarketDataService marketDataService = btcchinaa.getPollingMarketDataService();

        tradeService = btcchina.getPollingTradeService();

        AccountInfo accountInfo = accountService.getAccountInfo();
        System.out.println("AccountInfo as String: " + accountInfo.toString());
        System.out.println("Printing current open orders;");
        System.out.println("End of open orders.");

        Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_CNY);
    }

    public static class BTCChinaExamplesUtils
    {
        public static Exchange getExchange()
        {
            ExchangeSpecification exSpec = new ExchangeSpecification(BTCChinaExchange.class);
            exSpec.setSecretKey("");
            exSpec.setApiKey("");
            exSpec.setPassword("");

            return ExchangeFactory.INSTANCE.createExchange(exSpec);
        }
    }

}

        //Starting stuff for first orders
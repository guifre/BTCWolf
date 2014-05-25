package org.btcwolf;

import org.btcwolf.strategy.Strategy;
import org.btcwolf.strategy.TradingStrategyProvider;
import org.btcwolf.agent.AgentsFactory;
import org.btcwolf.agent.TraderAgent;

/**
 * Created by guifre on 20/05/14.
 */
public class BitCoinWolf {

    public static void main(String[] args) {
        Strategy tradingStrategy = TradingStrategyProvider.getDefaultStrategy();
        TraderAgent trader = AgentsFactory.buildTraderAgent(tradingStrategy);
        trader.run();
    }
}

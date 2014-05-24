package org.btcwolf.agent;

import org.btcwolf.strategy.Strategy;

import java.util.List;

/**
 * Created by guifre on 23/05/14.
 */
public class AgentsFactory {

    private static List<TraderAgent> agents;

    public static TraderAgent buildTraderAgent(Strategy strategy) {
       return new BTCChinaAgent(strategy);
    }

}

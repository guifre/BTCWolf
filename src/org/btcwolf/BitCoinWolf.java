package org.btcwolf;

import org.btcwolf.agent.AgentsProvider;
import org.btcwolf.agent.TraderAgent;

/**
 * Created by guifre on 20/05/14.
 */
public class BitCoinWolf {

    public static void main(String[] args) {

        for (TraderAgent trader : AgentsProvider.getTraderAgents()) {
            trader.run();
        }
    }
}

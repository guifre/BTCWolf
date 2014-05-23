package org.btcwolf.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guifre on 23/05/14.
 */
public class AgentsProvider {

    private static List<TraderAgent> agents;

    static {
        agents = new ArrayList<TraderAgent>();
        agents.add(new BTCChinaAgent());
    }

    public static List<TraderAgent> getTraderAgents() {
       return agents;
    }

}

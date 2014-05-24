package org.btcwolf.agent;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.btcwolf.persistance.Serializer;
import org.btcwolf.strategy.WinWinStrategy;
import org.btcwolf.strategy.Strategy;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by guifre on 24/05/14.
 */
public class TraderAgentTeset {

    public List<Ticker> getTicker() {
        List<Ticker> list2 = null;
        try {
            list2 = Serializer.read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list2;
    }
    @Test
    public void testAgent() {
        List<Ticker> data = getTicker();
        Strategy testedStratedy = new WinWinStrategy(BigDecimal.valueOf(0), BigDecimal.valueOf(500), BigDecimal.valueOf(1));
        System.out.println("profit " + testedStratedy.run(data).intValue());

    }
}

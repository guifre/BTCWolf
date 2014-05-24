package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.btcwolf.persistance.Serializer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

public class StrategyTest {

    public List<Ticker> getTicker() {
        List<Ticker> list2 = null;
        try {
            list2 = Serializer.read();
        } catch (FileNotFoundException e) {
            e.printStackTrace(

            );
        }
        return list2;
    }

    @Test
    public void testStrategy() {
        BigDecimal yuan = BigDecimal.valueOf(1496); //about £1k
        List<Ticker> data = getTicker();
        Strategy testedStrategy = new WinWinStrategy(BigDecimal.valueOf(0), yuan, BigDecimal.valueOf(1));
        System.out.println("profit " + String.format("%.4f", testedStrategy.run(data)));

    }
}

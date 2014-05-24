package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.btcwolf.persistance.Serializer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.ALL;

public class StrategyTest {

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
    public void testStrategy() {
        Logger logger = Logger.getLogger(AbstractStrategy.class.getSimpleName());
        logger.setLevel(ALL);
        BigDecimal bitThreshold = BigDecimal.valueOf(6);
        BigDecimal currThreshold = BigDecimal.valueOf(6);
        BigDecimal yuan = BigDecimal.valueOf(10496); //about £1k
        List<Ticker> data = getTicker();
        Strategy testedStrategy = new WinWinStrategy(BigDecimal.valueOf(0), yuan, bitThreshold, currThreshold);
        logger.info("BTC threshold[" + String.format("%.1f", bitThreshold) +
                "] Curr Threshold[" + String.format("%.1f", currThreshold) +
                "] Profit [" + String.format("%.4f", ((WinWinStrategy)testedStrategy).run(data)) + "]");
    }
}

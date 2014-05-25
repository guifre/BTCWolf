package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.btcwolf.agent.BTCChinaAgent;
import org.btcwolf.persistance.Serializer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

public class TradingStrategyTest {

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
        String log4jConfPath = "resources/logger.properties";
        PropertyConfigurator.configure(log4jConfPath);

        Logger logger = Logger.getLogger(AbstractTradingStrategy.class.getName());
        BigDecimal bitThreshold = BigDecimal.valueOf(5);
        BigDecimal currThreshold = BigDecimal.valueOf(3);
        BigDecimal yuan = BigDecimal.valueOf(1496); //about £1k
        List<Ticker> data = getTicker();
        TradingStrategy testedStrategy = new WinWinTradingStrategy(new BTCChinaAgent(), bitThreshold, currThreshold);
        logger.info("BTC threshold[" + String.format("%.1f", bitThreshold) +
                "] Curr Threshold[" + String.format("%.1f", currThreshold) +
                "] Profit [" + String.format("%.4f", ((WinWinTradingStrategy)testedStrategy).run(data)) + "]");
    }
}

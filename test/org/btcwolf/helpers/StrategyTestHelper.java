/*
 * BTCWolf Copyright (C) 2014 Guifre Ruiz <guifre.ruiz@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.btcwolf.helpers;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.TradingStrategy;
import org.btcwolf.strategy.TradingStrategyProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class StrategyTestHelper {

    public static void runTurtleTest(int turtleSpeed, int[] indexes, int amount, MarketExchangeAgent testerAgent, TestStrategyProvider strategyProvider) {

        //setup
        BigDecimal cny = BigDecimal.valueOf(0);
        BigDecimal btc = BigDecimal.valueOf(0.02);
        testerAgent.setBalance(cny, btc);
        testerAgent.setDataRange(indexes);

        //run
        BigDecimal lastAsk = runTest(testerAgent, strategyProvider);

        //validation
        BigDecimal finalMoney = testerAgent.getBitCoinBalance()
                .add(testerAgent.getCurrencyBalance().divide(lastAsk, 80, BigDecimal.ROUND_HALF_EVEN));
        BigDecimal profit = finalMoney.subtract(btc);
        if (profit.compareTo(BigDecimal.ZERO) == 1) {
            System.out.print("OK ");
        } else {
            System.out.print("KOO ");
        }
        System.out.println("speed [" + turtleSpeed + "] op div [" + amount + "] start money [" + String.format("%f.4", btc.doubleValue()) + "]" +
                " end money [" + String.format("%f.4", finalMoney.doubleValue()) + "][" +
                " profit [" + String.format("%f.4", profit) + "] [" +
                String.format("%f.1", finalMoney.divide(btc, 80, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100))) + "]%]" +
                " index [" + indexes[0] + "-" + indexes[1] + "] dynamic strategy [" + strategyProvider.isSwitchStrategy() + "]");
    }

    public static int[] getIndexes(int max) {
        int minimalTickersAmount = 1000;
        Random rand = new Random(System.nanoTime());
        int s = rand.nextInt(max);
        int f = rand.nextInt(max);
        while (s > f || f - s < minimalTickersAmount ) {
            s = rand.nextInt(max);
            f = rand.nextInt(max);
        }
        return new int[] {s, f};
    }

    private static BigDecimal runTest(TraderAgent testerAgent, TradingStrategyProvider testedStrategy) {
        BigDecimal lastAsk = null;
        //run
        Ticker ticker = testerAgent.pollTicker();
        while(ticker != null) {
            testedStrategy.getStrategy().onTickerReceived(ticker);
            lastAsk = ticker.getAsk();
            ticker = testerAgent.pollTicker();
        }
        return lastAsk;
    }
}

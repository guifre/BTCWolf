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

package org.btcwolf.strategy;

import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.impl.ExponentialMovingAverageStrategy;
import org.btcwolf.strategy.impl.SimpleWinWinTradingStrategy;
import org.btcwolf.strategy.impl.TurtleTradingStrategy;

import java.math.BigDecimal;

public class TradingStrategyFactory {

    private final boolean useTwitter;

    private final TraderAgent traderAgent;
    private TradingStrategy strategy;

    public TradingStrategyFactory(TraderAgent traderAgent, boolean useTwitter) {
        this.traderAgent = traderAgent;
        //this.strategy = buildTurtleStrategy(traderAgent);
        //this.strategy = getDefaultWinWinStrategy();
        this.strategy = buildExponentialMovingAverageStrategy();
        this.useTwitter = useTwitter;
    }

    public TradingStrategy buildWinWinStrategy() {
        return new SimpleWinWinTradingStrategy(this, traderAgent, useTwitter);
    }

    public TradingStrategy buildTurtleStrategy(TraderAgent traderAgent) {
        return buildTurtleStrategy(traderAgent, 4, 2);
    }

    public TradingStrategy buildTurtleStrategy(TraderAgent traderAgent, int turtleSpeed, int amountToSell) {
        return  new TurtleTradingStrategy(  this, traderAgent,  turtleSpeed,  amountToSell, useTwitter);
    }

    public TradingStrategy buildTestingWinWinStrategy(BigDecimal opThreshold) {
        return  new SimpleWinWinTradingStrategy(this, traderAgent, opThreshold, useTwitter);
    }

    public TradingStrategy buildExponentialMovingAverageStrategy() {
        return new ExponentialMovingAverageStrategy(this, traderAgent, useTwitter);
    }

    public TradingStrategy buildExponentialMovingAverageStrategy(int min, int max, boolean onlyWin) {
        return new ExponentialMovingAverageStrategy(this, traderAgent, useTwitter, min, max, onlyWin);
    }

    public void switchStrategy(TradingStrategy tradingStrategy) {
        this.strategy = tradingStrategy;
    }

    public TradingStrategy getTradingStrategy() {
        return this.strategy;
    }
}
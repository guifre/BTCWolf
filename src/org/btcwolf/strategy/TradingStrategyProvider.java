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
import org.btcwolf.strategy.impl.AdvancedStrategy;
import org.btcwolf.strategy.impl.SimpleWinWinTradingStrategy;
import org.btcwolf.strategy.impl.TurtleTradingStrategy;

import java.math.BigDecimal;

public class TradingStrategyProvider {

    private final boolean useTwitter;

    private final TraderAgent traderAgent;
    private TradingStrategy strategy;

    public TradingStrategyProvider(TraderAgent traderAgent, boolean useTwitter) {
        this.traderAgent = traderAgent;
        //this.strategy = getDefaultTurtleStrategy(traderAgent);
        this.strategy = getDefaultWinWinStrategy();
        this.useTwitter = useTwitter;
    }

    public TradingStrategy getStrategy() {
        return this.strategy;
    }

    protected TradingStrategy getDefaultWinWinStrategy() {
        return new SimpleWinWinTradingStrategy(this, traderAgent, useTwitter);
    }

    protected TradingStrategy getDefaultTurtleStrategy(TraderAgent traderAgent) {
        return geTurtleStrategy(traderAgent, 4, 2);
    }

    public TradingStrategy geTurtleStrategy(TraderAgent traderAgent, int turtleSpeed, int amountToSell) {
        return  new TurtleTradingStrategy(
                this,
                traderAgent,
                turtleSpeed,
                amountToSell,
                useTwitter
        );
    }

    public TradingStrategy getWinWinStrategy(BigDecimal opThreshold) {
        return  new SimpleWinWinTradingStrategy(this, traderAgent, opThreshold, useTwitter);
    }

    public TradingStrategy getAdvancedStrategy() {
        return new AdvancedStrategy(this, traderAgent, useTwitter);
    }

    public void switchToDefaultTurtleStrategy() {
        this.strategy = getDefaultTurtleStrategy(traderAgent);
    }

    public void switchToDefaultWinWinStrategy() {
        this.strategy = getDefaultWinWinStrategy();
    }

    public void switchToAdvancedStrategy() {
        this.strategy = getAdvancedStrategy();
    }

    public void switchStrategy(TradingStrategy tradingStrategy) {
        this.strategy = tradingStrategy;
    }

    public TradingStrategy getTradingStrategy() {
        return this.strategy;
    }
}
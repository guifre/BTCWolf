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
import org.btcwolf.strategy.impl.SimpleWinWinTradingStrategy;
import org.btcwolf.strategy.impl.TurtleTradingStrategy;

public class TradingStrategyProvider {

    private final boolean useTwitter;

    private final TraderAgent traderAgent;
    private TradingStrategy strategy;

    public TradingStrategyProvider(TraderAgent traderAgent, boolean useTwitter) {
        this.traderAgent = traderAgent;
        this.strategy = getDefaultTurtleStrategy(traderAgent);
        this.useTwitter = useTwitter;
    }

    public TradingStrategy getStrategy() {
        return this.strategy;
    }

    protected TradingStrategy getDefaultWinWinStrategy(TraderAgent traderAgent) {
        return new SimpleWinWinTradingStrategy(this, traderAgent, useTwitter);
    }

    protected TradingStrategy getDefaultTurtleStrategy(TraderAgent traderAgent) {
        return geTurtleStrategy(traderAgent, 4, 2);
    }

    protected TradingStrategy geTurtleStrategy(TraderAgent traderAgent, int turtleSpeed, int amountToSell) {
        return  new TurtleTradingStrategy(
                this,
                traderAgent,
                turtleSpeed,      //turtle speed
                amountToSell,      //amount to sell
                useTwitter
        );
    }

    public void switchToDefaultTurtleStrategy() {
        this.strategy = getDefaultTurtleStrategy(traderAgent);
    }

    public void switchToDefaultWinWinStrategy() {
        this.strategy = getDefaultWinWinStrategy(traderAgent);
    }

    public void switchStrategy() {
        if (this.strategy instanceof TurtleTradingStrategy) {
           // System.out.println("Previous strategy turtle, switching to winwin");
            this.strategy = getDefaultWinWinStrategy(this.traderAgent);
        } else if (this.strategy instanceof SimpleWinWinTradingStrategy) {
            //System.out.println("Previous strategy winwin, switching to turtle");
            this.strategy = getDefaultTurtleStrategy(this.traderAgent);
        }
    }

    public TradingStrategy getTradingStrategy() {
        return this.strategy;
    }
}
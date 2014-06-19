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

    private static final boolean USE_TWITTER = true;

    private final TraderAgent traderAgent;
    private TradingStrategy strategy;

    public TradingStrategyProvider(TraderAgent traderAgent) {
        this.traderAgent = traderAgent;
        this.strategy = getDefaultTurtleStrategy(traderAgent);

    }

    public TradingStrategy getStrategy() {
        return this.strategy;
    }

    protected TradingStrategy getDefaultWinWinStrategy(TraderAgent traderAgent) {
        return new SimpleWinWinTradingStrategy(this, traderAgent, USE_TWITTER);
    }


    protected TradingStrategy getDefaultTurtleStrategy(TraderAgent traderAgent) {
        return  new TurtleTradingStrategy(
                this,
                traderAgent,
                        4,      //turtle speed
                        2,      //amount to sell
                USE_TWITTER
        );
    }

    public void switchToDefaultTurtleStrategy() {
        this.strategy = getDefaultTurtleStrategy(traderAgent);
    }
    public void switchToDefaultWinWInStrategy() {
        this.strategy = getDefaultWinWinStrategy(traderAgent);
    }
}
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

import org.btcwolf.agent.TraderAgent;
import org.btcwolf.strategy.TradingStrategy;
import org.btcwolf.strategy.TradingStrategyFactory;

public class TestStrategyFactory extends TradingStrategyFactory {

    private boolean switchStrategy;

    public TestStrategyFactory(TraderAgent traderAgent, boolean switchStrategy) {
        super(traderAgent, false);
        this.switchStrategy = switchStrategy;
    }

    public boolean isSwitchStrategy() {
        return this.switchStrategy;
    }

    @Override
    public void switchStrategy(TradingStrategy tradingStrategy) {
        if (switchStrategy) {
            super.switchStrategy(tradingStrategy);
        }
    }
}

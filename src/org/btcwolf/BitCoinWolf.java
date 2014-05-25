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

package org.btcwolf;

import org.btcwolf.strategy.Strategy;
import org.btcwolf.strategy.TradingStrategyProvider;
import org.btcwolf.agent.AgentsFactory;
import org.btcwolf.agent.TraderAgent;

/**
 * Created by guifre on 20/05/14.
 */
public class BitCoinWolf {

    public static void main(String[] args) {
        Strategy tradingStrategy = TradingStrategyProvider.getDefaultStrategy();
        TraderAgent trader = AgentsFactory.buildTraderAgent(tradingStrategy);
        trader.run();
    }
}

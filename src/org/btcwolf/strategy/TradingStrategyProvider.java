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

import java.math.BigDecimal;

public class TradingStrategyProvider {

    public static final TradingStrategy getDefaultWinWinStrategy(TraderAgent traderAgent) {
        return new WinWinTradingStrategy(traderAgent, BigDecimal.valueOf(2), BigDecimal.valueOf(2));
    }

    public static final TradingStrategy getAgent(TraderAgent traderAgent, BigDecimal opBitCoinThreshold, BigDecimal opCurrencyThreshold) {
        return new WinWinTradingStrategy(traderAgent, opBitCoinThreshold, opCurrencyThreshold);
    }
}

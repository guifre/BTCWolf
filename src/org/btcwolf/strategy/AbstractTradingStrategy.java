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

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.log4j.Logger;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.twitter.TwitterAgent;

import java.math.BigDecimal;

import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

public abstract class AbstractTradingStrategy implements TradingStrategy {

    static final Logger logger = Logger.getLogger(AbstractTradingStrategy.class);

    final TraderAgent traderAgent;
    final TwitterAgent twitterAgent;

    BigDecimal totalProfit;

    public AbstractTradingStrategy(TraderAgent traderAgent, TwitterAgent twitterAgent) {
        this.traderAgent = traderAgent;
        this.totalProfit = valueOf(0);
        this.twitterAgent = twitterAgent;
    }

    abstract BigDecimal getBitCoinsToSell();

    abstract BigDecimal getBitCoinsToBuy();

    abstract void analyzeTicker(Ticker ticker);

    public void onTickerReceived(Ticker ticker) {
        logger.debug("New " + ticker);
        analyzeTicker(ticker);

        BigDecimal bitCoinsToBuy = getBitCoinsToBuy();

        if (bitCoinsToBuy.compareTo(ZERO) == 1) {
            buyBitCoins(bitCoinsToBuy);
        }

        BigDecimal currencyToBuy = getBitCoinsToSell();
        if (currencyToBuy.compareTo(ZERO) == 1) {
            buyCurrency(currencyToBuy);
        }
    }

    void buyBitCoins(BigDecimal bitCoinsToBuy) {
        BigDecimal myCurrency = traderAgent.getCurrencyBalance();
        if (myCurrency.compareTo(ZERO) == 0) {
            return;
        }
        logger.info("Placing order BID from [" + bitCoinsToBuy + "] YU");
        String orderResult = traderAgent.placeOrder(BID, bitCoinsToBuy);
        logger.info("Order of BID [ " + bitCoinsToBuy + "]YU placed, result [" + orderResult + "]");
    }

    void buyCurrency(BigDecimal bitCoinsToSell) {
        BigDecimal myBitCoins = traderAgent.getBitCoinBalance();
        if (myBitCoins.compareTo(ZERO) == 0) {
            return;
        }
        logger.info("Placing order ASK of [" + bitCoinsToSell + "] BTC");
        String orderResult = traderAgent.placeOrder(ASK, bitCoinsToSell);
        logger.info("Order of ASK [ " + bitCoinsToSell + "]BTC placed, result [" + orderResult + "]");
    }
}

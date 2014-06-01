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
import org.btcwolf.agent.TraderAgent;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class WinWinTradingStrategy extends AbstractTradingStrategy {


    private final BigDecimal opBitCoinThreshold;
    private final BigDecimal opCurrencyThreshold;

    private BigDecimal bitCoinsToSell = BigDecimal.valueOf(0d);
    private BigDecimal bitCoinsToBuy = BigDecimal.valueOf(0d);

    private BigDecimal previousAskUsed = BigDecimal.ZERO;
    private BigDecimal previousBidUsed = BigDecimal.ZERO;

    public WinWinTradingStrategy(TraderAgent traderAgent, BigDecimal opBitCoinThreshold, BigDecimal opCurrencyThreshold) {
        super(traderAgent);
        this.opBitCoinThreshold = opBitCoinThreshold;
        this.opCurrencyThreshold = opCurrencyThreshold;
    }

    @Override
    BigDecimal getBitCoinsToSell() {
        return bitCoinsToSell;
    }

    @Override
    BigDecimal getBitCoinsToBuy() {
        return bitCoinsToBuy;
    }

    @Override
    void analyzeTicker(Ticker ticker) {
        bitCoinsToBuy = BigDecimal.valueOf(0);
        bitCoinsToSell = BigDecimal.valueOf(0);
        computeWorthinessBuyingBitCoins(ticker);
        computeWorthinessSellingBitCoins(ticker);
    }

    private void computeWorthinessSellingBitCoins(Ticker ticker) {

        BigDecimal myBitCoins = this.traderAgent.getBitCoinBalance();
        BigDecimal priceDifference = previousAskUsed.subtract(ticker.getAsk());
        if (ticker.getAsk().compareTo(previousAskUsed.add(opCurrencyThreshold)) == 1 &&
                myBitCoins.compareTo(ZERO) == 1) { // new ask higher than the last one plus the threshold and be have money

            BigDecimal opProfit = priceDifference.multiply(myBitCoins);
            bitCoinsToSell = myBitCoins.multiply(ticker.getAsk());
            totalProfit = totalProfit.add(priceDifference);

            log("Placed order ASK [" + myBitCoins + "]BTC to YU for [" + String.format("%.1f", ticker.getAsk()) +
                    "]. Last used [" + String.format("%.1f", previousAskUsed + "]. Profit %[" +
                    String.format("%.1f", priceDifference)+"]. Net[" + String.format("%.4f", (opProfit)))+ "]");


            previousAskUsed = ticker.getAsk();
            previousBidUsed = ticker.getBid();
        }
        logger.debug("Ask [" + ticker.getAsk() + "] previous [" + previousAskUsed + "] profit of [" + String.format("%.4f", priceDifference) + "] current profit [" + String.format("%.4f", totalProfit) + "]"+ "] threshold [" + String.format("%.4f",opCurrencyThreshold.doubleValue()) + "].");
    }


    private void computeWorthinessBuyingBitCoins(Ticker ticker) {

        BigDecimal myCurrency = this.traderAgent.getCurrencyBalance();
        BigDecimal priceDifference = previousBidUsed.subtract(ticker.getBid());

        if ( (previousBidUsed.add(opBitCoinThreshold)).compareTo(ticker.getBid()) == 1 &&
                myCurrency.compareTo(ZERO) == 1) { // new bid is lower than the last one plus the threshold and be have money

            BigDecimal opProfit = priceDifference.multiply(myCurrency);
            bitCoinsToBuy = myCurrency.multiply(ticker.getBid());
            totalProfit = totalProfit.add(priceDifference);

            log("Placed order of BID [" + String.format("%.1f",myCurrency) + "]YU to [" + String.format("%.4f",bitCoinsToBuy) + "BTC for [" + String.format("%.1f", ticker.getBid()) +
                    "]. Last used [" + String.format("%.1f", previousBidUsed + "]. Profit %[" +
                    String.format("%.1f", priceDifference) + "]. Net[" + String.format("%.4f", (opProfit))) + "]");

            previousAskUsed = ticker.getAsk();
            previousBidUsed = ticker.getBid();
        }
        logger.debug("Bid [" + ticker.getBid() + "] previous [" + previousBidUsed + "] profit of [" + String.format("%.4f", priceDifference) + "] current profit [" + String.format("%.4f", totalProfit) + "] threshold [" + String.format("%.4f",opBitCoinThreshold.doubleValue()) + "].");
    }

    private void log(String message) {
        logger.info(message);
        twitterAgent.publish(message);
    }
}

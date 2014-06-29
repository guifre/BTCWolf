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

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.OpenOrders;
import com.xeiam.xchange.dto.trade.Wallet;
import org.apache.log4j.Logger;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.persistance.Serializer;
import org.btcwolf.persistance.plot.Plotting;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.xeiam.xchange.dto.Order.OrderType.ASK;
import static com.xeiam.xchange.dto.Order.OrderType.BID;

public class MarketExchangeAgent implements TraderAgent {

    private  final Logger LOGGER = Logger.getLogger(MarketExchangeAgent.class);

    private final List<Ticker> data;

    private int index = 0;

    private BigDecimal mCurrency;
    private BigDecimal mBitCoins;

    private int startTicker;
    private int finalTicker;

    private Plotting plotting;

    public MarketExchangeAgent(BigDecimal bitcoins, BigDecimal currency, Plotting plotting) {
        this(bitcoins, currency);
        this.plotting = plotting;
    }

    public MarketExchangeAgent(BigDecimal bitcoins, BigDecimal currency) {
        mBitCoins = bitcoins;
        mCurrency = currency;
        LOGGER.info("init wallet of [" + mBitCoins + "]BTC and [" + mCurrency + "]CNY");
        try {
            this.data = Serializer.read();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can not read xml file");
        }
        this.startTicker = 0;
        this.finalTicker = data.size();
    }

    public void setDataRange(int[] indexes) {
        this.startTicker = indexes[0];
        this.finalTicker = indexes[1];
        this.index = startTicker;
    }

    @Override
    public Ticker pollTicker() {
        if (index >= finalTicker -1) {
            return null;
        }
        LOGGER.info("new ticker [" + data.get(index).toString() + "]");
        return data.get(index++);
    }

    @Override
    public String placeOrder(Order.OrderType orderType, BigDecimal amount, Ticker ticker) {
        LOGGER.info("placed order [" + orderType + "] of [" + amount + "]");
        if (orderType == ASK) {
            if (amount.compareTo(mBitCoins) == 1) {
                LOGGER.info("no money to  [" + orderType + "] of [" + amount + "]");
                return "KO";
            }
            mCurrency = mCurrency.add(amount.multiply(ticker.getAsk()));
            mBitCoins = mBitCoins.subtract(amount);
            plotting.getPlottingDataProvider().addOp(ticker.getAsk());

        } else if (orderType == BID) {
            if (amount.multiply(ticker.getBid()).compareTo(mCurrency) == 1) {
                LOGGER.info("ERROR no money to  [" + orderType + "] of [" + amount.multiply(ticker.getBid()) + "] only [" + mCurrency);
                return "KO";
            }
            mBitCoins = mBitCoins.add(amount);
            mCurrency = mCurrency.subtract(amount.multiply(ticker.getBid()));
            plotting.getPlottingDataProvider().addOp(ticker.getBid());
        }
        LOGGER.info("wallet of [" + mBitCoins + "]BTC and [" + mCurrency + "]CNY");
        return "ok";
    }

    @Override
    public List<Wallet> getWallets() {
        return null;
    }

    @Override
    public BigDecimal getCurrencyBalance() {
        return this.mCurrency;
    }

    @Override
    public BigDecimal getBitCoinBalance() {
        return this.mBitCoins;
    }
    public int  getTickers() {
        return this.data.size();
    }

    @Override
    public OpenOrders getOpenOrders() {
        List orderList = new ArrayList(1);
        return new OpenOrders(orderList);
    }

    @Override
    public Trades getTrades() {
        return null;
    }

    @Override
    public CurrencyPair getCurrencyPair() {
        return null;
    }

    @Override
    public boolean cancelLimitOrder(LimitOrder limitOrder) {
        return true;
    }


    public void setBalance(BigDecimal cny, BigDecimal btc) {
        this.mCurrency = cny;
        this.mBitCoins = btc;
    }
}
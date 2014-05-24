package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.valueOf;

/**
 * Created by guifre on 20/05/14.
 */
public abstract class AbstractStrategy implements Strategy {

    int totalNumberOfTransactions = 0;
    BigDecimal transactionFee;
    BigDecimal mCurrency;
    BigDecimal mBitCoins;
    BigDecimal totalProfit;

    public AbstractStrategy(BigDecimal transactionFee, BigDecimal startCurrency) {
        this.transactionFee = transactionFee;
        this.mCurrency = startCurrency;
        this.mBitCoins = valueOf(0);
        this.totalProfit = valueOf(0);
        this.totalProfit = valueOf(0);
    }

    abstract BigDecimal getBitCoinsToSell();
    abstract BigDecimal getBitCoinsToBuy();

    abstract void onReceiveTicker(Ticker ticker);


    void process(Ticker ticker) { //main method that triggers the logic we apply fee

        onReceiveTicker(ticker);

        BigDecimal bitCoinsToBuy = getBitCoinsToBuy();

        if (bitCoinsToBuy.doubleValue() > 0d) {
            buyBitCoins(bitCoinsToBuy, ticker);
        }

        BigDecimal bitCoinsToSell = getBitCoinsToSell();
        if (bitCoinsToSell.doubleValue() > 0d) {
            sellBitCoins(bitCoinsToSell, ticker);
        }
    }

    public BigDecimal run(List<Ticker> list) {
        for (Ticker ticker : list) {
            process(ticker);
        }
        return this.totalProfit;
    }

    void buyBitCoins(BigDecimal bitCoinsToBuy, Ticker ticker) {
        if (this.mCurrency.doubleValue() == 0d) {
            return;
        }
        BigDecimal boughtBitCoins = bitCoinsToBuy.divide(ticker.getBid(), 20, ROUND_DOWN);
        this.mBitCoins = this.mBitCoins.add(boughtBitCoins);
        this.mCurrency = BigDecimal.valueOf(0);
        this.totalNumberOfTransactions++;
        System.out.println("BTC["+this.mBitCoins+"] Yu["+this.mCurrency+"]\n" );
    }

    void sellBitCoins(BigDecimal bitCoinsToSell, Ticker ticker) {
        if (this.mBitCoins.doubleValue() == 0d) {
            return;
        }
        BigDecimal currencyBought = bitCoinsToSell.multiply(ticker.getAsk());
        this.mCurrency = currencyBought;
        this.mBitCoins = BigDecimal.valueOf(0);
        this.totalNumberOfTransactions++;
        System.out.println("BTC["+this.mBitCoins+"] Yu["+this.mCurrency+"]\n" );
    }
}

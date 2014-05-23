package org.btcwolf.persistance;

import com.xeiam.xchange.currency.CurrencyPair;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by guifre on 23/05/14.
 */
public class MyTicker {

    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal volume;
    private BigDecimal last;
    private java.util.Date timestamp;
    private CurrencyPair currencyPair;



    public MyTicker() {
    }

    public MyTicker(CurrencyPair currencyPair, BigDecimal bid, BigDecimal ask, BigDecimal high, BigDecimal low, BigDecimal volume, Date timestamp, BigDecimal last) {
        this.bid = bid;
        this.ask = ask;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.timestamp = timestamp;
        this.last = last;
        this.currencyPair = currencyPair;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public BigDecimal getLast() {
        return last;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Ticker [" +
                "currencyPair=" + currencyPair +
                ", bid=" + bid +
                ", ask=" + ask +
                ", high=" + high +
                ", low=" + low +
                ", last=" + last +
                ", volume=" + volume +
                ", timestamp=" + timestamp +
                ']';
    }
}

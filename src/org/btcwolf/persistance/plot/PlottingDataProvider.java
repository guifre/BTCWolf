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

package org.btcwolf.persistance.plot;

import javafx.scene.chart.XYChart;

import java.math.BigDecimal;

import static javafx.scene.chart.XYChart.Series;

/**
 * Created by guifre on 28/06/14.
 */
public class PlottingDataProvider {

    private Series bid;
    private Series ask;
    private final Series shortEMA;

    private final Series longEMA;
    private int count;

    private double max;
    private double min;

    public PlottingDataProvider() {
        this.ask = new Series();
        this.bid = new Series();
        this.shortEMA = new Series();
        this.longEMA = new Series();
        this.max = -1;
        this.min = -1;
    }

    public void add(BigDecimal bid, BigDecimal ask, BigDecimal shortEMA, BigDecimal longEMA) {
        if (ask.doubleValue() > max) {
            max = ask.doubleValue();
        }
        if (ask.doubleValue() < min) {
            min = ask.doubleValue();
        }
        if (bid.doubleValue() > max) {
            max = bid.doubleValue();
        }
        if (bid.doubleValue() < min) {
            min = bid.doubleValue();
        }
        if (max == -1 || min == -1) {
            min = bid.doubleValue();
            max = bid.doubleValue();
        }
        this.ask.getData().add(new XYChart.Data(count, ask.doubleValue()));
        this.bid.getData().add(new XYChart.Data(count, bid.doubleValue()));
        if (shortEMA != null) {
            this.shortEMA.getData().add(new XYChart.Data(count, shortEMA.doubleValue()));
        }
        if (longEMA != null) {
            this.longEMA.getData().add(new XYChart.Data(count++, longEMA.doubleValue()));
        }
    }

    public Series getBid() {
        return bid;
    }

    public Series getLongEMA() {
        return longEMA;
    }

    public Series getShortEMA() {
        return shortEMA;
    }

    public double getMax() {
        return this.max;
    }
    public double getMin() {
        return this.min;
    }

    public Series getAsk() {
       return ask;
    }
}

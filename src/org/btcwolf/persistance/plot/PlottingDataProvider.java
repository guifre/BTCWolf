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
    private int count;

    private double max;
    private double min;

    public PlottingDataProvider() {
        this.ask = new Series();
        this.bid = new Series();
    }

    public void add(BigDecimal b, BigDecimal a) {
        if (a.doubleValue() > max) {
            max = a.doubleValue();
        }
        if (a.doubleValue() < min) {
            min = a.doubleValue();
        }
        if (b.doubleValue() > max) {
            max = b.doubleValue();
        }
        if (b.doubleValue() < min) {
            min = b.doubleValue();
        }

        ask.getData().add(new XYChart.Data(count, a.doubleValue()));
        bid.getData().add(new XYChart.Data(count++, b.doubleValue()));
    }

    public Series getLine() {
        return bid;
    }


    public double getMax() {
        return this.max;
    }
    public double getMin() {
        return this.min;
    }

    public Series getLine2() {
       return ask;
    }
}

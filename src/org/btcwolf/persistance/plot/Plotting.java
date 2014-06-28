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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;

public class Plotting  extends Application {

    public PlottingDataProvider getPlottingDataProvider() {
        return plottingDataProvider;
    }

    private PlottingDataProvider plottingDataProvider;


    public Plotting() {
        plottingDataProvider = new PlottingDataProvider();
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Line Chart Sample");
        //defining the axes
        final NumberAxis yAxis = new NumberAxis();
        final NumberAxis xAxis = new NumberAxis(plottingDataProvider.getMin(), plottingDataProvider.getMax(), 0.1);
        xAxis.setLabel("Number of Month");
        //creating the chart
        final LineChart<Number,Number> lineChart =  new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.getData().addAll(plottingDataProvider.getLine(), plottingDataProvider.getLine2());
         Scene scene  = new Scene(lineChart,800,600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
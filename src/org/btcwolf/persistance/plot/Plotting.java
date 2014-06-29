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
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
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

        stage.setTitle("BTCWolf Chart");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis(plottingDataProvider.getMin(), plottingDataProvider.getMax(), 0.1);
        xAxis.setLabel("Time");
        //creating the chart
        final LineChart<Number,Number> lineChart =  new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setCreateSymbols(false);

        lineChart.getData().addAll(
                plottingDataProvider.getBid(),
                plottingDataProvider.getAsk(),
                plottingDataProvider.getShortEMA(),
                plottingDataProvider.getLongEMA()

        );

//        final NumberAxis x2Axis = new NumberAxis(0, 10, 1);
//        final NumberAxis y2Axis = new NumberAxis(-100, 500, 100);
        final ScatterChart sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        sc.getData().addAll(plottingDataProvider.getOps());
        SplitPane splitPane1 = new SplitPane();
        StackPane stackpane = new StackPane();
        stackpane.getChildren().add(sc);
        stackpane.getChildren().add(lineChart);

        splitPane1.setOrientation(Orientation.VERTICAL);
        splitPane1.getItems().addAll(stackpane);
        splitPane1.setDividerPosition(0, 1);

        Scene scene = new Scene(splitPane1, 1000, 1000);

        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
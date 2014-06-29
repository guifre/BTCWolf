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
import javafx.scene.chart.XYChart;
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
        //lineChart.setCreateSymbols(false);

        lineChart.getData().addAll(
                plottingDataProvider.getBid(),
                plottingDataProvider.getAsk(),
                plottingDataProvider.getShortEMA(),
                plottingDataProvider.getLongEMA(),
                plottingDataProvider.getOpsA(),
                plottingDataProvider.getOpsB()

        );

//        final ScatterChart sc = new ScatterChart<Number,Number>(xAxis,yAxis);
//        sc.getData().addAll(plottingDataProvider.getOps());
//
//        sc.setLegendVisible(false);
//        sc.setAnimated(false);
//
//        lineChart.setLegendVisible(false);
//        lineChart.setAnimated(false);
//
//        configureOverlayChart(sc);
//        configureOverlayChart(lineChart);
//
//        StackPane stackpane = new StackPane();
//        stackpane.getChildren().add(sc);
//        stackpane.getChildren().add(lineChart);

        Scene scene = new Scene(lineChart, 1200, 1000);

        stage.setScene(scene);
        stage.show();

    }
    private void configureOverlayChart(final XYChart chart) {
        chart.setAlternativeRowFillVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.getXAxis().setVisible(false);
        chart.getYAxis().setVisible(false);

        chart.getStylesheets().addAll(".chart-plot-background { \n" +
                "  -fx-background-color: transparent; \n" +
                "}\n" +
                ".default-color0.chart-series-line { \n" +
                "  -fx-stroke: forestgreen; \n" +
                "}");
    }
    public static void main(String[] args) {
        launch(args);
    }

}
package org.btcwolf.strategy;

import org.btcwolf.persistance.HistoricalDataProvider;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by guifre on 20/05/14.
 */
public class StrategyTest {

    Double curr;
    Double diff = 2d;
    Double profit = 0d;
    boolean buy = true;

    @Test
    public void testHistoricData() throws Exception {
        new StrategyTest().test();
    }

    private HistoricalDataProvider dataProvider;

    public StrategyTest() throws IOException {
        dataProvider = new HistoricalDataProvider();
    }



    private List<Double> getRandomSubset(List<Double> data) {
        Random rand = new Random(System.currentTimeMillis()); // would make this static to the class
        int start = 0;
        int end = 0;
        while (end - start < data.size()/2) {
            start = rand.nextInt(data.size());
            end = rand.nextInt(data.size());
        }
        List subsetList = new ArrayList(end-start);
        for (int i = start; i < end; i++) {
            subsetList.add(data.get(i));
        }
        return subsetList;
    }

    @Test
    public void testSimpleStrategy() throws IOException {
        int fee = 0;
        double startDollars = 500;
        double opThreshold = 2;
        dataProvider = new HistoricalDataProvider();
        List<Double> data = dataProvider.getData();
        List<Double> rand = null;
        SimpleStrategy strategy;
        strategy = new SimpleStrategy(fee, startDollars, opThreshold);
        rand = getRandomSubset(data);
        double cu = strategy.run(rand);
        System.out.println(cu);
    }

    @Test
    public void testHistoricStrategy() throws IOException {
        int fee = 1;
        double startDollars = 500;
        int histricThreshold = 0;
        double simpleThreshold = 0.1;
        dataProvider = new HistoricalDataProvider();
        List<Double> data = dataProvider.getData();
        List<Double> rand = null;
        HistoricTrendStrategy histor = new HistoricTrendStrategy(fee, startDollars, histricThreshold);
        SimpleStrategy simple = new SimpleStrategy(fee, startDollars, simpleThreshold);

        rand = getRandomSubset(data);

        double histM = histor.run(rand);
        double simM = simple.run(rand);
        System.out.println("historic " + histM + " simple " + simM );

    }

    @Test
    public void testNewApproach() throws IOException {
        double fee = 0.5;
        double startDollars = 500;
        int histricThreshold = 0;
        double simpleThreshold = 0.1;
        dataProvider = new HistoricalDataProvider();
        List<Double> data = dataProvider.getData();
        List<Double> rand = null;

        AveragePrice averageStrategy = new AveragePrice(fee, startDollars);

        rand = getRandomSubset(data);
        double histM = averageStrategy.run(rand);
        System.out.println("historic " + histM  );
    }

    @Test
    public void test() throws IOException {
        int fee = 0;
        double cBitcoins =1500;
        double opThreshold = 1;

        dataProvider = new HistoricalDataProvider();
        //dataProvider.persistData();
        List<Double> data = dataProvider.getData();
        List<Double> rand = null;
        SimpleStrategy strategy;
        for (int i  =0; i <200; i++) {     // run the test 1000 times
            double inter = 0.1;double max = 0;double bestT=-1d;
            while (inter < 10) { //find the best threshold

                double won=0; int retries=100;
                for (int k=0;k<retries; k++) {
                    strategy = new SimpleStrategy(fee, cBitcoins, opThreshold);
                    rand = getRandomSubset(data);
                    double cu = strategy.run(rand);
                    won+=cu;
                }
                if (won/retries > max) {
                    bestT = inter;max = won/retries;
                }
                inter = inter + 0.1;
            }
            //System.out.println("Threshold " + String.format("%.2f",bestT) + " max won " + String.format("%.2f",max)  + " for num samples " + rand.size());

            double won = 0;int retries=10;
            for (int j  =0; j <retries; j++) {//run with the best 100 times and get the average won
                strategy = new SimpleStrategy(fee, cBitcoins, bestT);
                rand = getRandomSubset(data);
                won += strategy.run(rand);
            }
            System.out.println("Threshold " + String.format("%.2f",bestT) + " won on av " + String.format("%.2f",won/retries)  + " elems " + rand.size());
        }
    }
}

package org.btcwolf.strategy;

import com.xeiam.xchange.examples.bitcoinwolf.persitance.HistoricalDataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by guifre on 20/05/14.
 */
public class StrategyTester {

    Double curr;
    Double diff = 2d;
    Double profit = 0d;
    boolean buy = true;

    public static void main(String[] args) throws Exception {
        new StrategyTester().test();
    }
    private HistoricalDataProvider dataProvider;

    public StrategyTester() throws IOException {
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
    private void test() throws IOException {
        List<Double> data = dataProvider.getData();
        List<Double> rand = null;
        SimpleStrategy strategy;

        for (int i  =0; i <1000; i++) {     // run the test 1000 times
            double inter = 0.1;double max = 0;double bestT=-1d;
            while (inter < 10) { //find the best threshold

                double won=0; int retries=1000;
                for (int k=0;k<retries; k++) {
                    strategy = new SimpleStrategy();
                    strategy.opThreshold = inter;
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
                strategy = new SimpleStrategy();
                strategy.opThreshold = bestT;
                rand = getRandomSubset(data);
                won += strategy.run(rand);
            }
            System.out.println("Threshold " + String.format("%.2f",bestT) + " won on av " + String.format("%.2f",won/retries)  + " elems " + rand.size());
        }
    }
}

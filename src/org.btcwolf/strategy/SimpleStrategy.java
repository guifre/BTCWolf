
package org.btcwolf.strategy;

import java.io.IOException;
import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public class SimpleStrategy implements Strategy {


        int fee = 2;
        private int Noperations = 0;
        private Double cBitcoints;
        Double opThreshold;
        Double profit = 0d;
        boolean haveToBuy = false;

        private  void sell(double value) {
            double prof = (value - cBitcoints - fee);
            //System.out.println("Selling for [" + value + "] bought for [" + cBitcoints + "] got [+" + prof + "]");
            cBitcoints = value;
            haveToBuy = false;
            profit = profit + prof;
        }
        private  void buy(double value) {
            double prof = (cBitcoints - value - fee);
            //System.out.println("Buying for [" + value + "] sold for [" + cBitcoints + "] got [+" + prof + "]");
            cBitcoints = value;
            profit = profit + prof;
            haveToBuy = true;
        }

        public double run(List<Double> a) throws IOException {
            profit=0d;
            haveToBuy = true;
            cBitcoints = a.get(0);
            for (double e : a) {
                double value = e;
                if (haveToBuy) {
                    if (value >= (cBitcoints + opThreshold)) {
                        sell(value);
                    }
                } else {
                    if (value <= (cBitcoints - opThreshold)) {
                        buy(value);
                    }
                }
            }
            //System.out.println("final profit of " + profit);
            return profit;
        }
    }

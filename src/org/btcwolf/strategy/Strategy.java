package org.btcwolf.strategy;

import java.io.IOException;
import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public interface Strategy {

    public double run(List<Double> a) throws IOException;

}

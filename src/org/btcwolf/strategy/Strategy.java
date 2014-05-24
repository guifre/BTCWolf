package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;

import java.util.List;

/**
 * Created by guifre on 20/05/14.
 */
public interface Strategy {

    public java.math.BigDecimal run(List<Ticker> a);

}

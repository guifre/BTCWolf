package org.btcwolf.strategy;

import com.xeiam.xchange.dto.marketdata.Ticker;

/**
 * Created by guifre on 20/05/14.
 */
public interface Strategy {

    public void onTickerReceived(Ticker ticker);

}

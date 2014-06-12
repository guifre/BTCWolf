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

package org.btcwolf.agent.impl;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.btce.v3.BTCEExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import org.apache.log4j.Logger;
import org.btcwolf.agent.AbstractAgent;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.persistance.SettingsProvider;

public class BTCEAgent extends AbstractAgent {

    private static final Logger logger = Logger.getLogger(TraderAgent.class.getName());

    private static final String KEY_ENV = "Key";
    private static final String SECRET_ENV = "Secret";

    private static final CurrencyPair CURRENCY_PAIR = CurrencyPair.BTC_USD;

    public BTCEAgent() {
        super(CURRENCY_PAIR);
    }

    protected Exchange buildExchange() {
        if (SettingsProvider.getProperty(KEY_ENV) == null ||
                SettingsProvider.getProperty(SECRET_ENV) == null) {
            String msg = "Could not find credential arguments " + KEY_ENV +  SECRET_ENV;
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        ExchangeSpecification exSpec = new ExchangeSpecification(BTCEExchange.class);
        exSpec.setApiKey(SettingsProvider.getProperty(KEY_ENV));
        exSpec.setSecretKey(SettingsProvider.getProperty(SECRET_ENV));
        return ExchangeFactory.INSTANCE.createExchange(exSpec);
    }
}
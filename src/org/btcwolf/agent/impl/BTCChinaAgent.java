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
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.currency.CurrencyPair;
import org.apache.log4j.Logger;
import org.btcwolf.agent.AbstractAgent;
import org.btcwolf.agent.TraderAgent;
import org.btcwolf.persistance.SettingsProvider;

public class BTCChinaAgent extends AbstractAgent {

    private static final String SECRET_KEY_ENV = "SecretKey";
    private static final String API_KEY_ENV = "APIKey";
    private static final String PASSWORD_ENV = "Password";

    private static final CurrencyPair CURRENCY_PAIR = CurrencyPair.BTC_CNY;

    public BTCChinaAgent() {
        super(CURRENCY_PAIR);
    }

    protected Exchange buildExchange() {
        String msg = "Could not find credential arguments " +
                SECRET_KEY_ENV + ": " + SettingsProvider.getProperty(SECRET_KEY_ENV) + ", " +
                PASSWORD_ENV + ": " + SettingsProvider.getProperty(PASSWORD_ENV) + ", " +
                API_KEY_ENV + ": " + SettingsProvider.getProperty(API_KEY_ENV);

        if (SettingsProvider.getProperty(SECRET_KEY_ENV) == null ||
                SettingsProvider.getProperty(PASSWORD_ENV) == null ||
                SettingsProvider.getProperty(API_KEY_ENV) == null) {
               logger.error(msg);
            throw new RuntimeException(msg);
        }
        ExchangeSpecification exSpec = new ExchangeSpecification(BTCChinaExchange.class);
        exSpec.setSecretKey(SettingsProvider.getProperty(SECRET_KEY_ENV));
        exSpec.setApiKey(SettingsProvider.getProperty(API_KEY_ENV));
        exSpec.setPassword(SettingsProvider.getProperty(PASSWORD_ENV));
        return ExchangeFactory.INSTANCE.createExchange(exSpec);
    }
}
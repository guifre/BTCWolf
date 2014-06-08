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

public class BTCChinaAgent extends AbstractAgent {

    private static final Logger logger = Logger.getLogger(TraderAgent.class.getName());

    private static final String SECRET_KEY_ENV = "SecretKey";
    private static final String API_KEY_ENV = "APIKey";
    private static final String PASSWORD_ENV = "Password";

    private static final CurrencyPair CURRENCY_PAIR = CurrencyPair.BTC_CNY;

    public BTCChinaAgent() {
        super(CURRENCY_PAIR);
    }

    protected Exchange buildExchange() {
        if (System.getProperty(SECRET_KEY_ENV) == null ||
                System.getProperty(PASSWORD_ENV) == null ||
                System.getProperty(API_KEY_ENV) == null) {
            String msg = "Could not find credential arguments " + SECRET_KEY_ENV + System.getProperty(SECRET_KEY_ENV) + ", " + PASSWORD_ENV+System.getProperty(PASSWORD_ENV) + ", " + API_KEY_ENV + System.getProperty(API_KEY_ENV);
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        ExchangeSpecification exSpec = new ExchangeSpecification(BTCChinaExchange.class);
        exSpec.setSecretKey(System.getProperty(SECRET_KEY_ENV));
        exSpec.setApiKey(System.getProperty(API_KEY_ENV));
        exSpec.setPassword(System.getProperty(PASSWORD_ENV));
        return ExchangeFactory.INSTANCE.createExchange(exSpec);
    }
}
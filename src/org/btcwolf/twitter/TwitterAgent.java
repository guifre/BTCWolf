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

package org.btcwolf.twitter;

import org.apache.log4j.Logger;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by guifre on 25/05/14.
 */
public class TwitterAgent {

    private static final Logger LOGGER = Logger.getLogger(TwitterAgent.class);
    private static final String CONSUMER_KEY = "ConsumerKey";
    private static final String CONSUMER_SECRET = "ConsumerSecret";
    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String TOKEN_SECRET = "TokenSecret";

    private TwitterFactory twitter;

    public TwitterAgent() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TOKEN_SECRET).setUseSSL(true);
        this.twitter = new TwitterFactory(cb.build());
    }

    public void publish(String message) {
        try {
            LOGGER.debug("publishing twitter status " + message);
            twitter.getInstance().updateStatus(message);
        } catch (TwitterException e) {
            LOGGER.error("could not publish status " + e.getMessage());
        }


    }

}

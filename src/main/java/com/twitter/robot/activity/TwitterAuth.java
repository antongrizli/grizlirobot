package com.twitter.robot.activity;

import com.twitter.robot.model.TwitterLoginModel;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.Properties;

public class TwitterAuth {
    private final static String CONSUMER_KEY = "wJju6Grakyayi2uoCk8uFm8gj";
    private final static String CONSUMER_SECRET = "WBlIMF5mfbasFUqn7Cxu2UkXcCt7hZPcMf02Mfjltzzyn2BVFn";

    private String token = null;
    private String token_secret = null;
    private boolean connect = false;

    public final static long OWNER_ID = 2284447377l;


    public Twitter getTwitter() {
        Twitter twitter = null;
        loadToken();
        if ((token != null) && (token_secret != null) && (token.length() > 0) && (token_secret.length() > 0)) {
            twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            AccessToken accessToken = new AccessToken(token, token_secret);
            twitter.setOAuthAccessToken(accessToken);
            connect = true;
        }
        return twitter;
    }

    public Twitter getTwitter(String login, String password) {
        Twitter twitter = null;
        try {
            TwitterLoginModel loginModel = new TwitterLoginModel();

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(CONSUMER_KEY)
                    .setOAuthConsumerSecret(CONSUMER_SECRET)
                    .setOAuthAccessToken(null)
                    .setOAuthAccessTokenSecret(null);
            TwitterFactory factory = new TwitterFactory(builder.build());
            twitter = factory.getInstance();

            RequestToken requestToken = twitter.getOAuthRequestToken();
            AccessToken accessToken = null;
            while (null == accessToken) {
                String pin = loginModel.loginToTwitter(requestToken.getAuthorizationURL(), login, password);
                try {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    System.out.println("Successful");
                    connect = true;
                    saveToken(accessToken.getToken(), accessToken.getTokenSecret());
                } catch (TwitterException te) {
                    if (401 == te.getStatusCode()) {
                        System.out.println("Unable to get the access token.");
                    } else {
                        te.printStackTrace();
                    }
                }
            }
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
        return twitter;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public boolean isConnect() {
        return connect;
    }

    private void saveToken(String token, String secretToken) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("config.properties");

            // set the properties value
            prop.setProperty("token", token);
            prop.setProperty("secret_token", secretToken);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void loadToken() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            token = prop.getProperty("token");
            token_secret = prop.getProperty("secret_token");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


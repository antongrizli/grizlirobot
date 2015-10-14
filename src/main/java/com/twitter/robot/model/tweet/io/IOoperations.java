package com.twitter.robot.model.tweet.io;

import com.twitter.robot.model.parser.JaxbParser;
import com.twitter.robot.model.parser.Parser;
import com.twitter.robot.model.tweet.Tweet;
import com.twitter.robot.model.tweet.Tweets;
import com.twitter.robot.model.tweet.User;
import twitter4j.Status;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Антон on 01.10.2015.
 */
public class IOoperations {
    private static Parser parser;
    private static File file;

    public static void saveXml(List<Status> statusList) {
        Tweets tweets = new Tweets();
        List<Tweet> tweetList = new ArrayList<>();
        for (Status loop : statusList) {
            Tweet tweet = new Tweet();
            User user = new User();

            tweet.setText(loop.getText());
            tweet.setCreatedTweet(new Date());
            tweet.setIdTweet(loop.getId());
            tweet.setTweetDate(loop.getCreatedAt());
            tweet.setUserName(loop.getUser().getName());

            user.setLocation(loop.getUser().getLocation());
            user.setIdUser(loop.getUser().getId());
            user.setScreenNameUser(loop.getUser().getScreenName());
            user.setNameUser(loop.getUser().getName());

            tweet.setUser(user);

            tweetList.add(tweet);
        }
        tweets.setTweetList(tweetList);
        try {
            file = new File("person.xml");
            parser = new JaxbParser();
            parser.saveObject(file, tweets);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static List<Tweet> getXml() {
        parser = new JaxbParser();
        file = new File("person.xml");
        Tweets tweets = null;
        try {
            tweets = (Tweets) parser.getObject(file, Tweets.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return tweets.getTweetList();
    }
}

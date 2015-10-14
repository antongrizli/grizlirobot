package com.twitter.robot.model.tweet;

import com.twitter.robot.model.parser.JaxbParser;
import com.twitter.robot.model.parser.Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Антон on 21.09.2015.
 */
public class Test {
    private static Parser parser;
    private static File file;

    public static void main(String[] args) throws Exception {
        setUp();
        testSaveObject();
    }

    public static void setUp() throws Exception {
        parser = new JaxbParser();
        file = new File("person.xml");
    }


    public static void testSaveObject() throws Exception {
        Tweets tweets = new Tweets();
        List<Tweet> tweetList = new ArrayList<Tweet>();
        Tweet tweet = new Tweet();
        tweet.setTweetDate(new Date());
        tweet.setCreatedTweet(new Date());
        tweet.setIdTweet(12345l);
        tweet.setText("pisun");

        User user1 = new User();
        user1.setIdUser(123l);
        user1.setLocation("Moscow");
        user1.setNameUser("Dibil");
        user1.setScreenNameUser("#ivan");

        tweet.setUser(user1);

        Tweet tweet1 = new Tweet();
        tweet1.setTweetDate(new Date());
        tweet1.setCreatedTweet(new Date());
        tweet1.setIdTweet(12343l);
        tweet1.setText("Nemitut");

        User user2 = new User();
        user2.setIdUser(124l);
        user2.setLocation("Moscow");
        user2.setNameUser("Dibil");
        user2.setScreenNameUser("#ivan");

        tweet1.setUser(user2);

        tweetList.add(tweet);
        tweetList.add(tweet1);
        tweets.setTweetList(tweetList);

        parser.saveObject(file, tweets);
        System.out.println(tweets);
    }

    public static void testGetObject() throws Exception {
        Tweets person = (Tweets) parser.getObject(file, Tweets.class);
        System.out.println(person);
        List<Tweet> list = person.getTweetList();
        for (Tweet loop : list) {
            System.out.println("Tweet "+loop);
            User user = loop.getUser();
            System.out.println("User "+user);
        }
    }
}

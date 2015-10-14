package com.twitter.robot;

import com.twitter.robot.activity.Frends;
import com.twitter.robot.activity.TwitterAuth;
import com.twitter.robot.write.WriteToXML;
import twitter4j.*;

import java.util.List;

/**
 * Created by Антон on 12.08.2015.
 */
public class MainApp {
    final static String KEY_WORD = "retweet to win since:2015-08-01 until:2015-08-13";

    public static void main(String[] args) throws TwitterException {
        Twitter twitter = new TwitterAuth().getTwitter();

        int countFiends = Frends.getCountFriends(twitter);
        int countFollowers = Frends.getCountFollowers(twitter);

        System.out.println(String.format("You have %d friends and %d followers", countFiends, countFollowers));

        try {
            Query query = new Query(KEY_WORD);
            QueryResult result;
            query.setCount(60);
            int count = 0;
            while (query != null) {
                result = twitter.search(query);
                System.out.println(result.getCount());
                List<Status> tweets = result.getTweets();
                if (WriteToXML.appendToXML(tweets) == -1) break;
               /* for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                    Thread.sleep(1000);//1 second
                    twitter.createFriendship(tweet.getUser().getScreenName());
                    Thread.sleep(2000);//2 secunds
                    twitter.retweetStatus(tweet.getId());
                }//all time 3 minutes
                */
                query = result.nextQuery();
                //Thread.sleep(5 * 1000 * 60);//5 minutes
                count++;
                if (count * 60 > 900) break;
            }
            //Если в файле уже много записей, тогда нужно перейти на проверку актуальности
            //Не актульным считается твит который добавлен в XML больше 7 дней
            //Если такой найден, то нужно отписаться от человека и от твита

            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }
}

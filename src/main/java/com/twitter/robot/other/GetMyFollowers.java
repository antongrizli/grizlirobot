package com.twitter.robot.other;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Антон on 12.08.2015.
 */
public class GetMyFollowers {
    private Twitter twitter;

    public GetMyFollowers(Twitter twitter) {
        try {
            this.twitter = twitter;
            long cursor = -1;
            IDs ids;
            System.out.println("Listing followers's ids.");

            do {
                ids = this.twitter.getFollowersIDs(cursor);

                for (long id : ids.getIDs()) {
                    System.out.println(id + " " + this.twitter.showUser(id).getName());
                }
            } while ((cursor = ids.getNextCursor()) != 0);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get followers' ids: " + te.getMessage());
            System.exit(-1);
        }
    }
}

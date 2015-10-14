package com.twitter.robot.activity;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class Frends {

    public static void createFriendship(String userName, Twitter twitter) {
        try {
            twitter.createFriendship(userName);
            System.out.println("Successfully followed [" + userName + "].");
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to follow: " + te.getMessage());
            System.exit(-1);
        }
    }

    public static void destroyedFrienship(String screenName, Twitter twitter) {
        try {
            twitter.destroyFriendship(screenName);
            System.out.println("Successfully unfollowed [" + screenName + "].");
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to unfollow: " + te.getMessage());
            System.exit(-1);
        }
    }

    public static int getCountFriends(Twitter twitter) throws TwitterException {
        long followerCursor = -1;
        IDs friends = twitter.getFriendsIDs(TwitterAuth.OWNER_ID, followerCursor);
        return friends.getIDs().length;
    }

    public static int getCountFollowers(Twitter twitter) throws TwitterException {
        long followerCursor = -1;
        IDs followerIds = twitter.getFollowersIDs(TwitterAuth.OWNER_ID, followerCursor);
        return followerIds.getIDs().length;
    }
}

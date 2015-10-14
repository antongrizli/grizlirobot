package com.twitter.robot.model.tweet;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "tweets")
@XmlType(propOrder = "tweetList")
public class Tweets {
    private List<Tweet> tweetList = new ArrayList<Tweet>();

    public List<Tweet> getTweetList() {
        return tweetList;
    }

    @XmlElement(name = "tweet")
    @XmlElementWrapper
    public void setTweetList(List<Tweet> tweetList) {
        this.tweetList = tweetList;
    }

    @Override
    public String toString() {
        return "Tweets{" +
                "tweetList=" + tweetList +
                '}';
    }
}
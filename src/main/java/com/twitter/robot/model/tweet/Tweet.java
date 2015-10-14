package com.twitter.robot.model.tweet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created by Антон on 01.10.2015.
 */
@XmlType(propOrder = {"tweetDate", "idTweet", "createdTweet", "text", "user", "userName"})
public class Tweet {
    private Date tweetDate;
    private Long idTweet;
    private Date createdTweet;
    private User user;
    private String text;
    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlElement(name = "user_name")
    public String getUserName() {
        return userName;
    }

    @XmlElement(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlAttribute(name = "date")
    public Date getTweetDate() {
        return tweetDate;
    }

    public void setTweetDate(Date tweetDate) {
        this.tweetDate = tweetDate;
    }

    @XmlAttribute(name = "id")
    public Long getIdTweet() {
        return idTweet;
    }

    public void setIdTweet(Long idTweet) {
        this.idTweet = idTweet;
    }

    @XmlElement(name = "created")
    public Date getCreatedTweet() {
        return createdTweet;
    }

    public void setCreatedTweet(Date createdTweet) {
        this.createdTweet = createdTweet;
    }

    @XmlElement(name = "user")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "tweetDate=" + tweetDate +
                ", idTweet=" + idTweet +
                ", createdTweet=" + createdTweet +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }
}

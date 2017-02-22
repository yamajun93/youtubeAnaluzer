package com.yoanalizer.model;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentQuery {

    @Id
    public String id;
    public String vid;
    public int numOfComments;
    public int numOfCommentsProcessed;
    Map<String, Integer> keywordFound;
    boolean finished;
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date createdDate = new Date();

    public Date getCreatedDate() {
        return (Date) createdDate.clone();
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public int getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(int numOfComments) {
        this.numOfComments = numOfComments;
    }

    public int getNumOfCommentsProcessed() {
        return numOfCommentsProcessed;
    }

    public void setNumOfCommentsProcessed(int numOfCommentsProcessed) {
        this.numOfCommentsProcessed = numOfCommentsProcessed;
    }

    public Map<String, Integer> getKeywordFound() {
        return keywordFound;
    }

    public void setKeywordFound(Map<String, Integer> keywordFound) {
        this.keywordFound = keywordFound;
    }
}

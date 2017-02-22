package com.yoanalizer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.yoanalizer.model.CommentQuery;

@Repository
public interface YoutubeVideoRepository extends MongoRepository<CommentQuery, String>{

}

package com.yoanalizer.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoanalizer.model.CommentQuery;
import com.yoanalizer.services.YoutubeVideoService;

@RestController
@RequestMapping("/api")
public class YoutubeAnalyzerAPIController {

    @Autowired
    private YoutubeVideoService youtubeVideoService;

    @RequestMapping("/title")
    public String getTitleById(@RequestParam("vid") String videoId) throws IOException {
        return youtubeVideoService.getVideoTitle(videoId);
    }

    @RequestMapping("/process")
    public String placeInQueue(@RequestParam("vid") String videoId, @RequestParam("keywords") List<String> keyworkds)
            throws IOException {
        youtubeVideoService.validateVideo(videoId);
        return youtubeVideoService.startProcessing(videoId, keyworkds);
    }

    @RequestMapping("/status")
    public CommentQuery getStatus(@RequestParam("token") String accessToken) throws UnsupportedEncodingException {
        String id = youtubeVideoService.verifyToken(accessToken);
        return youtubeVideoService.getStatusById(id);
    }

    @RequestMapping("/verify")
    public String verifyToken(@RequestParam("token") String accessToken) throws UnsupportedEncodingException {
        return youtubeVideoService.verifyToken(accessToken);
    }
}
package com.yoanalizer.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class YoutubeAnalyzerController {

    @RequestMapping("")
    public String mainFunction(Map<String, Object> model){
        model.put("now", LocalDateTime.now());
        return "index";
    }
}

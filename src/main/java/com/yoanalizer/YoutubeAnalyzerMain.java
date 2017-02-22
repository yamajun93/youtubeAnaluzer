package com.yoanalizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@SpringBootApplication
public class YoutubeAnalyzerMain {

    private static final String APP_NAME = "youtubeanalyzer-159412";

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

    public static void main(String[] args) {
        SpringApplication.run(YoutubeAnalyzerMain.class, args);
    }
}

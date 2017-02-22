package com.yoanalizer.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.yoanalizer.exception.InvalidTokenException;
import com.yoanalizer.exception.VideoNotFoundException;
import com.yoanalizer.model.CommentQuery;
import com.yoanalizer.repository.YoutubeVideoRepository;

@Service
public class YoutubeVideoService {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeVideoService.class);
    private static final String SECRET = "!a81ne81n3hy#a.391!banaunwpl";
    private static final String API_KEY = "AIzaSyA2Uel2GJUA2-QI_ZSGooigcULMkp_3gvo";
    private static final int PARTITION = 20;

    @Autowired
    private TaskExecutor executor;

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private YoutubeVideoRepository youtubeVideoRepository;

    public String getVideoTitle(String videoId) throws IOException {
        String url = "https://www.googleapis.com/youtube/v3/videos?id=" + videoId
                     + "&fields=items(snippet(title))&part=snippet&key=" + API_KEY;
        Document doc = Jsoup.connect(url).ignoreContentType(true).timeout(10 * 1000).get();
        String getJson = doc.text();
        try {
            return JsonPath.parse(getJson).read("$.items[0].snippet.title");
        } catch (PathNotFoundException ignored) {
            logger.warn("Video not found: <>", videoId);
            throw new VideoNotFoundException(videoId);
        }
    }

    public String startProcessing(String videoId, List<String> keywords) throws UnsupportedEncodingException {
        Map<String, Integer> valuesLookup = new HashMap<>(keywords.size());
        for(String keyword : keywords){
            valuesLookup.put(keyword, 0);
        }
        CommentQuery commentQuery = new CommentQuery();
        commentQuery.setKeywordFound(valuesLookup);
        commentQuery.setNumOfComments(0);
        commentQuery.setNumOfCommentsProcessed(0);
        commentQuery.setVid(videoId);
        CommentQuery query = youtubeVideoRepository.save(commentQuery);

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Date tomorrowDate = Date.from(tomorrow.atZone(ZoneId.systemDefault()).toInstant());

        executor.execute(() -> {
            try {
                preProcessQuery(commentQuery);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        return JWT.create()
                  .withExpiresAt(tomorrowDate)
                  .withClaim("id", query.getId())
                  .sign(Algorithm.HMAC256(SECRET));
    }

    public CommentQuery getStatusById(String id){
        return youtubeVideoRepository.findOne(id);
    }

    public String verifyToken(String accessToken) throws UnsupportedEncodingException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                                  .build();
        try {
            verifier.verify(accessToken);
            JWT jwtToken = JWT.decode(accessToken);
            Claim id = jwtToken.getClaim("id");
            return id.asString();
        } catch (JWTVerificationException ignored) {
            throw new InvalidTokenException(accessToken);
        }
    }

    public void preProcessQuery(CommentQuery commentQuery) throws IOException, InterruptedException {

        String url = "https://www.googleapis.com/youtube/v3/videos?id=" + commentQuery.getVid()
                     + "&part=statistics&key=" + API_KEY;

        Document doc = Jsoup.connect(url).ignoreContentType(true).timeout(10 * 1000).get();
        String getJson = doc.text();
        try {
            String commentCountString = JsonPath.parse(getJson).read("$.items[0].statistics.commentCount");
            commentQuery.setNumOfComments(Integer.parseInt(commentCountString));
            youtubeVideoRepository.save(commentQuery);
        } catch (PathNotFoundException ignored) {
            logger.warn("Video not found: <>", commentQuery.getVid());
            throw new VideoNotFoundException(commentQuery.getVid());
        }

        Object[] data = { commentQuery.getVid(), commentQuery.getId() };

        String scriptCommand = String.format(
                "python /Users/lineplus/Develop/youtubeAnalyzer/youtubeCommentDownloader.py %s %s",
                data);

        Runtime.getRuntime().exec(scriptCommand);
        processQuery(commentQuery);
 }

    public void processQuery(CommentQuery commentQuery){
        String comment = "";
        int numComments = 0;
        do {
            comment = template.opsForList().rightPop(commentQuery.getId());
            if(comment != null && !"eop".equals(comment)){
                for(String key : commentQuery.getKeywordFound().keySet()){
                    if(comment.contains(key)){
                        int currentValue = commentQuery.getKeywordFound().get(key);
                        commentQuery.getKeywordFound().put(key, Integer.valueOf(currentValue + 1));
                    }
                }
                numComments ++;
                if(numComments % PARTITION == 0 ){
                    commentQuery.setNumOfCommentsProcessed(numComments);
                    youtubeVideoRepository.save(commentQuery);
                }
                System.out.println(comment);
            }
        } while (!"eop".equals(comment));

        commentQuery.setNumOfComments(numComments);
        commentQuery.setNumOfCommentsProcessed(numComments);
        commentQuery.setFinished(true);
        youtubeVideoRepository.save(commentQuery);
    }

    public void validateVideo(String vid) throws IOException {
        getVideoTitle(vid);
    }

}

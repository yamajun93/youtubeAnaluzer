package com.yoanalizer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() throws IOException {
		String key = "AIzaSyA2Uel2GJUA2-QI_ZSGooigcULMkp_3gvo";
		String videoId = "9PhqF59ls08";
		String url = "https://www.googleapis.com/youtube/v3/videos?id="+ videoId + "&fields=items(snippet(title))&part=snippet&key="+ key;
		Document doc = Jsoup.connect(url).timeout(10 * 1000).get();
		int a  = 2;
	}

}

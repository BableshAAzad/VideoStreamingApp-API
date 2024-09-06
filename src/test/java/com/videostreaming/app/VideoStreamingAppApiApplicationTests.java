package com.videostreaming.app;

import com.videostreaming.app.video.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VideoStreamingAppApiApplicationTests {

	@Autowired
	private VideoService videoService;

	@Test
	void contextLoads() {
		videoService.processVideo("ff56e20a-5e0d-48c6-b2db-df133c57ed34");
	}

}

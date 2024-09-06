package com.videostreaming.app.video.service;

import com.videostreaming.app.utility.ResponseStructure;
import com.videostreaming.app.video.dto.VideoRequest;
import com.videostreaming.app.video.dto.VideoResponse;
import com.videostreaming.app.video.entity.Video;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface VideoService {

    ResponseEntity<ResponseStructure<VideoResponse>> addVideo(
            VideoRequest videoRequest);

    ResponseEntity<Resource> getVideo(String videoId);

    ResponseEntity<ResponseStructure<PagedModel<VideoResponse>>> getVideos(int page, int size);

    ResponseEntity<Resource> streamVideoRange(String videoId, String range);

    void processVideo(Video video);

    ResponseEntity<Resource> serverMasterFile(String videoId);

    ResponseEntity<Resource> serveSegment(String videoId, String segment);
}

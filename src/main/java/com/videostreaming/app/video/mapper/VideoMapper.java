package com.videostreaming.app.video.mapper;

import com.videostreaming.app.video.dto.VideoRequest;
import com.videostreaming.app.video.dto.VideoResponse;
import com.videostreaming.app.video.entity.Video;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {

    public Video mapVideoRequestToVideo(VideoRequest videoRequest, Video video){
        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setCategory(videoRequest.getCategory());
        return video;
    }

    public VideoResponse mapVideoToVideoResponse(Video video){
        return VideoResponse.builder()
                .videoId(video.getVideoId())
                .title(video.getTitle())
                .description(video.getDescription())
                .category(video.getCategory())
                .contentType(video.getContentType())
                .filePath(video.getFilePath())
                .build();
    }
}

package com.videostreaming.app.video.service;

import com.videostreaming.app.utility.ResponseStructure;
import com.videostreaming.app.video.dto.VideoRequest;
import com.videostreaming.app.video.dto.VideoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    ResponseEntity<ResponseStructure<VideoResponse>> addVideo(
            VideoRequest videoRequest,
            MultipartFile file);
}

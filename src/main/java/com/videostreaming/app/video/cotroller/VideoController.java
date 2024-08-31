package com.videostreaming.app.video.cotroller;

import com.videostreaming.app.utility.ResponseStructure;
import com.videostreaming.app.video.dto.VideoRequest;
import com.videostreaming.app.video.dto.VideoResponse;
import com.videostreaming.app.video.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/videos")
    private ResponseEntity<ResponseStructure<VideoResponse>> addVideo(
            @ModelAttribute VideoRequest videoRequest,
            @RequestParam MultipartFile file){
        return videoService.addVideo(videoRequest, file);
    }

}

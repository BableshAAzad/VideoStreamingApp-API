package com.videostreaming.app.video.cotroller;

import com.videostreaming.app.utility.ResponseStructure;
import com.videostreaming.app.video.dto.VideoRequest;
import com.videostreaming.app.video.dto.VideoResponse;
import com.videostreaming.app.video.service.VideoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class VideoController {

    private final VideoService videoService;

//    ---------------------------------------------------------------------------------------------
    @PostMapping("/videos")
    private ResponseEntity<ResponseStructure<VideoResponse>> addVideo(
           @Valid @ModelAttribute VideoRequest videoRequest){
        return videoService.addVideo(videoRequest);
    }
//    ---------------------------------------------------------------------------------------------
    @GetMapping("/videos/{videoId}/stream")
    public ResponseEntity<Resource> getVideo(@PathVariable("videoId") String videoId){
        return videoService.getVideo(videoId);
    }

//    ---------------------------------------------------------------------------------------------
   @GetMapping("/videos") // videos?page=0&size=5
    public ResponseEntity<ResponseStructure<PagedModel<VideoResponse>>> getVideos(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size){
        return videoService.getVideos(page, size);
   }
//    ---------------------------------------------------------------------------------------------
    @GetMapping("/videos/{videoId}")
    public ResponseEntity<ResponseStructure<VideoResponse>> getVideoById(
            @PathVariable String videoId){
        return videoService.getVideoById(videoId);
    }

//    ---------------------------------------------------------------------------------------------
    @GetMapping("/videos/{videoId}/stream/range")
    public ResponseEntity<Resource> streamVideoRange(
            @PathVariable String videoId,
            @RequestHeader(value = "Range", required = false) String range){
       return videoService.streamVideoRange(videoId, range);
    }

//    ---------------------------------------------------------------------------------------------
     @GetMapping("/videos/{videoId}/master.m3u8")
     public ResponseEntity<Resource> serverMasterFile(
        @PathVariable String videoId) {
         return videoService.serverMasterFile(videoId);
      }
//    ---------------------------------------------------------------------------------------------

    //serve the segments
    @GetMapping("/videos/{videoId}/{segment}.ts")
    public ResponseEntity<Resource> serveSegments(
            @PathVariable String videoId,
            @PathVariable String segment) {
        return videoService.serveSegment(videoId, segment);
    }
//    ---------------------------------------------------------------------------------------------
    @GetMapping("/videos/search/{criteria}")
    public ResponseEntity<ResponseStructure<PagedModel<VideoResponse>>> searchVideos(
            @PathVariable String criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        try {
            String decodedCriteria = java.net.URLDecoder.decode(criteria, "UTF-8");
            return videoService.searchVideos(decodedCriteria, page, size);
        }catch (Exception e){
            throw new RuntimeException("Invalid search criteria format", e);
        }
    }
//    ---------------------------------------------------------------------------------------------
@GetMapping("/videos/filter/{query}")
public ResponseEntity<ResponseStructure<PagedModel<VideoResponse>>> filterVideos(
        @PathVariable String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size){
        return videoService.filterVideos(query, page, size);
}

//    ---------------------------------------------------------------------------------------------

}

package com.videostreaming.app.video.service.impl;

import com.videostreaming.app.utility.ResponseStructure;
import com.videostreaming.app.video.dto.VideoRequest;
import com.videostreaming.app.video.dto.VideoResponse;
import com.videostreaming.app.video.entity.Video;
import com.videostreaming.app.video.mapper.VideoMapper;
import com.videostreaming.app.video.repository.VideoRepository;
import com.videostreaming.app.video.service.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    @Value("${files.video}")
    private String DIR;

  public VideoServiceImpl(
          VideoRepository videoRepository,
          VideoMapper videoMapper){
        this.videoRepository = videoRepository;
        this.videoMapper = videoMapper;
    }

    @PostConstruct
    public void init() {
        File file = new File(DIR);
//        try {
//            Files.createDirectories(Paths.get(HSL_DIR));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        if (!file.exists()) {
            file.mkdir();
            System.out.println("Folder Created:");
        } else {
            System.out.println("Folder already created");
        }
    }

    @Override
    public ResponseEntity<ResponseStructure<VideoResponse>> addVideo(
            VideoRequest videoRequest,
            MultipartFile file) {
        Video video = videoMapper.mapVideoRequestToVideo(videoRequest, new Video());
        
        try {
            String fileName  =  file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            String cleanFileName = StringUtils.cleanPath(fileName); // ! file name
            String cleanFolder = StringUtils.cleanPath(DIR); // ! folder name

            Path path = Paths.get(cleanFolder, cleanFileName); //! file path

            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

            video.setVideoId(UUID.randomUUID().toString());
            video.setContentType(contentType);
            video.setFilePath(path.toString());
            video = videoRepository.save(video);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseStructure<VideoResponse>()
                            .setStatus(HttpStatus.OK.value())
                            .setMessage("Video Uploaded")
                            .setData(videoMapper.mapVideoToVideoResponse(video)));

        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Error in processing video ");
        }
    }
}

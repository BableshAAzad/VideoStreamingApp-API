package com.videostreaming.app.video.service.impl;

import com.videostreaming.app.enums.AppConstants;
import com.videostreaming.app.exception.VideoNotFoundException;
import com.videostreaming.app.utility.ResponseStructure;
import com.videostreaming.app.video.dto.VideoRequest;
import com.videostreaming.app.video.dto.VideoResponse;
import com.videostreaming.app.video.entity.Video;
import com.videostreaming.app.video.mapper.VideoMapper;
import com.videostreaming.app.video.repository.VideoRepository;
import com.videostreaming.app.video.service.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    @Value("${files.video_hls}")
    private String HLS_DIR;

  public VideoServiceImpl(
          VideoRepository videoRepository,
          VideoMapper videoMapper){
        this.videoRepository = videoRepository;
        this.videoMapper = videoMapper;
    }
//    ---------------------------------------------------------------------------------------------

    private PagedModel.PageMetadata getPageMetadata(Page<?> page) {
        return new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements()
        );
    }

    private <T> PagedModel<T> getPagedModel(Page<T> page) {
        return PagedModel.of(page.getContent(), getPageMetadata(page));
    }

    private ResponseEntity<ResponseStructure<PagedModel<VideoResponse>>> buildResponse(Page<VideoResponse> inventoryResponsePage, String message) {
        PagedModel<VideoResponse> pagedModel = getPagedModel(inventoryResponsePage);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseStructure<PagedModel<VideoResponse>>()
                .setStatus(HttpStatus.OK.value())
                .setMessage(message)
                .setData(pagedModel));
    }


//    ---------------------------------------------------------------------------------------------

    @PostConstruct
    public void init() {
        File file = new File(DIR);
//        File file1 = new File(HSL_DIR);
        try {
            Files.createDirectories(Paths.get(HLS_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        if(!file1.exists()){
//            file1.mkdir();
//        }

        if (!file.exists()) {
            file.mkdir();
//            System.out.println("Folder Created:");
        }
    }

//    ---------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<ResponseStructure<VideoResponse>> addVideo(
            VideoRequest videoRequest) {
        Video video = videoMapper.mapVideoRequestToVideo(videoRequest, new Video());
        
        try {
            MultipartFile file = videoRequest.getFile();
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
            processVideo(video);// process the video for different quality

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

//    ---------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<Resource> getVideo(String videoId) {
    Video video = videoRepository.findById(videoId)
              .orElseThrow(()->new VideoNotFoundException("video Id : "+videoId+", is not exist"));

        String contentType = video.getContentType();
        String filePath = video.getFilePath();
        Resource resource = new FileSystemResource(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
//    ---------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<ResponseStructure<PagedModel<VideoResponse>>> getVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> all = videoRepository.findAll(pageable);
        Page<VideoResponse> responseVideos = all.map(videoMapper::mapVideoToVideoResponse);
        return buildResponse(responseVideos, "Videos are founded");
    }
//    ---------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<Resource> streamVideoRange(String videoId, String range) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(()->new VideoNotFoundException("video Id : "+videoId+", is not exist"));

        Path path = Paths.get(video.getFilePath());
        Resource resource = new FileSystemResource(path);

        String contentType = video.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        long fileLength = path.toFile().length();

        if (range == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }

        long rangeStart;

        long rangeEnd;

        String[] ranges = range.replace("bytes=", "").split("-");
        rangeStart = Long.parseLong(ranges[0]);

        rangeEnd = rangeStart + AppConstants.CHUNK_SIZE - 1;

        if (rangeEnd >= fileLength) {
            rangeEnd = fileLength - 1;
        }

//        if (ranges.length > 1) {
//            rangeEnd = Long.parseLong(ranges[1]);
//        } else {
//            rangeEnd = fileLength - 1;
//        }
//
//        if (rangeEnd > fileLength - 1) {
//            rangeEnd = fileLength - 1;
//        }

        System.out.println("------------------------------------------------------------------------");
        System.out.println("range start : " + rangeStart);
        System.out.println("range end : " + rangeEnd);
        InputStream inputStream;

        try {

            inputStream = Files.newInputStream(path);
            inputStream.skip(rangeStart);
            long contentLength = rangeEnd - rangeStart + 1;


            byte[] data = new byte[(int) contentLength];
            int read = inputStream.read(data, 0, data.length);
            System.out.println("read(number of bytes) : " + read);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("X-Content-Type-Options", "nosniff");
            headers.setContentLength(contentLength);

            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new ByteArrayResource(data));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
//    ---------------------------------------------------------------------------------------------

//    @Override
//    public String processVideo(String videoId) {
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(()->new VideoNotFoundException("video Id : "+videoId+", is not exist"));
//
//        String filePath = video.getFilePath();
//
//        //path where to store data:
//        Path videoPath = Paths.get(filePath);
//        try {
//            // ffmpeg command
//            Path outputPath = Paths.get(HLS_DIR, videoId);
//
//            Files.createDirectories(outputPath);
//
//            String ffmpegCmd = String.format(
//                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
//                    videoPath, outputPath, outputPath
//            );
//
//            System.out.println(ffmpegCmd);
//            //file this command
//            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
//            processBuilder.inheritIO();
//            Process process = processBuilder.start();
//            int exit = process.waitFor();
//            if (exit != 0) {
//                throw new RuntimeException("video processing failed!!");
//            }
//            return videoId;
//        } catch (IOException ex) {
//            throw new RuntimeException("Video processing fail!!");
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public void processVideo(Video video) {

        String filePath = video.getFilePath();
        Path videoPath = Paths.get(filePath);

        try {
            // Define output path
            Path outputPath = Paths.get(HLS_DIR, video.getVideoId());
            Files.createDirectories(outputPath);

            // Construct FFmpeg command
            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s\\segment_%%3d.ts\" \"%s\\master.m3u8\"",
                    videoPath, outputPath, outputPath
            );

            System.out.println("Executing command: " + ffmpegCmd);

            // Check OS and build the process accordingly
            ProcessBuilder processBuilder;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
            } else {
                processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
            }

            processBuilder.redirectErrorStream(true);  // Combine stdout and stderr
            Process process = processBuilder.start();

            // Capture the output (for debugging)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);  // Print FFmpeg logs
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Video processing failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Video processing failed: " + ex.getMessage(), ex);
        }
    }
//    ---------------------------------------------------------------------------------------------
    @Override
    public ResponseEntity<Resource> serverMasterFile(String videoId) {
//        creating path
        Path path = Paths.get(HLS_DIR, videoId, "master.m3u8");
//        System.out.println(path);

        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource resource = new FileSystemResource(path);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(
                        HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl"
                )
                .body(resource);
    }
//    ---------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<Resource> serveSegment(String videoId, String segment) {
        // create path for segment
        Path path = Paths.get(HLS_DIR, videoId, segment + ".ts");
        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(path);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                .body(resource);
    }


//    ---------------------------------------------------------------------------------------------

}


package com.videostreaming.app.video.dto;

import com.videostreaming.app.enums.VideoCategory;
import com.videostreaming.app.validation.ValidVideoFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class VideoRequest {

    @NotBlank(message = "Video title can not be blank")
    @NotNull(message = "Video title can not be null")
    private  String title;

    @NotBlank(message = "Video description can not be blank")
    @NotNull(message = "Video description can not be null")
    private  String description;

    @NotNull(message = "category description can not be null")
    private VideoCategory category;

    @ValidVideoFile
    private MultipartFile file;

}

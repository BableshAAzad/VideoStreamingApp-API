package com.videostreaming.app.video.dto;

import com.videostreaming.app.enums.VideoCategory;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VideoResponse {

    private  String videoId;

    private  String title;

    private  String description;

    private  String  contentType;

    private VideoCategory category;

    private  String filePath;
}

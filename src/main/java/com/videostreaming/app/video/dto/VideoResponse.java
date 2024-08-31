package com.videostreaming.app.video.dto;

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

    private  String filePath;
}

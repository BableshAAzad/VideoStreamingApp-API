package com.videostreaming.app.video.entity;

import com.videostreaming.app.course.entity.Course;
import com.videostreaming.app.enums.VideoCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Video {
    @Id
    private  String videoId;

    private  String title;

    private  String description;

    @Enumerated(EnumType.STRING)
    private VideoCategory category;

    private  String  contentType;

    private  String filePath;

//    @ManyToOne
//    private Course course;
}

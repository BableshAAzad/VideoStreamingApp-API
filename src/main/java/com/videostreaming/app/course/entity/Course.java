package com.videostreaming.app.course.entity;

import com.videostreaming.app.video.entity.Video;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Course {
    @Id
    private  String courseId;

    private  String courseTitle;

//    @OneToMany(mappedBy = "course")
//    private List<Video> videos;
}

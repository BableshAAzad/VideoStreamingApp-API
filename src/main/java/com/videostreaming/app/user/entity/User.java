package com.videostreaming.app.user.entity;

import com.videostreaming.app.video.entity.Video;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String Username;

    private String email;

    private String password;

    @OneToMany(mappedBy = "user")
    private List<Video> videos;

}

package com.videostreaming.app.video.repository;

import com.videostreaming.app.enums.VideoCategory;
import com.videostreaming.app.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VideoRepository extends JpaRepository<Video, String>, JpaSpecificationExecutor<Video> {

    Page<Video> findByCategory(VideoCategory category, Pageable pageable);
}

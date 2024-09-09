package com.videostreaming.app.video.specification;

import com.videostreaming.app.enums.VideoCategory;
import com.videostreaming.app.video.entity.Video;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VideoSpecification {

    public static Specification<Video> hasSearchCriteria(String criteria) {
        return (Root<Video> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            String[] parts = criteria.split(" ");

            for (String part : parts) {
                // Try to interpret the part as a category
                try {
                    VideoCategory category = VideoCategory.valueOf(part.toUpperCase());
                    predicates.add(builder.equal(root.get("category"), category));
                } catch (IllegalArgumentException e) {
                    // If it's not a valid category, search title and description
                    predicates.add(
                            builder.or(
                                    builder.like(builder.lower(root.get("title")), "%" + part.toLowerCase() + "%"),
                                    builder.like(builder.lower(root.get("description")), "%" + part.toLowerCase() + "%")
                            )
                    );
                }
            }
            // Combine predicates with 'AND' so all parts must match
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

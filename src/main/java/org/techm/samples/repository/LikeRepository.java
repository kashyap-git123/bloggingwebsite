package org.techm.samples.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.techm.samples.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByGuestEmailAndPostId(String guestEmail, Long postId);
    List<Like> findByPostId(Long postId);
}

package org.techm.samples.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.techm.samples.entity.Like;

import jakarta.transaction.Transactional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByGuestEmailAndPostId(String guestEmail, Long postId);
    List<Like> findByPostId(Long postId);
	
    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
    
    //Like findByGuestEmailAndPostId(String guestEmail, Long postId);
    Optional<Like> findByGuestEmailAndPostId(String guestEmail, Long postId);


}

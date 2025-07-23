package org.techm.samples.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;

import jakarta.transaction.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(Status status);
    List<Post> findByAuthorId(Long userId);
	List<Post> findByAuthorIdAndStatus(Long id, Status status);
	
	@Modifying
	@Transactional
	/*@Query("DELETE FROM Comment c WHERE c.post.id = :postId")
	void deleteByPostId(@Param("postId") Long postId);*/
	@Query("DELETE FROM Post p WHERE p.id = :id")
	void deleteByCustomId(@Param("id") Long id);

    
}

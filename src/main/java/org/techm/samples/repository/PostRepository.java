package org.techm.samples.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus(Status status);
    List<Post> findByAuthorId(Long userId);

    
}

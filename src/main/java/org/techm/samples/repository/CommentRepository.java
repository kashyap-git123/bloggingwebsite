package org.techm.samples.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.techm.samples.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentIsNull(Long postId); // Top-level comments
    List<Comment> findByParentId(Long parentId); // Replies
}

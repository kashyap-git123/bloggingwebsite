package org.techm.samples.service;

import org.techm.samples.entity.Comment;

public interface CommentService {
 
    Comment addComment(Comment comment);
    Comment editComment(Long commentId, Comment updatedComment);
    void deleteComment(Long commentId);
    
}
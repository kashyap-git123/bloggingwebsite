package org.techm.samples.service;

import java.util.List;

import org.techm.samples.entity.Comment;

public interface CommentService {
 
	void addComment(Long postId, String guestName, String guestEmail, String content);
    Comment editComment(Long commentId, Comment updatedComment);
    void deleteComment(Long commentId);
    Comment getCommentById(Long id);
    List<Comment> getAllComments();
    
    void saveComment(Comment comment);
    

}
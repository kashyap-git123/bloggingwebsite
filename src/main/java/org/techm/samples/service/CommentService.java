package org.techm.samples.service;

import java.util.List;

import org.techm.samples.entity.Comment;
 
public interface CommentService {
 
    Comment addComment(Comment comment);
    Comment editComment(Long commentId, Comment updatedComment);
    void deleteComment(Long commentId);
    Comment replyToComment(Long parentId, Comment reply);
    List<Comment> getTopLevelComments(Long postId);
    List<Comment> getReplies(Long parentId);
}
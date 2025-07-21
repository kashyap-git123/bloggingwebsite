package org.techm.samples.service;

import java.time.LocalDateTime;
//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Comment;
import org.techm.samples.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepo;

    public Comment addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepo.save(comment);
    }

    public Comment editComment(Long commentId, Comment updatedComment) {
        Comment existingComment = commentRepo.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));

        existingComment.setGuestName(updatedComment.getGuestName());
        existingComment.setGuestEmail(updatedComment.getGuestEmail());
        existingComment.setContent(updatedComment.getContent());
        existingComment.setCreatedAt(LocalDateTime.now()); // optional: use updatedAt instead

        return commentRepo.save(existingComment);
    }

    public void deleteComment(Long commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepo.deleteById(commentId);
    }

    public Comment replyToComment(Long parentId, Comment reply) {
        Comment parentComment = commentRepo.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        reply.setParent(parentComment);
        reply.setPost(parentComment.getPost()); // inherit post from parent comment
        reply.setCreatedAt(LocalDateTime.now());

        return commentRepo.save(reply);
    }

   /* public List<Comment> getReplies(Long parentId) {
        return commentRepo.findByParentId(parentId);
    } */
}

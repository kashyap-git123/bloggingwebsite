package org.techm.samples.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Post;
import org.techm.samples.repository.CommentRepository;
import org.techm.samples.repository.PostRepository;
 
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
 
	@Autowired
    private CommentRepository commentRepo;

    @Autowired
    private PostRepository postRepo;

    public void saveComment(Comment comment) {
        commentRepo.save(comment);
        logger.atInfo()
            .addKeyValue("event.action", "comment.created")
            .addKeyValue("event.outcome", "success")
            .addKeyValue("post.id", comment.getPost().getId())
            .log("Comment created");
    }

    public void addComment(Long postId, String guestName, String guestEmail, String content) {
        Post post = postRepo.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setGuestName(guestName);
        comment.setGuestEmail(guestEmail);
        comment.setContent(content);
        saveComment(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepo.findAll();
    }

    public Comment getCommentById(Long id) {
        return commentRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
 
    @Override
    public Comment editComment(Long commentId, Comment updatedComment) {
        Comment existingComment = commentRepo.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
 
        existingComment.setGuestName(updatedComment.getGuestName());
        existingComment.setGuestEmail(updatedComment.getGuestEmail());
        existingComment.setContent(updatedComment.getContent());
        existingComment.setCreatedAt(LocalDateTime.now());
 
        return commentRepo.save(existingComment);
    }
 
    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepo.deleteById(commentId);
    }

   
}
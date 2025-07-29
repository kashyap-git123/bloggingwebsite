package org.techm.samples.controller;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.techm.samples.entity.Comment;
import org.techm.samples.service.CommentService;
import org.techm.samples.service.PostService;
import org.techm.samples.service.UserService;
 
@Controller
@RequestMapping("/blog/comments")
public class CommentController {
 
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postservice;
    @Autowired
    private UserService userService;
 
    @PostMapping("/add")
    public String addComment(@RequestParam("postId") Long postId,
                             @RequestParam("content") String content,
                             Authentication auth) {
        String email = auth.getName();
        String name = userService.userByUsername(email).getName();

        Comment comment = new Comment();
        comment.setPost(postservice.getPostById(postId));
        comment.setGuestName(name);
        comment.setGuestEmail(email);
        comment.setContent(content);

        commentService.saveComment(comment);
        return "redirect:/posts/view/" + postId;
    }


 
    
    @PutMapping("/edit/{id}")
    public ResponseEntity<Comment> editComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        Comment edited = commentService.editComment(id, updatedComment);
        return ResponseEntity.ok(edited);
    }
 
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }


    
 
}
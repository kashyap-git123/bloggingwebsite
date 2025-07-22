package org.techm.samples.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
 
import org.techm.samples.entity.Comment;
import org.techm.samples.service.CommentService;
 
@RestController
@RequestMapping("/blog/comments")
public class CommentController {
 
    @Autowired
    private CommentService commentService;
 
    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.addComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }
 
    
    @PutMapping("/edit/{id}")
    public ResponseEntity<Comment> editComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        Comment edited = commentService.editComment(id, updatedComment);
        return ResponseEntity.ok(edited);
    }
 
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
 
}
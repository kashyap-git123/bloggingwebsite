package org.techm.samples.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.techm.samples.entity.Post;
import org.techm.samples.service.PostService;

@RestController
@RequestMapping("/blog/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getPublishedPosts() {
        List<Post> posts = postService.getPublishedPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('BLOGGER')")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> allPosts = postService.getAllPosts();
        return ResponseEntity.ok(allPosts);
    }
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('BLOGGER')")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        List<Post> userPosts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(userPosts);
    }


    @PostMapping
    @PreAuthorize("hasRole('BLOGGER')")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping("/draft")
    @PreAuthorize("hasRole('BLOGGER')")
    public ResponseEntity<Post> saveAsDraft(@RequestBody Post post) {
        Post draftPost = postService.saveAsDraft(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(draftPost);
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('BLOGGER')")
    public ResponseEntity<Post> editPost(@PathVariable Long id, @RequestBody Post updatedPost) {
        Post editedPost = postService.editPost(id, updatedPost);
        if (editedPost != null) {
            return ResponseEntity.ok(editedPost);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('BLOGGER')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boolean isDeleted = postService.deletePost(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}


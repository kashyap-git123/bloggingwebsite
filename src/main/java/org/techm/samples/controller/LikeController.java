package org.techm.samples.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
 
import org.techm.samples.entity.Like;
import org.techm.samples.service.LikeService;
 
@RestController
@RequestMapping("/blog/likes")
public class LikeController {
 
    @Autowired
    private LikeService likeService;
 
    
    @PostMapping("/add")
    public ResponseEntity<?> addLike(@RequestBody Like like) {
        boolean alreadyLiked = likeService.alreadyLiked(like.getGuestEmail(), like.getPost().getId());
        if (alreadyLiked) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You have already liked this post.");
        }
        Like savedLike = likeService.addLike(like);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLike);
    }
 
    // Remove a like (optional use case)
    @DeleteMapping("/remove/{likeId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long likeId) {
        likeService.removeLike(likeId);
        return ResponseEntity.noContent().build();
    }
 
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesByPost(@PathVariable Long postId) {
        List<Like> likes = likeService.getLikesByPost(postId);
        return ResponseEntity.ok(likes);
    }
    @GetMapping
    public ResponseEntity<List<Like>> getAllLikes() {
        List<Like> allLikes = likeService.getAllLikes();
        return ResponseEntity.ok(allLikes);
    }

}
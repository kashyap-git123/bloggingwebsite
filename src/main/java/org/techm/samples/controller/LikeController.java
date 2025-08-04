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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.techm.samples.entity.Like;
import org.techm.samples.service.LikeService;
 
@Controller
@RequestMapping("/blog/likes")
public class LikeController {
 
    @Autowired
    private LikeService likeService;
 
    
    @PostMapping("/add")
    public String addLike(@RequestParam("postId") Long postId, Authentication auth) {
        String email = auth.getName();
        likeService.addLike(postId, email);
        return "redirect:/posts/view/" + postId;
    }

 
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
    
    @PostMapping("/toggle")
    public String toggleLike(@RequestParam Long postId, Authentication auth) {
        String guestEmail = auth.getName();
        likeService.toggleLike(postId, guestEmail);
        return "redirect:/posts/view/" + postId;
    }
    

}
package org.techm.samples.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Like;
import org.techm.samples.entity.Post;
import org.techm.samples.repository.LikeRepository;
import org.techm.samples.repository.PostRepository;
 
@Service
public class LikeServiceImpl implements LikeService {


	    @Autowired
	    private LikeRepository likeRepo;

	    @Autowired
	    private PostRepository postRepo;

	    public void addLike(Long postId, String guestEmail) {
	        Post post = postRepo.findById(postId)
	            .orElseThrow(() -> new RuntimeException("Post not found"));

	        boolean alreadyLiked = likeRepo.existsByGuestEmailAndPostId(guestEmail, postId);
	        if (!alreadyLiked) {
	            Like like = new Like();
	            like.setPost(post);
	            like.setGuestEmail(guestEmail);
	            likeRepo.save(like);
	        }
	    }

	    public List<Like> getLikesByPost(Long postId) {
	        return likeRepo.findByPostId(postId);
	    }
 
    @Override
    public void removeLike(Long likeId) {
        if (!likeRepo.existsById(likeId)) {
            throw new RuntimeException("Like not found");
        }
        likeRepo.deleteById(likeId);
    }
 
    @Override
    public boolean alreadyLiked(String guestEmail, Long postId) {
        return likeRepo.existsByGuestEmailAndPostId(guestEmail, postId);
    }
 
    @Override
    public List<Like> getAllLikes() {
        return likeRepo.findAll();
    }

    
    public boolean toggleLike(Long postId, String guestEmail) {
        boolean alreadyLiked = likeRepo.existsByGuestEmailAndPostId(guestEmail, postId);
        if (alreadyLiked) {
            Like existingLike = likeRepo.findByGuestEmailAndPostId(guestEmail, postId);
            likeRepo.delete(existingLike);
            return false; // like removed
        } else {
            Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            Like newLike = new Like();
            newLike.setGuestEmail(guestEmail);
            newLike.setPost(post);
            likeRepo.save(newLike);
            return true; // like added
        }
    }


}
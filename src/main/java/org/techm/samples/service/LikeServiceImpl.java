package org.techm.samples.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Like;
import org.techm.samples.entity.Post;
import org.techm.samples.repository.LikeRepository;
import org.techm.samples.repository.PostRepository;
 
@Service
public class LikeServiceImpl implements LikeService {

        private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

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
                logger.atInfo()
                        .addKeyValue("event.action", "post.liked")
                        .addKeyValue("event.outcome", "success")
                        .addKeyValue("post.id", postId)
                        .log("Post liked");
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
        logger.atInfo()
                .addKeyValue("event.action", "post.unliked")
                .addKeyValue("event.outcome", "success")
                .addKeyValue("like.id", likeId)
                .log("Post unliked");
    }
 
   
   /*@Override
    public boolean alreadyLiked(String guestEmail, Long postId) {
        return likeRepo.existsByGuestEmailAndPostId(guestEmail, postId);
    }*/
 
    @Override
    public List<Like> getAllLikes() {
        return likeRepo.findAll();
    }

    
   /* public boolean toggleLike(Long postId, String guestEmail) {
        boolean alreadyLiked = likeRepo.existsByGuestEmailAndPostId(guestEmail, postId);
        if (alreadyLiked) {
            Like existingLike = likeRepo.findByGuestEmailAndPostId(guestEmail, postId);
            likeRepo.delete(existingLike);
            return false;
        } else {
            Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            Like newLike = new Like();
            newLike.setGuestEmail(guestEmail);
            newLike.setPost(post);
            likeRepo.save(newLike);
            return true;
        }
    }*/
    public void removeLikeByEmailAndPost(String email, Long postId) {
        Optional<Like> like = likeRepo.findByGuestEmailAndPostId(email, postId);
        like.ifPresent(existingLike -> {
            likeRepo.delete(existingLike);
            logger.atInfo()
                    .addKeyValue("event.action", "post.unliked")
                    .addKeyValue("event.outcome", "success")
                    .addKeyValue("like.id", existingLike.getId())
                    .addKeyValue("post.id", postId)
                    .log("Post unliked");
        });
    }
    public boolean alreadyLiked(String email, Long postId) {
        return likeRepo.findByGuestEmailAndPostId(email, postId).isPresent();
    }

	@Override
	public boolean toggleLike(Long postId, String guestEmail) {
		// TODO Auto-generated method stub
		return false;
	}


}
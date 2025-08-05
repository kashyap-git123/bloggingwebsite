package org.techm.samples.service;
 
import java.util.List;
import org.techm.samples.entity.Like;
 
public interface LikeService {
 
	void addLike(Long postId, String guestEmail);
    void removeLike(Long likeId);
    boolean alreadyLiked(String guestEmail, Long postId);
    List<Like> getLikesByPost(Long postId);
    List<Like> getAllLikes();
    
    boolean toggleLike(Long postId, String guestEmail);
    void removeLikeByEmailAndPost(String email, Long postId);

    
}
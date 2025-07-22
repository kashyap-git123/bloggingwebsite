package org.techm.samples.service;
 
import java.util.List;
import org.techm.samples.entity.Like;
 
public interface LikeService {
 
    Like addLike(Like like);
    void removeLike(Long likeId);
    boolean alreadyLiked(String guestEmail, Long postId);
    List<Like> getLikesByPost(Long postId);
    List<Like> getAllLikes();

    
}
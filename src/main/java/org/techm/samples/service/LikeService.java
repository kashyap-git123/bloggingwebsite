package org.techm.samples.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Like;
import org.techm.samples.repository.LikeRepository;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepo;

    public Like addLike(Like like) {
        return likeRepo.save(like);
    }

    public void removeLike(Long likeId) {
        if (!likeRepo.existsById(likeId)) {
            throw new RuntimeException("Like not found");
        }
        likeRepo.deleteById(likeId);
    }

    public List<Like> getLikesByPost(Long postId) {
        return likeRepo.findByPostId(postId);
    }
}

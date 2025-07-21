package org.techm.samples.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;
import org.techm.samples.repository.PostRepository;

@Service

public class PostService {

    @Autowired

    private PostRepository postRepo;
 
    public List<Post> getPublishedPosts() {

        return postRepo.findByStatus(Status.PUBLISHED);

    }
 
    public Post createPost(Post post) {

        post.setCreatedAt(LocalDateTime.now());

        post.setStatus(Status.DRAFT); 
        return postRepo.save(post);

    }
 
    public Post editPost(Long postId, Post updatedPost) {

        Post existingPost = postRepo.findById(postId)

            .orElseThrow(() -> new RuntimeException("Post not found"));
 
        existingPost.setTitle(updatedPost.getTitle());

        existingPost.setContent(updatedPost.getContent());

        existingPost.setUpdatedAt(LocalDateTime.now()); 
        existingPost.setStatus(updatedPost.getStatus());
 
        return postRepo.save(existingPost);

    }
 
    public void deletePost(Long postId) {

        if (!postRepo.existsById(postId)) {

            throw new RuntimeException("Post not found");

        }

        postRepo.deleteById(postId);

    }
 
    public Post saveAsDraft(Post post) {

        post.setCreatedAt(LocalDateTime.now());

        post.setStatus(Status.DRAFT);

        return postRepo.save(post);

    }

}

 
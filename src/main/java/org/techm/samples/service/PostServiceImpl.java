package org.techm.samples.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;
import org.techm.samples.repository.PostRepository;
import org.techm.samples.repository.UserRepository;

@Service
public class PostServiceImpl implements PostService {
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
    

    @Override
    public List<Post> getPublishedPosts() {
        return postRepository.findByStatus(Status.PUBLISHED);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public Post createPost(Post post) {
    	User author = userRepository.findById(post.getAuthor().getId())
    		    .orElseThrow(() -> new RuntimeException("Author not found"));
    		post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(Status.PUBLISHED);
        return postRepository.save(post);
    }

    @Override
    public Post saveAsDraft(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(Status.DRAFT);
        return postRepository.save(post);
    }

    @Override
    public Post editPost(Long id, Post updatedPost) {
        Optional<Post> existing = postRepository.findById(id);
        if (existing.isPresent()) {
            Post post = existing.get();
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setStatus(updatedPost.getStatus());
            return postRepository.save(post);
        }
        return null;
    }

    @Override
    public boolean deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }
}


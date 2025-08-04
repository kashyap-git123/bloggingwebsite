package org.techm.samples.service;

import java.time.LocalDate;
import java.util.List;

import org.techm.samples.entity.Post;

public interface PostService {
    List<Post> getPublishedPosts();
    Post getPostById(Long id);
    Post createPost(Post post);
    Post saveAsDraft(Post post);
    Post editPost(Long id, Post updatedPost);
    boolean deletePost(Long id);
    List<Post> getAllPosts();
    List<Post> getPostByUserId(Long userId);
    List<Post> getPostsByUserEmail(String email);
    List<Post> getDraftsByUserEmail(String email);
	List<Post> getPublishedPostsByUserEmail(String email);
	void publishDraft(Long id);
	
	List<Post> getPostsByAuthorName(String name);

	List<Post> getPostsByDate(LocalDate date);

}

package org.techm.samples.service;

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
    List<Post> getPostsByUserId(Long userId);

    

}

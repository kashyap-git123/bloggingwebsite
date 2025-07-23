/*
 * package org.techm.samples.service;
 * 
 * import java.time.LocalDateTime; import java.util.List; import
 * java.util.Optional;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Service; import org.techm.samples.entity.Post;
 * import org.techm.samples.entity.Status; import org.techm.samples.entity.User;
 * import org.techm.samples.repository.CommentRepository; import
 * org.techm.samples.repository.LikeRepository; import
 * org.techm.samples.repository.PostRepository; import
 * org.techm.samples.repository.ReplyRepository; import
 * org.techm.samples.repository.UserRepository;
 * 
 * import jakarta.transaction.Transactional;
 * 
 * @Service public class PostServiceImpl implements PostService {
 * 
 * @Autowired private PostRepository postRepository;
 * 
 * @Autowired private LikeRepository likeRepository;
 * 
 * @Autowired private CommentRepository commentRepository;
 * 
 * @Autowired private ReplyRepository replyRepository;
 * 
 * @Autowired private UserRepository userRepository;
 * 
 * @Override public Post createPost(Post post) { User author =
 * userRepository.findById(post.getAuthor().getId()) .orElseThrow(() -> new
 * RuntimeException("Author not found")); post.setAuthor(author);
 * post.setCreatedAt(LocalDateTime.now()); post.setStatus(Status.PUBLISHED);
 * return postRepository.save(post); }
 * 
 * @Override public Post saveAsDraft(Post post) { User author =
 * userRepository.findById(post.getAuthor().getId()) .orElseThrow(() -> new
 * RuntimeException("Author not found")); post.setAuthor(author);
 * post.setCreatedAt(LocalDateTime.now()); post.setStatus(Status.DRAFT); return
 * postRepository.save(post); }
 * 
 * @Override public Post editPost(Long id, Post updatedPost) { Optional<Post>
 * existing = postRepository.findById(id); if (existing.isPresent()) { Post post
 * = existing.get(); post.setTitle(updatedPost.getTitle());
 * post.setContent(updatedPost.getContent());
 * post.setStatus(updatedPost.getStatus()); return postRepository.save(post); }
 * return null; }
 * 
 * @Override
 * 
 * @Transactional public boolean deletePost(Long id) { Optional<Post> post =
 * postRepository.findById(id); if (post.isPresent()) {
 * 
 * commentRepository.deleteByPostId(id); // You’ll need this method in
 * CommentRepository likeRepository.deleteByPostId(id);
 * 
 * 
 * System.out.println("Deleting comments...");
 * commentRepository.deleteByPostId(id);
 * System.out.println("Deleting likes..."); likeRepository.deleteByPostId(id);
 * postRepository.deleteByCustomId(id); //postRepository.deleteById(id); return
 * true; } return false; }
 * 
 * @Override public List<Post> getPublishedPosts() { return
 * postRepository.findByStatus(Status.PUBLISHED); }
 * 
 * @Override public Post getPostById(Long id) { return
 * postRepository.findById(id).orElse(null); }
 * 
 * @Override public List<Post> getAllPosts() { return postRepository.findAll();
 * }
 * 
 * @Override public List<Post> getPostByUserId(Long userId) { return
 * postRepository.findByAuthorId(userId); }
 * 
 * @Override public List<Post> getPostsByUserEmail(String email) { User user =
 * userRepository.findByEmail(email); return
 * postRepository.findByAuthorId(user.getId()); }
 * 
 * @Override public List<Post> getDraftsByUserEmail(String email) { User user =
 * userRepository.findByEmail(email); return
 * postRepository.findByAuthorIdAndStatus(user.getId(), Status.DRAFT); }
 * 
 * @Override public List<Post> getPublishedPostsByUserEmail(String email) { User
 * user = userRepository.findByEmail(email); return
 * postRepository.findByAuthorIdAndStatus(user.getId(), Status.PUBLISHED); } }
 */

package org.techm.samples.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;
import org.techm.samples.exception.AuthorNotFoundException;
import org.techm.samples.exception.PostNotFoundException;
import org.techm.samples.repository.CommentRepository;
import org.techm.samples.repository.LikeRepository;
import org.techm.samples.repository.PostRepository;
import org.techm.samples.repository.ReplyRepository;
import org.techm.samples.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Post createPost(Post post) {
        logger.info("Creating post for author ID: {}", post.getAuthor().getId());
        User author = userRepository.findById(post.getAuthor().getId())
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(Status.PUBLISHED);
        Post savedPost = postRepository.save(post);
        logger.info("Post created with ID: {}", savedPost.getId());
        return savedPost;
    }

    @Override
    public Post saveAsDraft(Post post) {
        logger.info("Saving post as draft for author ID: {}", post.getAuthor().getId());
        User author = userRepository.findById(post.getAuthor().getId())
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(Status.DRAFT);
        Post savedDraft = postRepository.save(post);
        logger.info("Draft saved with ID: {}", savedDraft.getId());
        return savedDraft;
    }

    @Override
    public Post editPost(Long id, Post updatedPost) {
        logger.info("Editing post with ID: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setStatus(updatedPost.getStatus());
        Post updated = postRepository.save(post);
        logger.info("Post updated with ID: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public boolean deletePost(Long id) {
        logger.info("Deleting post with ID: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));

        commentRepository.deleteByPostId(id);
        likeRepository.deleteByPostId(id);
        postRepository.deleteByCustomId(id);

        logger.info("Post and related data deleted for ID: {}", id);
        return true;
    }

    @Override
    public List<Post> getPublishedPosts() {
        logger.debug("Fetching all published posts");
        return postRepository.findByStatus(Status.PUBLISHED);
    }

    @Override
    public Post getPostById(Long id) {
        logger.debug("Fetching post by ID: {}", id);
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));
    }

    @Override
    public List<Post> getAllPosts() {
        logger.debug("Fetching all posts");
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostByUserId(Long userId) {
        logger.debug("Fetching posts by user ID: {}", userId);
        return postRepository.findByAuthorId(userId);
    }

    @Override
    public List<Post> getPostsByUserEmail(String email) {
        logger.debug("Fetching posts by user email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthorNotFoundException("User not found with email: " + email);
        }
        return postRepository.findByAuthorId(user.getId());
    }

    @Override
    public List<Post> getDraftsByUserEmail(String email) {
        logger.debug("Fetching drafts by user email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthorNotFoundException("User not found with email: " + email);
        }
        return postRepository.findByAuthorIdAndStatus(user.getId(), Status.DRAFT);
    }

    @Override
    public List<Post> getPublishedPostsByUserEmail(String email) {
        logger.debug("Fetching published posts by user email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthorNotFoundException("User not found with email: " + email);
        }
        return postRepository.findByAuthorIdAndStatus(user.getId(), Status.PUBLISHED);
    }
}

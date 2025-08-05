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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        return postRepository.save(post);
    }

    @Override
    public Post saveAsDraft(Post post) {
        logger.info("Saving post as draft for author ID: {}", post.getAuthor().getId());
        User author = userRepository.findById(post.getAuthor().getId())
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(Status.DRAFT);
        return postRepository.save(post);
    }

    @Override
    public Post editPost(Long id, Post updatedPost) {
        logger.info("Editing post with ID: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
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
        return true;
    }

    @Override
    public List<Post> getPublishedPosts() {
        return postRepository.findByStatus(Status.PUBLISHED);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostByUserId(Long userId) {
        return postRepository.findByAuthorId(userId);
    }

    @Override
    public List<Post> getPostsByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthorNotFoundException("User not found with email: " + email);
        }
        return postRepository.findByAuthorId(user.getId());
    }

    @Override
    public List<Post> getDraftsByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthorNotFoundException("User not found with email: " + email);
        }
        return postRepository.findByAuthorIdAndStatus(user.getId(), Status.DRAFT);
    }

    @Override
    public List<Post> getPublishedPostsByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthorNotFoundException("User not found with email: " + email);
        }
        return postRepository.findByAuthorIdAndStatus(user.getId(), Status.PUBLISHED);
    }

    @Override
    @Transactional
    public void publishDraft(Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        postOpt.ifPresent(post -> {
            post.setStatus(Status.PUBLISHED);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
        });
    }

    public List<Post> getPostsByAuthorName(String name) {
        return postRepository.findByAuthorNameContainingIgnoreCase(name);
    }

    public List<Post> getPostsByDate(LocalDate date) {
        return postRepository.findByCreatedAtBetween(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );
    }

 
    public Page<Post> getOtherPostsPaginated(String currentUserEmail, int page, int size) {
        logger.debug("Fetching paginated posts not authored by: {}", currentUserEmail);

        User currentUser = userRepository.findByEmail(currentUserEmail);
        if (currentUser == null) {
            throw new AuthorNotFoundException("User not found with email: " + currentUserEmail);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByAuthorNot(currentUser, pageable);
    }
    public Page<Post> getPublishedPostsByUserEmailPaginated(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByAuthorEmailAndStatus(email, Status.PUBLISHED, pageable);
    }
    public Page<Post> getPublishedPostsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByStatus(Status.PUBLISHED, pageable);
    }


}


package org.techm.samples.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Post;
import org.techm.samples.entity.Status;
import org.techm.samples.entity.User;
import org.techm.samples.service.CommentService;
import org.techm.samples.service.LikeService;
import org.techm.samples.service.PostService;
import org.techm.samples.service.UserService;

@Controller
@RequestMapping("/posts")
@PreAuthorize("hasRole('BLOGGER')")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "blogs/create-post";
    }

    @PostMapping("/create")
    public String handleCreate(@ModelAttribute("post") Post post, Authentication auth) {
        User user = userService.userByUsername(auth.getName());
        post.setAuthor(user);
        if (post.getStatus() == Status.DRAFT) {
            postService.saveAsDraft(post);
            return "redirect:/posts/drafts";
        } else {
            postService.createPost(post);
            return "redirect:/posts/mine";
        }
    }

    @GetMapping("/mine")
    public String showMyPosts(
        Authentication auth,
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        Page<Post> postPage = postService.getPublishedPostsByUserEmailPaginated(auth.getName(), page, size);
        
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());

        return "blogs/my-posts";
    }


    @GetMapping("/drafts")
    public String showMyDrafts(Authentication auth, Model model) {
        List<Post> drafts = postService.getDraftsByUserEmail(auth.getName());
        model.addAttribute("drafts", drafts);
        return "blogs/my-drafts";
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "blogs/edit-post";
    }

    @PostMapping("/edit/{id}")
    public String handleEdit(@PathVariable Long id, @ModelAttribute Post updatedPost) {
        updatedPost.setUpdatedAt(LocalDateTime.now());
        postService.editPost(id, updatedPost);
        return "redirect:/posts/mine";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        boolean deleted = postService.deletePost(id);
        return "redirect:/posts/mine";
    }

    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        int likeCount = likeService.getLikesByPost(id).size();

        List<Comment> allComments = commentService.getAllComments().stream()
            .filter(comment -> comment.getPost().getId().equals(id))
            .toList();

        int commentCount = allComments.size();

        model.addAttribute("post", post);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("comments", allComments);

        boolean alreadyLiked = likeService.alreadyLiked(post.getAuthor().getEmail(), id);
        model.addAttribute("alreadyLiked", alreadyLiked);

        return "blogs/view-post";
    }

    @PostMapping("/publish/{id}")
    public String publishDraft(@PathVariable Long id) {
        postService.publishDraft(id);
        return "redirect:/posts/mine";
    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public String showAllPosts(
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
    ) {
        Page<Post> postPage = postService.getPublishedPostsPaginated(page, size);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());

        return "blogs/all-posts";
    }


    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public String searchPosts(@RequestParam("query") String query, Model model) {
        List<Post> results;
        try {
            LocalDate date = LocalDate.parse(query, DateTimeFormatter.ofPattern("dd MMM yyyy"));
            results = postService.getPostsByDate(date);
        } catch (DateTimeParseException e) {
            results = postService.getPostsByAuthorName(query);
        }
        model.addAttribute("posts", results);
        model.addAttribute("searchQuery", query);
        return "blogs/all-posts";
    }

    
    @GetMapping("/others")
    public String showOtherBloggersPosts(
            Authentication auth,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String email = auth.getName();
        Page<Post> postPage = postService.getOtherPostsPaginated(email, page, size);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());

        return "blogs/explore-posts";
    }
}

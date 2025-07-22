package org.techm.samples.entity;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String guestName;
    private String guestEmail;
    private String content;
    private LocalDateTime createdAt;

    @ManyToOne
    @JsonIgnoreProperties({"comments", "likes", "author"})
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestEmail() {
		return guestEmail;
	}

	public void setGuestEmail(String guestEmail) {
		this.guestEmail = guestEmail;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public List<Reply> getReplies() {
		return replies;
	}

	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}

	public Comment(String guestName, String guestEmail, String content, LocalDateTime createdAt, Post post,
			List<Reply> replies) {
		super();
		this.guestName = guestName;
		this.guestEmail = guestEmail;
		this.content = content;
		this.createdAt = createdAt;
		this.post = post;
		this.replies = replies;
	}

	@Override
	public String toString() {
		return "Comment [guestName=" + guestName + ", guestEmail=" + guestEmail + ", content=" + content
				+ ", createdAt=" + createdAt + ", post=" + post + ", replies=" + replies + "]";
	}

	public Comment() {
		super();
	}
    


    
}


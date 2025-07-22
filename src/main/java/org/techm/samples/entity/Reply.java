package org.techm.samples.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Reply {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	@ManyToOne
	private Comment comment;
	
	private String reply;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public Reply(Comment comment, String reply) {
		super();
		this.comment = comment;
		this.reply = reply;
	}

	public Reply() {
		super();
	}

	@Override
	public String toString() {
		return "Reply [comment=" + comment + ", reply=" + reply + "]";
	}
	
	
	

}

package org.techm.samples.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Reply;
import org.techm.samples.service.CommentServiceImpl;
import org.techm.samples.service.ReplyServiceImpl;

@Controller
@RequestMapping("blog/reply")
public class ReplyController {
	
	@Autowired
	private ReplyServiceImpl replyService;
	@Autowired
	private CommentServiceImpl commentservice;
	
	@PostMapping("/add")
    public String addReply(@RequestParam("commentId") Long commentId,
                           @RequestParam("replyText") String replyText) {
        Comment comment = commentservice.getCommentById(commentId);
        Reply reply = new Reply();
        reply.setComment(comment);
        reply.setReply(replyText);

        replyService.addReply(reply);
        return "redirect:/posts/view/" + comment.getPost().getId();
        //return "redirect:/post/view/" + comment.getPost().getId();
        //return "blogs/view-post/"+comment.getPost().getId();
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getReply(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(replyService.getReply(id));
		}
		catch(Exception e) {
			return ResponseEntity.status(404).body("not_Found");
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getAll(){
		try {
			return ResponseEntity.ok(replyService.getAll());
		}
		catch(Exception e) {
			return ResponseEntity.status(404).body("not_Found");
		}
	}

}

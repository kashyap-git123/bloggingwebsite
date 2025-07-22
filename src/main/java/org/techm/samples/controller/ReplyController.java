package org.techm.samples.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.techm.samples.entity.Reply;
import org.techm.samples.service.ReplyServiceImpl;

@RestController
@RequestMapping("api/reply")
public class ReplyController {
	
	@Autowired
	private ReplyServiceImpl replyService;
	
	@PostMapping("/add")
	public ResponseEntity<?> addReply(@RequestBody Reply reply) {
		try {
			return ResponseEntity.ok(replyService.addReply(reply));
		}
		catch(Exception e) {
			return ResponseEntity.status(404).body("not_Created");
		}
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

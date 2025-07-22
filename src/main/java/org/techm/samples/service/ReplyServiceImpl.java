package org.techm.samples.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Reply;
import org.techm.samples.repository.CommentRepository;
import org.techm.samples.repository.ReplyRepository;

@Service
public class ReplyServiceImpl implements ReplyService {
	
	@Autowired
	private ReplyRepository replyRepo;
	@Autowired
	private CommentRepository commentrepo;
	
	public Reply addReply(Reply reply) {
		Long id1=reply.getComment().getId();
		Comment cmt=commentrepo.findById(id1).orElse(null);
		reply.setComment(cmt);
		replyRepo.save(reply);
		return reply;
	}
	
	public Reply getReply(Long id) {
		Reply reply = replyRepo.findById(id).orElse(null);
		return reply;
	}
	
	public List<Reply> getAll(){
		List<Reply> replies = replyRepo.findAll();
		return replies;
	}

}

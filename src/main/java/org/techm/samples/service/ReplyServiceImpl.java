package org.techm.samples.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Reply;
import org.techm.samples.repository.CommentRepository;
import org.techm.samples.repository.ReplyRepository;

@Service
public class ReplyServiceImpl implements ReplyService {

	private static final Logger logger = LoggerFactory.getLogger(ReplyServiceImpl.class);
	
	@Autowired
	private ReplyRepository replyRepo;
	@Autowired
	private CommentRepository commentrepo;
	
	public Reply addReply(Reply reply) {
		Long id1=reply.getComment().getId();
		Comment cmt=commentrepo.findById(id1).orElse(null);
		
		reply.setComment(cmt);
		Reply savedReply = replyRepo.save(reply);
		logger.atInfo()
				.addKeyValue("event.action", "comment.reply.created")
				.addKeyValue("event.outcome", "success")
				.addKeyValue("reply.id", savedReply.getId())
				.addKeyValue("comment.id", id1)
				.log("Reply created");
		return savedReply;
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

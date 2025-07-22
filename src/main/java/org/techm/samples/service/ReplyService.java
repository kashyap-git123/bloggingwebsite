package org.techm.samples.service;

import java.util.List;

import org.techm.samples.entity.Reply;

public interface ReplyService {

	Reply addReply(Reply reply);
	Reply getReply(Long id);
	List<Reply> getAll();
}

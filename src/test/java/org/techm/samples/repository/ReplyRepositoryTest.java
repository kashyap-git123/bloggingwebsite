package org.techm.samples.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Reply;

@DataJpaTest
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setContent("This is a comment");
        commentRepository.save(comment);

        Reply reply1 = new Reply(comment, "First reply");
        Reply reply2 = new Reply(comment, "Second reply");

        replyRepository.save(reply1);
        replyRepository.save(reply2);
    }

        		@Test
    void testFindReplyById() {
        Reply reply = new Reply(comment, "Find me");
        Reply saved = replyRepository.save(reply);

        Reply found = replyRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Find me", found.getReply());
    }
        
    @Test
    void testSaveReply() {
        Reply reply = new Reply(comment, "New reply");
        Reply savedReply = replyRepository.save(reply);

        assertNotNull(savedReply.getId());
        assertEquals("New reply", savedReply.getReply());
        assertEquals(comment.getId(), savedReply.getComment().getId());
    }

    @Test
    void testFindAllReplies() {
        List<Reply> replies = replyRepository.findAll();
        assertEquals(2, replies.size());
    }
}
   
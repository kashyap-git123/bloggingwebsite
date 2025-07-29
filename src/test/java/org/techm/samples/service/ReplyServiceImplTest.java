package org.techm.samples.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.techm.samples.entity.Comment;
import org.techm.samples.entity.Reply;
import org.techm.samples.repository.CommentRepository;
import org.techm.samples.repository.ReplyRepository;

public class ReplyServiceImplTest {

    @Mock
    private ReplyRepository replyRepo;

    @Mock
    private CommentRepository commentRepo;

    @InjectMocks
    private ReplyServiceImpl replyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    Comment comment = new Comment();
        comment.setId(1L);

        Reply reply = new Reply();
        reply.setComment(comment);

        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));
        when(replyRepo.save(reply)).thenReturn(reply);

        Reply savedReply = replyService.addReply(reply);

        assertNotNull(savedReply);
        assertEquals(comment, savedReply.getComment());
        verify(replyRepo, times(1)).save(reply);
    }

    @Test
    public void testGetReply() {
        Reply reply = new Reply();
        reply.setId(1L);

        when(replyRepo.findById(1L)).thenReturn(Optional.of(reply));

        Reply foundReply = replyService.getReply(1L);

        assertNotNull(foundReply);
        assertEquals(1L, foundReply.getId());
    }

    @Test
    public void testGetAllReplies() {
        Reply reply1 = new Reply();
        Reply reply2 = new Reply();

        when(replyRepo.findAll()).thenReturn(Arrays.asList(reply1, reply2));

        List<Reply> replies = replyService.getAll();

        assertEquals(2, replies.size());
    }
}

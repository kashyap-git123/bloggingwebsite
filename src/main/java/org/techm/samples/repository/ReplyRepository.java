package org.techm.samples.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.techm.samples.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long>{

}

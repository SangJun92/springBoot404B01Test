package org.zerock.springbootb01.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.springbootb01.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
  // 기본적인 crud 를 확인 가능.
  // 페이징 처리 위해서, JPQL 문법 이용하기.
  @Query("select r from Reply r where r.board.bno = :bno")
  Page<Reply> listOfBoard(Long bno, Pageable pageable);

  void deleteByBoard_Bno(Long bno);
}

package org.zerock.springbootb01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.springbootb01.domain.Board;
import org.zerock.springbootb01.domain.Reply;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {
  @Autowired
  private ReplyRepository replyRepository;

  @Test
  public void testInsert() {

    // 더미 데이터 예제
    // 게시글 번호 100번, 100번 게시글에 댓글 추가중.
    Long bno = 1L;

    // Reply 클래스에 멤버로 사용될 더미 예제
    Board board = Board.builder().bno(bno).build();

    // Reply 클래스 생성 , 빌더 패턴으로
    Reply reply = Reply.builder()
        .board(board)
        .replyText("샘플 댓글4")
        .replyer("이상용4")
        .build();

    // 저장하기. JPA
    replyRepository.save(reply);
  }

  // 페이징 처리 댓글 조회 테스트
  @Test
  @Transactional
  public void testBoardReplies() {
    // 100번 게시글의 댓글 목록 조회를 페이징 처리해서 조회함.
    Long bno = 100L;
    Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());

    Page<Reply> result = replyRepository.listOfBoard(bno,pageable);

    result.getContent().forEach(reply -> log.info(reply));
  }
}

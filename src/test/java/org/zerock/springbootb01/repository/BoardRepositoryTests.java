package org.zerock.springbootb01.repository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.springbootb01.domain.Board;
import org.zerock.springbootb01.domain.BoardImage;
import org.zerock.springbootb01.dto.BoardListAllDTO;
import org.zerock.springbootb01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;

  @Test
  public void testInsert(){
    IntStream.rangeClosed(1,100).forEach(i->{
        Board board = Board.builder()
            .title("title..."+i)
            .content("content..."+i)
            .writer("user"+(i%10))
        .build();
        Board result = boardRepository.save(board);
        log.info("BNO: " + result.getBno());
    });
  }
  @Test
  public void testSelect(){
    Long bno = 100L;
    Optional<Board> result = boardRepository.findById(bno);
    Board board = result.orElseThrow();
    log.info(board);
  }

  @Test
  public void testUpdate(){
    Long bno = 100L;
    Optional<Board> result = boardRepository.findById(bno);
    Board board = result.orElseThrow();
    board.change("update..title 100", "update content 100");
    boardRepository.save(board);
    log.info(board);
  }

  @Test
  public void testDelete(){
    Long bno = 101L;
    boardRepository.deleteById(bno);
  }

  @Test
  public void testPaging(){
    Pageable pageable =
        PageRequest.of(1,10, Sort.by("bno").descending());
    Page<Board> result = boardRepository.findAll(pageable);
    log.info("total Count : " + result.getTotalElements());
    log.info("total pages : " + result.getTotalPages());
    log.info("page number : " + result.getNumber());
    log.info("page size : " + result.getSize());
    List<Board> todoList = result.getContent();
    todoList.forEach(board -> log.info(board));
  }
  @Test
  public void testSearch1(){
    Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());
    boardRepository.search1(pageable);
  }

  @Test
  public void testSearchAll(){
    //동적 WHERE위한 조건식값 설정하기 title, content, writer
    String[] types = {"t","c","w"};
    // 검색할 문자열 저장
    String keyword = "1";
    //몇개의 데이터를 어떤 정렬로 검색할지 설정
    Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());
    //위의 조건식으로 데이터베이스에서 조회하는 레포지토리 실행
    Page<Board> result = boardRepository.searchAll(types,keyword,pageable);
    log.info(result.getTotalPages());
    log.info(result.getSize());
    log.info(result.getNumber());
    log.info(result.hasPrevious() + ":" + result.hasNext());
    result.getContent().forEach(board->log.info(board));
  }

  //게시글 조회시, 해당 댓글의 갯수도 같이 조회하기.
  @Test
  public void testSearchReplyCount(){
    //동적 WHERE위한 조건식값 설정하기 title, content, writer
    String[] types = {"t","c","w"};
    // 검색할 문자열 저장
    String keyword = "1";
    //몇개의 데이터를 어떤 정렬로 검색할지 설정
    Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());
    //위의 조건식으로 데이터베이스에서 조회하는 레포지토리 실행
    Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types,keyword,pageable);

    log.info(result.getTotalPages());
    log.info(result.getSize());
    log.info(result.getNumber());
    log.info(result.hasPrevious() + ":" + result.hasNext());
    result.getContent().forEach(board->log.info(board));
  }

  @Test
  public void testInsertWithImages() {
    Board board = Board.builder()
            .title("Image Test")
            .content("첨부파일 테스트")
            .writer("tester")
            .build();

    for (int i = 0; i < 3; i++) {
      board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
    }
    boardRepository.save(board);
  }

  @Test
  public void testReadWithImages() {

    // 반드시 존재하는 bno로 확인
    // PK를 이용해 게시글 SELECT 실행
    Optional<Board> result = boardRepository.findByIdWithImages(1L);

    // 데이터가 없는 경우 에러발생
    Board board = result.orElseThrow();

    // 데이터 확인
    log.info(board);
    log.info("-------------------");
    for (BoardImage boardImage : board.getImageSet()) {
      log.info(boardImage);
    }
  }

  @Transactional
  @Commit
  @Test
  public void testModifyImages() {
    Optional<Board> result = boardRepository.findByIdWithImages(1L);

    Board board = result.orElseThrow();

    // 기존의 첨부파일들은 삭제
    board.clearImages();

    // 새로운 첨부파일들
    for (int i = 0 ; i < 3; i++) {
      board.addImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
    }
    boardRepository.save(board);
  }

  @Transactional
  @Commit
  @Test
  public void testRemoveAll() {
    Long bno = 1L;
    replyRepository.deleteByBoard_Bno(bno);
    boardRepository.deleteById(bno);
  }

  @Test
  public void testInsertAll() {
    for (int i = 1; i <= 100; i++) {
      Board board = Board.builder()
              .title("Title.." + 1)
              .content("Content..." + i)
              .writer("writer..." + i)
              .build();
      for ( int j = 0; j < 3; j++) {
        if(i % 5 == 0) {
          continue;
        }
        board.addImage(UUID.randomUUID().toString(), i+ "file" +j+ "jpg");
      }
      boardRepository.save(board);
    }
  }

  @Transactional
  @Test
  public void testSearchImageReplyConut() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
//    boardRepository.searchWithAll(null, null, pageable);

    Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);
    log.info("-------------------");
    log.info(result.getTotalElements());

    result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));
  }
}


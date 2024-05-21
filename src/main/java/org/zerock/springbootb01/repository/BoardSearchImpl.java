package org.zerock.springbootb01.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.springbootb01.domain.Board;
import org.zerock.springbootb01.domain.QBoard;
import org.zerock.springbootb01.domain.QReply;
import org.zerock.springbootb01.dto.BoardImageDTO;
import org.zerock.springbootb01.dto.BoardListAllDTO;
import org.zerock.springbootb01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {
    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {
        //queryDSL을 이용한 객체 설정
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);
        //JPQL을 사용한 WHERE 메서드로 조건식 추가
        query.where(board.title.contains("1"));
        //pageable 설정
        this.getQuerydsl().applyPagination(pageable, query);

        //위에서 설정한 조건대로 데이터를 조회
        List<Board> list = query.fetch();
        //페이지수, 총 행수, 총 페이지수 데이터 조회
        long count = query.fetchCount();
        System.out.println(list);
        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {

        //querydsl로 생성된 qboard 설정
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        //검색 조건인 types와 키워드가 존재하는 확인 if문
        if ((types != null && types.length > 0) && (keyword != null)) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t":
                        // OR title LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        // OR content LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        // OR writer LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            // 실행할 쿼리문에 types, keyword 조건절 추가
            query.where(booleanBuilder);
        }
        // AND bno > 0 : WHERE 쿼리 추가
        query.where(board.bno.gt(0L));
        // ORDER BY bno DESC limit 0,10 : 정렬 및 리미트 SQL추가
        this.getQuerydsl().applyPagination(pageable, query);
        // SQL 실행
        List<Board> list = query.fetch();
        // count관련 SQL 실행
        long count = query.fetchCount();
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        //QueryDSL 의 QDomain 사용하기.
        // Query 애너테이션안에 문자열, sql 문법을 사용시, 컴파일 체크가 안됨.
        // 그래서, QueryDSL 동적으로 사용하면, 자바 문법 형식으로 데이터베이스 타입으로
        // 변환이 쉽다.
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));

        query.groupBy(board);

        // p544, 최종 코드로 확인.
// 위의 코드 중복되어서, 재사용 함.
        if ((types != null && types.length > 0) && (keyword != null)) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t":
                        // OR title LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        // OR content LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        // OR writer LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            // 실행할 쿼리문에 types, keyword 조건절 추가
            query.where(booleanBuilder);
        }
        // AND bno > 0 : WHERE 쿼리 추가
        query.where(board.bno.gt(0L));

        // p544 쪽 마지막 라인 부분.

        // 추가 부분.
        // Projections 도구를 이용해서, 엔티티 클래스를 자동으로 DTO타입으로 형변환해준다.
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.bean(
                BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count().as("replyCount")
        ));


        // ORDER BY bno DESC limit 0,10 : 정렬 및 리미트 SQL추가
        this.getQuerydsl().applyPagination(pageable, dtoQuery);
        // SQL 실행
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();
        // count관련 SQL 실행
        long count = dtoQuery.fetchCount();
        return new PageImpl<>(dtoList, pageable, count);
    }

//  @Override
//  public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
//
//    QBoard board = QBoard.board;
//    QReply reply = QReply.reply;
//
//    JPQLQuery<Board> boardJPQLQuery = from(board);
//    boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));
//
//    getQuerydsl().applyPagination(pageable, boardJPQLQuery);
//
//    List<Board> boardList = boardJPQLQuery.fetch();
//
//    boardList.forEach(board1 -> {
//      System.out.println(board1.getBno());
//      System.out.println(board1.getImageSet());
//      System.out.println("----------------------");
//    });
//    return null;
//  }

    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));
        if ((types != null && types.length > 0) && (keyword != null)) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t":
                        // OR title LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        // OR content LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        // OR writer LIKE '%keyword%' : SQL작성
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            // 실행할 쿼리문에 types, keyword 조건절 추가
            boardJPQLQuery.where(booleanBuilder);
        }
        boardJPQLQuery.groupBy(board);

        getQuerydsl().applyPagination(pageable, boardJPQLQuery);

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {
            Board board1 = (Board) tuple.get(board);
            long replyCount = tuple.get(1, Long.class);

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();
            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setBoardImages(imageDTOS);

            return dto;
        }).collect(Collectors.toList());
        long totalCount = boardJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

}














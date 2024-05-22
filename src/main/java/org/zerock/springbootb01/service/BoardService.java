package org.zerock.springbootb01.service;

import org.zerock.springbootb01.domain.Board;
import org.zerock.springbootb01.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface BoardService {
  Long register(BoardDTO boardDTO);
  BoardDTO readOne(Long bno);
  void modify(BoardDTO boardDTO);
  void remove(Long bno);
  PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);
  // 게시글 목록에 댓글 갯수 표시 하기.
  PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

  // 게시글의 이미지와 댓글의 숫자까지 처리
  PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

  // boardDTO를 board객체로 바꾸기 위해 사용하는 메서드
  default Board dtoToEntity(BoardDTO boardDTO) {
    Board board = Board.builder()
            .bno(boardDTO.getBno())
            .title(boardDTO.getTitle())
            .content(boardDTO.getContent())
            .writer(boardDTO.getWriter())

            .build();

    // boardDTO의 String[] 타입을 Set<BoardImage>타입으로 바꾸는 메서드
    if(boardDTO.getFileNames() != null) {

      // 반복문으로 String[]의 문자열을 하나씩 변경
      boardDTO.getFileNames().forEach(fileName -> {

        // uuid_파일이름.확장자 -> arr[0] = uuid, arr[1] = 파일이름.확장자
        String[] arr = fileName.split("_", 2);
        board.addImage(arr[0], arr[1]);
      });
    }
    return board;
  }

  // board를 boardDTO로 변환하기 위한 메서드
  default BoardDTO entityToDTO(Board board) {

    // 단순 데이터 설정
    BoardDTO boardDTO = BoardDTO.builder()
            .bno(board.getBno())
            .title(board.getTitle())
            .content(board.getContent())
            .writer(board.getWriter())
            .regDate(board.getRegDate())
            .modDate(board.getModDate())
            .build();

    // Set<boardImage>을 List<String>로 변환하기위한 코드
    // uuid_파일명.확장자 형식으로 boardImage데이터를 String 타입 변환
    // 변환한 결과값을 합쳐서 List타입으로 반환
    List<String> fileName = board.getImageSet().stream().sorted().map(boardImage ->
            boardImage.getUuid()+"_"+boardImage.getFileName()).collect(Collectors.toList());

    boardDTO.setFileNames(fileName);

    return boardDTO;
  }
}


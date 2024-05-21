package org.zerock.springbootb01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.springbootb01.domain.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardSearch{
  @Query(value = "SELECT now()", nativeQuery = true)
  String getTime();

  @EntityGraph(attributePaths = {"imageSet"})
  @Query("select b from Board b where b.bno = :bno")
  Optional<Board> findByIdWithImages(Long bno);
}

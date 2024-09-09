package com.around.tdd.repository;

import com.around.tdd.vo.BoardContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardContentRepository extends JpaRepository<BoardContent, Long> {

}

package com.around.tdd.repository;

import com.around.tdd.vo.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {

}

package com.around.tdd.repository;

import com.around.tdd.vo.Board;
import com.around.tdd.vo.BoardImage;
import com.around.tdd.vo.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class BoardImageRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardImageRepository boardImageRepository;

    @Test
    @DisplayName("게시판 이미지 저장 테스트")
    @Rollback(value = false)
    public void saveBoardImage() {
        //given
        Member member = Member
                .builder()
                .id("yejin1224")
                .password("1234560")
                .state(1)
                .build();

        Member savedMember = memberRepository.save(member);

        Board board = Board.builder()
                .title("Test Title")
                .member(savedMember)
                .views(0)
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .build();
        Board savedBoard = boardRepository.save(board);

        BoardImage boardImage = BoardImage.builder()
                .board(savedBoard)
                .imageUrl("/upload/test.jpg")
                .build();

        //when
        BoardImage savedBoardImage = boardImageRepository.save(boardImage);

        //then
        assertThat(savedBoardImage.getBoardImageSeq()).isNotNull();
        assertThat(savedBoardImage.getImageUrl()).isEqualTo(boardImage.getImageUrl());
    }
}

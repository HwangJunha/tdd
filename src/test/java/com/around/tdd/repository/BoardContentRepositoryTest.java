package com.around.tdd.repository;

import com.around.tdd.vo.Board;
import com.around.tdd.vo.BoardContent;
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
public class BoardContentRepositoryTest {
    @Autowired
    private BoardContentRepository boardContentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("게시판 내용 저장 테스트")
    @Rollback(value = false)
    public void saveBoardContent() {
        //given
        Member member = new Member();
        member.setId("yejin1224");
        member.setPassword("1234560");
        member.setState(1);

        Member savedMember = memberRepository.save(member);

        Board board = Board.builder()
                .title("Test Title")
                .member(savedMember)
                .views(0)
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .build();
        Board savedBoard = boardRepository.save(board);

        BoardContent boardContent = BoardContent.builder()
                .board(savedBoard)
                .content("Board Content Test")
                .build();

        //when
        BoardContent savedBoardContent = boardContentRepository.save(boardContent);

        //then
        assertThat(savedBoardContent.getBoard().getBoardSeq()).isNotNull();
        assertThat(savedBoardContent.getContent()).isEqualTo("Board Content Test");
    }
}

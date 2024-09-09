package com.around.tdd.repository;

import static org.assertj.core.api.Assertions.assertThat;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class BoardRepositoryTest {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("게시판 저장 테스트")
    @Rollback(value = false)
    public void saveBoardTest() {
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

        assertThat(savedBoard.getBoardSeq()).isNotNull();
        assertThat(savedBoard.getMember()).isEqualTo(savedMember);  // 멤버가 제대로 연결되었는지 확인
        assertThat(savedBoard.getTitle()).isEqualTo("Test Title");
    }
}

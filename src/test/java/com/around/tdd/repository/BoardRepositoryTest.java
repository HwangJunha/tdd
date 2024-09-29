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
import java.util.Optional;

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
        Member member = Member.builder()
                .id("yejin1224")
                .password("123456")
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

        assertThat(savedBoard.getBoardSeq()).isNotNull();
        assertThat(savedBoard.getMember()).isEqualTo(savedMember);  // 멤버가 제대로 연결되었는지 확인
        assertThat(savedBoard.getTitle()).isEqualTo("Test Title");
    }

    @Test
    @DisplayName("게시판 상세 조회 성공 테스트")
    public void testFindByIdBoardExists() {
        // Given: 게시판 엔티티를 저장
        Member member = Member.builder()
                .id("yejin1224")
                .password("123456")
                .state(1)
                .build();

        Board board = Board.builder()
                .boardSeq(1L)
                .title("Test Title")
                .member(member)
                .views(100)
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .build();
        Member savedMember = memberRepository.save(member);
        Board savedboard = boardRepository.save(board);

        // When: 해당 boardSeq로 게시판을 조회
        Optional<Board> foundBoard = boardRepository.findById(board.getBoardSeq());

        // Then: 조회된 결과가 있는지 확인
        assertThat(foundBoard).isPresent();
        assertThat(foundBoard.get().getTitle()).isEqualTo("Test Title");
        assertThat(foundBoard.get().getViews()).isEqualTo(100);
    }

    @Test
    @DisplayName("게시판 상세 조회 실패 테스트")
    public void testFindById_BoardNotExists() {
        // Given: 존재하지 않는 boardSeq로 조회 시도
        Long nonExistentBoardSeq = 999L;

        // When: 해당 boardSeq로 게시판을 조회
        Optional<Board> foundBoard = boardRepository.findById(nonExistentBoardSeq);

        // Then: 조회된 결과가 없는지 확인
        assertThat(foundBoard).isNotPresent();
    }
}

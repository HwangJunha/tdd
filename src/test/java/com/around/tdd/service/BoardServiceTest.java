package com.around.tdd.service;


import com.around.tdd.repository.BoardRepository;
import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.BoardDTO;
import com.around.tdd.vo.Member;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest
@Transactional
public class BoardServiceTest {
    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("정상 포스팅 테스트")
    public void testSavePost() throws Exception {
        //멤버 값 먼저 설정
        Member member = new Member();
        member.setId("yejin1224");
        member.setPassword("1234560");
        member.setState(1);

        memberRepository.save(member);

        // givin
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setTitle("제목 test");
        boardDTO.setMemberSeq(1L);
        boardDTO.setImageUrl("img url test");

        //when
        Board board = boardService.savePost(boardDTO);

        //then
        Long boardSeq = board.getBoardSeq();

        BoardDTO findBoardDTO = boardService.findPostById(boardSeq);

        Assertions.assertNotNull(findBoardDTO.getBoardSeq(), "게시글 저장이 실패했습니다.");
        Assertions.assertEquals(boardDTO.getTitle(), board.getTitle(), "게시글 제목이 비정상입니다.");
        Assertions.assertEquals(boardDTO.getMemberSeq(), board.getMember().getMemberSeq(), "게시글 작성자가 비정상입니다.");
    }

    @Test
    @DisplayName("게시판 id로 조회 기능 테스트")
    public void testFindPostById() {

        // 멤버 먼저 설정
        Member member = new Member();
        member.setId("yejin1224");
        member.setPassword("1234560");
        member.setState(1);

        Member savedMember = memberRepository.save(member);

        // 게시판 먼저 설정
        Board board = new Board();
        Member postMember = new Member();

        //given
        postMember.setMemberSeq(savedMember.getMemberSeq());
        board.setMember(postMember);
        board.setTitle("find by post id test");
        board.setViews(0);
        board.setInputDt(LocalDateTime.now());
        board.setUpdateDT(LocalDateTime.now());

        Board savedBoard = boardRepository.save(board);
        Long boardSeq = savedBoard.getBoardSeq();

        // when
        BoardDTO boardDTO = boardService.findPostById(boardSeq);

        //then
        Assertions.assertEquals(boardDTO.getBoardSeq(), boardSeq);
    }
}

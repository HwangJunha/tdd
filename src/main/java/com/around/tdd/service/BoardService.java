package com.around.tdd.service;

import com.around.tdd.repository.BoardRepository;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.BoardContent;
import com.around.tdd.vo.BoardDTO;
import com.around.tdd.vo.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Transactional
    public Board savePost(BoardDTO boardDTO) {
        try {
            // 회원 인증
            // 게시판 작성
            Board board = saveBoard(boardDTO);
            return board;
        } catch (Exception e) {
            System.out.println(e);
            return new Board();
        }
    }

    public Board saveBoard(BoardDTO boardDTO) {
        Board board = new Board();
        Member member = new Member();
        member.setMemberSeq(boardDTO.getMemberSeq());

        board.setMember(member);
        board.setTitle(boardDTO.getTitle());
        board.setViews(0);
        board.setInputDt(LocalDateTime.now());
        board.setUpdateDT(LocalDateTime.now());

        return boardRepository.save(board);
    }

    public BoardDTO findPostById(Long boardSeq) {
        Optional<Board> board = boardRepository.findById(boardSeq);
        Long savedBoardSeq = board.orElseThrow().getBoardSeq();

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardSeq(savedBoardSeq);

        return boardDTO;
    }
}
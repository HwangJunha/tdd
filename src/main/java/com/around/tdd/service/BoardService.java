package com.around.tdd.service;

import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.repository.BoardContentRepository;
import com.around.tdd.repository.BoardImageRepository;
import com.around.tdd.repository.BoardRepository;
import com.around.tdd.vo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardContentRepository boardContentRepository;

    @Autowired
    private BoardImageRepository boardImageRepository;

    @Autowired
    private FileStorageService fileStorageService;


    @Transactional
    public Board savePost(BoardDTO boardDTO, List<MultipartFile> boardImages) {
        // 유효성 체크
        validateBoardRequest(boardDTO);

        // 게시판 작성
        Board board = saveBoard(boardDTO);

        // 게시글 작성
        BoardContent boardContent = saveBoardContent(boardDTO, board);

        // 게시글 이미지 작성
        if (boardImages != null && !boardImages.isEmpty()) {
            List<BoardImage> boardImage = saveBoardImage(boardImages, board);
        }

        return board;
    }

    public Board saveBoard(BoardDTO boardDTO) {
        // TODO 회원 조회
//        Member member = new Member();
//        member.setMemberSeq(1L);
//        member.setId("yejin1224");
//        member.setPassword("123456");

        Board board = Board.builder()
//                .member(member)
                .title(boardDTO.getTitle())
                .views(0)
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .build();

        return boardRepository.save(board);
    }

    public BoardContent saveBoardContent(BoardDTO boardDTO, Board board) {
        BoardContent boardContent = BoardContent.builder()
                .board(board)
                .content(boardDTO.getContent())
                .build();

        return boardContentRepository.save(boardContent);
    }

    public List<BoardImage> saveBoardImage(List<MultipartFile> requestBoardImages, Board board) {
        List<BoardImage> boardImages = new ArrayList<>();

        for (MultipartFile file : requestBoardImages) {
            String fileName = fileStorageService.store(file);

            BoardImage boardImage = BoardImage.builder()
                    .imageUrl("/uploads/" + fileName)
                    .board(board)
                    .saveName(fileName)
                    .inputDt(LocalDateTime.now())
                    .updateDt(LocalDateTime.now())
                    .build();

            boardImageRepository.save(boardImage);
            boardImages.add(boardImage);
        }
        return boardImages;
    }

    public Board findPostById(Long boardSeq) {
        return boardRepository.findById(boardSeq).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public void validateBoardRequest(BoardDTO boardDTO) {
        // 제목 길이 검증 (최대 1000자)
        if (boardDTO.getTitle().length() > 1000) {
            throw new BoardSaveException("제목 유효성 검증에 실패 하였습니다.");
        }

        // 내용 길이 검증 (최대 160,000자)
        if (boardDTO.getContent().length() > 160000) {
            throw new BoardSaveException("게시글 유효성 검증에 실패 하였습니다.");
        }

        // 이모지 유효성 검증은 따로 필요 없지만, UTF-8/UTF-16 문자셋 지원 확인 가능
        if (!StringUtils.hasText(boardDTO.getTitle()) || !StringUtils.hasText(boardDTO.getContent())) {
            throw new BoardSaveException("불가능한 문자셋입니다.");
        }
    }
}

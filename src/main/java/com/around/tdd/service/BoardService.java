package com.around.tdd.service;

import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.repository.BoardContentRepository;
import com.around.tdd.repository.BoardImageRepository;
import com.around.tdd.repository.BoardRepository;
import com.around.tdd.vo.*;
import com.around.tdd.vo.request.BoardRequest;
import com.around.tdd.vo.response.BoardResponse;
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
    public Board savePost(BoardRequest boardRequest, List<MultipartFile> boardImages) {
        // 유효성 체크
        validateBoardRequest(boardRequest);

        // 게시판 작성
        Board board = saveBoard(boardRequest);

        // 게시글 작성
        BoardContent boardContent = saveBoardContent(boardRequest, board);

        // 게시글 이미지 작성
        if (boardImages != null && !boardImages.isEmpty()) {
            List<BoardImage> boardImage = saveBoardImage(boardImages, board);
        }

        return board;
    }

    public Board saveBoard(BoardRequest boardRequest) {
        // TODO 회원 조회
        Member member = Member.builder()
                .id("yejin1224")
                .password("123456")
                .build();

        Board board = Board.builder()
                .member(member)
                .title(boardRequest.getTitle())
                .views(0)
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .build();

        return boardRepository.save(board);
    }

    public BoardContent saveBoardContent(BoardRequest boardRequest, Board board) {
        BoardContent boardContent = BoardContent.builder()
                .board(board)
                .content(boardRequest.getContent())
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

    public BoardResponse getBoardById(Long boardSeq) {
        Board board = boardRepository.findById(boardSeq).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        BoardContent boardContent = boardContentRepository.findById(boardSeq).orElseThrow(() -> new IllegalArgumentException("게시글 내용을 찾을 수 없습니다."));

        BoardResponse boardResponse = BoardResponse.builder()
                .boardSeq(boardSeq)
                .title(board.getTitle())
                .content(boardContent.getContent())
                .memberId("yejin1224")
                .views(board.getViews())
                .build();

        return boardResponse;
    }

    public void validateBoardRequest(BoardRequest boardRequest) {
        // 제목 길이 검증 (최대 1000자)
        if (boardRequest.getTitle().length() > 1000) {
            throw new BoardSaveException("제목 유효성 검증에 실패 하였습니다.");
        }

        // 내용 길이 검증 (최대 160,000자)
        if (boardRequest.getContent().length() > 160000) {
            throw new BoardSaveException("게시글 유효성 검증에 실패 하였습니다.");
        }

        // 이모지 유효성 검증은 따로 필요 없지만, UTF-8/UTF-16 문자셋 지원 확인 가능
        if (!StringUtils.hasText(boardRequest.getTitle()) || !StringUtils.hasText(boardRequest.getContent())) {
            throw new BoardSaveException("불가능한 문자셋입니다.");
        }
    }
}

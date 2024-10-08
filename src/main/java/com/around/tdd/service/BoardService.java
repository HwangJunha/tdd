package com.around.tdd.service;

import com.around.tdd.exception.BoardNotFoundException;
import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.repository.BoardContentRepository;
import com.around.tdd.repository.BoardImageRepository;
import com.around.tdd.repository.BoardRepository;
import com.around.tdd.vo.*;
import com.around.tdd.vo.request.BoardRequest;
import com.around.tdd.vo.response.BoardDetailResponse;
import com.around.tdd.vo.response.BoardListResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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

    public BoardDetailResponse getBoardById(Long boardSeq) {
        Board board = boardRepository.findById(boardSeq).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다."));
        BoardContent boardContent = boardContentRepository.findById(boardSeq).orElseThrow(() -> new BoardNotFoundException("게시글 내용을 찾을 수 없습니다."));

        // 게시판 조회수 증가
        board.incrementViewCount();
        boardRepository.save(board);

        BoardDetailResponse boardDetailResponse = BoardDetailResponse.builder()
                .boardSeq(boardSeq)
                .title(board.getTitle())
                .content(boardContent.getContent())
                .memberId("yejin1224")
                .views(board.getViews())
                .build();

        return boardDetailResponse;
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

    // 페이징 처리된 게시판 리스트 반환
    public List<BoardListResponse> getBoardList(Pageable pageable) {
        List<BoardListResponse> boardListResponses = new ArrayList<>();
        Page<Board> boardPage = boardRepository.findAll(pageable);

        for (Board board: boardPage) {
            BoardListResponse boardListResponse = BoardListResponse.builder()
                    .boardSeq(board.getBoardSeq())
                    .title(board.getTitle())
                    .memberId(board.getMember().getId())
                    .views(board.getViews())
                    .inputDt(board.getInputDt())
                    .build();

            boardListResponses.add(boardListResponse);
        }

        return boardListResponses;
    }

    public BoardDetailResponse updateBoard(BoardRequest boardRequest) {
        Long boardSeq = boardRequest.getBoardSeq();
        Optional<Board> boardOptional = boardRepository.findById(boardSeq);
        Optional<BoardContent> boardContentOptional = boardContentRepository.findById(boardSeq);

        // TODO 권한 추가

        if (boardOptional.isPresent() && boardContentOptional.isPresent()) {
            // 게시글 수정
            Board board = Board.builder()
                    .boardSeq(boardSeq)
                    .title(boardRequest.getTitle() != null ? boardRequest.getTitle() : boardOptional.get().getTitle())
                    .member(boardOptional.get().getMember())
                    .views(boardOptional.get().getViews())
                    .updateDT(LocalDateTime.now())
                    .build();

            // 게시글 내용 수정
            BoardContent boardContent = BoardContent.builder()
                    .boardSeq(boardSeq)
                    .content(boardRequest.getContent() != null ? boardRequest.getContent() : boardContentOptional.get().getContent())
                    .board(board)
                    .build();

            Board updatedBoard = boardRepository.save(board);
            BoardContent updatedBoardContent = boardContentRepository.save(boardContent);


            return BoardDetailResponse.builder()
                    .boardSeq(boardSeq)
                    .title(board.getTitle())
                    .content(boardContent.getContent())
                    .build();
        } else {
            throw new BoardNotFoundException("해당 게시글을 찾을 수 없습니다.");
        }
    }
}

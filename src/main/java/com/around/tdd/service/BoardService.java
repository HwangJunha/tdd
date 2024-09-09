package com.around.tdd.service;

import com.around.tdd.repository.BoardContentRepository;
import com.around.tdd.repository.BoardImageRepository;
import com.around.tdd.repository.BoardRepository;
import com.around.tdd.vo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        try {
            // 게시판 작성
            Board board = saveBoard(boardDTO);

            // 게시글 작성
            BoardContent boardContent = saveBoardContent(boardDTO, board);

            // 게시글 이미지 작성
            if (boardImages != null && !boardImages.isEmpty()) {
                List<BoardImage> boardImage = saveBoardImage(boardImages, board);
            }

            return board;
        } catch (Exception e) {
            return Board.builder().build();
        }
    }

    public Board saveBoard(BoardDTO boardDTO) {
        Member member = new Member();
        member.setMemberSeq(1L);
        member.setId("yejin1224");
        member.setPassword("123456");

        Board board = Board.builder()
                .member(member)
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
}

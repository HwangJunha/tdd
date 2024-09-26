package com.around.tdd.service;


import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.repository.BoardContentRepository;
import com.around.tdd.repository.BoardImageRepository;
import com.around.tdd.repository.BoardRepository;
import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.*;
import com.around.tdd.vo.request.BoardRequest;
import com.around.tdd.vo.response.BoardResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardContentRepository boardContentRepository;

    @Mock
    private BoardImageRepository boardImageRepository;
    @Mock
    private FileStorageService fileStorageService;

    @Test
    @DisplayName("정상 게시판 등록 테스트")
    void testSavePost() {
        Board mockBoard = Board.builder()
                .boardSeq(1L)
                .title("Test Title")
                .build();
        when(boardRepository.save(any(Board.class))).thenReturn(mockBoard);

        BoardContent mockBoardContent = BoardContent.builder()
                .board(mockBoard)
                .content("Test Content")
                .build();

        when(boardContentRepository.save(any(BoardContent.class))).thenReturn(mockBoardContent);

        BoardRequest boardRequest = BoardRequest.builder()
                .title("Test Title")
                .memberId("yejin1224")
                .content("Test Content")
                .build();

        List<MultipartFile> mockFiles = createMockImages();


        // BoardService의 savePost 메서드 호출
        Board savedBoard = boardService.savePost(boardRequest, mockFiles);

        // 결과 검증
        assert savedBoard != null;
        assert savedBoard.getBoardSeq().equals(1L);
        assert savedBoard.getTitle().equals("Test Title");
    }

    @Test
    @DisplayName("정상 포스팅 테스트")
    void testSaveBoard() {
        //멤버 값 먼저 설정
        // Member member = new Member();
        Member member = Member.builder()
                .id("yejin1224")
                .build();

        // given
        BoardRequest boardRequest = BoardRequest.builder()
                .title("제목 test")
                .memberId(member.getId())
                .build();

        boardRequest.setTitle("제목 test");
        boardRequest.setMemberId(member.getId());

        Board board = Board.builder()
                .title(boardRequest.getTitle())
                .views(0)
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .member(member)
                .build();

        when(boardRepository.save(any(Board.class))).thenReturn(board);

        //when
        Board saveBoard = boardService.saveBoard(boardRequest);

        //then
        Assertions.assertEquals("제목 test", saveBoard.getTitle());
        Assertions.assertEquals("yejin1224", saveBoard.getMember().getId());
    }

    @Test
    @DisplayName("정상 게시글 저장 테스트")
    void testSaveBoardContent() {
        // given
        Board board = Board.builder()
                .boardSeq(1L)
                .build();

        BoardContent boardContent = BoardContent.builder()
                .board(board)
                .content("Test Board Content")
                .build();

        BoardRequest boardRequest = BoardRequest.builder()
                .content(boardContent.getContent())
                .build();

        when(boardContentRepository.save(any(BoardContent.class))).thenReturn(boardContent);

        //when
        BoardContent savedBoardContent = boardService.saveBoardContent(boardRequest, board);

        //then
        Assertions.assertEquals("Test Board Content", savedBoardContent.getContent());
    }

    @Test
    @DisplayName("게시판 이미지 저장 테스트")
    void testSaveBoardImage() {
        // given
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);

        when(fileStorageService.store(mockFile1)).thenReturn("image1.jpg");
        when(fileStorageService.store(mockFile2)).thenReturn("image2.jpg");

        MultipartFile[] mockFiles = {mockFile1, mockFile2};

        Board board = Board.builder()
                .boardSeq(1L)
                .build();

        //when
        List<BoardImage> savedBoardImage = boardService.saveBoardImage(List.of(mockFiles), board);

        //then
        Assertions.assertNotNull(savedBoardImage);
        Assertions.assertEquals(2, savedBoardImage.size());

        // 첫번째 이미지 확인
        String firstImage = savedBoardImage.get(0).getImageUrl();
        Assertions.assertEquals("/uploads/image1.jpg", firstImage);

        // 두번째 이미지 확인
        String secondImage = savedBoardImage.get(1).getImageUrl();
        Assertions.assertEquals("/uploads/image2.jpg", secondImage);
    }

    @Test
    @DisplayName("게시판 상세 조회 성공 테스트")
    public void testGetBoardByIdSuccess() {
        // 멤버 설정
        Member member = Member.builder()
                .id("yejin1224")
                .build();

        // 게시판 설정
        Board board = Board.builder()
                .boardSeq(1L)
                .member(member)
                .title("Test title")
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .views(0)
                .build();

        BoardContent boardContent = BoardContent.builder()
                .boardSeq(1L)
                .content("content test")
                .build();

        //given
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardContentRepository.findById(1L)).thenReturn(Optional.of(boardContent));

        // when
        BoardResponse responseBoard = boardService.getBoardById(1L);

        //then
        Assertions.assertNotNull(responseBoard);
        Assertions.assertEquals(board.getBoardSeq(), responseBoard.getBoardSeq());
        Assertions.assertEquals(board.getTitle(), responseBoard.getTitle());
        Assertions.assertEquals(board.getViews(), responseBoard.getViews());
        Assertions.assertEquals(boardContent.getContent(), responseBoard.getContent());
    }

    @Test
    @DisplayName("게시판 상세조회 게시글 존재하지 않은 경우 테스트")
    public void testGetBoardByIdBoardNotFound() {
        Long boardSeq = 1L;

        //given
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.getBoardById(boardSeq);
        });

        //then
        Assertions.assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게시판 상세 조회 게시글 내용 존재하지 않은 경우 테스트")
    public void testGetBoardByIdBoardContentNotFound() {
        Long boardSeq = 1L;
        Board board = Board.builder()
                .boardSeq(boardSeq)
                .title("Test0 Title")
                .build();

        //given
        when(boardRepository.findById(boardSeq)).thenReturn(Optional.of(board));
        when(boardContentRepository.findById(boardSeq)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.getBoardById(boardSeq);
        });

        //then
        Assertions.assertEquals("게시글 내용을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void testValidationBoardRequestImoji() {
        BoardRequest boardRequest = BoardRequest.builder()
                .title("Test Title")
                .content("Valid content with emojis 😊😂👍")
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        boardService.savePost(boardRequest, mockFiles);

        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    public void testValidationBoardRequestTitleTooLong() {
        // 1001자 제목
        String longTitle = "t".repeat(1001);
        BoardRequest boardRequest = BoardRequest.builder()
                .title(longTitle)
                .content("Valid Content")
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        assertThrows(BoardSaveException.class, () -> boardService.savePost(boardRequest, mockFiles));
    }

    @Test
    public void testValidationBoardRequestContentTooLong() {
        // 160,001자 내용
        String longContent = "a".repeat(160001);
        BoardRequest boardRequest = BoardRequest.builder()
                .title("Valid Title")
                .content(longContent)
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        assertThrows(BoardSaveException.class, () -> boardService.savePost(boardRequest, mockFiles));
    }

    @Test
    public void testValidationBoardRequestContentBlank() {
        BoardRequest boardRequest = BoardRequest.builder()
                .title("Valid Title")
                .content("")
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        assertThrows(BoardSaveException.class, () -> boardService.savePost(boardRequest, mockFiles));
    }

    public List<MultipartFile> createMockImages() {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);

        return List.of(new MultipartFile[]{mockFile1, mockFile2});
    }
}

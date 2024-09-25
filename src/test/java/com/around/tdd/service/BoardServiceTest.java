package com.around.tdd.service;


import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.repository.BoardContentRepository;
import com.around.tdd.repository.BoardImageRepository;
import com.around.tdd.repository.BoardRepository;
import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.Assert.assertThrows;
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

    private Member member;

    @BeforeEach
    void setUp(){
        this.member = Member
                .builder()
                .id("yejin1224")
                .password("1234560")
                .state(1)
                .build();
    }

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

        BoardDTO boardDTO = BoardDTO.builder()
                .title("Test Title")
                .memberId("yejin1224")
                .content("Test Content")
                .build();

        List<MultipartFile> mockFiles = createMockImages();


        // BoardService의 savePost 메서드 호출
        Board savedBoard = boardService.savePost(boardDTO, mockFiles);

        // 결과 검증
        assert savedBoard != null;
        assert savedBoard.getBoardSeq().equals(1L);
        assert savedBoard.getTitle().equals("Test Title");
    }

    @Test
    @DisplayName("정상 포스팅 테스트")
    void testSaveBoard() {
        //멤버 값 먼저 설정

        // given
        BoardDTO boardDTO = BoardDTO.builder()
                .title("제목 test")
                .memberId(member.getId())
                .build();

        boardDTO.setTitle("제목 test");
        boardDTO.setMemberId(member.getId());

        Board board = Board.builder()
                .title(boardDTO.getTitle())
                .views(0)
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .member(member)
                .build();

        when(boardRepository.save(any(Board.class))).thenReturn(board);

        //when
        Board saveBoard = boardService.saveBoard(boardDTO);

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

        BoardDTO boardDTO = BoardDTO.builder()
                .content(boardContent.getContent())
                .build();

        when(boardContentRepository.save(any(BoardContent.class))).thenReturn(boardContent);

        //when
        BoardContent savedBoardContent = boardService.saveBoardContent(boardDTO, board);

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
    @DisplayName("게시판 id로 조회 기능 테스트")
    public void testFindPostById() {
        // 멤버 설정

        // 게시판 설정
        Board board = Board.builder()
                .boardSeq(1L)
                .member(member)
                .title("Test title")
                .inputDt(LocalDateTime.now())
                .updateDT(LocalDateTime.now())
                .views(0)
                .build();

        //given
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        Board foundBoard = boardService.findPostById(1L);

        //then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(board.getBoardSeq(), foundBoard.getBoardSeq());
        Assertions.assertEquals(board.getTitle(), foundBoard.getTitle());
    }

    @Test
    public void testValidationBoardRequestImoji() {
        BoardDTO boardDTO = BoardDTO.builder()
                .title("Test Title")
                .content("Valid content with emojis 😊😂👍")
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        boardService.savePost(boardDTO, mockFiles);

        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    public void testValidationBoardRequestTitleTooLong() {
        // 1001자 제목
        String longTitle = "t".repeat(1001);
        BoardDTO boardDTO = BoardDTO.builder()
                .title(longTitle)
                .content("Valid Content")
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        assertThrows(BoardSaveException.class, () -> boardService.savePost(boardDTO, mockFiles));
    }

    @Test
    public void testValidationBoardRequestContentTooLong() {
        // 160,001자 내용
        String longContent = "a".repeat(160001);
        BoardDTO boardDTO = BoardDTO.builder()
                .title("Valid Title")
                .content(longContent)
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        assertThrows(BoardSaveException.class, () -> boardService.savePost(boardDTO, mockFiles));
    }

    @Test
    public void testValidationBoardRequestContentBlank() {
        BoardDTO boardDTO = BoardDTO.builder()
                .title("Valid Title")
                .content("")
                .build();

        List<MultipartFile> mockFiles = createMockImages();
        assertThrows(BoardSaveException.class, () -> boardService.savePost(boardDTO, mockFiles));
    }

    public List<MultipartFile> createMockImages() {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);

        return List.of(new MultipartFile[]{mockFile1, mockFile2});
    }
}

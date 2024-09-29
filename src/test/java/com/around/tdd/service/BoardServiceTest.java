package com.around.tdd.service;


import com.around.tdd.exception.BoardNotFoundException;
import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.repository.BoardContentRepository;
import com.around.tdd.repository.BoardImageRepository;
import com.around.tdd.repository.BoardRepository;
import com.around.tdd.repository.MemberRepository;
import com.around.tdd.vo.*;
import com.around.tdd.vo.request.BoardRequest;
import com.around.tdd.vo.response.BoardDetailResponse;
import com.around.tdd.vo.response.BoardListResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        BoardDetailResponse responseBoard = boardService.getBoardById(1L);

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
        BoardNotFoundException exception = assertThrows(BoardNotFoundException.class, () -> {
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
        BoardNotFoundException exception = assertThrows(BoardNotFoundException.class, () -> {
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

    @Test
    @DisplayName("게시판 페이징 기능 테스트")
    public void testGetBoardList() {
        // Given
        List<Board> boardList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder()
                    .id("member" + i)
                    .build();

            Board board = Board.builder()
                    .boardSeq((long) i)
                    .title("제목" + i)
                    .member(member)
                    .views(i)
                    .inputDt(LocalDateTime.now())
                    .build();

            boardList.add(board);
        }

        Pageable pageable = PageRequest.of(0, 10); // 0번 페이지, 10개씩
        Page<Board> page = new PageImpl<>(boardList.subList(0, 10), pageable, boardList.size());

        when(boardRepository.findAll(pageable)).thenReturn(page);

        // When
        List<BoardListResponse> result = boardService.getBoardList(pageable);

        // Then
        Assertions.assertEquals(result.size(), 10);
        Assertions.assertEquals("제목1", result.get(0).getTitle());
        Assertions.assertEquals("member1", result.get(0).getMemberId());
        Assertions.assertEquals(1, result.get(0).getViews());

    }

    public List<MultipartFile> createMockImages() {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);

        return List.of(new MultipartFile[]{mockFile1, mockFile2});
    }

    @Test
    @DisplayName("게시판 조회수 정상 증가 테스트")
    public void testIncrementView() {
        // given
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

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardContentRepository.findById(1L)).thenReturn(Optional.of(boardContent));

        // when
        boardService.getBoardById(1L);

        // then
        Assertions.assertEquals(1, board.getViews());
    }
    @Test
    @DisplayName("게시판 수정 테스트 코드 작성")
    public void testUpdateBoard() {
        // Given
        Long boardSeq = 1L;
        // 멤버 설정
        Member member = Member.builder()
                .id("yejin1224")
                .build();

        Board existingBoard = Board.builder()
                .boardSeq(boardSeq)
                .title("Old Title")
                .member(member)  // 가짜 멤버 생성
                .views(100)
                .updateDT(LocalDateTime.of(2024, 9, 29, 0, 0))
                .build();

        BoardContent existingBoardContent = BoardContent.builder()
                .boardSeq(boardSeq)
                .content("Old Content")
                .board(existingBoard)
                .build();

        BoardRequest boardRequest = BoardRequest.builder()
                .boardSeq(boardSeq)
                .title("New Title")
                .content("New Content")
                .build();

        // 기존 게시글과 게시글 내용을 반환하도록 설정
        when(boardRepository.findById(boardSeq)).thenReturn(Optional.of(existingBoard));
        when(boardContentRepository.findById(boardSeq)).thenReturn(Optional.of(existingBoardContent));

        // When
        BoardDetailResponse updatedBoard = boardService.updateBoard(boardRequest);

        // Then
        Assertions.assertNotNull(updatedBoard);  // 수정된 게시글이 반환되었는지 확인
        Assertions.assertEquals("New Title", updatedBoard.getTitle());  // 제목이 수정되었는지 확인
        Assertions.assertEquals("New Content", updatedBoard.getContent());  // 내용이 수정되었는지 확인

        // 리포지토리가 한 번씩 호출되었는지 확인
        verify(boardRepository, times(1)).save(any(Board.class));
        verify(boardContentRepository, times(1)).save(any(BoardContent.class));
    }

    @Test
    @DisplayName("게시글 수정 테스트 - 게시글 존재하지 않은 경우")
    public void testUpdateBoardNotFound() {
        // given
        Long boardSeq = 1L;

        BoardRequest boardRequest = BoardRequest.builder()
                .boardSeq(boardSeq)
                .title("New Title")
                .content("New Content")
                .build();

        // 게시글이나 게시글 내용을 찾을 수 없는 경우
        when(boardRepository.findById(boardSeq)).thenReturn(Optional.empty());
        when(boardContentRepository.findById(boardSeq)).thenReturn(Optional.empty());

        // when
        BoardNotFoundException exception = assertThrows(BoardNotFoundException.class, () -> {
            boardService.updateBoard(boardRequest);
        });

        // then
        Assertions.assertEquals("해당 게시글을 찾을 수 없습니다.", exception.getMessage());

        // 리포지토리가 저장되지 않았는지 확인
        verify(boardRepository, never()).save(any(Board.class));
        verify(boardContentRepository, never()).save(any(BoardContent.class));
    }

    @Test
    @DisplayName("게시글 수정 테스트 - 부분 수정")
    public void testUpdateBoardPartialUpdate() {
        // given
        Long boardSeq = 1L;

        // 멤버 설정
        Member member = Member.builder()
                .id("yejin1224")
                .build();

        Board existingBoard = Board.builder()
                .boardSeq(boardSeq)
                .title("Old Title")
                .member(member)  // 가짜 멤버 생성
                .views(100)
                .updateDT(LocalDateTime.of(2024, 9, 29, 0, 0))
                .build();

        BoardContent existingBoardContent = BoardContent.builder()
                .boardSeq(boardSeq)
                .content("Old Content")
                .board(existingBoard)
                .build();

        BoardRequest boardRequest = BoardRequest.builder()
                .boardSeq(boardSeq)
                .title(null)
                .content("New Content")
                .build();

        when(boardRepository.findById(boardSeq)).thenReturn(Optional.of(existingBoard));
        when(boardContentRepository.findById(boardSeq)).thenReturn(Optional.of(existingBoardContent));

        // when
        BoardDetailResponse response = boardService.updateBoard(boardRequest);

        // then
        Assertions.assertEquals("Old Title", response.getTitle());  // 제목이 수정되지 않았는지 확인
        Assertions.assertEquals("New Content", response.getContent());  // 내용이 수정되었는지 확인

        // Verify the repository interactions
        verify(boardRepository, times(1)).save(any(Board.class));
        verify(boardContentRepository, times(1)).save(any(BoardContent.class));
    }
}

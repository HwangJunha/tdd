package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.exception.BoardNotFoundException;
import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.service.BoardService;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.request.BoardRequest;
import com.around.tdd.vo.response.BoardDetailResponse;
import com.around.tdd.vo.response.BoardListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(BoardController.class)
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/api/v1/board";

    // 게시판 등록 성공 테스트
    @Test
    public void saveBoardSuccess() throws Exception {
        BoardRequest boardRequest = createBoardDTO();
        Board board = createBoard();

        ObjectMapper objectMapper = new ObjectMapper();
        String boardDtoJson = objectMapper.writeValueAsString(boardRequest);
        MockMultipartFile boardDtoFile = createBoardDtoFile(boardDtoJson);

        MockMultipartFile mockImage = createMockImage();

        when(boardService.savePost(any(BoardRequest.class), any())).thenReturn(board);
        mockMvc.perform(MockMvcRequestBuilders.multipart(baseUrl + "/")
                .file(mockImage)
                .file(boardDtoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 저장 성공"));
    }

    private BoardRequest createBoardDTO() {
        return BoardRequest.builder()
                .title("Test Title")
                .memberId("yejin1224")
                .content("Test Content")
                .build();
    }

    private Board createBoard() {
        return Board.builder()
                .boardSeq(1L)
                .title("Test Title")
                .inputDt(LocalDateTime.now())
                .build();
    }

    private MockMultipartFile createMockImage() {
        return new MockMultipartFile(
                "files",                       // form 필드명
                "image1.jpg",                        // 파일 이름
                "image/jpeg",                        // 파일 타입
                "Test Image 1 Content".getBytes()    // 파일 내용
        );
    }

    private MockMultipartFile createBoardDtoFile(String boardDtoJson) {
        return new MockMultipartFile(
                "board",                          // form 필드명
                "boardDto.json",                     // 파일 이름
                "application/json",                  // JSON 타입
                boardDtoJson.getBytes()              // JSON 데이터를 byte[]로 변환
        );
    }

    @Test
    public void testHandleBoardSaveException() throws Exception {
        // given
        BoardRequest boardRequest = createBoardDTO();
        Board board = createBoard();

        ObjectMapper objectMapper = new ObjectMapper();

        String boardDtoJson = objectMapper.writeValueAsString(boardRequest);
        MockMultipartFile boardDtoFile = createBoardDtoFile(boardDtoJson);

        MockMultipartFile mockImage = createMockImage();

        doThrow(new BoardSaveException("잘못된 요청입니다.")).when(boardService).savePost(any(BoardRequest.class), any());

        //when
        mockMvc.perform(MockMvcRequestBuilders.multipart(baseUrl + "/")
                        .file(mockImage)
                        .file(boardDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
    }

    @Test
    public void testGetBoardSuccess() throws Exception {
        // Given
        Long boardSeq = 1L;
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("boardSeq", String.valueOf(boardSeq));

        BoardDetailResponse boardDetailResponse = BoardDetailResponse.builder()
                .boardSeq(boardSeq)
                .content("Test Content")
                .memberId("yejin1224")
                .title("Test Title")
                .views(100)
                .build();

        ApiResponse<BoardDetailResponse> apiResponse = new ApiResponse<>(
                Map.of("boardDetail", boardDetailResponse),
                "게시글 상세 조회 성공",
                HttpStatus.OK
        );

        when(boardService.getBoardById(boardSeq)).thenReturn(boardDetailResponse);

        // When & Then
        mockMvc.perform(get(baseUrl + "/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 상세 조회 성공"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.boardDetail.boardSeq").value(boardSeq))
                .andExpect(jsonPath("$.data.boardDetail.title").value("Test Title"))
                .andExpect(jsonPath("$.data.boardDetail.content").value("Test Content"))
                .andExpect(jsonPath("$.data.boardDetail.memberId").value("yejin1224"))
                .andExpect(jsonPath("$.data.boardDetail.views").value(100));
    }

    @Test
    public void testGetBoardDetail_BoardNotFound() throws Exception {
        // Given
        Long boardSeq = 1L;
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("boardSeq", String.valueOf(boardSeq));

        when(boardService.getBoardById(boardSeq)).thenThrow(new BoardNotFoundException("게시글을 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(get(baseUrl + "/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(request))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."));
    }

    @DisplayName("게시판 목록 조회 테스트")
    @Test
    void testGetBoardList() throws Exception {
        // Given
        List<BoardListResponse> boardListResponses = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            BoardListResponse boardListResponse = BoardListResponse.builder()
                    .boardSeq((long) i)
                    .title("제목" + i)
                    .memberId("member" + i)
                    .views(i * 10)
                    .inputDt(LocalDateTime.now())
                    .build();
            boardListResponses.add(boardListResponse);
        }

        Pageable pageable = PageRequest.of(0, 10);

        when(boardService.getBoardList(pageable)).thenReturn(boardListResponses);

        // When & Then
        mockMvc.perform(get(baseUrl + "/list?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시판 목록 조회 성공")) // 메시지 검증
                .andExpect(jsonPath("$.data.boardList[0].title").value("제목1")) // 첫 번째 게시글 제목 검증
                .andExpect(jsonPath("$.data.boardList[1].memberId").value("member2")) // 두 번째 게시글 멤버 ID 검증
                .andExpect(jsonPath("$.data.boardList[4].views").value(50)); // 다섯 번째 게시글 조회수 검증
    }

    @Test
    @DisplayName("게시판 수정 성공 테스트")
    public void testUpdateBoardSuccess() throws Exception {
        // Given
        Long boardSeq = 1L;
        BoardRequest boardRequest = BoardRequest.builder()
                .boardSeq(boardSeq)
                .title("Updated Title")
                .content("Updated Content")
                .build();

        BoardDetailResponse boardResponse = BoardDetailResponse.builder()
                .boardSeq(boardSeq)
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(boardService.updateBoard(any(BoardRequest.class))).thenReturn(boardResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updatedBoard.boardSeq").value(boardSeq))
                .andExpect(jsonPath("$.data.updatedBoard.title").value("Updated Title"))
                .andExpect(jsonPath("$.data.updatedBoard.content").value("Updated Content"))
                .andExpect(jsonPath("$.message").value("게시판 수정 성공"));
    }

    @Test
    @DisplayName("게시판 수정 실패 테스트 - 게시글을 찾을 수 없는 경우")
    public void testUpdateBoard_NotFound() throws Exception {
        // Given
        Long boardSeq = 1L;
        BoardRequest boardRequest = BoardRequest.builder()
                .boardSeq(boardSeq)
                .memberId("yejin1224")
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(boardService.updateBoard(any(BoardRequest.class))).thenThrow(new BoardNotFoundException("해당 게시글을 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("해당 게시글을 찾을 수 없습니다."));
    }
}

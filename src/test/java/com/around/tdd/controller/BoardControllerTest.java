package com.around.tdd.controller;

import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.service.BoardService;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.BoardDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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

    private final String baseUrl = "/api/v1";

    // 게시판 등록 성공 테스트
    @Test
    public void saveBoardSuccess() throws Exception {
        BoardDTO boardDTO = createBoardDTO();
        Board board = createBoard();

        ObjectMapper objectMapper = new ObjectMapper();
        String boardDtoJson = objectMapper.writeValueAsString(boardDTO);
        MockMultipartFile boardDtoFile = createBoardDtoFile(boardDtoJson);

        MockMultipartFile mockImage = createMockImage();

        when(boardService.savePost(any(BoardDTO.class), any())).thenReturn(board);
        mockMvc.perform(MockMvcRequestBuilders.multipart(baseUrl + "/board")
                .file(mockImage)
                .file(boardDtoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 저장 성공"));
    }

    private BoardDTO createBoardDTO() {
        return BoardDTO.builder()
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
        BoardDTO boardDTO = createBoardDTO();
        Board board = createBoard();

        ObjectMapper objectMapper = new ObjectMapper();

        String boardDtoJson = objectMapper.writeValueAsString(boardDTO);
        MockMultipartFile boardDtoFile = createBoardDtoFile(boardDtoJson);

        MockMultipartFile mockImage = createMockImage();

        doThrow(new BoardSaveException("잘못된 요청입니다.")).when(boardService).savePost(any(BoardDTO.class), any());

        //when
        mockMvc.perform(MockMvcRequestBuilders.multipart(baseUrl + "/board")
                        .file(mockImage)
                        .file(boardDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
    }
}

package com.around.tdd.controller;

import com.around.tdd.service.BoardService;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.BoardDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
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

        MockMultipartFile mockImage1 = new MockMultipartFile(
                "files",                       // form 필드명
                "image1.jpg",                        // 파일 이름
                "image/jpeg",                        // 파일 타입
                "Test Image 1 Content".getBytes()    // 파일 내용
        );
        MockMultipartFile mockImage2 = new MockMultipartFile(
                "files",                       // form 필드명
                "image2.jpg",                        // 파일 이름
                "image/jpeg",                        // 파일 타입
                "Test Image 2 Content".getBytes()    // 파일 내용
        );

        MockMultipartFile boardDtoFile = new MockMultipartFile(
                "board",                          // form 필드명
                "boardDto.json",                     // 파일 이름
                "application/json",                  // JSON 타입
                boardDtoJson.getBytes()              // JSON 데이터를 byte[]로 변환
        );


        when(boardService.savePost(any(BoardDTO.class), any())).thenReturn(board);
        mockMvc.perform(MockMvcRequestBuilders.multipart(baseUrl + "/board")
                .file(mockImage2)
                .file(boardDtoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
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
}

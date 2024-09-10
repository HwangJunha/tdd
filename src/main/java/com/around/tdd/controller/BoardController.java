package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.controller.response.ErrorResponse;
import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.service.BoardService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.BoardDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BoardController {
        private final BoardService boardService;
        @PostMapping("/board")
        public ResponseEntity<ApiResponse<String>> saveBoard(
                @RequestPart("board") @Valid BoardDTO boardDTO,
                @RequestPart("files") List<MultipartFile> boardImages) {

                Map<String, String> responseData = new HashMap<>();

                // TODO 관리자 권한 확인 필요

                HttpHeaders headers = HttpUtil.createJsonHeaders();

                Board board = boardService.savePost(boardDTO, boardImages);

                responseData.put("savedBoardSeq", String.valueOf(board.getBoardSeq()));
                responseData.put("title", board.getTitle());
                responseData.put("inputDate", String.valueOf(board.getInputDt()));

                ApiResponse<String> response = new ApiResponse<>(responseData, "게시글 저장 성공", HttpStatus.OK);

                return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        @ExceptionHandler(BoardSaveException.class)
        public ResponseEntity<ErrorResponse>  handleBoardSaveException(BoardSaveException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "잘못된 요청입니다.",
                        LocalDateTime.now(),
                        "/api/v1/board",
                        Map.of("error", ex.getMessage())
                );

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
}
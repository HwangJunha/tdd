package com.around.tdd.controller;

import com.around.tdd.controller.response.ApiResponse;
import com.around.tdd.controller.response.ErrorResponse;
import com.around.tdd.exception.BoardNotFoundException;
import com.around.tdd.exception.BoardSaveException;
import com.around.tdd.service.BoardService;
import com.around.tdd.util.HttpUtil;
import com.around.tdd.vo.Board;
import com.around.tdd.vo.request.BoardRequest;
import com.around.tdd.vo.response.BoardResponse;
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
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class BoardController {
        private final BoardService boardService;
        HttpHeaders headers = HttpUtil.createJsonHeaders();

        @PostMapping("/")
        public ResponseEntity<ApiResponse<String>> saveBoard(
                @RequestPart("board") @Valid BoardRequest boardRequest,
                @RequestPart("files") List<MultipartFile> boardImages) {

                Map<String, String> responseData = new HashMap<>();

                // TODO 관리자 권한 확인 필요

                Board board = boardService.savePost(boardRequest, boardImages);

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

        @GetMapping("/detail")
        public ResponseEntity<ApiResponse<BoardResponse>> getBoardDetail(@RequestParam(value="boardSeq") Long boardSeq) {
                Map<String, BoardResponse> responseData = new HashMap<>();
                BoardResponse boardResponse = boardService.getBoardById(boardSeq);

                responseData.put("boardDetail", boardResponse);

                ApiResponse<BoardResponse> response = new ApiResponse<>(responseData, "게시글 상세 조회 성공", HttpStatus.OK);
                return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        @ExceptionHandler(BoardNotFoundException.class)
        public ResponseEntity<ErrorResponse>  getBoardDetailException (BoardNotFoundException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.NO_CONTENT.value(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        "/api/v1/board/detail",
                        Map.of("error", ex.getMessage())
                );

                return new ResponseEntity<>(errorResponse, HttpStatus.NO_CONTENT);
        }

}
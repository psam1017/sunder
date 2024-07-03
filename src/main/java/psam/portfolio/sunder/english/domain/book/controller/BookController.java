package psam.portfolio.sunder.english.domain.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
import psam.portfolio.sunder.english.domain.book.model.request.BookPageSearchCond;
import psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList;
import psam.portfolio.sunder.english.domain.book.model.response.BookAndWordFullResponse;
import psam.portfolio.sunder.english.domain.book.service.BookCommandService;
import psam.portfolio.sunder.english.domain.book.service.BookQueryService;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/books")
@RestController
public class BookController {

    private final BookCommandService bookCommandService;
    private final BookQueryService bookQueryService;
    private final ObjectMapper objectMapper;

    /**
     * 새 교재 등록 서비스
     *
     * @param teacherId 사용자 아이디
     * @param replace   등록할 교재 정보
     * @return 등록에 성공한 교재 아이디
     */
    @PostMapping("")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> registerBook(@UserId UUID teacherId,
                                                       @RequestBody BookReplace replace) {
        UUID newBookId = bookCommandService.replaceBook(teacherId, null, replace);
        return ApiResponse.ok(Map.of("bookId", newBookId));
    }

    /**
     * 교재 정보 수정 서비스
     *
     * @param teacherId 사용자 아이디
     * @param bookId    수정할 교재 아이디
     * @param replace   수정할 교재 정보
     * @return 수정에 성공한 교재 아이디
     */
    @PatchMapping("/{bookId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> updateBook(@UserId UUID teacherId,
                                                     @PathVariable UUID bookId,
                                                     @RequestBody BookReplace replace) {
        UUID updateBookId = bookCommandService.replaceBook(teacherId, bookId, replace);
        return ApiResponse.ok(Map.of("bookId", updateBookId));
    }

    /**
     * 교재에 단어 추가 서비스. JSON 형식으로 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
     *
     * @param teacherId 사용자 아이디
     * @param bookId    교재 아이디
     * @param postList  생성/교체할 단어 목록
     * @return 생성/교체된 단어들이 속한 교재 아이디
     */
    @PostMapping(
            value = "/{bookId}/words/json",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> replaceWordsByJson(@UserId UUID teacherId,
                                                             @PathVariable UUID bookId,
                                                             @RequestBody @Valid WordPOSTList postList) {
        UUID updateBookId = bookCommandService.replaceWords(teacherId, bookId, postList);
        return ApiResponse.ok(Map.of("bookId", updateBookId));
    }

    /**
     * 교재에 단어 추가 서비스. 엑셀 파일을 업로드하여 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
     *
     * @param teacherId 사용자 아이디
     * @param bookId    교재 아이디
     * @param file      생성/교체할 단어가 입력된 엑셀 파일
     * @return 생성/교체된 단어들이 속한 교재 아이디
     */
    @PostMapping(
            value = "/{bookId}/words/excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> replaceWordsByExcel(@UserId UUID teacherId,
                                                              @PathVariable UUID bookId,
                                                              @RequestParam MultipartFile file) throws IOException {
        UUID updateBookId = bookCommandService.replaceWords(teacherId, bookId, file);
        return ApiResponse.ok(Map.of("bookId", updateBookId));
    }

    /**
     * 교재 목록 조회 서비스
     *
     * @param userId 사용자 아이디
     * @param cond   교재 목록 조회 조건
     * @return 교재 목록과 페이지 정보
     */
    @GetMapping("")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Map<String, Object>> getBookList(@UserId UUID userId,
                                                   @ModelAttribute BookPageSearchCond cond) {
        Map<String, Object> response = bookQueryService.getBookList(userId, cond);
        return ApiResponse.ok(response);
    }

    /**
     * 교재 상세 정보와 단어 목록 조회 서비스
     *
     * @param userId 사용자 아이디
     * @param bookId 조회할 교재 아이디
     * @return 교재 상세 정보와 단어 목록
     */
    @GetMapping("/{bookId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<?> getBookDetail(@UserId UUID userId,
                                        @PathVariable UUID bookId) {
        BookAndWordFullResponse response = bookQueryService.getBookDetail(userId, bookId);
        return ApiResponse.ok(response);
    }

    /**
     * 교재 삭제 서비스. 교재 삭제 시 단어도 함께 삭제된다.
     *
     * @param teacherId 선생님 아이디
     * @param bookId    삭제할 교재 아이디
     * @return 삭제된 교재 아이디
     */
    @DeleteMapping("/{bookId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> deleteBook(@UserId UUID teacherId,
                                                     @PathVariable UUID bookId) {
        UUID deletedBookId = bookCommandService.deleteBook(teacherId, bookId);
        return ApiResponse.ok(Map.of("bookId", deletedBookId));
    }
}

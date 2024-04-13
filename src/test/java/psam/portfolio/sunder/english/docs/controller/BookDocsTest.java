package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
import psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList;
import psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList.WordPOST;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

public class BookDocsTest extends RestDocsEnvironment {

    @DisplayName("선생님이 학원 교재를 등록할 수 있다.")
    @Test
    void saveBook() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        BookReplace replace = BookReplace.builder()
                .openToPublic(false)
                .publisher("publisher")
                .bookName("bookName")
                .chapter("chapter")
                .subject("subject")
                .build();

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/book")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createToken(teacher))
                        .content(createJson(replace))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("openToPublic").type(BOOLEAN).description("교재 공개 여부"),
                                fieldWithPath("publisher").type(STRING).description("출판사"),
                                fieldWithPath("bookName").type(STRING).description("교재명"),
                                fieldWithPath("chapter").type(STRING).description("챕터"),
                                fieldWithPath("subject").type(STRING).description("주제")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.bookId").type(STRING).description("등록된 교재 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 학원 교재 정보를 수정할 수 있다.")
    @Test
    void updateBook() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);

        BookReplace replace = BookReplace.builder()
                .openToPublic(true)
                .publisher("newPublisher")
                .bookName("newBookName")
                .chapter("newChapter")
                .subject("newSubject")
                .build();

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/book/{bookId}", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createToken(teacher))
                        .content(createJson(replace))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("bookId").description("수정할 교재 아이디")
                        ),
                        requestFields(
                                fieldWithPath("openToPublic").type(BOOLEAN).description("교재 공개 여부"),
                                fieldWithPath("publisher").type(STRING).description("출판사"),
                                fieldWithPath("bookName").type(STRING).description("교재명"),
                                fieldWithPath("chapter").type(STRING).description("챕터"),
                                fieldWithPath("subject").type(STRING).description("주제")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.bookId").type(STRING).description("수정된 교재 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 json 으로 교재에 단어 목록을 등록할 수 있다.")
    @Test
    void replaceWordsByJson() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);

        WordPOSTList postList = WordPOSTList.builder()
                .words(List.of(
                        new WordPOST("apple", "사과"),
                        new WordPOST("banana", "바나나"),
                        new WordPOST("cherry", "체리")
                ))
                .build();

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/book/{bookId}/words/json", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createToken(teacher))
                        .content(createJson(postList))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("bookId").description("단어를 추가할 교재 아이디")
                        ),
                        requestFields(
                                fieldWithPath("words[].english").type(STRING).description("영어 단어"),
                                fieldWithPath("words[].korean").type(STRING).description("한글 뜻")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.bookId").type(STRING).description("단어가 추가된 교재 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 엑셀 파일로 교재에 단어 목록을 등록할 수 있다.")
    @Test
    void replaceWordsByExcel() throws Exception {
        // given
        given(excelUtils.readExcel(any(), any(), any()))
                .willReturn(List.of(
                        List.of("apple", "사과"),
                        List.of("banana", "바나나"),
                        List.of("cherry", "체리")
                ));

        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "words.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "xxx.yyy.zzz".getBytes()
        );

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/api/book/{bookId}/words/excel", book.getId())
                        .file(file)
                        .header(AUTHORIZATION, createToken(teacher))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("bookId").description("단어를 추가할 교재 아이디")
                        ),
                        requestParts(
                                partWithName("file").description("엑셀 파일")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.bookId").type(STRING).description("단어가 추가된 교재 아이디")
                        )
                ));
    }

    @DisplayName("교재 목록을 조회할 수 있다.")
    @Test
    void getBookList() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        dataCreator.registerBook(false, "능률(김성곤)", "중3", "1과", "본문", academy);
        dataCreator.registerBook(false, "미래(최연희)", "중3", "1과", "본문", academy);
        dataCreator.registerBook(false, "능률(김성곤)", "중2", "1과", "본문", academy);
        dataCreator.registerBook(false, "능률(김성곤)", "중3", "2과", "본문", academy);
        dataCreator.registerBook(false, "능률(김성곤)", "중3", "1과", "예문", academy);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/book/list")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createToken(teacher))
                        .param("page", "1")
                        .param("size", "10")
                        .param("keyword", "능률 김 중3 1과 본문")
                        .param("privateOnly", "true")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("page").description("조회할 페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("keyword").description("검색 키워드"),
                                parameterWithName("privateOnly").description("개인 교재만 조회 여부"),
                                parameterWithName("year").description("교재 등록 연도").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.books[].id").type(STRING).description("교재 아이디"),
                                fieldWithPath("data.books[].publisher").type(STRING).description("출판사"),
                                fieldWithPath("data.books[].bookName").type(STRING).description("교재명"),
                                fieldWithPath("data.books[].chapter").type(STRING).description("챕터"),
                                fieldWithPath("data.books[].subject").type(STRING).description("주제"),
                                fieldWithPath("data.books[].academyId").type(STRING).description("학원 아이디"),
                                fieldWithPath("data.books[].openToPublic").type(BOOLEAN).description("공개 여부"),
                                fieldWithPath("data.books[].createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.books[].modifiedDateTime").type(STRING).description("수정 일시"),
                                fieldWithPath("data.books[].createdBy").type(STRING).description("생성자 아이디").optional(),
                                fieldWithPath("data.books[].modifiedBy").type(STRING).description("수정자 아이디").optional(),
                                fieldWithPath("data.pageInfo.page").type(NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.pageInfo.size").type(NUMBER).description("페이지 크기"),
                                fieldWithPath("data.pageInfo.total").type(NUMBER).description("전체 학원 수"),
                                fieldWithPath("data.pageInfo.lastPage").type(NUMBER).description("마지막 페이지 번호"),
                                fieldWithPath("data.pageInfo.start").type(NUMBER).description("페이지 세트의 시작 번호"),
                                fieldWithPath("data.pageInfo.end").type(NUMBER).description("페이지 세트의 끝 번호"),
                                fieldWithPath("data.pageInfo.hasPrev").type(BOOLEAN).description("이전 페이지 존재 여부"),
                                fieldWithPath("data.pageInfo.hasNext").type(BOOLEAN).description("다음 페이지 존재 여부")
                        )
                ));
    }

    @DisplayName("교재 상세 정보와 단어 목록을 조회할 수 있다.")
    @Test
    void getBookDetail() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);
        dataCreator.registerWord("apple", "사과", book);
        dataCreator.registerWord("banana", "바나나", book);
        dataCreator.registerWord("cherry", "체리", book);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/book/{bookId}", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createToken(teacher))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("bookId").description("조회할 교재 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.book.id").type(STRING).description("교재 아이디"),
                                fieldWithPath("data.book.publisher").type(STRING).description("출판사"),
                                fieldWithPath("data.book.bookName").type(STRING).description("교재명"),
                                fieldWithPath("data.book.chapter").type(STRING).description("챕터"),
                                fieldWithPath("data.book.subject").type(STRING).description("주제"),
                                fieldWithPath("data.book.academyId").type(STRING).description("학원 아이디"),
                                fieldWithPath("data.book.openToPublic").type(BOOLEAN).description("공개 여부"),
                                fieldWithPath("data.book.createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.book.modifiedDateTime").type(STRING).description("수정 일시"),
                                fieldWithPath("data.book.createdBy").type(STRING).description("생성자 아이디").optional(),
                                fieldWithPath("data.book.modifiedBy").type(STRING).description("수정자 아이디").optional(),
                                fieldWithPath("data.words[].id").type(NUMBER).description("단어 아이디"),
                                fieldWithPath("data.words[].english").type(STRING).description("영어 단어"),
                                fieldWithPath("data.words[].korean").type(STRING).description("한글 뜻")
                        )
                ));
    }

    @DisplayName("선생님이 교재를 삭제할 수 있다.")
    @Test
    void deleteBook() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/book/{bookId}", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createToken(teacher))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("bookId").description("삭제할 교재 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.bookId").type(STRING).description("삭제된 교재 아이디")
                        )
                ));
    }
}

//package psam.portfolio.sunder.english.domain.book.controller;
//
//        import jakarta.validation.Valid;
//        import lombok.RequiredArgsConstructor;
//        import org.springframework.http.MediaType;
//        import org.springframework.security.access.annotation.Secured;
//        import org.springframework.web.bind.annotation.*;
//        import org.springframework.web.multipart.MultipartFile;
//        import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
//        import psam.portfolio.sunder.english.domain.book.model.request.BookSearchCond;
//        import psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList;
//        import psam.portfolio.sunder.english.domain.book.service.BookCommandService;
//        import psam.portfolio.sunder.english.domain.book.service.BookQueryService;
//        import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
//        import psam.portfolio.sunder.english.global.resolver.argument.UserId;
//
//        import java.io.IOException;
//        import java.util.Map;
//        import java.util.UUID;
//
//@RequiredArgsConstructor
//@RequestMapping("/api/book")
//@RestController
//public class BookController {
//
//    private final BookCommandService bookCommandService;
//    private final BookQueryService bookQueryService;
//
//    /**
//     * 새 교재 등록 서비스
//     *
//     * @param teacherId 사용자 아이디
//     * @param replace   등록할 교재 정보
//     * @return 등록에 성공한 교재 아이디
//     */
//    @PostMapping("")
//    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
//    public ApiResponse<Map<String, UUID>> saveBook(@UserId UUID teacherId,
//                                                   @RequestBody BookReplace replace) {
//        UUID newBookId = bookCommandService.replaceBook(teacherId, null, replace);
//        return ApiResponse.ok(Map.of("bookId", newBookId));
//    }
//
//    /**
//     * 교재 정보 수정 서비스
//     *
//     * @param teacherId 사용자 아이디
//     * @param bookId    수정할 교재 아이디
//     * @param replace   수정할 교재 정보
//     * @return 수정에 성공한 교재 아이디
//     */
//    @PatchMapping("/{bookId}")
//    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
//    public ApiResponse<Map<String, UUID>> updateBook(@UserId UUID teacherId,
//                                                     @PathVariable UUID bookId,
//                                                     @RequestBody BookReplace replace) {
//        UUID updateBookId = bookCommandService.replaceBook(teacherId, bookId, replace);
//        return ApiResponse.ok(Map.of("bookId", updateBookId));
//    }
//
//    /**
//     * 교재에 단어 추가 서비스. JSON 형식으로 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
//     *
//     * @param teacherId 사용자 아이디
//     * @param bookId    교재 아이디
//     * @param postList  생성/교체할 단어 목록
//     * @return 생성/교체된 단어들이 속한 교재 아이디
//     */
//    @PostMapping(
//            value = "/{bookId}/words/json",
//            consumes = MediaType.APPLICATION_JSON_VALUE
//    )
//    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
//    public ApiResponse<Map<String, UUID>> replaceWordsByJson(@UserId UUID teacherId,
//                                                             @PathVariable UUID bookId,
//                                                             @RequestBody @Valid WordPOSTList postList) {
//        UUID updateBookId = bookCommandService.replaceWords(teacherId, bookId, postList);
//        return ApiResponse.ok(Map.of("bookId", updateBookId));
//    }
//
//    /**
//     * 교재에 단어 추가 서비스. 엑셀 파일을 업로드하여 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
//     *
//     * @param teacherId 사용자 아이디
//     * @param bookId    교재 아이디
//     * @param file      생성/교체할 단어가 입력된 엑셀 파일
//     * @return 생성/교체된 단어들이 속한 교재 아이디
//     */
//    @PostMapping(
//            value = "/{bookId}/words/excel",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
//    public ApiResponse<Map<String, UUID>> replaceWordsByExcel(@UserId UUID teacherId,
//                                                              @PathVariable UUID bookId,
//                                                              @RequestParam MultipartFile file) throws IOException {
//        UUID updateBookId = bookCommandService.replaceWords(teacherId, bookId, file);
//        return ApiResponse.ok(Map.of("bookId", updateBookId));
//    }
//
//    /**
//     * 교재 목록 조회 서비스
//     *
//     * @param userId 사용자 아이디
//     * @param cond   교재 목록 조회 조건
//     * @return 교재 목록과 페이지 정보
//     */
//    @GetMapping("/list")
//    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
//    public ApiResponse<Map<String, Object>> getBookList(@UserId UUID userId,
//                                                        @ModelAttribute BookSearchCond cond
//    ) {
//        Map<String, Object> response = bookQueryService.getBookList(userId, cond);
//        return ApiResponse.ok(response);
//    }
//
//    /**
//     * 교재 상세 정보와 단어 목록 조회 서비스
//     *
//     * @param userId 사용자 아이디
//     * @param bookId 조회할 교재 아이디
//     * @return 교재 상세 정보와 단어 목록
//     */
//    @GetMapping("/{bookId}")
//    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
//    public ApiResponse<Map<String, Object>> getBookDetail(@UserId UUID userId,
//                                                          @PathVariable UUID bookId) {
//        Map<String, Object> response = bookQueryService.getBookDetail(userId, bookId);
//        return ApiResponse.ok(response);
//    }
//
//    /**
//     * 교재 삭제 서비스. 교재 삭제 시 단어도 함께 삭제된다.
//     *
//     * @param teacherId 선생님 아이디
//     * @param bookId    삭제할 교재 아이디
//     * @return 삭제된 교재 아이디
//     */
//    @DeleteMapping("/{bookId}")
//    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
//    public ApiResponse<Map<String, UUID>> deleteBook(@UserId UUID teacherId,
//                                                     @PathVariable UUID bookId) {
//        UUID deletedBookId = bookCommandService.deleteBook(teacherId, bookId);
//        return ApiResponse.ok(Map.of("bookId", deletedBookId));
//    }
//}


//
//
//    @Autowired
//    AcademyCommandService academyCommandService;
//
//    @Autowired
//    TeacherQueryRepository teacherQueryRepository;
//
//    @DisplayName("academy 의 name 중복체크를 할 수 있다.")
//    @Test
//    void checkNameDupl() throws Exception {
//        // given
//        String name = infoContainer.getUniqueAcademyName();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                get("/api/academy/check-dupl")
//                        .contentType(APPLICATION_JSON)
//                        .param("name", name)
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                                queryParameters(
//                                        parameterWithName("name").description("중복체크할 학원 이름")
//                                ),
//                                relaxedResponseFields(
//                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
//                                )
//                        )
//                );
//    }
//
//    @DisplayName("academy 의 phone 중복체크를 할 수 있다.")
//    @Test
//    void checkPhoneDupl() throws Exception {
//        // given
//        String phone = infoContainer.getUniquePhoneNumber();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                get("/api/academy/check-dupl")
//                        .contentType(APPLICATION_JSON)
//                        .param("phone", phone)
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                                queryParameters(
//                                        parameterWithName("phone").description("중복체크할 전화번호")
//                                ),
//                                relaxedResponseFields(
//                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
//                                )
//                        )
//                );
//    }
//
//    @DisplayName("academy 의 email 중복체크를 할 수 있다.")
//    @Test
//    void checkEmailDupl() throws Exception {
//        // given
//        String email = infoContainer.getUniqueEmail();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                get("/api/academy/check-dupl")
//                        .contentType(APPLICATION_JSON)
//                        .param("email", email)
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                                queryParameters(
//                                        parameterWithName("email").description("중복체크할 이메일")
//                                ),
//                                relaxedResponseFields(
//                                        fieldWithPath("data.isOk").type(BOOLEAN).description("중복 검사 결과")
//                                )
//                        )
//                );
//    }
//
//    @DisplayName("academy 와 director 를 등록할 수 있다.")
//    @Test
//    void registerAcademy() throws Exception {
//        // mocking
//        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
//                .willReturn(true);
//
//        // given
//        Address anyAddress = infoContainer.getAnyAddress();
//
//        AcademyDirectorPOST.AcademyPOST buildAcademyPOST = AcademyDirectorPOST.AcademyPOST.builder()
//                .name(infoContainer.getUniqueAcademyName())
//                .phone(infoContainer.getUniquePhoneNumber())
//                .email(infoContainer.getUniqueEmail())
//                .street(anyAddress.getStreet())
//                .addressDetail(anyAddress.getDetail())
//                .postalCode(anyAddress.getPostalCode())
//                .openToPublic(true)
//                .build();
//
//        AcademyDirectorPOST.DirectorPOST buildDirectorPOST = AcademyDirectorPOST.DirectorPOST.builder()
//                .loginId(infoContainer.getUniqueLoginId())
//                .loginPw("P@ssw0rd")
//                .name("홍길동")
//                .email(infoContainer.getUniqueEmail())
//                .phone(infoContainer.getUniquePhoneNumber())
//                .street(anyAddress.getStreet())
//                .addressDetail(anyAddress.getDetail())
//                .postalCode(anyAddress.getPostalCode())
//                .build();
//
//        AcademyDirectorPOST post = new AcademyDirectorPOST(buildAcademyPOST, buildDirectorPOST);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                post("/api/academy")
//                        .contentType(APPLICATION_JSON)
//                        .content(createJson(post))
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                        requestFields(
//                                fieldWithPath("academy.name").type(STRING).description("학원 이름"),
//                                fieldWithPath("academy.phone").type(STRING).description("학원 전화번호").optional(),
//                                fieldWithPath("academy.email").type(STRING).description("학원 이메일").optional(),
//                                fieldWithPath("academy.street").type(STRING).description("학원 주소").optional(),
//                                fieldWithPath("academy.addressDetail").type(STRING).description("학원 상세주소").optional(),
//                                fieldWithPath("academy.postalCode").type(STRING).description("학원 우편번호").optional(),
//                                fieldWithPath("academy.openToPublic").description("학원 공개 여부").type(BOOLEAN),
//                                fieldWithPath("director.loginId").type(STRING).description("학원장 로그인 아이디"),
//                                fieldWithPath("director.loginPw").type(STRING).description("학원장 로그인 비밀번호"),
//                                fieldWithPath("director.name").type(STRING).description("학원장 이름"),
//                                fieldWithPath("director.email").type(STRING).description("학원장 이메일"),
//                                fieldWithPath("director.phone").type(STRING).description("학원장 전화번호").optional(),
//                                fieldWithPath("director.street").type(STRING).description("학원장 주소").optional(),
//                                fieldWithPath("director.addressDetail").type(STRING).description("학원장 상세주소").optional(),
//                                fieldWithPath("director.postalCode").type(STRING).description("학원장 우편번호").optional()
//                        ),
//                        relaxedResponseFields(
//                                fieldWithPath("data.directorId").type(STRING).description("등록된 학원장의 아이디")
//                        )
//                ));
//    }
//
//    @DisplayName("academy 의 uuid 를 검증하고 승인할 수 있다.")
//    @Test
//    void verifyAcademy() throws Exception {
//        // mocking
//        given(mailUtils.sendMail(anyString(), anyString(), anyString()))
//                .willReturn(true);
//
//        // given
//        Address anyAddress = infoContainer.getAnyAddress();
//
//        AcademyDirectorPOST.AcademyPOST buildAcademyPOST = AcademyDirectorPOST.AcademyPOST.builder()
//                .name(infoContainer.getUniqueAcademyName())
//                .phone(infoContainer.getUniquePhoneNumber())
//                .email(infoContainer.getUniqueEmail())
//                .street(anyAddress.getStreet())
//                .addressDetail(anyAddress.getDetail())
//                .postalCode(anyAddress.getPostalCode())
//                .openToPublic(true)
//                .build();
//
//        AcademyDirectorPOST.DirectorPOST buildDirectorPOST = AcademyDirectorPOST.DirectorPOST.builder()
//                .loginId(infoContainer.getUniqueLoginId())
//                .loginPw("P@ssw0rd")
//                .name("홍길동")
//                .email(infoContainer.getUniqueEmail())
//                .phone(infoContainer.getUniquePhoneNumber())
//                .street(anyAddress.getStreet())
//                .addressDetail(anyAddress.getDetail())
//                .postalCode(anyAddress.getPostalCode())
//                .build();
//
//        UUID directorId = refreshAnd(() -> academyCommandService.registerDirectorWithAcademy(buildAcademyPOST, buildDirectorPOST));
//        Teacher getDirector = teacherQueryRepository.getById(directorId);
//        UUID academyId = getDirector.getAcademy().getId();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                RestDocumentationRequestBuilders.post("/api/academy/{academyId}/verify", academyId.toString())
//                        .contentType(APPLICATION_JSON)
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                        pathParameters(
//                                parameterWithName("academyId").description("학원 아이디")
//                        ),
//                        relaxedResponseFields(
//                                fieldWithPath("data.verified").type(BOOLEAN).description("학원 승인 여부")
//                        )
//                ));
//    }
//
//    @DisplayName("선생이 자기 학원의 상세 정보를 조회할 수 있다.")
//    @Test
//    void getDetailByTeacher() throws Exception {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, ROLE_TEACHER);
//        String token = createToken(teacher);
//
//        refresh();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                get("/api/academy")
//                        .contentType(APPLICATION_JSON)
//                        .header(AUTHORIZATION, token)
//                        .param("select", "teacher")
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                                queryParameters(
//                                        parameterWithName("select").description("""
//                                                같이 조회할 정보 옵션 +
//                                                - teacher : 학원에 소속된 선생 목록을 같이 조회
//                                                """).optional()
//                                ),
//                                relaxedResponseFields(
//                                        fieldWithPath("data.academy.id").type(STRING).description("학원 아이디"),
//                                        fieldWithPath("data.academy.name").type(STRING).description("학원 이름"),
//                                        fieldWithPath("data.academy.street").type(STRING).description("학원 주소 (도로명)"),
//                                        fieldWithPath("data.academy.addressDetail").type(STRING).description("학원 주소 (상세주소)"),
//                                        fieldWithPath("data.academy.postalCode").type(STRING).description("학원 주소 (우편번호)"),
//                                        fieldWithPath("data.academy.phone").type(STRING).description("학원 전화번호"),
//                                        fieldWithPath("data.academy.email").type(STRING).description("학원 이메일"),
//                                        fieldWithPath("data.academy.openToPublic").type(BOOLEAN).description("학원 공개 여부"),
//                                        fieldWithPath("data.academy.status").type(STRING).description("학원 상태"),
//                                        fieldWithPath("data.academy.createdDateTime").type(STRING).description("학원 생성일시"),
//                                        fieldWithPath("data.academy.modifiedDateTime").type(STRING).description("학원 수정일시"),
//                                        fieldWithPath("data.teachers[].id").type(STRING).description("선생 아이디"),
//                                        fieldWithPath("data.teachers[].loginId").type(STRING).description("선생 로그인 아이디"),
//                                        fieldWithPath("data.teachers[].name").type(STRING).description("선생 이름"),
//                                        fieldWithPath("data.teachers[].email").type(STRING).description("선생 이메일"),
//                                        fieldWithPath("data.teachers[].emailVerified").type(BOOLEAN).description("선생 이메일 인증 여부"),
//                                        fieldWithPath("data.teachers[].phone").type(STRING).description("선생 전화번호"),
//                                        fieldWithPath("data.teachers[].street").type(STRING).description("선생 주소 (도로명)"),
//                                        fieldWithPath("data.teachers[].addressDetail").type(STRING).description("선생 주소 (상세주소)"),
//                                        fieldWithPath("data.teachers[].postalCode").type(STRING).description("선생 주소 (우편번호)"),
//                                        fieldWithPath("data.teachers[].status").type(STRING).description("선생 상태"),
//                                        fieldWithPath("data.teachers[].roles[]").type(ARRAY).description("선생 권한"),
//                                        fieldWithPath("data.teachers[].lastPasswordChangeDateTime").type(STRING).description("선생 마지막 비밀번호 변경일시"),
//                                        fieldWithPath("data.teachers[].academyId").type(STRING).description("선생이 속한 학원 아이디"),
//                                        fieldWithPath("data.teachers[].createdDateTime").type(STRING).description("선생 생성일시"),
//                                        fieldWithPath("data.teachers[].modifiedDateTime").type(STRING).description("선생 수정일시"),
//                                        fieldWithPath("data.teachers[].createdBy").type(STRING).description("생성자 아이디").optional(),
//                                        fieldWithPath("data.teachers[].modifiedBy").type(STRING).description("수정자 아이디").optional()
//                                )
//                        )
//                );
//    }
//
//    @DisplayName("학생이 자기 학원의 상세 정보를 조회할 수 있다.")
//    @Test
//    void getDetailByStudent() throws Exception {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//
//        List<Teacher> saveTeachers = new ArrayList<>();
//        saveTeachers.add(dataCreator.registerTeacher("Alice", UserStatus.ACTIVE, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Bob", UserStatus.ACTIVE, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Charlie", UserStatus.TRIAL, academy));
//        saveTeachers.add(dataCreator.registerTeacher("David", UserStatus.TRIAL, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Eve", UserStatus.PENDING, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Frank", UserStatus.PENDING, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Grace", UserStatus.WITHDRAWN, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Hank", UserStatus.WITHDRAWN, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Ivy", UserStatus.FORBIDDEN, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Jack", UserStatus.FORBIDDEN, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Kate", UserStatus.TRIAL_END, academy));
//        saveTeachers.add(dataCreator.registerTeacher("Liam", UserStatus.TRIAL_END, academy));
//        for (Teacher t : saveTeachers) {
//            dataCreator.createUserRoles(t, ROLE_TEACHER);
//        }
//        Teacher director = dataCreator.registerTeacher("Director", UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
//
//        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(student, ROLE_STUDENT);
//        String token = createToken(student);
//
//        refresh();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                get("/api/academy")
//                        .contentType(APPLICATION_JSON)
//                        .header(AUTHORIZATION, token)
//                        .param("select", "teacher")
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                                queryParameters(
//                                        parameterWithName("select").description("""
//                                                같이 조회할 정보 옵션 +
//                                                - teacher : 학원에 소속된 선생 목록을 같이 조회
//                                                """).optional()
//                                ),
//                                relaxedResponseFields(
//                                        fieldWithPath("data.academy.id").type(STRING).description("학원 아이디"),
//                                        fieldWithPath("data.academy.name").type(STRING).description("학원 이름"),
//                                        fieldWithPath("data.academy.street").type(STRING).description("학원 주소 (도로명)"),
//                                        fieldWithPath("data.academy.addressDetail").type(STRING).description("학원 주소 (상세주소)"),
//                                        fieldWithPath("data.academy.postalCode").type(STRING).description("학원 주소 (우편번호)"),
//                                        fieldWithPath("data.academy.phone").type(STRING).description("학원 전화번호"),
//                                        fieldWithPath("data.academy.email").type(STRING).description("학원 이메일"),
//                                        fieldWithPath("data.academy.openToPublic").type(BOOLEAN).description("학원 공개 여부"),
//                                        fieldWithPath("data.academy.status").type(STRING).description("학원 상태"),
//                                        fieldWithPath("data.academy.createdDateTime").type(STRING).description("학원 생성일시"),
//                                        fieldWithPath("data.academy.modifiedDateTime").type(STRING).description("학원 수정일시"),
//                                        fieldWithPath("data.teachers[].id").type(STRING).description("선생 아이디"),
//                                        fieldWithPath("data.teachers[].name").type(STRING).description("선생 이름"),
//                                        fieldWithPath("data.teachers[].status").type(STRING).description("선생 상태"),
//                                        fieldWithPath("data.teachers[].roles[]").type(ARRAY).description("선생 권한"),
//                                        fieldWithPath("data.teachers[].createdDateTime").type(STRING).description("선생 생성일시"),
//                                        fieldWithPath("data.teachers[].modifiedDateTime").type(STRING).description("선생 수정일시")
//                                )
//                        )
//                );
//    }
//
//    @DisplayName("academy 의 정보를 수정할 수 있다.")
//    @Test
//    void updateInfo() throws Exception {
//        // given
//        Academy academy = dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
//        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
//        String token = createToken(director);
//
//        refresh();
//
//        AcademyPATCH academyPATCH = AcademyPATCH.builder()
//                .name("수정된학원이름")
//                .phone("01012345678")
//                .email("academy@sunder.edu")
//                .street("수정된 학원 주소")
//                .addressDetail("수정된 학원 상세주소")
//                .postalCode("12345")
//                .openToPublic(false)
//                .build();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                patch("/api/academy")
//                        .contentType(APPLICATION_JSON)
//                        .header(AUTHORIZATION, token)
//                        .content(createJson(academyPATCH))
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                                requestFields(
//                                        fieldWithPath("name").type(STRING).description("학원 이름"),
//                                        fieldWithPath("phone").type(STRING).description("학원 전화번호").optional(),
//                                        fieldWithPath("email").type(STRING).description("학원 이메일").optional(),
//                                        fieldWithPath("street").type(STRING).description("학원 주소").optional(),
//                                        fieldWithPath("addressDetail").type(STRING).description("학원 상세주소").optional(),
//                                        fieldWithPath("postalCode").type(STRING).description("학원 우편번호").optional(),
//                                        fieldWithPath("openToPublic").type(BOOLEAN).description("학원 공개 여부")
//                                ),
//                                relaxedResponseFields(
//                                        fieldWithPath("data.academyId").type(STRING).description("수정을 완료한 학원 아이디")
//                                )
//                        )
//                );
//    }
//
//    @DisplayName("공개된 학원 목록을 조회할 수 있다.")
//    @Test
//    void getPublicList() throws Exception {
//        // given
//        for (int i = 0; i < 3; i++) {
//            dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
//        }
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                get("/api/academy/list")
//                        .contentType(APPLICATION_JSON)
//                        .param("page", "1")
//                        .param("size", "10")
//                        .param("prop", "name")
//                        .param("dir", "asc")
//                        .param("academyName", "학원")
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                                queryParameters(
//                                        parameterWithName("page").description("페이지 번호. 최소 1"),
//                                        parameterWithName("size").description("페이지 크기. 최소 10"),
//                                        parameterWithName("prop").description("""
//                                                정렬 기준 +
//                                                - name : 학원 이름 +
//                                                - (default) 생성 순서
//                                                """).optional(),
//                                        parameterWithName("dir").description("""
//                                                정렬 방향 +
//                                                - asc : 오름차순 +
//                                                - desc : 내림차순(기본값)
//                                                """).optional(),
//                                        parameterWithName("academyName").description("검색할 학원 이름").optional()
//                                ),
//                                relaxedResponseFields(
//                                        fieldWithPath("data.academies[].id").type(STRING).description("학원 아이디"),
//                                        fieldWithPath("data.academies[].name").type(STRING).description("학원 이름"),
//                                        fieldWithPath("data.academies[].street").type(STRING).description("학원 주소 (도로명)"),
//                                        fieldWithPath("data.academies[].addressDetail").type(STRING).description("학원 주소 (상세주소)"),
//                                        fieldWithPath("data.academies[].postalCode").type(STRING).description("학원 주소 (우편번호)"),
//                                        fieldWithPath("data.academies[].phone").type(STRING).description("학원 전화번호"),
//                                        fieldWithPath("data.academies[].email").type(STRING).description("학원 이메일"),
//                                        fieldWithPath("data.academies[].openToPublic").type(BOOLEAN).description("학원 공개 여부"),
//                                        fieldWithPath("data.academies[].status").type(STRING).description("학원 상태"),
//                                        fieldWithPath("data.academies[].createdDateTime").type(STRING).description("학원 생성일시"),
//                                        fieldWithPath("data.academies[].modifiedDateTime").type(STRING).description("학원 수정일시"),
//                                        fieldWithPath("data.pageInfo.page").type(NUMBER).description("현재 페이지 번호"),
//                                        fieldWithPath("data.pageInfo.size").type(NUMBER).description("페이지 크기"),
//                                        fieldWithPath("data.pageInfo.total").type(NUMBER).description("전체 학원 수"),
//                                        fieldWithPath("data.pageInfo.lastPage").type(NUMBER).description("마지막 페이지 번호"),
//                                        fieldWithPath("data.pageInfo.start").type(NUMBER).description("페이지 세트의 시작 번호"),
//                                        fieldWithPath("data.pageInfo.end").type(NUMBER).description("페이지 세트의 끝 번호"),
//                                        fieldWithPath("data.pageInfo.hasPrev").type(BOOLEAN).description("이전 페이지 존재 여부"),
//                                        fieldWithPath("data.pageInfo.hasNext").type(BOOLEAN).description("다음 페이지 존재 여부")
//                                )
//                        )
//                );
//    }
//
//
//    @DisplayName("학원장은 자기 학원을 폐쇄 신청할 수 있다.")
//    @Test
//    void withdraw() throws Exception {
//        // given
//        Academy academy = dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
//        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
//        String token = createToken(director);
//
//        refresh();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                delete("/api/academy")
//                        .contentType(APPLICATION_JSON)
//                        .header(AUTHORIZATION, token)
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                        relaxedResponseFields(
//                                fieldWithPath("data.academyId").type(STRING).description("페쇄를 신청한 학원 아이디")
//                        )
//                ));
//    }
//
//    @DisplayName("학원장은 자기 학원의 폐쇄 신청을 취소할 수 있다.")
//    @Test
//    void revokeWithdrawal() throws Exception {
//        // given
//        Academy academy = dataCreator.registerAcademy(true, AcademyStatus.VERIFIED);
//        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
//
//        refreshAnd(() -> academyCommandService.withdraw(director.getId()));
//
//        String token = createToken(director);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                patch("/api/academy/revoke")
//                        .contentType(APPLICATION_JSON)
//                        .header(AUTHORIZATION, token)
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                        relaxedResponseFields(
//                                fieldWithPath("data.academyId").type(STRING).description("페쇄를 취소한 학원 아이디")
//                        )
//                ));
//    }
//
//    @DisplayName("사용 체험 중인 학원장이 정규회원으로 전환할 수 있다.")
//    @Test
//    void endTrial() throws Exception {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher director = dataCreator.registerTeacher(UserStatus.TRIAL, academy);
//        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);
//        String token = createToken(director);
//
//        UserLoginForm loginForm = new UserLoginForm(director.getLoginId(), infoContainer.getAnyRawPassword());
//
//        refresh();
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                patch("/api/academy/end-trial")
//                        .contentType(APPLICATION_JSON)
//                        .header(AUTHORIZATION, token)
//                        .content(createJson(loginForm))
//        );
//
//        // then
//        resultActions
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("code").value("200"))
//                .andDo(restDocs.document(
//                        requestFields(
//                                fieldWithPath("loginId").type(STRING).description("학원장 로그인 아이디"),
//                                fieldWithPath("loginPw").type(STRING).description("학원장 로그인 비밀번호")
//                        ),
//                        relaxedResponseFields(
//                                fieldWithPath("data.endTrial").type(BOOLEAN).description("정규전환 성공 여부")
//                        )
//                ));
//    }
//
// package psam.portfolio.sunder.english.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
//import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
//import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
//import psam.portfolio.sunder.english.domain.book.model.entity.Book;
//import psam.portfolio.sunder.english.domain.book.model.request.BookSearchCond;
//import psam.portfolio.sunder.english.domain.book.model.response.BookFullResponse;
//import psam.portfolio.sunder.english.domain.book.service.BookQueryService;
//import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
//import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
//import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.tuple;
//
//@SuppressWarnings("unchecked")
//public class BookQueryServiceTest extends AbstractSunderApplicationTest {
//
//    @Autowired
//    BookQueryService sut; // system under test
//
//    @DisplayName("교재 목록을 출판사, 교재명, 챕터, 주제 상관 없이 검색할 수 있다.")
//    @Test
//    void getBookList() {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//
//        dataCreator.registerBook(false, "능률(김성곤)", "중3", "1과", "본문", academy);
//        dataCreator.registerBook(false, "미래(최연희)", "중3", "1과", "본문", academy);
//        dataCreator.registerBook(false, "능률(김성곤)", "중2", "1과", "본문", academy);
//        dataCreator.registerBook(false, "능률(김성곤)", "중3", "2과", "본문", academy);
//        dataCreator.registerBook(false, "능률(김성곤)", "중3", "1과", "예문", academy);
//
//        BookSearchCond cond = BookSearchCond.builder()
//                .keyword("능률 김 중3 1과 본문")
//                .privateOnly(false)
//                .year(null)
//                .build();
//
//        // when
//        Map<String, Object> result = refreshAnd(() -> sut.getBookList(teacher.getId(), cond));
//
//        // then
//        List<BookFullResponse> books = (List<BookFullResponse>) result.get("books");
//        assertThat(books).hasSize(1)
//                .extracting("publisher", "bookName", "chapter", "subject")
//                .containsExactly(
//                        tuple("능률(김성곤)", "중3", "1과", "본문")
//                );
//    }
//
//    @DisplayName("교재 상세와 함께 교재의 단어 목록을 조회할 수 있다.")
//    @Test
//    void getBookDetail() {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//        Book book = dataCreator.registerBook(academy);
//        dataCreator.registerWord("apple", "사과", book);
//        dataCreator.registerWord("banana", "바나나", book);
//        dataCreator.registerWord("cherry", "체리", book);
//
//        // when
//        Map<String, Object> result = refreshAnd(() -> sut.getBookDetail(teacher.getId(), academy.getBooks().get(0).getId()));
//
//        // then
//        BookFullResponse getBook = (BookFullResponse) result.get("book");
//        assertThat(getBook.getPublisher()).isEqualTo(book.getPublisher());
//        assertThat(getBook.getBookName()).isEqualTo(book.getBookName());
//        assertThat(getBook.getChapter()).isEqualTo(book.getChapter());
//        assertThat(getBook.getSubject()).isEqualTo(book.getSubject());
//
//        List<Map<String, Object>> words = (List<Map<String, Object>>) result.get("words");
//        assertThat(words).hasSize(3)
//                .extracting("korean", "english")
//                .containsExactly(
//                        tuple("사과", "apple"),
//                        tuple("바나나", "banana"),
//                        tuple("체리", "cherry")
//                );
//    }
//}
//
// package psam.portfolio.sunder.english.service;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.BDDMockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mock.web.MockMultipartFile;
//import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
//import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
//import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
//import psam.portfolio.sunder.english.domain.book.enumeration.BookStatus;
//import psam.portfolio.sunder.english.domain.book.exception.NoSuchBookException;
//import psam.portfolio.sunder.english.domain.book.model.entity.Book;
//import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
//import psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList;
//import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
//import psam.portfolio.sunder.english.domain.book.service.BookCommandService;
//import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
//import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
//import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//import static psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList.*;
//
//public class BookCommandServiceTest extends AbstractSunderApplicationTest {
//
//    @Autowired
//    BookCommandService sut; // system under test
//
//    @Autowired
//    BookQueryRepository bookQueryRepository;
//
//    @DisplayName("bookId 가 없으면 새로운 교재를 생성할 수 있다.")
//    @Test
//    void replaceBookWithoutBookId() {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//
//        String publisher = "publisher";
//        String bookName = "bookName";
//        String chapter = "chapter";
//        String subject = "subject";
//
//        BookReplace replace = BookReplace.builder()
//                .openToPublic(false)
//                .publisher(publisher)
//                .bookName(bookName)
//                .chapter(chapter)
//                .subject(subject)
//                .build();
//
//        // when
//        UUID bookId = refreshAnd(() -> sut.replaceBook(teacher.getId(), null, replace));
//
//        // then
//        Book getBook = bookQueryRepository.getById(bookId);
//        assertThat(getBook.getPublisher()).isEqualTo(publisher);
//        assertThat(getBook.getBookName()).isEqualTo(bookName);
//        assertThat(getBook.getChapter()).isEqualTo(chapter);
//        assertThat(getBook.getSubject()).isEqualTo(subject);
//
//        assertThat(getBook.getAcademy().getId()).isEqualTo(academy.getId());
//    }
//
//    @DisplayName("bookId 가 있으면 해당 교재를 수정할 수 있다.")
//    @Test
//    void replaceBookWithBookId() {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//        Book book = dataCreator.registerBook(true, "publisher", "bookName", "chapter", "subject", academy);
//
//        boolean openToPublic = false;
//        String publisher = "updatedPublisher";
//        String bookName = "updatedBookName";
//        String chapter = "updatedChapter";
//        String subject = "updatedSubject";
//
//        BookReplace replace = BookReplace.builder()
//                .openToPublic(openToPublic)
//                .publisher(publisher)
//                .bookName(bookName)
//                .chapter(chapter)
//                .subject(subject)
//                .build();
//
//        // when
//        UUID bookId = refreshAnd(() -> sut.replaceBook(teacher.getId(), book.getId(), replace));
//
//        // then
//        Book getBook = bookQueryRepository.getById(bookId);
//        assertThat(getBook.isOpenToPublic()).isEqualTo(openToPublic);
//        assertThat(getBook.getPublisher()).isEqualTo(publisher);
//        assertThat(getBook.getBookName()).isEqualTo(bookName);
//        assertThat(getBook.getChapter()).isEqualTo(chapter);
//        assertThat(getBook.getSubject()).isEqualTo(subject);
//
//        assertThat(getBook.getAcademy().getId()).isEqualTo(academy.getId());
//    }
//
//    @DisplayName("삭제된 교재는 수정할 수 없다.")
//    @Test
//    void replaceDeletedBook() {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//        Book book = dataCreator.registerBook(academy);
//        book.setStatus(BookStatus.DELETED);
//
//        BookReplace replace = BookReplace.builder()
//                .openToPublic(false)
//                .publisher("updatedPublisher")
//                .bookName("updatedBookName")
//                .chapter("updatedChapter")
//                .subject("updatedSubject")
//                .build();
//
//        // when
//        // then
//        assertThatThrownBy(() -> sut.replaceBook(teacher.getId(), book.getId(), replace))
//                .isInstanceOf(NoSuchBookException.class);
//    }
//
//    @DisplayName("DTO 로 단어 목록을 갱신할 수 있다.")
//    @Test
//    void replaceWords() {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//        Book book = dataCreator.registerBook(academy);
//
//        WordPOSTList postList = new WordPOSTList(
//                List.of(
//                        new WordPOST("korean1", "english1"),
//                        new WordPOST("korean2", "english2"),
//                        new WordPOST("korean3", "english3")
//                        )
//        );
//
//        // when
//        UUID bookId = refreshAnd(() -> sut.replaceWords(teacher.getId(), book.getId(), postList));
//
//        // then
//        Book getBook = bookQueryRepository.getById(bookId);
//        assertThat(getBook.getWords()).hasSize(3)
//                .extracting("korean", "english")
//                .containsExactlyInAnyOrder(
//                        tuple("korean1", "english1"),
//                        tuple("korean2", "english2"),
//                        tuple("korean3", "english3")
//                );
//    }
//
//    @DisplayName("엑셀 파일로 단어 목록을 갱신할 수 있다.")
//    @Test
//    void replaceWordsWithExcelFile() throws IOException {
//        // mocking
//        given(excelUtils.readExcel(any(), any(), any()))
//                .willReturn(List.of(
//                        List.of("english1", "korean1"),
//                        List.of("english2", "korean2"),
//                        List.of("english3", "korean3")
//                ));
//
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//        Book book = dataCreator.registerBook(academy);
//
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "test.xlsx",
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
//                "test".getBytes()
//        );
//
//        // when
//        UUID bookId = refreshAnd(() -> {
//            try {
//                return sut.replaceWords(teacher.getId(), book.getId(), file);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        // then
//        Book getBook = bookQueryRepository.getById(bookId);
//        assertThat(getBook.getWords()).hasSize(3)
//                .extracting("korean", "english")
//                .containsExactlyInAnyOrder(
//                        tuple("korean1", "english1"),
//                        tuple("korean2", "english2"),
//                        tuple("korean3", "english3")
//                );
//    }
//
//    @DisplayName("교재를 삭제할 수 있다.")
//    @Test
//    void deleteBook() {
//        // given
//        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
//        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
//        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
//        Book book = dataCreator.registerBook(academy);
//        dataCreator.registerWord("english", "korean", book);
//
//        // when
//        UUID bookId = refreshAnd(() -> sut.deleteBook(teacher.getId(), book.getId()));
//
//        // then
//        Book getBook = bookQueryRepository.getById(bookId);
//        assertThat(getBook.getStatus()).isEqualTo(BookStatus.DELETED);
//        assertThat(getBook.getWords()).isEmpty();
//    }
//}
//
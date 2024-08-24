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
import psam.portfolio.sunder.english.domain.book.model.request.WordPUT;
import psam.portfolio.sunder.english.domain.book.model.request.WordPUT.WordPUTObject;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

public class BookDocsTest extends RestDocsEnvironment {

    @DisplayName("선생님이 학원 교재를 등록할 수 있다.")
    @Test
    void registerBook() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        BookReplace replace = BookReplace.builder()
                .openToPublic(false)
                .publisher("publisher")
                .name("name")
                .chapter("chapter")
                .subject("subject")
                .schoolGrade(3)
                .build();

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .content(createJson(replace))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("openToPublic").type(BOOLEAN).description("교재 공개 여부"),
                                fieldWithPath("publisher").type(STRING).description("출판사").optional(),
                                fieldWithPath("name").type(STRING).description("교재명"),
                                fieldWithPath("chapter").type(STRING).description("챕터").optional(),
                                fieldWithPath("subject").type(STRING).description("주제").optional(),
                                fieldWithPath("schoolGrade").type(NUMBER).description("학년(1~12)").optional()
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
                .name("newName")
                .chapter("newChapter")
                .subject("newSubject")
                .schoolGrade(2)
                .build();

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/books/{bookId}", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
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
                                fieldWithPath("publisher").type(STRING).description("출판사").optional(),
                                fieldWithPath("name").type(STRING).description("교재명"),
                                fieldWithPath("chapter").type(STRING).description("챕터").optional(),
                                fieldWithPath("subject").type(STRING).description("주제").optional(),
                                fieldWithPath("schoolGrade").type(NUMBER).description("학년(1~12)").optional()
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

        WordPUT postList = WordPUT.builder()
                .words(List.of(
                        new WordPUTObject("apple", "사과"),
                        new WordPUTObject("banana", "바나나"),
                        new WordPUTObject("cherry", "체리")
                ))
                .build();

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/books/{bookId}/words/json", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
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
        // mocking
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
                RestDocumentationRequestBuilders
                        .multipart("/api/books/{bookId}/words/excel", book.getId())
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(AUTHORIZATION, createBearerToken(teacher))
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
                get("/api/books")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
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
                                parameterWithName("schoolGrade").description("학년(1~12)").optional(),
                                parameterWithName("year").description("교재 등록 연도").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.books[].id").type(STRING).description("교재 아이디"),
                                fieldWithPath("data.books[].publisher").type(STRING).description("출판사").optional(),
                                fieldWithPath("data.books[].name").type(STRING).description("교재명").optional(),
                                fieldWithPath("data.books[].chapter").type(STRING).description("챕터").optional(),
                                fieldWithPath("data.books[].subject").type(STRING).description("주제").optional(),
                                fieldWithPath("data.books[].schoolGrade").type(NUMBER).description("학년(1~12)").optional(),
                                fieldWithPath("data.books[].academyId").type(STRING).description("학원 아이디"),
                                fieldWithPath("data.books[].openToPublic").type(BOOLEAN).description("공개 여부"),
                                fieldWithPath("data.books[].createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.books[].modifiedDateTime").type(STRING).description("수정 일시"),
                                fieldWithPath("data.books[].createdBy").type(STRING).description("생성자 아이디").optional(),
                                fieldWithPath("data.books[].modifiedBy").type(STRING).description("수정자 아이디").optional(),
                                fieldWithPath("data.books[].wordCount").type(NUMBER).description("단어 수"),
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
                RestDocumentationRequestBuilders.get("/api/books/{bookId}", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
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
                                fieldWithPath("data.book.publisher").type(STRING).description("출판사").optional(),
                                fieldWithPath("data.book.name").type(STRING).description("교재명").optional(),
                                fieldWithPath("data.book.chapter").type(STRING).description("챕터").optional(),
                                fieldWithPath("data.book.subject").type(STRING).description("주제").optional(),
                                fieldWithPath("data.book.schoolGrade").type(NUMBER).description("학년(1~12)").optional(),
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
                RestDocumentationRequestBuilders.delete("/api/books/{bookId}", book.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
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

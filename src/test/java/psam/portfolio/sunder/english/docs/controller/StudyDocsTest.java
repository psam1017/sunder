package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;
import psam.portfolio.sunder.english.domain.study.model.request.*;
import psam.portfolio.sunder.english.domain.study.repository.StudyQueryRepository;
import psam.portfolio.sunder.english.domain.study.service.StudyCommandService;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudyDocsTest extends RestDocsEnvironment {

    @Autowired
    StudyCommandService studyCommandService;

    @Autowired
    StudyQueryRepository studyQueryRepository;

    @DisplayName("선생님이 숙제를 생성할 수 있다.")
    @Test
    public void assignStudy() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        Student student1 = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student1, RoleName.ROLE_STUDENT);
        Student student2 = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student2, RoleName.ROLE_STUDENT);

        Book book1 = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book1);
        }

        Book book2 = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "3과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("banana" + i, "바나나" + i, book2);
        }

        StudyPOSTAssign post = new StudyPOSTAssign(
                List.of(book1.getId(), book2.getId()),
                true,
                20,
                StudyType.WRITING,
                StudyClassification.EXAM,
                StudyTarget.KOREAN,
                List.of(student1.getId(), student2.getId()),
                true
        );

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/studies/assign")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .content(createJson(post))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("bookIds").type(ARRAY).description("학습할 교재 아이디 목록"),
                                fieldWithPath("ignoreCase").type(BOOLEAN).description("대소문자 구분 여부").optional(),
                                fieldWithPath("numberOfWords").type(NUMBER).description("학습할 단어 수"),
                                fieldWithPath("type").type(STRING).description("학습 유형"),
                                fieldWithPath("classification").type(STRING).description("학습 분류"),
                                fieldWithPath("target").type(STRING).description("학습 대상"),
                                fieldWithPath("studentIds").type(ARRAY).description("학습할 학생 아이디 목록"),
                                fieldWithPath("shuffleEach").type(BOOLEAN).description("단어 섞기 여부")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studyIds").type(ARRAY).description("생성된 숙제 아이디 목록")
                        )
                ));
    }

    @DisplayName("학생이 학습을 시작할 수 있다.")
    @Test
    public void startStudy() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book1 = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book1);
        }
        Book book2 = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "1과", "본문", academy);
        for (int i = 11; i <= 20; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book2);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book1.getId(), book2.getId()), true, 20, StudyType.TRACING, StudyClassification.PRACTICE, StudyTarget.KOREAN);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/studies")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(student))
                        .content(createJson(post))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("bookIds").type(ARRAY).description("학습할 교재 아이디 목록"),
                                fieldWithPath("ignoreCase").type(BOOLEAN).description("대소문자 구분 여부").optional(),
                                fieldWithPath("numberOfWords").type(NUMBER).description("학습할 단어 수"),
                                fieldWithPath("type").type(STRING).description("학습 유형"),
                                fieldWithPath("classification").type(STRING).description("학습 분류"),
                                fieldWithPath("target").type(STRING).description("학습 대상")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studyId").type(STRING).description("생성된 학습 아이디")
                        )
                ));
    }

    @DisplayName("학생이 학습을 제출할 수 있다.")
    @Test
    public void submitStudy() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 5; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 5, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID startStudyId = studyCommandService.start(student.getId(), post);

        Study startStudy = studyQueryRepository.getById(startStudyId);
        List<StudyPATCHSubmit.StudyWordPATCHSubmit> submitStudyWords = new ArrayList<>();
        for (StudyWord sw : startStudy.getStudyWords()) {
            submitStudyWords.add(new StudyPATCHSubmit.StudyWordPATCHSubmit(sw.getId(), sw.getAnswer()));
        }
        StudyPATCHSubmit patchSubmit = new StudyPATCHSubmit(submitStudyWords);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/studies/{studyId}/submit", startStudyId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(student))
                        .content(createJson(patchSubmit))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("studyId").description("제출할 학습 아이디")
                        ),
                        requestFields(
                                fieldWithPath("studyWords[].id").type(NUMBER).description("단어 아이디"),
                                fieldWithPath("studyWords[].submit").type(STRING).description("제출할 답변").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studyId").type(STRING).description("제출된 학습 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 자기 학원 학생들의 학습통계를 조회할 수 있다.")
    @Test
    public void getStatisticByTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        // 배정 상태
        dataCreator.assignAnyStudy(teacher.getId(), List.of(book.getId()), List.of(student.getId()));

        // 시작 상태
        dataCreator.startAnyStudy(student.getId(), List.of(book.getId()));

        // 제출 상태 - 정답
        List<UUID> correctStudyIds = dataCreator.assignAnyStudy(teacher.getId(), List.of(book.getId()), List.of(student.getId()));
        Study correctStudy = studyQueryRepository.getById(correctStudyIds.stream().findAny().orElseThrow());
        studyCommandService.submit(student.getId(), correctStudy.getId(), new StudyPATCHSubmit(correctStudy.getStudyWords().stream().map(sw -> new StudyPATCHSubmit.StudyWordPATCHSubmit(sw.getId(), sw.getAnswer())).toList()));

        // 제출 상태 - 오답
        List<UUID> incorrectStudyIds = dataCreator.assignAnyStudy(teacher.getId(), List.of(book.getId()), List.of(student.getId()));
        Study incorrectStudy = studyQueryRepository.getById(incorrectStudyIds.stream().findAny().orElseThrow());
        studyCommandService.submit(student.getId(), incorrectStudy.getId(), new StudyPATCHSubmit(incorrectStudy.getStudyWords().stream().map(sw -> new StudyPATCHSubmit.StudyWordPATCHSubmit(sw.getId(), sw.getAnswer() + "NO")).toList()));

        StudyStatisticSearchCond cond = new StudyStatisticSearchCond(null, LocalDate.now().minusDays(14), LocalDate.now().minusDays(7));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/studies/statistic")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .param("studentId", "")
                        .param("startDateTime", cond.getStartDateTime().toString())
                        .param("endDateTime", cond.getEndDateTime().toString())
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("studentId").description("학생 아이디. 검색할 경우 bestAnswerRates, worstAnswerRates, bestStudyCounts, worstStudyCounts 가 응답되지 않는다.").optional(),
                                parameterWithName("startDateTime").description("조회 시작 일자. 조회 종료 일자로부터 한 달 전까지만 검색할 수 있다.").optional(),
                                parameterWithName("endDateTime").description("조회 종료 일자. 오늘 일자 이후로 검색할 수 없다.").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.statuses[].status").type(STRING).description("학습 상태"),
                                fieldWithPath("data.statuses[].count").type(NUMBER).description("상태별 학습 수"),
                                fieldWithPath("data.types[].type").type(STRING).description("학습 유형"),
                                fieldWithPath("data.types[].count").type(NUMBER).description("유형별 학습 수"),
                                fieldWithPath("data.classifications[].classification").type(STRING).description("학습 분류"),
                                fieldWithPath("data.classifications[].count").type(NUMBER).description("분류별 학습 수"),
                                fieldWithPath("data.targets[].target").type(STRING).description("학습 대상"),
                                fieldWithPath("data.targets[].count").type(NUMBER).description("대상별 학습 수"),
                                fieldWithPath("data.days[].studyDate").type(STRING).description("학습 일자"),
                                fieldWithPath("data.days[].studyCount").type(NUMBER).description("학습한 횟수"),
                                fieldWithPath("data.days[].correctStudyWordCount").type(NUMBER).description("맞춘 단어 개수"),
                                fieldWithPath("data.days[].totalStudyWordCount").type(NUMBER).description("학습한 단어 개수"),
                                fieldWithPath("data.oldHomeworks[].studyId").type(STRING).description("숙제 아이디"),
                                fieldWithPath("data.oldHomeworks[].title").type(STRING).description("학습 제목"),
                                fieldWithPath("data.oldHomeworks[].status").type(STRING).description("학습 상태"),
                                fieldWithPath("data.oldHomeworks[].createdDateTime").type(STRING).description("숙제 생성 일시"),
                                fieldWithPath("data.oldHomeworks[].studentId").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.oldHomeworks[].studentName").type(STRING).description("학생 이름"),
                                fieldWithPath("data.oldHomeworks[].schoolName").type(STRING).description("학교 이름"),
                                fieldWithPath("data.oldHomeworks[].schoolGrade").type(NUMBER).description("학년"),
                                fieldWithPath("data.bestAnswerRates[].studentId").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.bestAnswerRates[].studentName").type(STRING).description("학생 이름"),
                                fieldWithPath("data.bestAnswerRates[].schoolName").type(STRING).description("학교 이름"),
                                fieldWithPath("data.bestAnswerRates[].schoolGrade").type(NUMBER).description("학년"),
                                fieldWithPath("data.bestAnswerRates[].correctPercent").type(NUMBER).description("정답률"),
                                fieldWithPath("data.bestAnswerRates[].studyCount").type(NUMBER).description("학습 횟수"),
                                fieldWithPath("data.bestAnswerRates[].studyWordCount").type(NUMBER).description("학습 단어 수"),
                                fieldWithPath("data.worstAnswerRates[].studentId").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.worstAnswerRates[].studentName").type(STRING).description("학생 이름"),
                                fieldWithPath("data.worstAnswerRates[].schoolName").type(STRING).description("학교 이름"),
                                fieldWithPath("data.worstAnswerRates[].schoolGrade").type(NUMBER).description("학년"),
                                fieldWithPath("data.worstAnswerRates[].correctPercent").type(NUMBER).description("정답률"),
                                fieldWithPath("data.worstAnswerRates[].studyCount").type(NUMBER).description("학습 횟수"),
                                fieldWithPath("data.worstAnswerRates[].studyWordCount").type(NUMBER).description("학습 단어 수"),
                                fieldWithPath("data.bestStudyCounts[].studentId").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.bestStudyCounts[].studentName").type(STRING).description("학생 이름"),
                                fieldWithPath("data.bestStudyCounts[].schoolName").type(STRING).description("학교 이름"),
                                fieldWithPath("data.bestStudyCounts[].schoolGrade").type(NUMBER).description("학년"),
                                fieldWithPath("data.bestStudyCounts[].correctPercent").type(NUMBER).description("정답률"),
                                fieldWithPath("data.bestStudyCounts[].studyCount").type(NUMBER).description("학습 횟수"),
                                fieldWithPath("data.bestStudyCounts[].studyWordCount").type(NUMBER).description("학습 단어 수"),
                                fieldWithPath("data.worstStudyCounts[].studentId").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.worstStudyCounts[].studentName").type(STRING).description("학생 이름"),
                                fieldWithPath("data.worstStudyCounts[].schoolName").type(STRING).description("학교 이름"),
                                fieldWithPath("data.worstStudyCounts[].schoolGrade").type(NUMBER).description("학년"),
                                fieldWithPath("data.worstStudyCounts[].correctPercent").type(NUMBER).description("정답률"),
                                fieldWithPath("data.worstStudyCounts[].studyCount").type(NUMBER).description("학습 횟수"),
                                fieldWithPath("data.worstStudyCounts[].studyWordCount").type(NUMBER).description("학습 단어 수")
                        )
                ));
    }

    @DisplayName("학생이 자신의 학습통계를 조회할 수 있다.")
    @Test
    public void getStatisticByStudent() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        // 배정 상태
        dataCreator.assignAnyStudy(teacher.getId(), List.of(book.getId()), List.of(student.getId()));

        // 시작 상태
        dataCreator.startAnyStudy(student.getId(), List.of(book.getId()));

        // 제출 상태 - 정답
        List<UUID> correctStudyIds = dataCreator.assignAnyStudy(teacher.getId(), List.of(book.getId()), List.of(student.getId()));
        Study correctStudy = studyQueryRepository.getById(correctStudyIds.stream().findAny().orElseThrow());
        studyCommandService.submit(student.getId(), correctStudy.getId(), new StudyPATCHSubmit(correctStudy.getStudyWords().stream().map(sw -> new StudyPATCHSubmit.StudyWordPATCHSubmit(sw.getId(), sw.getAnswer())).toList()));

        // 제출 상태 - 오답
        List<UUID> incorrectStudyIds = dataCreator.assignAnyStudy(teacher.getId(), List.of(book.getId()), List.of(student.getId()));
        Study incorrectStudy = studyQueryRepository.getById(incorrectStudyIds.stream().findAny().orElseThrow());
        studyCommandService.submit(student.getId(), incorrectStudy.getId(), new StudyPATCHSubmit(incorrectStudy.getStudyWords().stream().map(sw -> new StudyPATCHSubmit.StudyWordPATCHSubmit(sw.getId(), sw.getAnswer() + "NO")).toList()));

        StudyStatisticSearchCond cond = new StudyStatisticSearchCond(null, LocalDate.now().minusDays(14), LocalDate.now().minusDays(7));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/studies/statistic")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(student))
                        .param("startDateTime", cond.getStartDateTime().toString())
                        .param("endDateTime", cond.getEndDateTime().toString())
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("startDateTime").description("조회 시작 일자. 조회 종료 일자로부터 한 달 전까지만 검색할 수 있다.").optional(),
                                parameterWithName("endDateTime").description("조회 종료 일자. 오늘 일자 이후로 검색할 수 없다.").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.statuses[].status").type(STRING).description("학습 상태"),
                                fieldWithPath("data.statuses[].count").type(NUMBER).description("상태별 학습 수"),
                                fieldWithPath("data.types[].type").type(STRING).description("학습 유형"),
                                fieldWithPath("data.types[].count").type(NUMBER).description("유형별 학습 수"),
                                fieldWithPath("data.classifications[].classification").type(STRING).description("학습 분류"),
                                fieldWithPath("data.classifications[].count").type(NUMBER).description("분류별 학습 수"),
                                fieldWithPath("data.targets[].target").type(STRING).description("학습 대상"),
                                fieldWithPath("data.targets[].count").type(NUMBER).description("대상별 학습 수"),
                                fieldWithPath("data.days[].studyDate").type(STRING).description("학습 일자"),
                                fieldWithPath("data.days[].studyCount").type(NUMBER).description("학습한 횟수"),
                                fieldWithPath("data.days[].correctStudyWordCount").type(NUMBER).description("맞춘 단어 개수"),
                                fieldWithPath("data.days[].totalStudyWordCount").type(NUMBER).description("학습한 단어 개수"),
                                fieldWithPath("data.oldHomeworks[].studyId").type(STRING).description("숙제 아이디"),
                                fieldWithPath("data.oldHomeworks[].title").type(STRING).description("학습 제목"),
                                fieldWithPath("data.oldHomeworks[].status").type(STRING).description("학습 상태"),
                                fieldWithPath("data.oldHomeworks[].createdDateTime").type(STRING).description("숙제 생성 일시"),
                                fieldWithPath("data.oldHomeworks[].studentId").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.oldHomeworks[].studentName").type(STRING).description("학생 이름"),
                                fieldWithPath("data.oldHomeworks[].schoolName").type(STRING).description("학교 이름"),
                                fieldWithPath("data.oldHomeworks[].schoolGrade").type(NUMBER).description("학년")
                        )
                ));
    }

    @DisplayName("학습 목록을 조회할 수 있다.")
    @Test
    public void getStudyList() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        for (int i = 0; i < 3; i++) {
            StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
            studyCommandService.start(student.getId(), post);
        }

        long lastSequence = studyQueryRepository.findNextSequenceOfLastStudy();

        refresh();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/studies")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .param("size", "10")
                        .param("lastSequence", String.valueOf(lastSequence + 1))
                        .param("startDateTime", LocalDateTime.now().minusDays(1).toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(1).toString())
                        .param("studyTitle", book.getName())
                        .param("studentName", student.getName())
                        .param("schoolGrade", String.valueOf(student.getSchool().getGrade()))
                        .param("studyStatus", StudyStatus.STARTED.name())
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("size").description("한 번에 조회할 성적 수"),
                                parameterWithName("lastSequence").description("마지막으로 조회한 sequence. 기본값으로 null 을 전송."),
                                parameterWithName("startDateTime").description("조회 시작 일시").optional(),
                                parameterWithName("endDateTime").description("조회 종료 일시").optional(),
                                parameterWithName("studyTitle").description("학습 제목").optional(),
                                parameterWithName("studentName").description("학생 이름").optional(),
                                parameterWithName("schoolGrade").description("학년").optional(),
                                parameterWithName("studyStatus").description("학습 상태").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studies[].id").type(STRING).description("학습 아이디"),
                                fieldWithPath("data.studies[].sequence").type(NUMBER).description("학습 생성 시퀀스"),
                                fieldWithPath("data.studies[].title").type(STRING).description("학습 제목"),
                                fieldWithPath("data.studies[].status").type(STRING).description("학습 상태"),
                                fieldWithPath("data.studies[].type").type(STRING).description("학습 유형"),
                                fieldWithPath("data.studies[].classification").type(STRING).description("학습 분류"),
                                fieldWithPath("data.studies[].target").type(STRING).description("학습 대상"),
                                fieldWithPath("data.studies[].submitDateTime").type(STRING).description("학습 제출 일시").optional(),
                                fieldWithPath("data.studies[].studentId").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.studies[].attendanceId").type(STRING).description("출석 아이디").optional(),
                                fieldWithPath("data.studies[].studentName").type(STRING).description("학생 이름"),
                                fieldWithPath("data.studies[].schoolName").type(STRING).description("학교 이름").optional(),
                                fieldWithPath("data.studies[].schoolGrade").type(NUMBER).description("학년").optional(),
                                fieldWithPath("data.studies[].correctCount").type(NUMBER).description("정답 수"),
                                fieldWithPath("data.studies[].totalCount").type(NUMBER).description("전체 단어 수"),
                                fieldWithPath("data.slicingInfo.size").type(NUMBER).description("조회된 학습 수"),
                                fieldWithPath("data.slicingInfo.lastSequence").type(NUMBER).description("마지막 sequence"),
                                fieldWithPath("data.slicingInfo.hasNext").type(BOOLEAN).description("다음 게시물 존재 여부")
                        )
                ));
    }

    @DisplayName("학습 상세를 조회할 수 있다.")
    @Test
    public void getStudyDetail() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 5; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.SELECT, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID startStudyId = studyCommandService.start(student.getId(), post);

        Study startStudy = studyQueryRepository.getById(startStudyId);
        List<StudyPATCHSubmit.StudyWordPATCHSubmit> submitStudyWords = new ArrayList<>();
        for (StudyWord sw : startStudy.getStudyWords()) {
            submitStudyWords.add(new StudyPATCHSubmit.StudyWordPATCHSubmit(sw.getId(), sw.getAnswer()));
        }
        StudyPATCHSubmit patchSubmit = new StudyPATCHSubmit(submitStudyWords);
        studyCommandService.submit(student.getId(), startStudyId, patchSubmit);

        StudyWordPATCHCorrect patchCorrect = new StudyWordPATCHCorrect(false, "REASON");
        Long studyWordId = startStudy.getStudyWords().get(0).getId();

        refreshAnd(() -> studyCommandService.correctStudyWord(teacher.getId(), startStudyId, studyWordId, patchCorrect));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/studies/{studyId}", startStudyId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("studyId").description("조회할 학습 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studyWords[].id").type(NUMBER).description("단어 아이디"),
                                fieldWithPath("data.studyWords[].question").type(STRING).description("질문"),
                                fieldWithPath("data.studyWords[].submit").type(STRING).description("제출된 답변").optional(),
                                fieldWithPath("data.studyWords[].answer").type(STRING).description("정답(시험 중인 학생은 조회 불가)").optional(),
                                fieldWithPath("data.studyWords[].correct").type(BOOLEAN).description("정답 여부").optional(),
                                fieldWithPath("data.studyWords[].reason").type(STRING).description("정오 채점 이유").optional(),
                                fieldWithPath("data.studyWords[].choices").type(ARRAY).description("객관식인 경우 선택지").optional(),
                                fieldWithPath("data.study.id").type(STRING).description("학습 아이디"),
                                fieldWithPath("data.study.sequence").type(NUMBER).description("학습 순서"),
                                fieldWithPath("data.study.ignoreCase").type(BOOLEAN).description("대소문자 무시 여부"),
                                fieldWithPath("data.study.title").type(STRING).description("학습 제목"),
                                fieldWithPath("data.study.status").type(STRING).description("학습 상태"),
                                fieldWithPath("data.study.type").type(STRING).description("학습 유형"),
                                fieldWithPath("data.study.classification").type(STRING).description("학습 분류"),
                                fieldWithPath("data.study.target").type(STRING).description("학습 대상"),
                                fieldWithPath("data.study.submitDateTime").type(STRING).description("학습 제출 날짜 및 시간"),
                                fieldWithPath("data.study.studyRanges[].bookPublisher").type(STRING).description("교재 출판사"),
                                fieldWithPath("data.study.studyRanges[].bookName").type(STRING).description("교재 이름"),
                                fieldWithPath("data.study.studyRanges[].bookChapter").type(STRING).description("교재 챕터"),
                                fieldWithPath("data.study.studyRanges[].bookSubject").type(STRING).description("교재 주제"),
                                fieldWithPath("data.teacher.id").type(STRING).description("교사 아이디").optional(),
                                fieldWithPath("data.teacher.name").type(STRING).description("교사 이름").optional(),
                                fieldWithPath("data.teacher.status").type(STRING).description("교사 상태").optional(),
                                fieldWithPath("data.teacher.roles").type(ARRAY).description("교사 역할").optional(),
                                fieldWithPath("data.teacher.createdDateTime").type(STRING).description("생성 날짜 및 시간").optional(),
                                fieldWithPath("data.teacher.modifiedDateTime").type(STRING).description("수정 날짜 및 시간").optional(),
                                fieldWithPath("data.student.id").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.student.loginId").type(STRING).description("학생 로그인 아이디"),
                                fieldWithPath("data.student.name").type(STRING).description("학생 이름"),
                                fieldWithPath("data.student.email").type(STRING).description("학생 이메일"),
                                fieldWithPath("data.student.phone").type(STRING).description("학생 전화번호"),
                                fieldWithPath("data.student.street").type(STRING).description("학생 거리 주소"),
                                fieldWithPath("data.student.addressDetail").type(STRING).description("학생 상세 주소"),
                                fieldWithPath("data.student.postalCode").type(STRING).description("학생 우편번호"),
                                fieldWithPath("data.student.status").type(STRING).description("학생 상태"),
                                fieldWithPath("data.student.roles").type(ARRAY).description("학생 역할"),
                                fieldWithPath("data.student.lastPasswordChangeDateTime").type(STRING).description("마지막 비밀번호 변경 날짜 및 시간"),
                                fieldWithPath("data.student.attendanceId").type(STRING).description("출석 아이디"),
                                fieldWithPath("data.student.note").type(STRING).description("노트"),
                                fieldWithPath("data.student.schoolName").type(STRING).description("학교 이름"),
                                fieldWithPath("data.student.schoolGrade").type(NUMBER).description("학년"),
                                fieldWithPath("data.student.parentName").type(STRING).description("부모 이름"),
                                fieldWithPath("data.student.parentPhone").type(STRING).description("부모 전화번호"),
                                fieldWithPath("data.student.academyId").type(STRING).description("학원 아이디"),
                                fieldWithPath("data.student.createdDateTime").type(STRING).description("생성 날짜 및 시간"),
                                fieldWithPath("data.student.modifiedDateTime").type(STRING).description("수정 날짜 및 시간"),
                                fieldWithPath("data.student.createdBy").type(STRING).description("생성자").optional(),
                                fieldWithPath("data.student.modifiedBy").type(STRING).description("수정자").optional()
                        )
                ));
    }

    @DisplayName("선생님이 학습 단어를 정정할 수 있다.")
    @Test
    public void correctStudyWord() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 5; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 5, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID startStudyId = studyCommandService.start(student.getId(), post);

        Study startStudy = studyQueryRepository.getById(startStudyId);
        List<StudyPATCHSubmit.StudyWordPATCHSubmit> submitStudyWords = new ArrayList<>();
        for (StudyWord sw : startStudy.getStudyWords()) {
            submitStudyWords.add(new StudyPATCHSubmit.StudyWordPATCHSubmit(sw.getId(), sw.getAnswer()));
        }
        StudyPATCHSubmit patchSubmit = new StudyPATCHSubmit(submitStudyWords);
        studyCommandService.submit(student.getId(), startStudyId, patchSubmit);

        StudyWordPATCHCorrect patchCorrect = new StudyWordPATCHCorrect(false, "REASON");
        Long studyWordId = startStudy.getStudyWords().get(0).getId();

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/studies/{studyId}/study-words/{studyWordId}/correct", startStudyId, studyWordId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .content(createJson(patchCorrect))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("studyId").description("정정할 학습 아이디"),
                                parameterWithName("studyWordId").description("정정할 학습 단어 아이디")
                        ),
                        requestFields(
                                fieldWithPath("correct").type(BOOLEAN).description("정답 여부"),
                                fieldWithPath("reason").type(STRING).description("정오 채점 이유").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studyId").type(STRING).description("정정된 학습 아이디"),
                                fieldWithPath("data.studyWordId").type(NUMBER).description("정정된 학습 단어 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 학습을 삭제할 수 있다.")
    @Test
    public void deleteStudy() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 5; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 5, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID startStudyId = studyCommandService.start(student.getId(), post);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/studies/{studyId}", startStudyId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("studyId").description("삭제할 학습 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studyId").type(STRING).description("삭제된 학습 아이디")
                        )
                ));
    }
}

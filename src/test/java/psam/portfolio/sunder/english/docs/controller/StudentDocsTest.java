package psam.portfolio.sunder.english.docs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import psam.portfolio.sunder.english.docs.RestDocsEnvironment;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHInfo;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHStatus;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPOST;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_DIRECTOR;
import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.ROLE_TEACHER;

public class StudentDocsTest extends RestDocsEnvironment {

    @DisplayName("선생님이 학원의 출석 아이디 중복을 검사할 수 있다.")
    @Test
    void checkDuplication() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        String attendanceId = infoContainer.getUniqueAttendanceId();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/students/check-dupl")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .param("attendanceId", attendanceId)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("attendanceId").description("출석 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.isOk").type(BOOLEAN).description("중복 여부")
                        )
                ));
    }

    // 선생님이 학생을 등록할 수 있다.
    @DisplayName("선생님이 학생을 등록할 수 있다.")
    @Test
    void registerStudent() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        String loginId = infoContainer.getUniqueLoginId();
        String password = infoContainer.getAnyRawPassword();
        String name = "선더학생";
        String email = infoContainer.getUniqueEmail();
        String phoneNumber = infoContainer.getUniquePhoneNumber();
        Address address = infoContainer.getAnyAddress();
        String attendanceId = infoContainer.getUniqueAttendanceId();
        String note = "note about student";
        School school = infoContainer.getAnySchool();
        Parent parent = infoContainer.getAnyParent();

        StudentPOST post = new StudentPOST(
                loginId,
                password,
                name,
                email,
                phoneNumber,
                address.getStreet(),
                address.getDetail(),
                address.getPostalCode(),
                attendanceId,
                note,
                school.getName(),
                school.getGrade(),
                parent.getName(),
                parent.getPhone()
        );

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/students")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(director))
                        .content(createJson(post))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("loginId").type(STRING).description("학생 로그인 아이디"),
                                fieldWithPath("loginPw").type(STRING).description("학생 로그인 비밀번호"),
                                fieldWithPath("name").type(STRING).description("학생 이름"),
                                fieldWithPath("email").type(STRING).description("학생 이메일"),
                                fieldWithPath("phone").type(STRING).description("학생 전화번호"),
                                fieldWithPath("street").type(STRING).description("학생 주소"),
                                fieldWithPath("addressDetail").type(STRING).description("학생 상세주소"),
                                fieldWithPath("postalCode").type(STRING).description("학생 우편번호"),
                                fieldWithPath("attendanceId").type(STRING).description("학생 출석 아이디"),
                                fieldWithPath("note").type(STRING).description("학생 메모"),
                                fieldWithPath("schoolName").type(STRING).description("학생 학교 이름"),
                                fieldWithPath("schoolGrade").type(NUMBER).description("학생 학년"),
                                fieldWithPath("parentName").type(STRING).description("학생 부모 이름"),
                                fieldWithPath("parentPhone").type(STRING).description("학생 부모 전화번호")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studentId").type(STRING).description("등록된 학생 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 학생 목록을 조회할 수 있다.")
    @Test
    void searchStudentByTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher director = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(director, ROLE_DIRECTOR, ROLE_TEACHER);

        dataCreator.registerStudent("Bart Simpson", "HIGH1", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);
        dataCreator.registerStudent("Lisa Simpson", "HIGH2", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);
        dataCreator.registerStudent("Bart Simpson", "MID1", new Address("124 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);
        dataCreator.registerStudent("Bart Simpson", "MID2", new Address("123 Main St", "Apt 4C", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);
        dataCreator.registerStudent("Bart Simpson", "MID3", new Address("123 Main St", "Apt 4B", "12345"), new School("Shelbyville Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);
        dataCreator.registerStudent("Bart Simpson", "ELEM1", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 5), new Parent("Homer Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);
        dataCreator.registerStudent("Bart Simpson", "ELEM2", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Marge Simpson", "555-123-4567"), UserStatus.ACTIVE, academy);
        dataCreator.registerStudent("Bart Simpson", "ELEM3", new Address("123 Main St", "Apt 4B", "12345"), new School("Springfield Elementary", 4), new Parent("Homer Simpson", "555-123-4567"), UserStatus.WITHDRAWN, academy);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/students")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(director))
                        .param("page", "1")
                        .param("size", "10")
                        .param("prop", "name")
                        .param("dir", "asc")
                        .param("address", "Main")
                        .param("status", "ACTIVE")
                        .param("name", "Bart")
                        .param("attendanceId", "HIGH1")
                        .param("schoolName", "Springfield Elementary")
                        .param("schoolGrade", "4")
                        .param("parentName", "Homer")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("prop").description("""
                                        정렬 기준 +
                                        - name: 이름 +
                                        - status: 상태 +
                                        - schoolName: 학교 이름 +
                                        - attendanceId: 출석 아이디
                                        """).optional(),
                                parameterWithName("dir").description("""
                                        정렬 방향 +
                                        - asc: 오름차순 +
                                        - desc: 내림차순
                                        """).optional(),
                                parameterWithName("address").description("검색할 주소. 주소, 상세주소, 우편번호 모두 포함").optional(),
                                parameterWithName("status").description("검색할 학생 상태").optional(),
                                parameterWithName("name").description("검색할 학생 이름").optional(),
                                parameterWithName("attendanceId").description("검색할 학생 출석 아이디").optional(),
                                parameterWithName("schoolName").description("검색할 학교 이름").optional(),
                                parameterWithName("schoolGrade").description("검색할 학년").optional(),
                                parameterWithName("parentName").description("검색할 부모 이름").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.students[].id").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.students[].loginId").type(STRING).description("학생 로그인 아이디"),
                                fieldWithPath("data.students[].name").type(STRING).description("학생 이름"),
                                fieldWithPath("data.students[].email").type(STRING).description("학생 이메일"),
                                fieldWithPath("data.students[].phone").type(STRING).description("학생 전화번호"),
                                fieldWithPath("data.students[].street").type(STRING).description("학생 주소"),
                                fieldWithPath("data.students[].addressDetail").type(STRING).description("학생 상세주소"),
                                fieldWithPath("data.students[].postalCode").type(STRING).description("학생 우편번호"),
                                fieldWithPath("data.students[].status").type(STRING).description("학생 상태"),
                                fieldWithPath("data.students[].attendanceId").type(STRING).description("학생 출석 아이디"),
                                fieldWithPath("data.students[].schoolName").type(STRING).description("학생 학교 이름"),
                                fieldWithPath("data.students[].schoolGrade").type(NUMBER).description("학생 학년"),
                                fieldWithPath("data.students[].parentName").type(STRING).description("학생 부모 이름"),
                                fieldWithPath("data.students[].parentPhone").type(STRING).description("학생 부모 전화번호"),
                                fieldWithPath("data.students[].createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.students[].modifiedDateTime").type(STRING).description("수정 일시"),
                                fieldWithPath("data.students[].createdBy").type(STRING).description("생성자").optional(),
                                fieldWithPath("data.students[].modifiedBy").type(STRING).description("수정자").optional(),
                                fieldWithPath("data.pageInfo.page").type(NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.pageInfo.size").type(NUMBER).description("페이지 크기"),
                                fieldWithPath("data.pageInfo.total").type(NUMBER).description("전체 학생 수"),
                                fieldWithPath("data.pageInfo.lastPage").type(NUMBER).description("마지막 페이지 번호"),
                                fieldWithPath("data.pageInfo.start").type(NUMBER).description("페이지 세트의 시작 번호"),
                                fieldWithPath("data.pageInfo.end").type(NUMBER).description("페이지 세트의 끝 번호"),
                                fieldWithPath("data.pageInfo.hasPrev").type(BOOLEAN).description("이전 페이지 존재 여부"),
                                fieldWithPath("data.pageInfo.hasNext").type(BOOLEAN).description("다음 페이지 존재 여부")
                        )
                ));
    }

    @DisplayName("선생님이 학생 상세 정보를 조회할 수 있다.")
    @Test
    void getStudentDetailByTeacher() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_TEACHER);

        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/students/{studentId}", student.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("studentId").description("학생 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.id").type(STRING).description("학생 아이디"),
                                fieldWithPath("data.loginId").type(STRING).description("학생 로그인 아이디"),
                                fieldWithPath("data.name").type(STRING).description("학생 이름"),
                                fieldWithPath("data.email").type(STRING).description("학생 이메일"),
                                fieldWithPath("data.emailVerified").type(BOOLEAN).description("이메일 인증 여부"),
                                fieldWithPath("data.phone").type(STRING).description("학생 전화번호"),
                                fieldWithPath("data.street").type(STRING).description("학생 주소"),
                                fieldWithPath("data.addressDetail").type(STRING).description("학생 상세주소"),
                                fieldWithPath("data.postalCode").type(STRING).description("학생 우편번호"),
                                fieldWithPath("data.status").type(STRING).description("학생 상태"),
                                fieldWithPath("data.roles").type(ARRAY).description("학생 역할"),
                                fieldWithPath("data.lastPasswordChangeDateTime").type(STRING).description("마지막 비밀번호 변경 일시"),
                                fieldWithPath("data.attendanceId").type(STRING).description("학생 출석 아이디"),
                                fieldWithPath("data.note").type(STRING).description("학생 메모"),
                                fieldWithPath("data.schoolName").type(STRING).description("학생 학교 이름"),
                                fieldWithPath("data.schoolGrade").type(NUMBER).description("학생 학년"),
                                fieldWithPath("data.parentName").type(STRING).description("학생 부모 이름"),
                                fieldWithPath("data.parentPhone").type(STRING).description("학생 부모 전화번호"),
                                fieldWithPath("data.academyId").type(STRING).description("학원 아이디"),
                                fieldWithPath("data.createdDateTime").type(STRING).description("생성 일시"),
                                fieldWithPath("data.modifiedDateTime").type(STRING).description("수정 일시"),
                                fieldWithPath("data.createdBy").type(STRING).description("생성자").optional(),
                                fieldWithPath("data.modifiedBy").type(STRING).description("수정자").optional()
                        )
                ));
    }

    @DisplayName("선생님이 학생 정보를 수정할 수 있다.")
    @Test
    void updateStudentInfo() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        // when
        String updatedName = "수정된선더학생";
        String updatedEmail = infoContainer.getUniqueEmail();
        String updatedPhoneNumber = infoContainer.getUniquePhoneNumber();
        Address updatedAddress = Address.builder()
                .street("서울특별시 선더구 수정된 선더로 1")
                .detail("수정된 선더빌딩")
                .postalCode("11111")
                .build();
        String updatedAttendanceId = infoContainer.getUniqueAttendanceId();
        String updatedNote = "updated note about student";
        School updatedSchool = School.builder()
                .name("수정된 선더초등학교")
                .grade(4)
                .build();
        Parent updatedParent = Parent.builder()
                .name("수정된 선더부모")
                .phone("01087654321")
                .build();

        StudentPATCHInfo patch = StudentPATCHInfo.builder()
                .name(updatedName)
                .phone(updatedPhoneNumber)
                .email(updatedEmail)
                .street(updatedAddress.getStreet())
                .addressDetail(updatedAddress.getDetail())
                .postalCode(updatedAddress.getPostalCode())
                .attendanceId(updatedAttendanceId)
                .note(updatedNote)
                .schoolName(updatedSchool.getName())
                .schoolGrade(updatedSchool.getGrade())
                .parentName(updatedParent.getName())
                .parentPhone(updatedParent.getPhone())
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/students/{studentId}/personal-info", student.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .content(createJson(patch))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("studentId").description("학생 아이디")
                        ),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("학생 이름"),
                                fieldWithPath("phone").type(STRING).description("학생 전화번호"),
                                fieldWithPath("email").type(STRING).description("학생 이메일"),
                                fieldWithPath("street").type(STRING).description("학생 주소"),
                                fieldWithPath("addressDetail").type(STRING).description("학생 상세주소"),
                                fieldWithPath("postalCode").type(STRING).description("학생 우편번호"),
                                fieldWithPath("attendanceId").type(STRING).description("학생 출석 아이디"),
                                fieldWithPath("note").type(STRING).description("학생 메모"),
                                fieldWithPath("schoolName").type(STRING).description("학생 학교 이름"),
                                fieldWithPath("schoolGrade").type(NUMBER).description("학생 학년"),
                                fieldWithPath("parentName").type(STRING).description("학생 부모 이름"),
                                fieldWithPath("parentPhone").type(STRING).description("학생 부모 전화번호")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studentId").type(STRING).description("수정된 학생 아이디")
                        )
                ));
    }

    @DisplayName("선생님이 학생 상태를 변경할 수 있다.")
    @Test
    void changeStudentStatus() throws Exception {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, ROLE_DIRECTOR, ROLE_TEACHER);
        Student student = dataCreator.registerStudent(UserStatus.PENDING, academy);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        StudentPATCHStatus patch = StudentPATCHStatus.builder()
                .status(UserStatus.ACTIVE)
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/students/{studentId}/status", student.getId())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, createBearerToken(teacher))
                        .content(createJson(patch))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("200"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("studentId").description("학생 아이디")
                        ),
                        requestFields(
                                fieldWithPath("status").type(STRING).description("""
                                        변경할 학생 상태 +
                                        - PENDING: 대기 +
                                        - ACTIVE: 활성 +
                                        - WITHDRAWN: 탈퇴
                                        """)
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data.studentId").type(STRING).description("변경된 학생 아이디"),
                                fieldWithPath("data.status").type(STRING).description("변경된 학생 상태")
                        )
                ));
    }
}

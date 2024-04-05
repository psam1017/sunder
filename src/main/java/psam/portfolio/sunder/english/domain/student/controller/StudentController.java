package psam.portfolio.sunder.english.domain.student.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHInfo;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHStatus;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPOST;
import psam.portfolio.sunder.english.domain.student.model.request.StudentSearchCond;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.student.service.StudentCommandService;
import psam.portfolio.sunder.english.domain.student.service.StudentQueryService;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/student")
@RestController
public class StudentController {

    private final StudentCommandService studentCommandService;
    private final StudentQueryService studentQueryService;

    /**
     * 학생 중복 체크 서비스
     *
     * @param academyId    학원 아이디
     * @param attendanceId 출석 아이디
     * @return 중복 여부 - 가능 = true, 중복 = false
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    @GetMapping("{academyId}/check-dupl")
    public ApiResponse<Map<String, Boolean>> checkDuplication(@PathVariable UUID academyId,
                                                              @RequestParam String attendanceId) {
        boolean result = studentQueryService.checkDuplication(academyId, attendanceId);
        return ApiResponse.ok(Map.of("isOk", result));
    }

    /**
     * 학생 등록 서비스
     *
     * @param studentId 학원에 등록할 학생의 아이디
     * @param post      등록할 학생 정보
     * @return 등록에 성공한 학생 아이디
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    @PostMapping("")
    public ApiResponse<Map<String, UUID>> registerStudent(@UserId UUID studentId,
                                                          @RequestBody @Valid StudentPOST post) {
        UUID newStudentId = studentCommandService.register(studentId, post);
        return ApiResponse.ok(Map.of("studentId", newStudentId));
    }

    /**
     * 학생 목록 조회 서비스. 페이징 없이 모든 학생 목록을 반환한다.
     *
     * @param userId 사용자 아이디
     * @param cond   학생 목록 조회 조건
     * @return 학생 목록
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getStudentList(@UserId UUID userId,
                                                           @ModelAttribute StudentSearchCond cond) {
        Map<String, Object> response = studentQueryService.getList(userId, cond);
        return ApiResponse.ok(response);
    }

    /**
     * 학생 상세 정보 조회 서비스
     *
     * @param userId    사용자 아이디
     * @param studentId 조회할 학생 아이디
     * @return 학생 상세 정보
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/{studentId}")
    public ApiResponse<Object> getStudent(@UserId UUID userId,
                                          @PathVariable UUID studentId) {
        StudentFullResponse studentResponse = studentQueryService.get(userId, studentId);
        return ApiResponse.ok(studentResponse);
    }

    /**
     * 학생 정보 수정 서비스
     *
     * @param userId    사용자 아이디
     * @param studentId 수정할 학생 아이디
     * @param patch     수정할 학생 정보
     * @return 수정에 성공한 학생 아이디
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    @PatchMapping("/{studentId}/personal-info")
    public ApiResponse<Map<String, UUID>> updateStudent(@UserId UUID userId,
                                                        @PathVariable UUID studentId,
                                                        @RequestBody @Valid StudentPATCHInfo patch) {
        UUID updatedStudentId = studentCommandService.updateInfo(userId, studentId, patch);
        return ApiResponse.ok(Map.of("studentId", updatedStudentId));
    }

    /**
     * 학생 상태 변경 서비스. 탈퇴 상태로 변경도 포함한다.
     *
     * @param directorId 학원장 아이디
     * @param studentId  상태를 변경할 학생 아이디
     * @param patch      변경할 상태 - 가능한 값 : PENDING, ACTIVE, WITHDRAWN
     * @return 학생 아이디와 변경된 상태
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    @PatchMapping("/{studentId}/status")
    public ApiResponse<Map<String, String>> changeStudentStatus(@UserId UUID directorId,
                                                                @PathVariable UUID studentId,
                                                                @RequestBody @Valid StudentPATCHStatus patch) {
        UserStatus status = studentCommandService.changeStatus(directorId, studentId, patch);
        return ApiResponse.ok(Map.of(
                "studentId", studentId.toString(),
                "status", status.name()
        ));
    }
}

package psam.portfolio.sunder.english.domain.teacher.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.teacher.model.request.*;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherCommandService;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherQueryService;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/teacher")
@RestController
public class TeacherController {

    private final TeacherCommandService teacherCommandService;
    private final TeacherQueryService teacherQueryService;

    /**
     * 선생님 등록 서비스
     *
     * @param teacherId 학원에 등록할 선생의 아이디
     * @param post      등록할 선생님 정보
     * @return 등록에 성공한 선생님 아이디
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    @PostMapping("")
    public ApiResponse<Map<String, UUID>> registerTeacher(@UserId UUID teacherId,
                                                          @RequestBody @Valid TeacherPOST post) {
        UUID newTeacherId = teacherCommandService.register(teacherId, post);
        return ApiResponse.ok(Map.of("teacherId", newTeacherId));
    }

    /**
     * 선생님 목록 조회 서비스. 페이징 없이 모든 선생 목록을 반환한다.
     *
     * @param userId 사용자 아이디
     * @param cond   선생님 목록 조회 조건
     * @return 선생님 목록
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/list")
    public ApiResponse<Map<String, List<?>>> getTeacherList(@UserId UUID userId,
                                                            @ModelAttribute TeacherSearchCond cond) {
        List<?> teachers = teacherQueryService.getList(userId, cond);
        return ApiResponse.ok(Map.of("teachers", teachers));
    }

    /**
     * 선생님 상세 정보 조회 서비스
     *
     * @param userId    사용자 아이디
     * @param teacherId 선생님 아이디
     * @return 선생님 상세 정보
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/{teacherId}")
    public ApiResponse<Object> getTeacherDetail(@UserId UUID userId,
                                                @PathVariable UUID teacherId) {
        Object teacher = teacherQueryService.getDetail(userId, teacherId);
        return ApiResponse.ok(teacher);
    }

    /**
     * 선생님 상태 변경 서비스. 탈퇴 상태로 변경도 포함한다.
     *
     * @param directorId 학원장 아이디
     * @param teacherId  상태를 변경할 선생님 아이디
     * @param patch      변경할 상태 - 가능한 값 : PENDING, ACTIVE, WITHDRAWN
     * @return 선생님 아이디와 변경된 상태
     */
    @Secured("ROLE_DIRECTOR")
    @PatchMapping("/{teacherId}/status")
    public ApiResponse<Map<String, String>> changeTeacherStatus(@UserId UUID directorId,
                                                                @PathVariable UUID teacherId,
                                                                @RequestBody @Valid TeacherPATCHStatus patch) {
        UserStatus status = teacherCommandService.changeStatus(directorId, teacherId, patch);
        return ApiResponse.ok(Map.of(
                "teacherId", teacherId.toString(),
                "status", status.name()
        ));
    }

    /**
     * 선생님 권한 변경 서비스
     *
     * @param directorId 학원장 아이디
     * @param teacherId  권한을 변경할 선생님 아이디
     * @param put        변경할 권한 - 가능한 값 : ROLE_TEACHER, ROLE_DIRECTOR
     * @return 선생님 아이디와 변경 완료된 권한 목록
     */
    @Secured("ROLE_DIRECTOR")
    @PutMapping("/{teacherId}/roles")
    public ApiResponse<Map<String, Object>> changeTeacherRoles(@UserId UUID directorId,
                                                              @PathVariable UUID teacherId,
                                                              @RequestBody @Valid TeacherPUTRoles put) {
        Set<RoleName> roles = teacherCommandService.changeRoles(directorId, teacherId, put);
        return ApiResponse.ok(Map.of(
                "teacherId", teacherId.toString(),
                "roles", roles
        ));
    }

    /**
     * 선생님 개인정보 수정 서비스
     *
     * @param teacherId 선생님 아이디
     * @param patch     변경할 개인정보
     * @return 개인정보 변경 완료된 선생님 아이디
     */
    @Secured("ROLE_TEACHER")
    @PatchMapping("/personal-info")
    public ApiResponse<Map<String, UUID>> updateTeacherInfo(@UserId UUID teacherId,
                                                            @RequestBody @Valid TeacherPATCHInfo patch) {
        UUID updatedTeacherId = teacherCommandService.updateInfo(teacherId, patch);
        return ApiResponse.ok(Map.of("teacherId", updatedTeacherId));
    }
}

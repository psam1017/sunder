package psam.portfolio.sunder.english.domain.teacher.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.teacher.exception.TeacherAccessDeniedException;
import psam.portfolio.sunder.english.domain.teacher.model.request.*;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherCommandService;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherQueryService;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/api/teachers")
@RestController
public class TeacherController {

    private final TeacherCommandService teacherCommandService;
    private final TeacherQueryService teacherQueryService;

    /**
     * 선생님 등록 서비스
     * 등록 직후는 PENDING 상태이며, 본인인증을 통해 ACTIVE 상태로 변경할 수 있다.
     *
     * @param teacherId 학원에 등록할 선생님의 아이디
     * @param post      등록할 선생님 정보
     * @return 등록에 성공한 선생님 아이디
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    @PostMapping("")
    public ApiResponse<Map<String, Boolean>> registerTeacher(@UserId UUID teacherId,
                                                             @RequestBody @Valid TeacherPOST post) {
        teacherCommandService.register(teacherId, post);
        return ApiResponse.ok(Map.of("registered", true));
    }

    /**
     * 선생님 가입 승인 서비스
     * 승인 이후의 상태는 학원장의 상태를 따라간다.
     *
     * @param teacherId 인증할 선생님 아이디
     * @return 선생님 가입 승인 여부
     */
    @GetMapping("/{teacherId}/verify")
    public ApiResponse<Map<String, Boolean>> verifyTeacher(@PathVariable String teacherId) {
        boolean result = teacherCommandService.verifyTeacher(UUID.fromString(teacherId));
        return ApiResponse.ok(Map.of("verified", result));
    }

    /**
     * 선생님 목록 조회 서비스. 페이징 없이 모든 선생 목록을 반환한다.
     *
     * @param userId 사용자 아이디
     * @param cond   선생님 목록 조회 조건
     * @return 선생님 목록
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("")
    public ApiResponse<Map<String, List<?>>> getTeacherList(@UserId UUID userId,
                                                            @ModelAttribute TeacherPageSearchCond cond) {
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
    public ApiResponse<?> getTeacherDetail(@UserId UUID userId,
                                           @PathVariable UUID teacherId) {
        Object teacher = teacherQueryService.getDetail(userId, teacherId);
        return ApiResponse.ok(teacher);
    }

    /**
     * 선생님 상태 변경 서비스. 탈퇴 상태로 변경도 포함한다.
     *
     * @param directorId 학원장 아이디
     * @param teacherId  상태를 변경할 선생님 아이디
     * @param patch      변경할 상태 - 가능한 값 : ACTIVE, WITHDRAWN
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
     * 선생님 개인정보 수정 서비스. 자기 자신의 정보만 수정할 수 있다.
     *
     * @param teacherId 선생님 아이디
     * @param patch     변경할 개인정보
     * @return 개인정보 변경 완료된 선생님 아이디
     */
    @Secured("ROLE_TEACHER")
    @PatchMapping("/{teacherId}/personal-info")
    public ApiResponse<Map<String, UUID>> updateTeacherInfo(@UserId UUID userId,
                                                            @PathVariable UUID teacherId,
                                                            @RequestBody @Valid TeacherPATCHInfo patch) {
        if (!Objects.equals(userId, teacherId)) {
            throw new TeacherAccessDeniedException();
        }
        UUID updatedTeacherId = teacherCommandService.updateInfo(teacherId, patch);
        return ApiResponse.ok(Map.of("teacherId", updatedTeacherId));
    }
}

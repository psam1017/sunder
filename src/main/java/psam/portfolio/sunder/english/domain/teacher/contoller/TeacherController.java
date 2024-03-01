package psam.portfolio.sunder.english.domain.teacher.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHInfo;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherSearchCond;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHStatus;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherCommandService;
import psam.portfolio.sunder.english.domain.teacher.service.TeacherQueryService;

import java.util.List;
import java.util.Map;
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
     * @param post 등록할 선생님 정보
     * @return 선생님 아이디
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    @PostMapping("")
    public ApiResponse<Map<String, UUID>> registerTeacher(@UserId UUID teacherId,
                                                          @RequestBody @Valid TeacherPOST post) {
        UUID newTeacherId = teacherCommandService.register(teacherId, post);
        return ApiResponse.ok(Map.of("teacherId", newTeacherId));
    }

    /**
     * 선생님 목록 조회 서비스
     *
     * @param userId      사용자 아이디
     * @param page        페이지 번호
     * @param size        페이지 사이즈
     * @param prop        정렬할 컬럼
     * @param dir         정렬 방향
     * @param status      선생님 상태
     * @param studentName 학생 이름
     * @return 선생님 목록
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    @GetMapping("/list")
    public ApiResponse<Map<String, List<?>>> getTeacherList(@UserId UUID userId,
                                                            @RequestParam Integer page,
                                                            @RequestParam Integer size,
                                                            @RequestParam(required = false) String prop,
                                                            @RequestParam(required = false) String dir,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(required = false) String studentName
    ) {
        TeacherSearchCond buildCond = TeacherSearchCond.builder()
                .page(page)
                .size(size)
                .prop(prop)
                .order(dir)
                .status(status)
                .studentName(studentName)
                .build();
        List<?> teachers = teacherQueryService.getList(userId, buildCond);
        return ApiResponse.ok(Map.of("teachers", teachers));
    }

    /**
     * 선생님 상세 정보 조회 서비스
     *
     * @param userId 사용자 아이디
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
     * @param patch      변경할 상태
     * @return 선생님 아이디와 변경된 상태
     */
    @Secured("ROLE_DIRECTOR")
    @PatchMapping("/{teacherId}/status")
    public ApiResponse<Map<String, String>> changeTeacherStatus(@UserId UUID directorId,
                                                                @PathVariable UUID teacherId,
                                                                @RequestBody @Valid TeacherPATCHStatus patch) {
        UserStatus status = teacherCommandService.changeStatus(directorId, patch);
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
     * @param patch      변경할 권한
     * @return 선생님 아이디와 변경 완료된 권한 목록
     */
    @Secured("ROLE_DIRECTOR")
    @PatchMapping("/{teacherId}/roles")
    public ApiResponse<Map<String, Object>> changeTeacherRole(@UserId UUID directorId,
                                                              @PathVariable UUID teacherId,
                                                              @RequestBody @Valid TeacherPATCHStatus patch) {
        List<RoleName> roles = teacherCommandService.changeRoles(directorId, patch);
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
    public ApiResponse<Map<String, UUID>> updateInfo(@UserId UUID teacherId,
                                                     @RequestBody @Valid TeacherPATCHInfo patch) {
        UUID updatedTeacherId = teacherCommandService.updateInfo(teacherId, patch);
        return ApiResponse.ok(Map.of("teacherId", updatedTeacherId));
    }
}

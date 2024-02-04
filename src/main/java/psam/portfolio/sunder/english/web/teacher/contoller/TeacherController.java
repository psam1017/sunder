package psam.portfolio.sunder.english.web.teacher.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;
import psam.portfolio.sunder.english.web.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.web.teacher.service.TeacherCommandService;
import psam.portfolio.sunder.english.web.teacher.service.TeacherQueryService;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/teacher")
@RestController
public class TeacherController {

    private final TeacherCommandService teacherCommandService;
    private final TeacherQueryService teacherQueryService;

    /**
     * 선생 등록 서비스
     *
     * @param teacherId 학원에 등록할 선생의 아이디
     * @param post 등록할 선생 정보
     * @return 선생 아이디
     */
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    @PostMapping("")
    public ApiResponse<Map<String, UUID>> registerTeacher(@UserId UUID teacherId,
                                                          @RequestBody @Valid TeacherPOST post) {
        UUID newTeacherId = teacherCommandService.register(teacherId, post);
        return ApiResponse.ok(Map.of("teacherId", newTeacherId));
    }

    /* todo
    POST /api/teacher/new
    선생님 가입 서비스

    POST /api/teacher/verify?token={token}
    이메일로 보낸 인증토큰이 포함된 링크를 클릭하여 인증하는 서비스

    PUT /api/teacher/status
    선생님 상태 변경 서비스

    PUT /api/teacher/info
    선생님 정보 수정 서비스

    PUT /api/teacher/password
    선생님 비밀번호 변경 서비스

    POST /api/teacher/password/temporary
    선생님 임시 비밀번호 발급 서비스

    PUT /api/teacher/withdraw
    선생님 탈퇴 서비스

    GET /api/teacher/detail?teacherUuid={teacherUuid}
    선생님 상세 정보 조회 서비스

    GET /api/teacher/list?academyUuid={academyUuid}&status={status}&studentName={studentName}&sort={status|teacherName}
    선생님 목록 조회 서비스
     */
}

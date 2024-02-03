package psam.portfolio.sunder.english.web.teacher.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/teacher")
@RestController
public class TeacherController {

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

    GET /api/teacher/detail?teacherUuid={teacherUuid}&select={lesson}
    선생님 상세 정보 조회 서비스

    GET /api/teacher/list?academyUuid={academyUuid}&status={status}&lessonDay={lessonDay}&lessonTime={lessonTime}&studentName={studentName}&sort={status|lessonDay|lessonTime|grade|teacherName}
    선생님 목록 조회 서비스
     */
}

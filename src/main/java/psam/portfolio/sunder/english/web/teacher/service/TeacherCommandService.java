package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TeacherCommandService {

    // TODO: 2024-01-25 임시 선생님, 임시 학생 생성. 임시 선생과 학생은 공통(기본)자료를 사용할 수 있고, 3번까지 교재 파일을 올릴 수 있다.
    // TODO: 2024-01-23 EmailUtils
    // TODO: 2024-01-23 PasswordUtils

    private final TeacherCommandRepository teacherCommandRepository;
    private final TeacherQueryRepository teacherQueryRepository;

    /*
    todo
    POST /api/teacher/new
    선생님 가입 서비스 - EmailUtils, PasswordUtils 필요. academy uuid 를 알고 있으므로 이미 director 로부터 academy 정보를 전달 받은 인증된 사용자라는 것으로 간주한다.

    POST /api/teacher/verify?token={token}
    이메일로 보낸 인증토큰이 포함된 링크를 클릭하여 인증하는 서비스

    PUT /api/teacher/status
    선생님 상태 변경 서비스

    PUT /api/teacher/info
    선생님 정보 수정 서비스

    PUT /api/teacher/password
    선생님 비밀번호 변경 서비스 - PasswordUtils 필요

    POST /api/teacher/password/temporary
    선생님 임시 비밀번호 발급 서비스 - PasswordUtils 필요. EmailUtils 필요?

    PUT /api/teacher/withdraw
    선생님 탈퇴 서비스
     */
}

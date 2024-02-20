package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.web.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TeacherCommandService {

    private final TemplateEngine templateEngine;
    private final MailUtils mailUtils;
    private final PasswordUtils passwordUtils;

    private final TeacherCommandRepository teacherCommandRepository;
    private final TeacherQueryRepository teacherQueryRepository;

    /**
     * 선생 등록 서비스
     *
     * @param teacherId 학원에 등록할 선생의 아이디
     * @param post      등록할 선생 정보
     * @return 선생 아이디
     */
    public UUID register(UUID teacherId, TeacherPOST post) {
        return null;
    }

    /*
    todo 유저 기능 포함

    3개월 지나면 비밀번호 바꾸기
    lastPasswordChangeDateTime 갱신하기

    어드민 - 회원관리, 권한관리

    등록 후 한달이 지나면 TRIAL_END
    TRIAN_END 에서 ACTIVE 로 변경하기 -> 원장만 가능. 변경 시 모든 본인, 모든 선생, 모든 학생의 롤과 상태를 변경

    // 등록 후 한달이 지나면 TRIAL 에서 TRIAL_END 로 변경 => @Scheduled 로 처리

    PATCH /api/teacher/status
    선생님 상태 변경 서비스. TRIAN_END -> ACTIVE 포함

    PATCH /api/teacher/info
    선생님 정보 수정 서비스

    PATCH /api/teacher/password
    선생님 비밀번호 변경 서비스 - PasswordUtils 필요

    POST /api/teacher/password/temporary
    선생님 임시 비밀번호 발급 서비스 - PasswordUtils 필요. EmailUtils 필요?

    PATCH /api/teacher/withdraw
    선생님 탈퇴 서비스
     */

    private String setTempPasswordMailText(String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);
        return templateEngine.process("mail-temp-password", context);
    }
}

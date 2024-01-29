package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;

@RequiredArgsConstructor
@Transactional(readOnly = false)
@Service
public class TeacherCommandService {

    private final TemplateEngine templateEngine;
    private final MailUtils mailUtils;
    private final PasswordUtils passwordUtils;

    private final TeacherCommandRepository teacherCommandRepository;
    private final TeacherQueryRepository teacherQueryRepository;

    /*
    todo
    // 등록 후 한달이 지나면 TRIAL 에서 TRIAL_END 로 변경 => @Scheduled 로 처리

    POST /api/teacher/verify?token={token}
    이메일로 보낸 인증토큰이 포함된 링크를 클릭하여 인증하는 서비스. PENDING ->TRIAL 로 변경

    PUT /api/teacher/status
    선생님 상태 변경 서비스. TRIAN_END -> ACTIVE 포함

    PUT /api/teacher/info
    선생님 정보 수정 서비스

    PUT /api/teacher/password
    선생님 비밀번호 변경 서비스 - PasswordUtils 필요

    POST /api/teacher/password/temporary
    선생님 임시 비밀번호 발급 서비스 - PasswordUtils 필요. EmailUtils 필요?

    PUT /api/teacher/withdraw
    선생님 탈퇴 서비스
     */

    private String setTempPasswordMailText(String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);
        return templateEngine.process("mail-temp-password", context);
    }
}

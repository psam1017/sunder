package psam.portfolio.sunder.english.domain.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHInfo;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHStatus;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;

import java.util.List;
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

    // TODO

    /**
     * 선생님 등록 서비스
     *
     * @param teacherId 학원에 등록할 선생의 아이디
     * @param post      등록할 선생님 정보
     * @return 선생님 아이디
     */
    public UUID register(UUID teacherId, TeacherPOST post) {
        return null;
    }

    /**
     * 선생님 상태 변경 서비스
     *
     * @param directorId 학원장 아이디
     * @param patch      변경할 선생님 상태
     * @return 변경된 선생님 상태
     */
    public UserStatus changeStatus(UUID directorId, TeacherPATCHStatus patch) {
        return null;
    }

    /**
     * 선생님 권한 변경 서비스
     *
     * @param directorId 학원장 아이디
     * @param patch      변경할 선생님 권한 목록
     * @return 변경된 선생님 권한 목록
     */
    public List<RoleName> changeRoles(UUID directorId, TeacherPATCHStatus patch) {
        return null;
    }

    /**
     * 선생님 개인정보 변경 서비스
     *
     * @param teacherId 선생님 아이디
     * @param patch     변경할 개인정보
     * @return 개인정보 변경 완료된 선생님 아이디
     */
    public UUID updateInfo(UUID teacherId, TeacherPATCHInfo patch) {
        return null;
    }

    private String setTempPasswordMailText(String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);
        return templateEngine.process("mail-temp-password", context);
    }
}

package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.infrastructure.mail.MailFailException;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.exception.DuplicateAcademyException;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyDirectorPOST.AcademyPOST;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyDirectorPOST.DirectorPOST;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.web.user.model.entity.UserRole;
import psam.portfolio.sunder.english.web.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.web.user.repository.UserRoleCommandRepository;

import java.util.Locale;
import java.util.UUID;

import static psam.portfolio.sunder.english.web.teacher.model.entity.QAcademy.academy;
import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.PENDING;
import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.TRIAL;
import static psam.portfolio.sunder.english.web.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional
@Service
public class AcademyCommandService {

    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final MailUtils mailUtils;
    private final PasswordUtils passwordUtils;

    private final AcademyCommandRepository academyCommandRepository;
    private final AcademyQueryRepository academyQueryRepository;
    private final TeacherCommandRepository teacherCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserRoleCommandRepository userRoleCommandRepository;

    /**
     * 학원을 등록하는 서비스
     * @param academyPOST 학원 등록 정보
     * @param directorPOST 학원장 등록 정보
     * @return 학원장의 uuid
     */
    public String registerDirectorWithAcademy(
        AcademyPOST academyPOST,
        DirectorPOST directorPOST
    ) {
        // 우선 academy name, phone, email 에서 중복 체크. 상태는 상관 없음
        academyQueryRepository.findOne(
                academy.name.eq(academyPOST.getName())
                .or(academy.phone.eq(academyPOST.getPhone()))
                .or(academy.email.eq(academyPOST.getEmail())),
                academy.status.ne(AcademyStatus.PENDING)
        ).ifPresent(academy -> {
            throw new DuplicateAcademyException();
        });

        // teacher 의 loginId, email, phone 에서 중복 체크. userStatusNotIn(PENDING), userEmailVerifiedEq(true)
        userQueryRepository.findOne(
                user.loginId.eq(directorPOST.getLoginId())
                .or(user.email.eq(directorPOST.getEmail()))
                .or(user.phone.eq(directorPOST.getPhone())),
                user.status.ne(PENDING),
                user.emailVerified.eq(true)
        ).ifPresent(user -> {
            throw new DuplicateUserException();
        });

        // academy 생성
        Academy saveAcademy = academyCommandRepository.save(academyPOST.toEntity());

        // passwordUtils 로 loginPw 암호화
        String encodeLoginPw = passwordUtils.encode(directorPOST.getLoginPw());

        // teacher 생성
        Teacher saveDirector = teacherCommandRepository.save(directorPOST.toEntity(saveAcademy, encodeLoginPw));

        // UserRole 에 ROLE_DIRECTOR 로 생성
        UserRole buildUserRole = UserRole.builder()
                .user(saveDirector)
                .roleName(RoleName.ROLE_TRIAL_DIRECTOR)
                .build();
        userRoleCommandRepository.save(buildUserRole);

        // mailUtils 로 verification mail 발송
        boolean mailResult = mailUtils.sendMail(
                saveDirector.getEmail(),
                messageSource.getMessage("mail.verification.academy.subject", null, Locale.getDefault()),
                setVerificationMailText(saveAcademy)
        );
        if (!mailResult) {
            throw new MailFailException();
        }

        return saveDirector.getUuid().toString();
    }

    // UUID from Academy
    private String setVerificationMailText(Academy academy) {
        String url = messageSource.getMessage("mail.verification.academy.url", new Object[]{academy.getUuid()}, Locale.getDefault());

        Context context = new Context();
        context.setVariable("url", url);
        return templateEngine.process("mail-verification", context);
    }

    /**
     * 학원 인증 서비스
     * @param academyUuid 학원 uuid
     * @return 인증 성공 여부
     */
    public boolean verifyAcademy(UUID academyUuid) {
        Academy getAcademy = academyQueryRepository.getById(academyUuid);

        // 학원 인증은 최초 한 번만 가능
        if (getAcademy.isVerified()) {
            return false;
        }
        getAcademy.verify();

        // 인증 시점에는 모든 선생(=학원장)의 상태를 인증함으로 변경한다.
        getAcademy.getTeachers().forEach(teacher -> {
            teacher.startTrial();
            teacher.verifyEmail(true);
        });

        return true;
    }

    /* todo
    POST /api/academy/teacher/new - @Secured("ROLE_DIRECTOR")
    학원에서 선생님을 바로 등록하는 서비스

    PUT /api/academy/teacher/roles
    선생님 권한 변경 서비스

    PUT /api/academy/info - @Secured("ROLE_DIRECTOR")
    학원 정보 수정 서비스
     */
}

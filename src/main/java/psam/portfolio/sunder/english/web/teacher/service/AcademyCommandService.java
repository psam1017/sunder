package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.infrastructure.mail.MailFailException;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.exception.DuplicateAcademyException;
import psam.portfolio.sunder.english.web.teacher.exception.OneParamToCheckAcademyDuplException;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyRegistration;
import psam.portfolio.sunder.english.web.teacher.model.request.DirectorRegistration;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.web.user.model.UserRole;
import psam.portfolio.sunder.english.web.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.web.user.repository.UserRoleCommandRepository;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.web.teacher.model.entity.QAcademy.academy;
import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.PENDING;
import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.TRIAL;
import static psam.portfolio.sunder.english.web.user.model.QUser.user;

@RequiredArgsConstructor
@Transactional
@Service
public class AcademyCommandService {

    // TODO: 2024-01-30 api 만들고, docs

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
     * 학원 등록 시 중복 체크 서비스
     * @param name 학원 이름
     * @param phone 학원 전화번호
     * @param email 학원 이메일
     * @return 중복 여부
     */
    public boolean checkDuplication(String name, String phone, String email) {
        boolean hasName = StringUtils.hasText(name);
        boolean hasPhone = StringUtils.hasText(phone);
        boolean hasEmail = StringUtils.hasText(email);

        if (!hasOnlyOne(hasName, hasPhone, hasEmail)) {
            throw new OneParamToCheckAcademyDuplException();
        }

        Optional<Academy> optAcademy = Optional.empty();
        if (hasName) {
            optAcademy = academyQueryRepository.findOne(
                    academy.name.eq(name),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        } else if (hasPhone) {
            optAcademy = academyQueryRepository.findOne(
                    academy.phone.eq(phone),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        } else if (hasEmail) {
            optAcademy = academyQueryRepository.findOne(
                    academy.email.eq(email),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        }
        return optAcademy.isEmpty();
    }

    private static boolean hasOnlyOne(boolean a, boolean b, boolean c) {
        return a ^ b ^ c && !(a && b && c);
    }

    /**
     * 학원을 등록하는 서비스
     * @param registerAcademy 학원 등록 정보
     * @param registerDirector 학원장 등록 정보
     * @return 학원장의 uuid
     */
    public String registerDirectorWithAcademy(
        AcademyRegistration registerAcademy,
        DirectorRegistration registerDirector
    ) {
        // 우선 academy name, phone, email 에서 중복 체크. 상태는 상관 없음
        academyQueryRepository.findOne(
                academy.name.eq(registerAcademy.getName())
                .or(academy.phone.eq(registerAcademy.getPhone()))
                .or(academy.email.eq(registerAcademy.getEmail())),
                academy.status.ne(AcademyStatus.PENDING)
        ).ifPresent(academy -> {
            throw new DuplicateAcademyException();
        });

        // teacher 의 loginId, email, phone 에서 중복 체크. userStatusNotIn(PENDING, TRIAL), userEmailVerifiedEq(true)
        userQueryRepository.findOne(
                user.loginId.eq(registerDirector.getLoginId())
                .or(user.email.eq(registerDirector.getEmail()))
                .or(user.phone.eq(registerDirector.getPhone())),
                user.status.notIn(PENDING, TRIAL),
                user.emailVerified.eq(true)
        ).ifPresent(user -> {
            throw new DuplicateUserException();
        });

        // academy 생성
        Academy saveAcademy = academyCommandRepository.save(registerAcademy.toEntity());

        // passwordUtils 로 loginPw 암호화
        String encodeLoginPw = passwordUtils.encode(registerDirector.getLoginPw());

        // teacher 생성
        Teacher saveDirector = teacherCommandRepository.save(registerDirector.toEntity(saveAcademy, encodeLoginPw));

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

    /*
    POST /api/academy/teacher/new - @Secured("ROLE_DIRECTOR")
    학원에서 선생님을 바로 등록하는 서비스

    PUT /api/academy/teacher/roles
    선생님 권한 변경 서비스

    PUT /api/academy/info - @Secured("ROLE_DIRECTOR")
    학원 정보 수정 서비스
     */
}

/*
package psam.portfolio.sunder.english.web.teacher.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import psam.portfolio.sunder.english.global.enumpattern.EnumPattern;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.model.UserRole;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DirectorRegistration {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$")
    private String loginId;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$")
    private String loginPw;

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,10}$")
    private String name;

    @Email
    private String email;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;

    private String street;
    private String detail;
    @Pattern(regexp = "^[0-9]{5}$")
    private String postalCode;

    public Teacher toEntity(Academy academy) {
        return Teacher.builder()
                .loginId(loginId)
                .loginPw(loginPw)
                .name(name)
                .email(email)
                .emailVerified(false)
                .phone(phone)
                .address(Address.builder()
                        .street(street)
                        .detail(detail)
                        .postalCode(postalCode)
                        .build())
                .status(UserStatus.PENDING)
                .academy(academy)
                .build();
    }
}

package psam.portfolio.sunder.english.web.teacher.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AcademyRegistration {

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,30}$")
    private String name;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;

    @Email
    private String email;

    private String street;
    private String detail;
    @Pattern(regexp = "^[0-9]{5}$")
    private String postalCode;

    @NotNull
    private Boolean openToPublic;

    public Academy toEntity() {
        return Academy.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .openToPublic(openToPublic)
                .address(Address.builder()
                        .street(street)
                        .detail(detail)
                        .postalCode(postalCode)
                        .build())
                .status(AcademyStatus.USING)
                .build();
    }
}

 */

/*


    private final UserQueryRepository userQueryRepository;

     * GET /api/user/check-dupl?loginId={loginId}&email={email}&phone={phone}
     * 아이디, 이메일, 연락처 중복 체크 서비스
     * - 단, PENDING 과 TRIAL 은 중복체크에서 제외
     * - 아직 email 인증을 하지 않은 경우도 중복체크에서 제외
public boolean checkDuplication(String loginId, String email, String phone) {
    boolean hasLoginId = StringUtils.hasText(loginId);
    boolean hasEmail = StringUtils.hasText(email);
    boolean hasPhone = StringUtils.hasText(phone);

    if (!hasOnlyOne(hasLoginId, hasEmail, hasPhone)) {
        throw new OneParamToCheckDuplException();
    }

    Optional<User> optUser = Optional.empty();
    if (hasLoginId) {
        optUser = userQueryRepository.findOne(
                user.loginId.eq(loginId),
                userStatusNotIn(PENDING, TRIAL),
                userEmailVerifiedEq(true));
    } else if (hasEmail) {
        optUser = userQueryRepository.findOne(
                user.email.eq(email),
                userStatusNotIn(PENDING, TRIAL),
                userEmailVerifiedEq(true));
    } else if (hasPhone) {
        optUser = userQueryRepository.findOne(
                user.phone.eq(phone),
                userStatusNotIn(PENDING, TRIAL),
                userEmailVerifiedEq(true));
    }
    return optUser.isEmpty();
}

    private static boolean hasOnlyOne(boolean a, boolean b, boolean c) {
        return a ^ b ^ c && !(a && b && c);
    }

    private static BooleanExpression userStatusNotIn(UserStatus... statuses) {
        return user.status.notIn(statuses);
    }

    private static BooleanExpression userEmailVerifiedEq(boolean verified) {
        return user.emailVerified.eq(verified);
    }
 */
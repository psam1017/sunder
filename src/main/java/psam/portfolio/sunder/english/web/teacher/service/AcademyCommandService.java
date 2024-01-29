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
import psam.portfolio.sunder.english.web.teacher.exception.DuplicateAcademyException;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyRegistration;
import psam.portfolio.sunder.english.web.teacher.model.request.DirectorRegistration;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.exception.DuplicateUserInfoException;
import psam.portfolio.sunder.english.web.user.model.UserRole;
import psam.portfolio.sunder.english.web.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.web.user.repository.UserRoleCommandRepository;

import java.util.Locale;

import static psam.portfolio.sunder.english.web.teacher.model.entity.QAcademy.academy;
import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.PENDING;
import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.TRIAL;
import static psam.portfolio.sunder.english.web.user.model.QUser.user;

@RequiredArgsConstructor
@Transactional(readOnly = false)
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

    /*
    todo 학원의 name, phone, email 중복 체크하는 서비스
     */

    /*
    todo test, 주석 정리, 메모 추가
    POST /api/academy/new
    ROLE_DIRECTOR 권한을 가진 사용자가 학원을 생성하는 서비스 - EmailUtils, PasswordUtils 필요.
    학원 및 원장 생성 서비스 - EmailUtils, PasswordUtils 필요. Teacher 와 Academy 를 같이 활성화
     */
    public String registerDirectorWithAcademy(
        AcademyRegistration registerAcademy,
        DirectorRegistration registerDirector
    ) {
        // 우선 academy name, phone, email 에서 중복 체크. 상태는 상관 없음
        academyQueryRepository.findOne(
                academy.name.eq(registerAcademy.getName())
                .or(academy.phone.eq(registerAcademy.getPhone()))
                .or(academy.email.eq(registerAcademy.getEmail()))
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
            throw new DuplicateUserInfoException();
        });

        // academy 생성
        Academy saveAcademy = academyCommandRepository.save(registerAcademy.toEntity());

        // passwordUtils 로 loginPw 암호화
        String encodeLoginPw = passwordUtils.encode(registerDirector.getLoginPw());

        // teacher 생성
        Teacher saveDirector = registerDirector.toEntity(saveAcademy, encodeLoginPw);

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

    // uuid from Academy
    private String setVerificationMailText(Academy academy) {
        String url = messageSource.getMessage("mail.verification.academy.url", new Object[]{academy.getUuid()}, Locale.getDefault());

        Context context = new Context();
        context.setVariable("url", url);
        return templateEngine.process("mail-verification", context);
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
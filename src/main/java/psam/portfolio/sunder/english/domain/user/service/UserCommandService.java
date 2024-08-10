package psam.portfolio.sunder.english.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.domain.user.exception.LoginFailException;
import psam.portfolio.sunder.english.domain.user.exception.PasswordMismatchException;
import psam.portfolio.sunder.english.domain.user.exception.UnauthorizedToChangePasswordException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginPwForm;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional
@Service
public class UserCommandService {

    private final UserQueryRepository userQueryRepository;

    private final MailUtils mailUtils;
    private final MessageSource messageSource;
    private final TemplateEngine templateEngine;
    private final PasswordUtils passwordUtils;

    private static final int PASSWORD_CHANGE_ALLOWED_AMOUNT = 3;

    /**
     * 비밀번호 변경 지연 서비스
     *
     * @param userId 비밀번호 변경 지연을 요청한 사용자 아이디
     * @return 지연 성공 여부
     */
    public boolean alterPasswordChangeLater(UUID userId) {
        User user = userQueryRepository.getById(userId);
        user.setLastPasswordChangeDateTime(LocalDateTime.now());
        return true;
    }

    /**
     * 비밀번호 새로 발급하는 서비스
     *
     * @param userInfo 비밀번호를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    public boolean issueNewPassword(LostLoginPwForm userInfo) {
        Optional<User> optUser = userQueryRepository.findOne(
                user.loginId.eq(userInfo.getLoginId()),
                user.email.eq(userInfo.getEmail()),
                user.name.eq(userInfo.getName())
        );

        if (optUser.isPresent()) {
            String tempPassword = createTempPassword();
            String encodedPassword = passwordUtils.encode(tempPassword);

            User getUser = optUser.get();
            getUser.setLoginPw(encodedPassword);
            getUser.setLastPasswordChangeDateTime(LocalDateTime.now());

            return mailUtils.sendMail(
                    getUser.getEmail(),
                    messageSource.getMessage("mail.temp-password.subject", null, Locale.getDefault()),
                    setIssueTempPasswordMailText(tempPassword)
            );
        }
        return false;
    }

    private String setIssueTempPasswordMailText(String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);
        return templateEngine.process("mail-temp-password", context);
    }

    // 영문 3글자 + 숫자 3글자 + 특수문자 3글자
    private String createTempPassword() {
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int random = (int) (Math.random() * 26) + 65; // A ~ Z
            tempPassword.append((char) random);
        }

        for (int i = 0; i < 3; i++) {
            int random = (int) (Math.random() * 10);
            tempPassword.append(random);
        }

        for (int i = 0; i < 3; i++) {
            int random = (int) (Math.random() * 8);
            tempPassword.append("!@#$%^&*".charAt(random));
        }
        return tempPassword.toString();
    }

    /**
     * 비밀번호 변경을 위해 재인증하는 서비스
     *
     * @param userId 비밀번호 변경 요청을 하려는 사용자 아이디
     * @param loginPw 기존 비밀번호
     * @return 비밀번호 변경이 유효한 시간
     */
    public int authenticateToChangePassword(UUID userId, String loginPw) {
        User getUser = userQueryRepository.getById(userId);
        if (!passwordUtils.matches(loginPw, getUser.getLoginPw())) {
            throw new PasswordMismatchException();
        }

        LocalDateTime passwordChangeAllowedDateTime = LocalDateTime.now().plusMinutes(PASSWORD_CHANGE_ALLOWED_AMOUNT);
        getUser.setPasswordChangeAllowedDateTime(passwordChangeAllowedDateTime);
        return PASSWORD_CHANGE_ALLOWED_AMOUNT;
    }

    /**
     * 비밀번호 변경 서비스
     *
     * @param newLoginPw 새로운 패스워드
     * @return 패스워드 변경 성공 여부
     */
    public boolean changePassword(UUID userId, String newLoginPw) {
        User user = userQueryRepository.getById(userId);
        if (!user.isPasswordChangeAllowed()) {
            throw new UnauthorizedToChangePasswordException();
        }

        user.setLoginPw(passwordUtils.encode(newLoginPw));
        user.setLastPasswordChangeDateTime(LocalDateTime.now());
        return true;
    }
}

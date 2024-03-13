package psam.portfolio.sunder.english.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLostPw;
import psam.portfolio.sunder.english.domain.user.repository.UserCommandRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional
@Service
public class UserCommandService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

    private final MailUtils mailUtils;
    private final MessageSource messageSource;
    private final TemplateEngine templateEngine;
    private final PasswordUtils passwordUtils;

    // TODO endTrial 및 usercontroller 의 login 이하 모두 테스트 및 문서 생성

    /**
     * POST /api/user/issue-temp-password
     * @param userInfo 비밀번호를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    public boolean issueTempPassword(UserPOSTLostPw userInfo) {
        Optional<User> optUser = userQueryRepository.findOne(
                user.loginId.eq(userInfo.getLoginId()),
                user.email.eq(userInfo.getEmail()),
                user.name.eq(userInfo.getName())
        );

        if (optUser.isPresent()) {
            User getUser = optUser.get();
            return mailUtils.sendMail(
                    getUser.getEmail(),
                    messageSource.getMessage("mail.login-id.subject", null, Locale.getDefault()),
                    setIssueTempPasswordMailText(getUser)
            );
        }
        return false;
    }

    private String setIssueTempPasswordMailText(User user) {
        Context context = new Context();
        String tempPassword = createTempPassword();
        String encodedPassword = passwordUtils.encode(tempPassword);
        user.setLoginPw(encodedPassword);

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
     * POST /api/user/change-password
     * @param loginPw 새로운 패스워드
     * @return 패스워드 변경 성공 여부
     */
    public boolean changePassword(String loginPw) {
        return false;
    }

    /**
     * POST /api/user/delay-password-change
     * @param userId 비밀번호 변경 지연을 요청한 사용자 아이디
     * @return 지연 성공 여부
     */
    public boolean delayPasswordChange(UUID userId) {
        return false;
    }
}

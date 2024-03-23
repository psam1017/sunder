package psam.portfolio.sunder.english.domain.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.LoginFailException;
import psam.portfolio.sunder.english.domain.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginIdForm;
import psam.portfolio.sunder.english.domain.user.model.response.LoginResult;
import psam.portfolio.sunder.english.domain.user.model.response.TokenRefreshResponse;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.user.enumeration.UserStatus.*;
import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;
import static psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final JwtUtils jwtUtils;
    private final PasswordUtils passwordUtils;
    private final MailUtils mailUtils;
    private final MessageSource messageSource;
    private final TemplateEngine templateEngine;

    /**
     * GET /api/user/check-dupl?loginId={loginId}&email={email}&phone={phone}
     * 아이디, 이메일, 연락처 중복 체크 서비스
     * - 단, PENDING 과 TRIAL 은 중복체크에서 제외
     * - 아직 email 인증을 하지 않은 경우도 중복체크에서 제외
     *
     * @param loginId 아이디
     * @param email   이메일
     * @param phone   연락처
     */
    public boolean checkDuplication(String loginId, String email, String phone) {
        boolean hasLoginId = StringUtils.hasText(loginId);
        boolean hasEmail = StringUtils.hasText(email);
        boolean hasPhone = StringUtils.hasText(phone);

        if (!hasOnlyOne(hasLoginId, hasEmail, hasPhone)) {
            throw new OneParamToCheckUserDuplException();
        }

        Optional<User> optUser = Optional.empty();
        if (hasLoginId) {
            optUser = userQueryRepository.findOne(
                    user.loginId.eq(loginId),
                    userStatusNe(PENDING),
                    userEmailVerifiedEq(true));
        } else if (hasEmail) {
            optUser = userQueryRepository.findOne(
                    user.email.eq(email),
                    userStatusNe(PENDING),
                    userEmailVerifiedEq(true));
        } else if (hasPhone) {
            optUser = userQueryRepository.findOne(
                    user.phone.eq(phone),
                    userStatusNe(PENDING),
                    userEmailVerifiedEq(true));
        }
        return optUser.isEmpty();
    }

    private static boolean hasOnlyOne(boolean a, boolean b, boolean c) {
        return a ^ b ^ c && !(a && b && c);
    }

    private static BooleanExpression userStatusNe(UserStatus userStatus) {
        return user.status.ne(userStatus);
    }

    private static BooleanExpression userEmailVerifiedEq(boolean verified) {
        return user.emailVerified.eq(verified);
    }

    /**
     * POST /api/user/login
     * 로그인 서비스
     *
     * @param loginLoginInfo 로그인 정보
     * @return 인증한 사용자에게 발급하는 토큰과 함께 비밀번호 변경 알림 여부를 반환
     */
    public LoginResult login(UserLoginForm loginLoginInfo) {

        User getUser = userQueryRepository.findOne(
                user.loginId.eq(loginLoginInfo.getLoginId())
        ).orElseThrow(LoginFailException::new);

        if (!passwordUtils.matches(loginLoginInfo.getLoginPw(), getUser.getLoginPw())) {
            throw new LoginFailException();
        }

        UserStatus userStatus = getUser.getStatus();
        if (userStatus != ACTIVE && userStatus != TRIAL) {
            throw new ApiException() {
                @Override
                public ApiResponse<?> initialize() {
                    return ApiResponse.error(ApiStatus.ILLEGAL_STATUS, User.class, userStatus.toString(), "로그인할 수 없는 상태입니다. [" + userStatus + "]");
                }
            };
        }

        // 토큰 만료 시간은 3시간
        String token = jwtUtils.generateToken(getUser.getUuid().toString(), 1000 * 60 * 60 * 3);
        return new LoginResult(token, getUser.isPasswordExpired());
    }

    /**
     * POST /api/user/new-token
     * 토큰 재발급 서비스
     *
     * @param userId 사용자 아이디
     * @return 새로 발급한 토큰
     */
    public TokenRefreshResponse refreshToken(UUID userId) {
        // 토큰 만료 시간은 3시간
        String token = jwtUtils.generateToken(userId.toString(), 1000 * 60 * 60 * 3);
        return new TokenRefreshResponse(token);
    }

    /**
     * POST /api/user/find-login-id
     * 로그인 아이디를 분실한 경우 가입 여부를 확인하는 서비스
     *
     * @param userInfo 로그인 아이디를 분실한 가입자 정보
     * @return 이메일 발송 성공 여부
     */
    public boolean findLoginId(LostLoginIdForm userInfo) {
        Optional<User> optUser = userQueryRepository.findOne(
                user.email.eq(userInfo.getEmail()),
                user.name.eq(userInfo.getName())
        );

        if (optUser.isPresent()) {
            User getUser = optUser.get();
            return mailUtils.sendMail(
                    getUser.getEmail(),
                    messageSource.getMessage("mail.login-id.subject", null, Locale.getDefault()),
                    setFindLoginIdMailText(getUser)
            );
        }
        return false;
    }

    private String setFindLoginIdMailText(User user) {
        Context context = new Context();
        context.setVariable("loginId", user.getLoginId());
        return templateEngine.process("mail-login-id", context);
    }

    /**
     * POST /api/user/request-password-change
     *
     * @param userId 비밀번호 변경 요청을 하려는 사용자 아이디
     * @param loginPw 기존 비밀번호
     * @return 패스워드 변경이 가능한 토큰
     */
    public TokenRefreshResponse authenticateToChangePassword(UUID userId, String loginPw) {
        User getUser = userQueryRepository.getById(userId);
        if (!passwordUtils.matches(loginPw, getUser.getLoginPw())) {
            throw new LoginFailException();
        }

        Map<String, Object> claims = Map.of(PASSWORD_CHANGE.toString(), true);
        String token = jwtUtils.generateToken(userId.toString(), 1000 * 60 * 60 * 3, claims);
        return new TokenRefreshResponse(token);
    }
}

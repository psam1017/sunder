package psam.portfolio.sunder.english.domain.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLogin;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLostID;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLostPW;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.Optional;

import static psam.portfolio.sunder.english.domain.user.enumeration.UserStatus.*;
import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final JwtUtils jwtUtils;
    private final PasswordUtils passwordUtils;

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
     * @param loginInfo 로그인 정보
     * @return 인증한 사용자에게 발급하는 토큰
     */
    public String login(UserPOSTLogin loginInfo) {

        User getUser = userQueryRepository.getOne(
                user.loginId.eq(loginInfo.getLoginId()),
                user.loginPw.eq(loginInfo.getLoginPw())
        );

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
        return jwtUtils.generateToken(getUser.getUuid().toString(), 1000 * 60 * 60 * 3);
    }

    // TODO

    /**
     * POST /api/user/new-token
     * 토큰 재발급 서비스
     *
     * @param token 기존 토큰
     * @return 새로 발급한 토큰
     */
    public String reissueToken(String token) {
        return null;
    }

    /**
     * POST /api/user/find-login-id
     * 로그인 아이디를 분실한 경우 가입 여부를 확인하는 서비스
     *
     * @param userInfo 로그인 아이디를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    public boolean findLoginId(UserPOSTLostID userInfo) {
        return false;
    }

    /**
     * POST /api/user/request-password-change
     * @param loginPw 기존 패스워드
     * @return 패스워드 변경이 가능한 토큰
     */
    public String requestPasswordChange(String loginPw) {
        return null;
    }
}

package psam.portfolio.sunder.english.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherFullResponse;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.IllegalStatusUserException;
import psam.portfolio.sunder.english.domain.user.exception.LoginFailException;
import psam.portfolio.sunder.english.domain.user.exception.NotAUserException;
import psam.portfolio.sunder.english.domain.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginIdForm;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.domain.user.model.response.LoginResult;
import psam.portfolio.sunder.english.domain.user.model.response.TokenRefreshResponse;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.*;

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
    private final ObjectMapper objectMapper;

    /**
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
     * 로그인 서비스
     *
     * @param form 로그인 정보
     * @param remoteIp 사용자 IP
     * @return 인증한 사용자에게 발급하는 토큰과 함께 비밀번호 변경 알림 여부를 반환
     */
    public LoginResult login(UserLoginForm form, String remoteIp) {

        User getUser = userQueryRepository.findOne(
                user.loginId.eq(form.getLoginId())
        ).orElseThrow(LoginFailException::new);

        if (!passwordUtils.matches(form.getLoginPw(), getUser.getLoginPw())) {
            throw new LoginFailException();
        }

        UserStatus status = getUser.getStatus();
        if (status != ACTIVE && status != TRIAL) {
            throw new IllegalStatusUserException(status);
        }

        Map<String, Object> claims = Map.of(
                PASSWORD.toString(), getUser.getLoginPw(),
                USER_STATUS.toString(), getUser.getStatus().toString(),
                REMOTE_IP.toString(), remoteIp,
                ROLE_NAMES.toString(), createJson(getUser.getRoles().stream().map(UserRole::getRoleName).toList())
        );
        return new LoginResult(
                jwtUtils.generateToken(getUser.getId().toString(), 10800000, claims), // accessToken 만료 시간은 3시간
                jwtUtils.generateToken(getUser.getId().toString(), 43200000), // refreshToken 만료 시간은 12시간
                getUser.isPasswordExpired()
        );
    }

    /**
     * 토큰 재발급 서비스
     *
     * @param userId 사용자 아이디
     * @param remoteIp 사용자 IP
     * @return 새로 발급한 토큰
     */
    public TokenRefreshResponse refreshToken(UUID userId, String remoteIp) {
        User getUser = userQueryRepository.getById(userId);
        Map<String, Object> claims = Map.of(
                PASSWORD.toString(), getUser.getLoginPw(),
                USER_STATUS.toString(), getUser.getStatus().toString(),
                REMOTE_IP.toString(), remoteIp,
                ROLE_NAMES.toString(), createJson(getUser.getRoles().stream().map(UserRole::getRoleName).toList())
        );
        return new TokenRefreshResponse(
                jwtUtils.generateToken(getUser.getId().toString(), 10800000, claims), // accessToken 만료 시간은 3시간
                jwtUtils.generateToken(getUser.getId().toString(), 43200000) // refreshToken 만료 시간은 12시간
        );
    }

    /**
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
     * 자기 정보 조회 서비스
     *
     * @param userId 사용자 아이디
     * @return 자기 자신의 상세 정보
     */
    public Object getMyInfo(UUID userId) {
        User getUser = userQueryRepository.getById(userId);

        if (getUser instanceof Teacher t) {
            return TeacherFullResponse.from(t);
        } else if (getUser instanceof Student s) {
            return StudentFullResponse.from(s, false); // 학생이 자신에 대한 note 를 볼 수 없다.
        }
        // TODO: 2024-04-06 admin 조회도 생성
        throw new NotAUserException();
    }

    /**
     * 객체를 JSON 문자열로 변환.
     * JWT claims 는 기본적으로 간단한 타입만을 변환할 수 있고,
     * 그것이 이미 json 일 것을 가정하고 있기에 claims 를 생성할 때 복잡합 타입의 객체는 json 으로 변환하여 넣어준다.
     *
     * @param obj 변환할 객체
     * @return JSON 문자열
     */
    private String createJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

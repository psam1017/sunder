package psam.portfolio.sunder.english.web.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.exception.OneParamToCheckUserDuplException;
import psam.portfolio.sunder.english.web.user.model.entity.User;
import psam.portfolio.sunder.english.web.user.repository.UserQueryRepository;

import java.util.Optional;

import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.PENDING;
import static psam.portfolio.sunder.english.web.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    /**
     * GET /api/user/check-dupl?loginId={loginId}&email={email}&phone={phone}
     * 아이디, 이메일, 연락처 중복 체크 서비스
     * - 단, PENDING 과 TRIAL 은 중복체크에서 제외
     * - 아직 email 인증을 하지 않은 경우도 중복체크에서 제외
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
}

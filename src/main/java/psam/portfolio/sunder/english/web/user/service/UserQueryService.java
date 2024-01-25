package psam.portfolio.sunder.english.web.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.exception.NoParamToCheckDuplException;
import psam.portfolio.sunder.english.web.user.exception.OneParamToCheckDuplException;
import psam.portfolio.sunder.english.web.user.repository.UserQueryRepository;

import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.PENDING;
import static psam.portfolio.sunder.english.web.user.enumeration.UserStatus.TRIAL;
import static psam.portfolio.sunder.english.web.user.model.QUser.user;

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

        if (hasLoginId && hasEmail && hasPhone) {
            throw new OneParamToCheckDuplException();
        }

        if (hasLoginId) {
            return userQueryRepository.findOne(
                    user.loginId.eq(loginId),
                    userStatusNotIn(PENDING, TRIAL),
                    userEmailVerifiedEq(true)
            ).isEmpty();
        } else if (hasEmail) {
            return userQueryRepository.findOne(
                    user.email.eq(email),
                    userStatusNotIn(PENDING, TRIAL),
                    userEmailVerifiedEq(true)
            ).isEmpty();
        } else if (hasPhone) {
            return userQueryRepository.findOne(
                    user.phone.eq(phone),
                    userStatusNotIn(PENDING, TRIAL),
                    userEmailVerifiedEq(true)
            ).isEmpty();
        } else {
            throw new NoParamToCheckDuplException();
        }
    }

    private static BooleanExpression userStatusNotIn(UserStatus... statuses) {
        return user.status.notIn(statuses);
    }

    private static BooleanExpression userEmailVerifiedEq(boolean verified) {
        return user.emailVerified.eq(verified);
    }
}

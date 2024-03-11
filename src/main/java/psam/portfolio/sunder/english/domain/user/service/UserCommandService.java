package psam.portfolio.sunder.english.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLostPW;
import psam.portfolio.sunder.english.domain.user.repository.UserCommandRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class UserCommandService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

    // TODO

    /**
     * POST /api/user/issue-temp-password
     * @param userInfo 비밀번호를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    public boolean issueTempPassword(UserPOSTLostPW userInfo) {
        return false;
    }

    /**
     * POST /api/user/change-password
     * @param loginPw 새로운 패스워드
     * @return 패스워드 변경 성공 여부
     */
    public boolean changePassword(String loginPw) {
        return false;
    }
}

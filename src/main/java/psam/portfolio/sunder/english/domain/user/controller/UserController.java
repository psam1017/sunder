package psam.portfolio.sunder.english.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginIdForm;
import psam.portfolio.sunder.english.domain.user.model.request.LostLoginPwForm;
import psam.portfolio.sunder.english.domain.user.model.request.UserPATCHPassword;
import psam.portfolio.sunder.english.domain.user.model.response.LoginResult;
import psam.portfolio.sunder.english.domain.user.model.response.TokenRefreshResponse;
import psam.portfolio.sunder.english.domain.user.service.UserCommandService;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;
import psam.portfolio.sunder.english.global.resolver.argument.Token;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    /**
     * 아이디, 이메일, 연락처 중복 체크 서비스 - 단, PENDING 과 TRIAL 은 중복체크에서 제외
     *
     * @param loginId 아이디
     * @param email   이메일
     * @param phone   연락처
     * @return 중복 여부 - 가능 = true, 중복 = false
     */
    @GetMapping("/check-dupl")
    public ApiResponse<Map<String, Boolean>> checkDuplication(@RequestParam(required = false) String loginId,
                                                              @RequestParam(required = false) String email,
                                                              @RequestParam(required = false) String phone) {
        boolean result = userQueryService.checkDuplication(loginId, email, phone);
        return ApiResponse.ok(Map.of("isOk", result));
    }

    /**
     * 로그인 서비스
     *
     * @param loginInfo 로그인 정보
     * @return 인증한 사용자에게 발급하는 토큰
     */
    @PostMapping("/login")
    public ApiResponse<LoginResult> login(@RequestBody @Valid UserLoginForm loginInfo) {
        LoginResult result = userQueryService.login(loginInfo);
        return ApiResponse.ok(result);
    }

    /**
     * 로그인 비밀번호 변경 알림 지연 서비스
     *
     * @return 지연 성공 여부
     */
    @PostMapping("/password/alert-later")
    @Secured({"ROLE_ADMIN", "ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Map<String, Boolean>> alterPasswordChangeLater(@UserId UUID userId) {
        boolean result = userCommandService.alterPasswordChangeLater(userId);
        return ApiResponse.ok(Map.of("delay", result));
    }

    /**
     * 토큰 재발급 서비스
     *
     * @return 새로 발급한 토큰
     */
    @PostMapping("/token/refresh")
    @Secured({"ROLE_ADMIN", "ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<TokenRefreshResponse> refreshToken(@UserId UUID userId) {
        TokenRefreshResponse response = userQueryService.refreshToken(userId);
        return ApiResponse.ok(response);
    }

    /**
     * 자기 정보 조회 서비스
     *
     * @param userId 조회할 사용자 아이디
     * @return 사용자 정보
     */
    @GetMapping("/me")
    @Secured({"ROLE_ADMIN", "ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Object> getMyInfo(@UserId UUID userId) {
        Object myInfo = userQueryService.getMyInfo(userId);
        return ApiResponse.ok(myInfo);
    }

    /**
     * 로그인 아이디를 분실한 경우 가입 여부를 확인하는 서비스
     *
     * @param userInfo 로그인 아이디를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    @PostMapping("/login-id/find")
    public ApiResponse<Map<String, Boolean>> findLoginId(@RequestBody @Valid LostLoginIdForm userInfo) {
        boolean result = userQueryService.findLoginId(userInfo);
        return ApiResponse.ok(Map.of("emailSent", result));
    }

    /**
     * 비밀번호 분실한 경우 새로운 비밀번호를 발급하는 서비스
     *
     * @param userInfo 비밀번호를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    @PostMapping("/password/new")
    public ApiResponse<Map<String, Boolean>> issueNewPassword(@RequestBody @Valid LostLoginPwForm userInfo) {
        boolean result = userCommandService.issueNewPassword(userInfo);
        return ApiResponse.ok(Map.of("emailSent", result));
    }

    /**
     * 비밀번호 변경을 위한 재인증 요청 서비스
     *
     * @param userId   비밀번호 변경 요청을 하려는 사용자 아이디
     * @param password 기존 패스워드
     * @return 패스워드 변경이 가능한 토큰
     * @apiNote 응답하는 token 의 calim 에는 PASSWORD_CHANGE 가 포함되어야 한다.
     */
    @PostMapping("/password/change-auth")
    @Secured({"ROLE_ADMIN", "ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<TokenRefreshResponse> authenticateToChangePassword(@UserId UUID userId,
                                                                          @RequestBody @Valid UserPATCHPassword password) {
        TokenRefreshResponse response = userQueryService.authenticateToChangePassword(userId, password.getLoginPw());
        return ApiResponse.ok(response);
    }

    /**
     * 재인증에 성공하고 비밀번호를 변경하는 서비스
     *
     * @param token 비밀번호을 변경하는 사용자의 토큰
     * @param password 변경할 패스워드
     * @return 변경 성공 여부
     *
     * @apiNote 전달된 token 의 calim 에는 PASSWORD_CHANGE 가 포함되어야 한다.
     */
    @PatchMapping("/password/change-new")
    @Secured({"ROLE_ADMIN", "ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Map<String, Boolean>> changePassword(@Token String token,
                                                            @RequestBody @Valid UserPATCHPassword password) {
        boolean result = userCommandService.changePassword(token, password.getLoginPw());
        return ApiResponse.ok(Map.of("newPassword", result));
    }
}
package psam.portfolio.sunder.english.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLogin;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLostID;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTLostPW;
import psam.portfolio.sunder.english.domain.user.model.request.UserPOSTPassword;
import psam.portfolio.sunder.english.domain.user.service.UserCommandService;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;

import java.util.Map;

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
     * @param loginInfo 로그인 정보
     * @return 인증한 사용자에게 발급하는 토큰
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody @Valid UserPOSTLogin loginInfo) {
        String token = userQueryService.login(loginInfo);
        return ApiResponse.ok(Map.of("token", token));
    }

    /**
     * 토큰 재발급 서비스
     * @return 새로 발급한 토큰
     */
    @PostMapping("/new-token")
    public ApiResponse<Map<String, String>> reissueToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String newToken = userQueryService.reissueToken(token);
        return ApiResponse.ok(Map.of("token", newToken));
    }

    /**
     * 로그인 아이디를 분실한 경우 가입 여부를 확인하는 서비스
     *
     * @param userInfo 로그인 아이디를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    @PostMapping("/find-login-id")
    public ApiResponse<Map<String, Boolean>> findLoginId(@RequestBody @Valid UserPOSTLostID userInfo) {
        boolean result = userQueryService.findLoginId(userInfo);
        return ApiResponse.ok(Map.of("email", result));
    }

    /**
     * 비밀번호 분실한 경우 임시 비밀번호를 발급하는 서비스
     *
     * @param userInfo 비밀번호를 분실한 가입자 정보
     * @return 이메일 발송 여부
     */
    @PostMapping("/issue-temp-password")
    public ApiResponse<Map<String, Boolean>> issueTempPassword(@RequestBody @Valid UserPOSTLostPW userInfo) {
        boolean result = userCommandService.issueTempPassword(userInfo);
        return ApiResponse.ok(Map.of("email", result));
    }

    /**
     * 비밀번호 변경을 위한 재인증 요청 서비스
     * @param password 기존 패스워드
     * @return 패스워드 변경이 가능한 토큰
     */
    @PostMapping("/request-password-change")
    public ApiResponse<Map<String, String>> requestPasswordChange(@RequestBody @Valid UserPOSTPassword password) {
        String token = userQueryService.requestPasswordChange(password.getLoginPw());
        return ApiResponse.ok(Map.of("token", token));
    }

    /**
     * 재인증에 성공하고 비밀번호를 변경하는 서비스
     * @param password 변경할 패스워드
     * @return 변경 성공 여부
     */
    @PostMapping("/change-password")
    public ApiResponse<Map<String, Boolean>> changePassword(@RequestBody @Valid UserPOSTPassword password) {
        boolean result = userCommandService.changePassword(password.getLoginPw());
        return ApiResponse.ok(Map.of("newPassword", result));
    }
}
package psam.portfolio.sunder.english.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.domain.user.service.UserQueryService;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserQueryService userQueryService;

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

    /*
    todo
    1. 로그인 서비스
    2. 토큰 재발급 서비스
    3. 마이페이지에서 비밀번호 변경
        (1) 현재 비밀번호를 입력하면 토큰에 "비밀번호 변경 가능 = true" 값을 추가한다.
        (2) "비밀번호 변경 가능 = true" 값이 있다면 비밀번호를 변경할 수 있다.
    4. 로그인아이디 분실 시 - 가입 여부 확인 서비스. 이메일, 이름으로 가입 여부를 확인하고 로그인 아이디를 해당 이메일로 전송한다.
    5. 비밀번호 분실 시 - 이메일로 임시 비밀번호 전송 서비스
     */
}

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
}

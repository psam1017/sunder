package psam.portfolio.sunder.english.web.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.web.user.service.UserQueryService;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    // TODO: 2024-01-26 Guide 문서 만들기.
    // TODO: 2024-01-26 User 중복 검사 API 문서 - email, phone case 만들기

    private final UserQueryService userQueryService;

    /**
     * 아이디, 이메일, 연락처 중복 체크 서비스 - 단, PENDING 과 TRIAL 은 중복체크에서 제외
     *
     * @param loginId 아이디
     * @param email 이메일
     * @param phone 연락처
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

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

    private final UserQueryService userQueryService;

    // TODO: 2024-01-26 Service 문서 정리하고, Controller 로 Docs 만들고, Docs 정리
    // TODO: 2024-01-26 파라미터 변수명을 필드명으로 사용하기 옵션

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

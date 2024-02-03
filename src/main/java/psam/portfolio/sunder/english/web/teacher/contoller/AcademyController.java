package psam.portfolio.sunder.english.web.teacher.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.web.teacher.model.request.AcademyDirectorPOST;
import psam.portfolio.sunder.english.web.teacher.service.AcademyCommandService;
import psam.portfolio.sunder.english.web.teacher.service.AcademyQueryService;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/academy")
@RestController
public class AcademyController {

    private final AcademyCommandService academyCommandService;
    private final AcademyQueryService academyQueryService;

    /**
     * 학원 이름 중복 체크 서비스
     * @param name 학원 이름
     * @param phone 학원 전화번호
     * @param email 학원 이메일
     * @return 중복 여부
     */
    @GetMapping("/check-dupl")
    public ApiResponse<Map<String, Boolean>> checkDuplication(@RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String phone,
                                                              @RequestParam(required = false) String email) {
        boolean result = academyQueryService.checkDuplication(name, phone, email);
        return ApiResponse.ok(Map.of("isOk", result));
    }

    /**
     * 학원과 학원장을 등록하는 서비스
     * @param post 학원과 학원장 정보
     * @return 학원장 uuid
     */
    @PostMapping("/new")
    public ApiResponse<Map<String, String>> registerAcademy(@RequestBody @Valid AcademyDirectorPOST post) {
        String directorUuid = academyCommandService.registerDirectorWithAcademy(post.getAcademy(), post.getDirector());
        return ApiResponse.ok(Map.of("directorUuid", directorUuid));
    }

    /**
     * 학원 검증 및 승인 서비스
     * @param academyUuid 학원 uuid
     * @return 학원 승인 여부
     */
    @GetMapping("/verify/{academyUuid}")
    public ApiResponse<Map<String, Boolean>> verifyAcademy(@PathVariable String academyUuid) {
        boolean result = academyCommandService.verifyAcademy(UUID.fromString(academyUuid));
        return ApiResponse.ok(Map.of("verified", result));
    }

    /*
    todo
    POST /api/academy/teacher/new - @Secured("ROLE_DIRECTOR")
    학원에서 선생님을 등록하는 서비스

    PUT /api/academy/info - @Secured("ROLE_DIRECTOR")
    학원 정보 수정 서비스

    GET /api/academy/detail?academyUuid={academyUuid}&select={teachers,students}
    학원 상세 정보 조회 서비스
     */
}

package psam.portfolio.sunder.english.web.teacher.contoller;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/check-dupl")
    public ApiResponse<Map<String, Boolean>> checkDuplication(@RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String phone,
                                                              @RequestParam(required = false) String email) {
        boolean result = academyQueryService.checkDuplication(name, phone, email);
        return ApiResponse.ok(Map.of("isOk", result));
    }

    @PostMapping("/new")
    public ApiResponse<Map<String, String>> registerAcademy(@RequestBody AcademyDirectorPOST request) {
        String teacherUuid = academyCommandService.registerDirectorWithAcademy(request.getAcademyPOST(), request.getDirectorPOST());
        return ApiResponse.ok(Map.of("teacherUuid", teacherUuid));
    }

    @PostMapping("verification")
    public ApiResponse<Map<String, Boolean>> verifyAcademy(@RequestParam String uuid) {
        boolean result = academyCommandService.verifyAcademy(UUID.fromString(uuid));
        return ApiResponse.ok(Map.of("academyUuid", result));
    }

    /*

    POST /api/academy/teacher/new - @Secured("ROLE_DIRECTOR")
    학원에서 선생님을 등록하는 서비스

    PUT /api/academy/info - @Secured("ROLE_DIRECTOR")
    학원 정보 수정 서비스
     */

    /*
    GET /api/academy/check-dupl?name={name}
    학원 이름 중복 체크 서비스

    GET /api/academy/detail?academyUuid={academyUuid}&select={teachers,students,lessons}
    학원 상세 정보 조회 서비스
     */
}

package psam.portfolio.sunder.english.domain.academy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.academy.exception.AcademyAccessDeniedException;
import psam.portfolio.sunder.english.domain.user.model.request.UserLoginForm;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.AcademyId;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyDirectorPOST;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPATCH;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPublicPageSearchCond;
import psam.portfolio.sunder.english.domain.academy.service.AcademyCommandService;
import psam.portfolio.sunder.english.domain.academy.service.AcademyQueryService;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/academies")
@RestController
public class AcademyController {

    private final AcademyCommandService academyCommandService;
    private final AcademyQueryService academyQueryService;

    /**
     * 학원 이름 중복 체크 서비스
     *
     * @param name  학원 이름
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
     *
     * @param post 학원과 학원장 정보
     * @return 학원장 아이디
     */
    @PostMapping("")
    public ApiResponse<Map<String, UUID>> register(@RequestBody @Valid AcademyDirectorPOST post) {
        UUID directorId = academyCommandService.registerDirectorWithAcademy(post.getAcademy(), post.getDirector());
        return ApiResponse.ok(Map.of("directorId", directorId));
    }

    /**
     * 학원 검증 및 승인 서비스
     *
     * @param academyId 학원 아이디
     * @return 학원 승인 여부
     */
    @GetMapping("/{academyId}/verify")
    public ApiResponse<Map<String, Boolean>> verify(@PathVariable String academyId) {
        boolean result = academyCommandService.verify(UUID.fromString(academyId));
        return ApiResponse.ok(Map.of("verified", result));
    }

    /**
     * openToPublic = true 인 공개 학원 목록 조회 서비스
     *
     * @param cond 학원 검색 조건
     * @return 학원 목록
     */
    @GetMapping("")
    public ApiResponse<Map<String, Object>> getPublicList(@ModelAttribute @Valid AcademyPublicPageSearchCond cond) {
        Map<String, Object> response = academyQueryService.getPublicList(cond);
        return ApiResponse.ok(response);
    }

    /**
     * 학원 상세 정보 조회 서비스. 학원에 소속된 사용자가 자기 학원의 정보만 조회할 수 있다.
     *
     * @param userId 조회할 사용자 아이디
     * @param select 같이 조회할 정보 = {teacher}
     * @return 학원 상세 정보 + (선생님 목록)
     * @apiNote 학생이 요청할 때와 선생이 요청할 때 응답스펙이 다르다.
     */
    @GetMapping("/{academyId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Map<String, Object>> getAcademyDetail(@UserId UUID userId,
                                                             @PathVariable UUID academyId,
                                                             @RequestParam(required = false) String select) {
        Map<String, Object> responseData = academyQueryService.getDetail(academyId, userId, select);
        return ApiResponse.ok(responseData);
    }

    /**
     * 학원 정보 수정 서비스. 본인의 학원만 수정할 수 있다.
     *
     * @param directorId   학원장 아이디
     * @param patch        학원의 수정할 정보
     * @return 수정을 완료한 학원 아이디
     */
    @Secured("ROLE_DIRECTOR")
    @PatchMapping("/{academyId}")
    public ApiResponse<Map<String, UUID>> updateInfo(@UserId UUID directorId,
                                                     @AcademyId UUID tokenAcademyId,
                                                     @PathVariable UUID academyId,
                                                     @RequestBody @Valid AcademyPATCH patch) {
        if (!Objects.equals(tokenAcademyId, academyId)) {
            throw new AcademyAccessDeniedException();
        }
        UUID updateAcademyId = academyCommandService.updateInfo(directorId, patch);
        return ApiResponse.ok(Map.of("academyId", updateAcademyId));
    }

    /**
     * 학원 페쇄 신청 서비스. 페쇄 신청을 하고 7일 후에 DB 에서 완전히 삭제된다.
     *
     * @param directorId 학원장 아이디
     * @return 페쇄를 신청한 학원 아이디
     */
    @Secured("ROLE_DIRECTOR")
    @DeleteMapping("")
    public ApiResponse<Map<String, UUID>> withdraw(@UserId UUID directorId) {
        UUID deletedAcademyId = academyCommandService.withdraw(directorId);
        return ApiResponse.ok(Map.of("academyId", deletedAcademyId));
    }

    /**
     * 학원 페쇄 취소 서비스
     *
     * @param directorId 학원장 아이디
     * @return 페쇄를 취소한 학원 아이디
     */
    @Secured("ROLE_DIRECTOR")
    @PatchMapping("/{academyId}/revoke")
    public ApiResponse<Map<String, UUID>> revokeWithdraw(@UserId UUID directorId,
                                                         @AcademyId UUID tokenAcademyId,
                                                         @PathVariable UUID academyId) {
        if (!Objects.equals(tokenAcademyId, academyId)) {
            throw new AcademyAccessDeniedException();
        }
        UUID deletedAcademyId = academyCommandService.revokeWithdrawal(directorId);
        return ApiResponse.ok(Map.of("academyId", deletedAcademyId));
    }

    /**
     * 체험판을 종료하고 정식으로 서비스를 사용하기 위해 회원 상태를 전환하는 서비스
     * - 학원장의 아이디와 비밀번호로만 가능하며, 해당 학원 소속의 모든 사용자의 상태가 전환된다.
     * @param loginForm 학원장의 아이디와 비밀번호
     * @return 전환 완료 여부
     */
    @PostMapping("/end-trial")
    public ApiResponse<Map<String, Boolean>> endTrial(@RequestBody @Valid UserLoginForm loginForm) {
        boolean result = academyCommandService.endTrial(loginForm);
        return ApiResponse.ok(Map.of("endTrial", result));
    }
}

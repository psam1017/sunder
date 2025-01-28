package psam.portfolio.sunder.english.domain.academy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.academy.exception.AcademyAccessDeniedException;
import psam.portfolio.sunder.english.domain.academy.exception.NoSuchAcademyException;
import psam.portfolio.sunder.english.domain.academy.model.response.AcademyShareSummary;
import psam.portfolio.sunder.english.domain.academy.service.AcademyShareCommandService;
import psam.portfolio.sunder.english.domain.academy.service.AcademyShareQueryService;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.AcademyId;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/academies/{academyId}/shares")
@RestController
public class AcademyShareController {

    private final AcademyShareCommandService academyShareCommandService;
    private final AcademyShareQueryService academyShareQueryService;

    /**
     * 학원 간 교재 공유 서비스
     *
     * @param academyId       공유할 학원 아이디
     * @param tokenAcademyId  토큰 학원 아이디
     * @param sharedAcademyId 공유 받는 학원 아이디
     * @return 공유 받은 학원 아이디
     */
    @Secured("ROLE_DIRECTOR")
    @PutMapping("/{sharedAcademyId}")
    public ApiResponse<Map<String, String>> share(@PathVariable String academyId,
                                                  @AcademyId UUID tokenAcademyId,
                                                  @PathVariable String sharedAcademyId) {

        if (isNotUuid(sharedAcademyId) || !Objects.equals(tokenAcademyId, UUID.fromString(academyId))) {
            throw new NoSuchAcademyException();
        }
        academyShareCommandService.share(UUID.fromString(academyId), UUID.fromString(sharedAcademyId));
        return ApiResponse.ok(Map.of("sharedAcademyId", sharedAcademyId));
    }

    /**
     * 학원 간 교재 공유 목록 조회 서비스
     *
     * @param academyId      공유할 학원 아이디
     * @param tokenAcademyId 토큰 학원 아이디
     * @return 공유 목록
     */
    @Secured("ROLE_DIRECTOR")
    @GetMapping("")
    public ApiResponse<Map<String, List<AcademyShareSummary>>> getShares(@PathVariable String academyId,
                                                                         @AcademyId UUID tokenAcademyId) {
        if (!Objects.equals(tokenAcademyId, UUID.fromString(academyId))) {
            throw new AcademyAccessDeniedException();
        }
        List<AcademyShareSummary> shares = academyShareQueryService.getShares(UUID.fromString(academyId));
        return ApiResponse.ok(Map.of("shares", shares));
    }

    /**
     * 학원 간 교재 공유 해제 서비스
     * @param academyId       공유할 학원 아이디
     * @param tokenAcademyId  토큰 학원 아이디
     * @param sharedAcademyId 공유 받는 학원 아이디
     * @return 공유 해제된 학원 아이디
     */
    @Secured("ROLE_DIRECTOR")
    @DeleteMapping("/{sharedAcademyId}")
    public ApiResponse<Map<String, String>> cancelShare(@PathVariable String academyId,
                                                        @AcademyId UUID tokenAcademyId,
                                                        @PathVariable String sharedAcademyId) {
        if (!Objects.equals(tokenAcademyId, UUID.fromString(academyId))) {
            throw new AcademyAccessDeniedException();
        }
        academyShareCommandService.cancelShare(UUID.fromString(academyId), UUID.fromString(sharedAcademyId));
        return ApiResponse.ok(Map.of("sharedAcademyId", sharedAcademyId));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean isNotUuid(String str) {
        try {
            UUID.fromString(str);
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}

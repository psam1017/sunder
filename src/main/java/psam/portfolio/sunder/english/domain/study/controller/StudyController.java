package psam.portfolio.sunder.english.domain.study.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPATCHSubmit;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPOSTStart;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.request.StudyWordPATCHCorrect;
import psam.portfolio.sunder.english.domain.study.model.response.StudyFullResponse;
import psam.portfolio.sunder.english.domain.study.service.StudyCommandService;
import psam.portfolio.sunder.english.domain.study.service.StudyQueryService;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.Map;
import java.util.UUID;

/**
 * TBD
 * 만약 ROLE_SECRETARY 가 새로 추가된다면, 이는 숙제를 내줄 수는 있지만, 성적을 정정할 수는 없다.
 * 그 외 ROLE_SECRETARY 만 할 수 있는 별도의 업무가 있을 수도 있다. ex) 학원의 행정 관리는 ROLE_DIRECTOR, ROLE_SECRETARY 만 가능
 */
@RequiredArgsConstructor
@RequestMapping("/api/studies")
@RestController
public class StudyController {

    private final StudyCommandService studyCommandService;
    private final StudyQueryService studyQueryService;

    // TODO: 2024-06-29 선생님이 숙제를 내주는 POST API. assignStudy
    // TODO: 2024-06-29 선생의 학생 성적 통계 대시보드
    // TODO: 2024-06-29 학생의 자기 성적 통계 대시보드

    /*
    @PostMapping("/assign")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> assignStudy(@UserId UUID teacherId,
                                                     @RequestBody @Valid StudyPOSTAssign studyPOSTAssign) {
        UUID studyId = studyCommandService.assignStudy(teacherId, studyPOSTAssign);
        return ApiResponse.ok(Map.of("studyId", studyId)));
    }
     */

    @PostMapping("")
    @Secured("ROLE_STUDENT")
    public ApiResponse<Map<String, UUID>> startStudy(@UserId UUID studentId,
                                                     @RequestBody @Valid StudyPOSTStart post) {
        UUID studyId = studyCommandService.start(studentId, post);
        return ApiResponse.ok(Map.of("studyId", studyId));
    }

    @GetMapping("")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Map<String, Object>> getStudyList(@UserId UUID userId,
                                                         @ModelAttribute StudySlicingSearchCond cond) {
        Map<String, Object> studies = studyQueryService.getStudyList(userId, cond);
        return ApiResponse.ok(Map.of("studies", studies));
    }

    @GetMapping("/{studyId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<StudyFullResponse> getStudyDetail(@UserId UUID userId,
                                                         @PathVariable UUID studyId,
                                                         @RequestParam(required = false) String select) {
        StudyFullResponse study = studyQueryService.getDetail(userId, studyId, select);
        return ApiResponse.ok(study);
    }

    @PatchMapping("/{studyId}/submit")
    @Secured("ROLE_STUDENT")
    public ApiResponse<Map<String, UUID>> submitStudy(@UserId UUID studentId,
                                                      @PathVariable UUID studyId,
                                                      @RequestBody @Valid StudyPATCHSubmit patch) {
        UUID submitStudyId = studyCommandService.submit(studentId, studyId, patch);
        return ApiResponse.ok(Map.of("studyId", submitStudyId));
    }

    @PatchMapping("/{studyId}/study-words/{studyWordId}/correct")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, Object>> correctStudyWord(@UserId UUID teacherId,
                                           @PathVariable UUID studyId,
                                           @PathVariable Long studyWordId,
                                           @RequestBody @Valid StudyWordPATCHCorrect patch) {
        Long correctStudyWordId = studyCommandService.correctStudyWord(teacherId, studyId, studyWordId, patch);
        return ApiResponse.ok(
                Map.of(
                        "studyId", studyId,
                        "studyWordId", correctStudyWordId
                )
        );
    }

    @DeleteMapping("/{studyId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> deleteStudy(@UserId UUID teacherId,
                                      @PathVariable UUID studyId) {
        UUID deleteStudyId = studyCommandService.delete(teacherId, studyId);
        return ApiResponse.ok(Map.of("studyId", deleteStudyId));
    }
}

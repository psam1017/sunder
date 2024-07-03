package psam.portfolio.sunder.english.domain.study.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.domain.study.model.request.*;
import psam.portfolio.sunder.english.domain.study.model.response.StudyFullResponse;
import psam.portfolio.sunder.english.domain.study.service.StudyCommandService;
import psam.portfolio.sunder.english.domain.study.service.StudyQueryService;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.List;
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

    // TODO: 2024-06-29 선생님의 학생 성적 통계 대시보드
    // TODO: 2024-06-29 학생의 자기 성적 통계 대시보드

    /**
     * 숙제 생성 서비스
     *
     * @param teacherId 숙제를 내주는 선생님 아이디
     * @param post      생성할 숙제 정보
     * @return 생성에 성공한 숙제 아이디 배열
     */
    @PostMapping("/assign")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, List<UUID>>> assignStudy(@UserId UUID teacherId,
                                                            @RequestBody @Valid StudyPOSTAssign post) {
        List<UUID> studyIds = studyCommandService.assign(teacherId, post);
        return ApiResponse.ok(Map.of("studyIds", studyIds));
    }

    /**
     * 학습 시작 서비스
     *
     * @param studentId 학생 아이디
     * @param post      시작할 학습 정보
     * @return 시작에 성공한 학습 아이디
     */
    @PostMapping("")
    @Secured("ROLE_STUDENT")
    public ApiResponse<Map<String, UUID>> startStudy(@UserId UUID studentId,
                                                     @RequestBody @Valid StudyPOSTStart post) {
        UUID studyId = studyCommandService.start(studentId, post);
        return ApiResponse.ok(Map.of("studyId", studyId));
    }

    /**
     * 학습 목록 조회 서비스
     * 학생이라면 자신의 학습 목록을 조회할 수 있다.
     * 선생님이라면 같은 학원 학생들의 학습 목록을 조회할 수 있다.
     *
     * @param userId 사용자 아이디
     * @param cond   학습 목록 조회 조건
     * @return 학습 목록과 슬라이싱 정보
     */
    @GetMapping("")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Map<String, Object>> getStudyList(@UserId UUID userId,
                                                         @ModelAttribute StudySlicingSearchCond cond) {
        Map<String, Object> response = studyQueryService.getStudyList(userId, cond);
        return ApiResponse.ok(response);
    }

    /**
     * 학습 상세 정보 조회 서비스
     * 학생이라면 자신의 학습 목록을 조회할 수 있다. 단, 아직 제출하지 않은 학습은 answer 를 조회할 수 없다.
     * 선생님이라면 같은 학원 학생들의 학습 목록을 조회할 수 있다.
     *
     * @param userId  사용자 아이디
     * @param studyId 조회할 학습 아이디
     * @return 학습 상세 정보
     */
    @GetMapping("/{studyId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<Map<String, Object>> getStudyDetail(@UserId UUID userId,
                                                           @PathVariable UUID studyId) {
        Map<String, Object> response = studyQueryService.getDetail(userId, studyId);
        return ApiResponse.ok(response);
    }

    /**
     * 학습 제출 서비스
     * @param studentId 학생 아이디
     * @param studyId   제출할 학습 아이디
     * @param patch     제출할 학습 정보
     * @return 제출에 성공한 학습 아이디
     */
    @PatchMapping("/{studyId}/submit")
    @Secured("ROLE_STUDENT")
    public ApiResponse<Map<String, UUID>> submitStudy(@UserId UUID studentId,
                                                      @PathVariable UUID studyId,
                                                      @RequestBody @Valid StudyPATCHSubmit patch) {
        UUID submitStudyId = studyCommandService.submit(studentId, studyId, patch);
        return ApiResponse.ok(Map.of("studyId", submitStudyId));
    }

    /**
     * 학습 단어 정정 서비스
     * 선생님이 같은 학원 학생의 학습 단어를 정정할 수 있다.
     *
     * @param teacherId   성적을 정정하는 선생님 아이디
     * @param studyId     정정할 학습 아이디
     * @param studyWordId 정정할 학습 단어 아이디
     * @param patch       정정할 학습 단어 정보
     * @return 정정에 성공한 학습 단어 아이디
     */
    @PatchMapping("/{studyId}/study-words/{studyWordId}/correct")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, Object>> correctStudyWord(@UserId UUID teacherId,
                                                             @PathVariable UUID studyId,
                                                             @PathVariable Long studyWordId,
                                                             @RequestBody @Valid StudyWordPATCHCorrect patch) {
        Long correctStudyWordId = studyCommandService.correctStudyWord(teacherId, studyId, studyWordId, patch);
        return ApiResponse.ok(Map.of(
                "studyId", studyId,
                "studyWordId", correctStudyWordId
        ));
    }

    /**
     * 학습 삭제 서비스
     * 선생님이 같은 학원 학생의 학습을 삭제할 수 있다.
     *
     * @param teacherId 성적을 삭제하는 선생님 아이디
     * @param studyId   삭제할 학습 아이디
     * @return 삭제에 성공한 학습 아이디
     */
    @DeleteMapping("/{studyId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<Map<String, UUID>> deleteStudy(@UserId UUID teacherId,
                                                      @PathVariable UUID studyId) {
        UUID deleteStudyId = studyCommandService.delete(teacherId, studyId);
        return ApiResponse.ok(Map.of("studyId", deleteStudyId));
    }
}

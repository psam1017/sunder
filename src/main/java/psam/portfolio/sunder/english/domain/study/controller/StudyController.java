package psam.portfolio.sunder.english.domain.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.resolver.argument.UserId;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/studies")
@RestController
public class StudyController {

    // 시험 성적 제출하기 : book id 와 함께 word 들의 결과를 전달하기. 채점 후 성적 등을 history 로 기록한다.
    // 시험 성적 목록 조회하기(슬라이싱) : 학생은 본인만, 선생 이상은 학원 내 성적만 조회할 수 있다. 슬라이싱으로 구현한다.
    // 시험 성적 상세 조회하기 : 학생은 본인만, 선생 이상은 학원 내 성적만 조회할 수 있다.
    // 시험 성적 수정하기(정오만) : 선생님 이상만 가능하다.
    // 시험 성적 삭제하기 : 선생님 이상만 가능하다.
    // 시험 성적 대시보드 조회하기 : 최근 성적과 평균, 가장 많이 공부한 교재 등

    @PostMapping("")
    @Secured("ROLE_STUDENT")
    public ApiResponse<?> submitStudy(@UserId UUID userId) {
        return null;
    }

    @GetMapping("")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<?> getStudyList(@UserId UUID userId) {
        return null;
    }

    @GetMapping("/{studyId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER", "ROLE_STUDENT"})
    public ApiResponse<?> getStudyDetail(@PathVariable UUID studyId) {
        return null;
    }

    @PatchMapping("/{studyId}/correct")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<?> correctStudy(@PathVariable UUID studyId) {
        return null;
    }

    @DeleteMapping("/{studyId}")
    @Secured({"ROLE_DIRECTOR", "ROLE_TEACHER"})
    public ApiResponse<?> deleteStudy(@PathVariable UUID studyId) {
        return null;
    }
}

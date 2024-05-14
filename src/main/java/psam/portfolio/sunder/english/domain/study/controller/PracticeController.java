package psam.portfolio.sunder.english.domain.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/practices")
@RestController
public class PracticeController {

    // book id 와 함께 word 들의 결과를 전달하기. 성적 등을 history 로 기록한다.
//    @PostMapping("")

    // 연습 성적 조회하기. 학생은 본인만, 선생 이상은 학원 내 성적만 조회할 수 있다. 슬라이싱으로 구현한다.
//    @GetMapping("")

    // 연습 성적 삭제하기. 선생님 이상만 가능하다.
//    @DeleteMapping("/{practiceId}")
}

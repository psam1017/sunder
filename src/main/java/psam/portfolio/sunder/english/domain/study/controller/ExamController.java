package psam.portfolio.sunder.english.domain.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/exams")
@RestController
public class ExamController {

    // 시험지 출력 기능은 프론트에서 해야 할 듯
    // exam 의 경우 학생들의 불법적 이용을 방지하기 위해 SSR 이 필요
}

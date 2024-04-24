package psam.portfolio.sunder.english.domain.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/exams")
@RestController
public class ExamController {

    // 시험지 출력 기능은 프론트에서 해야 할 듯
    // SSR 을 할까 했지만 굳이 필요해보이진 않는다. 정답 체크는 id 를 받아서 db 랑 비교하자.
}

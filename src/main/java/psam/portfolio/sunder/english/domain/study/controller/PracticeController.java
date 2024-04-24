package psam.portfolio.sunder.english.domain.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/practices")
@RestController
public class PracticeController {

    // SSR 을 할까 했지만 굳이 필요해보이진 않는다. 어차피 단어 연습하는 거니까 json 으로 노출해도 크게 신경쓰지는 말자.
}

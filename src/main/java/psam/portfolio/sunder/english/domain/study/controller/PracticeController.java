package psam.portfolio.sunder.english.domain.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/practices")
@RestController
public class PracticeController {

    // TODO js 로 개발자 도구 차단
    // https://graykang.tistory.com/entry/Detect-browser-developer-tools-by-javascript-2chrome-firefox-Edge-javascript%EB%A1%9C-%EB%B8%8C%EB%9D%BC%EC%9A%B0%EC%A0%80-%EA%B0%9C%EB%B0%9C%EC%9E%90-%EB%8F%84%EA%B5%AC-%EC%82%AC%EC%9A%A9-%EB%B0%A9%EC%A7%80
    // practice 의 경우 학생들의 불법적 이용을 방지하기 위해 SSR 이 필요
}

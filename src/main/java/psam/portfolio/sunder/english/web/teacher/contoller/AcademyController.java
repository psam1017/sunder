package psam.portfolio.sunder.english.web.teacher.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/academy")
@RestController
public class AcademyController {


    /*
    POST /api/academy/new
    학원 및 원장 생성 서비스 - EmailUtils, PasswordUtils 필요. Teacher 와 Academy 를 같이 활성화

    POST /api/academy/teacher/new - @Secured("ROLE_DIRECTOR")
    학원에서 선생님을 등록하는 서비스

    PUT /api/academy/info - @Secured("ROLE_DIRECTOR")
    학원 정보 수정 서비스
     */

    /*
    GET /api/academy/check-dupl?name={name}
    학원 이름 중복 체크 서비스

    GET /api/academy/detail?academyUuid={academyUuid}&select={teachers,students,lessons}
    학원 상세 정보 조회 서비스
     */
}

package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AcademyCommandService {

    private final AcademyCommandRepository academyCommandRepository;
    private final AcademyQueryRepository academyQueryRepository;

    /*
    todo
    POST /api/academy/new
    학원 및 원장 생성 서비스 - EmailUtils, PasswordUtils 필요. Teacher 와 Academy 를 같이 활성화

    POST /api/academy/teacher/new - @Secured("ROLE_DIRECTOR")
    학원에서 선생님을 등록하는 서비스

    PUT /api/academy/info - @Secured("ROLE_DIRECTOR")
    학원 정보 수정 서비스
     */

}

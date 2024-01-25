package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AcademyQueryService {

    private final AcademyQueryRepository academyQueryRepository;

    /*
    GET /api/academy/check-dupl?name={name}
    학원 이름 중복 체크 서비스

    GET /api/academy/detail?academyUuid={academyUuid}&select={teachers,students,lessons}
    학원 상세 정보 조회 서비스
     */
}

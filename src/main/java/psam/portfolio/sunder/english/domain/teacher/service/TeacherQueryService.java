package psam.portfolio.sunder.english.domain.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherSearchCond;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TeacherQueryService {

    private final TeacherQueryRepository teacherQueryRepository;

    /**
     * 선생님 목록 조회 서비스
     *
     * @param userId 사용자 아이디
     * @param cond   선생님 검색 조건
     * @return 선생님 목록
     * @apiNote 학생이 요청할 때와 선생이 요청할 때 응답스펙이 다르다.
     */
    public List<?> getList(UUID userId, TeacherSearchCond cond) {
        return null;
    }

    /**
     * 선생님 상세 조회 서비스
     *
     * @param userId 사용자 아이디
     * @param teacherId 선생님 아이디
     * @return 선생님 상세 정보
     * @apiNote 학생이 요청할 때와 선생이 요청할 때 응답스펙이 다르다.
     */
    public Object getDetail(UUID userId, UUID teacherId) {
        return null;
    }
}

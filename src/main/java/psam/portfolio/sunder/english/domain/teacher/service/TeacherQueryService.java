package psam.portfolio.sunder.english.domain.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherSearchCond;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherFullResponse;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherPublicResponse;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.exception.NoSuchUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class TeacherQueryService {

    private final UserQueryRepository userQueryRepository;
    private final TeacherQueryRepository teacherQueryRepository;

    /**
     * 선생님 목록 조회 서비스. 페이징 없이 모든 선생 목록을 반환한다.
     *
     * @param userId 사용자 아이디
     * @param cond   선생님 검색 조건
     * @return 선생님 목록
     * @apiNote 학생이 요청할 때와 선생이 요청할 때 응답스펙이 다르다.
     */
    public List<?> getList(UUID userId, TeacherSearchCond cond) {
        User getUser = userQueryRepository.getById(userId);

        List<Teacher> teachers = teacherQueryRepository.findAllBySearchCond(cond);
        if (getUser instanceof Student) {
            return teachers.stream().map(TeacherPublicResponse::from).toList();
        } else if (getUser instanceof Teacher) {
            return teachers.stream().map(TeacherFullResponse::from).toList();
        }

        throw new NoSuchUserException();
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
        User getUser = userQueryRepository.getById(userId);

        if (getUser instanceof Student) {
            return TeacherPublicResponse.from(teacherQueryRepository.getById(teacherId));
        } else if (getUser instanceof Teacher) {
            return TeacherFullResponse.from(teacherQueryRepository.getById(teacherId));
        }

        throw new NoSuchUserException();
    }
}

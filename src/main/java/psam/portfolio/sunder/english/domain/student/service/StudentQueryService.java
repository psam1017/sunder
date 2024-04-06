package psam.portfolio.sunder.english.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.QStudent;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.request.StudentSearchCond;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.student.model.response.StudentPublicResponse;
import psam.portfolio.sunder.english.domain.student.repository.StudentCommandRepository;
import psam.portfolio.sunder.english.domain.student.repository.StudentQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.global.pagination.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StudentQueryService {

    private final static int PAGE_SET_AMOUNT = 10;

    private final StudentCommandRepository studentCommandRepository;
    private final StudentQueryRepository studentQueryRepository;

    private final AcademyQueryRepository academyQueryRepository;

    /**
     * 학생 중복 체크 서비스
     *
     * @param academyId    학원 아이디
     * @param attendanceId 출석 아이디
     * @return 중복 여부 - 가능 = true, 중복 = false
     */
    public boolean checkDuplication(UUID academyId, String attendanceId) {
        Optional<Student> optStudent = studentQueryRepository.findOne(
                QStudent.student.academy.uuid.eq(academyId),
                QStudent.student.attendanceId.eq(attendanceId)
        );
        return optStudent.isEmpty();
    }

    /**
     * 학생 목록 조회 서비스
     *
     * @param teacherId 학생 목록을 조회하는 선생님 아이디
     * @param cond      학생 목록 조회 조건
     * @return 학생 목록
     */
    public Map<String, Object> getList(UUID teacherId, StudentSearchCond cond) {

        Academy getAcademy = academyQueryRepository.getOne(
                QAcademy.academy.teachers.any().uuid.eq(teacherId)
        );

        List<Student> students = studentQueryRepository.findAllBySearchCond(getAcademy.getUuid(), cond);
        long count = studentQueryRepository.countBySearchCond(getAcademy.getUuid(), cond);

        return Map.of(
                "students", students.stream().map(StudentPublicResponse::from).toList(),
                "pageInfo", new PageInfo(cond.getPage(), cond.getSize(), count, PAGE_SET_AMOUNT)
        );
    }

    public StudentFullResponse get(UUID userId, UUID studentId) {
        return null;
    }
}

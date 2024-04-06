package psam.portfolio.sunder.english.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.exception.AcademyAccessDeniedException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.QStudent;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.request.StudentSearchCond;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.student.model.response.StudentPublicResponse;
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

    private final StudentQueryRepository studentQueryRepository;

    private final TeacherQueryRepository teacherQueryRepository;
    private final AcademyQueryRepository academyQueryRepository;

    /**
     * 학생 중복 체크 서비스
     *
     * @param teacherId    검색하는 선생님 아이디
     * @param attendanceId 출석 아이디
     * @return 중복 여부 - 가능 = true, 중복 = false
     */
    public boolean checkDuplication(UUID teacherId, String attendanceId) {
        Academy getAcademy = academyQueryRepository.getOne(
                QAcademy.academy.teachers.any().uuid.eq(teacherId)
        );

        Optional<Student> optStudent = studentQueryRepository.findOne(
                QStudent.student.academy.uuid.eq(getAcademy.getUuid()),
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
        long count = studentQueryRepository.countBySearchCond(students.size(), getAcademy.getUuid(), cond);

        return Map.of(
                "students", students.stream().map(StudentPublicResponse::from).toList(),
                "pageInfo", new PageInfo(cond.getPage(), cond.getSize(), count, PAGE_SET_AMOUNT)
        );
    }

    /**
     * 학생 상세 정보 조회 서비스.
     *
     * @param teacherId 학생을 조회하는 선생님 아이디
     * @param studentId 조회할 학생 아이디
     * @return 학생 상세 정보
     */
    public StudentFullResponse getDetail(UUID teacherId, UUID studentId) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Student getStudent = studentQueryRepository.getById(studentId);

        if (!getTeacher.hasSameAcademy(getStudent)) {
            throw new AcademyAccessDeniedException();
        }
        return StudentFullResponse.from(getStudent, true);
    }
}

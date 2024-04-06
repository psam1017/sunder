package psam.portfolio.sunder.english.domain.student.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.exception.NoSuchStudentException;
import psam.portfolio.sunder.english.domain.student.model.request.StudentSearchCond;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.student.model.entity.QStudent.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class StudentQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Optional<Student> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Student.class, uuid));
    }

    public Optional<Student> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(student)
                        .from(student)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Student getById(UUID uuid) {
        Student entity = em.find(Student.class, uuid);
        if (entity == null) {
            throw new NoSuchStudentException();
        }
        return entity;
    }

    public Student getOne(BooleanExpression... expressions) {
        Student entity = query.select(student)
                .from(student)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchStudentException();
        }
        return entity;
    }

    public List<Student> findAll(BooleanExpression... expressions) {
        return query.select(student)
                .from(student)
                .where(expressions)
                .fetch();
    }

    public List<Student> findAllBySearchCond(UUID academyId, StudentSearchCond cond) {
        return query.selectDistinct(student)
                .from(student)
                .where(
                        academyIdEq(academyId),
                        addressContains(cond.getAddress()),
                        statusEq(cond.getStatus()),
                        studentNameContains(cond.getName()),
                        attendanceIdContains(cond.getAttendanceId()),
                        schoolNameContains(cond.getSchoolName()),
                        schoolGradeEq(cond.getSchoolGrade()),
                        parentNameContains(cond.getParentName())
                )
                .offset(cond.getOffset())
                .limit(cond.getLimit())
                .fetch();
    }

    public long countBySearchCond(UUID academyId, StudentSearchCond cond) {
        return 0;
    }

    private BooleanExpression academyIdEq(UUID academyId) {
        return academyId != null ? student.academy.uuid.eq(academyId) : null;
    }

    private BooleanExpression addressContains(String address) {
        StringExpression concatAddress = student.address.street
                .concat(student.address.detail)
                .concat(student.address.postalCode)
                .toLowerCase();

        if (StringUtils.hasText(address)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", concatAddress)
                    .contains(address.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private BooleanExpression statusEq(UserStatus status) {
        return status != null ? student.status.eq(status) : null;
    }

    private BooleanExpression studentNameContains(String studentName) {
        if (StringUtils.hasText(studentName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", student.name.toLowerCase())
                    .contains(studentName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    /**
     * attendanceId는 생성 시점에 공백이 허용되지 않음
     */
    private BooleanExpression attendanceIdContains(String attendanceId) {
        if (StringUtils.hasText(attendanceId)) {
            return student.attendanceId.toLowerCase().contains(attendanceId.toLowerCase());
        }
        return null;
    }

    private BooleanExpression schoolNameContains(String schoolName) {
        if (StringUtils.hasText(schoolName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", student.school.name.toLowerCase())
                    .contains(schoolName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private BooleanExpression schoolGradeEq(Integer grade) {
        return grade != null ? student.school.grade.eq(grade) : null;
    }

    private BooleanExpression parentNameContains(String parentName) {
        if (StringUtils.hasText(parentName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", student.parent.name.toLowerCase())
                    .contains(parentName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }
}
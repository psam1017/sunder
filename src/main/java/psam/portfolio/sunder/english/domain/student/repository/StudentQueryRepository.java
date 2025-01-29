package psam.portfolio.sunder.english.domain.student.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import psam.portfolio.sunder.english.domain.student.model.request.StudentPageSearchCond;
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

    public List<Student> findAllByPageSearchCond(UUID academyId, StudentPageSearchCond cond) {

        OrderSpecifier<?> customOrder = specifyCustomOrder(cond);
        OrderSpecifier<String> defaultOrder = specifyDefaultOrder();
        OrderSpecifier<?>[] orders = customOrder != null ? new OrderSpecifier[]{customOrder, defaultOrder} : new OrderSpecifier[]{defaultOrder};

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
                .orderBy(orders)
                .offset(cond.getOffset())
                .limit(cond.getLimit())
                .fetch();
    }

    public long countByPageSearchCond(long contentSize, UUID academyId, StudentPageSearchCond cond) {
        if (cond.getSize() > contentSize) {
            if (cond.getOffset() == 0 || contentSize != 0) {
                return cond.getOffset() + contentSize;
            }
        }
        return this.countByPageSearchCondQuery(academyId, cond);
    }

    private long countByPageSearchCondQuery(UUID academyId, StudentPageSearchCond cond) {
        Long count = query.select(student.countDistinct())
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
                .fetchOne();
        return count == null ? 0L : count;
    }

    private static BooleanExpression academyIdEq(UUID academyId) {
        return academyId != null ? student.academy.id.eq(academyId) : null;
    }

    private static BooleanExpression addressContains(String address) {
        if (StringUtils.hasText(address)) {
            StringExpression concatAddress = student.address.street
                    .concat(student.address.detail)
                    .concat(student.address.postalCode)
                    .toLowerCase();

            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", concatAddress)
                    .contains(address.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static BooleanExpression statusEq(UserStatus status) {
        return status != null ? student.status.eq(status) : null;
    }

    private static BooleanExpression studentNameContains(String studentName) {
        if (StringUtils.hasText(studentName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", student.name.toLowerCase())
                    .contains(studentName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static BooleanExpression attendanceIdContains(String attendanceId) {
        if (StringUtils.hasText(attendanceId)) {
            return student.attendanceId.toLowerCase().contains(attendanceId.toLowerCase());
        }
        return null;
    }

    private static BooleanExpression schoolNameContains(String schoolName) {
        if (StringUtils.hasText(schoolName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", student.school.name.toLowerCase())
                    .contains(schoolName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static BooleanExpression schoolGradeEq(Integer grade) {
        return grade != null ? student.school.grade.eq(grade) : null;
    }

    private static BooleanExpression parentNameContains(String parentName) {
        if (StringUtils.hasText(parentName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", student.parent.name.toLowerCase())
                    .contains(parentName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static OrderSpecifier<?> specifyCustomOrder(StudentPageSearchCond cond) {
        String prop = cond.getProp();
        Order order = cond.getDir();

        return switch (prop) {
            case "name" -> new OrderSpecifier<>(order, student.name);
            case "status" -> new OrderSpecifier<>(order, student.status);
            case "schoolName" -> new OrderSpecifier<>(order, student.school.name);
            case "attendanceId" -> new OrderSpecifier<>(order, student.attendanceId);
            default -> null;
        };
    }

    private static OrderSpecifier<String> specifyDefaultOrder() {
        return student.attendanceId.asc().nullsLast();
    }

    public List<UUID> findAllIds(BooleanExpression... expressions) {
        return query.select(student.id)
                .from(student)
                .where(expressions)
                .fetch();
    }
}

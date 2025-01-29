package psam.portfolio.sunder.english.domain.teacher.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.exception.NoSuchTeacherException;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPageSearchCond;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.teacher.model.entity.QTeacher.teacher;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class TeacherQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Teacher getById(UUID uuid) {
        Teacher entity = em.find(Teacher.class, uuid);
        if (entity == null) {
            throw new NoSuchTeacherException();
        }
        return entity;
    }

    public Teacher getOne(BooleanExpression... expressions) {
        Teacher entity = query
                .select(teacher)
                .from(teacher)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchTeacherException();
        }
        return entity;
    }

    public Optional<Teacher> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(teacher)
                        .from(teacher)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<Teacher> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Teacher.class, uuid));
    }

    public List<Teacher> findAll(BooleanExpression... expressions) {
        return query.select(teacher)
                .from(teacher)
                .where(expressions)
                .fetch();
    }

    public List<Teacher> findAllByPageSearchCond(UUID academyId, TeacherPageSearchCond cond) {
        return query.selectDistinct(teacher)
                .from(teacher)
                .where(
                        academyIdEq(academyId),
                        teacherNameContains(cond.getTeacherName()),
                        userStatusEq(cond.getStatus())
                )
                .orderBy(specifiedOrder(cond))
                .fetch();
    }

    private static Predicate academyIdEq(UUID academyId) {
        return academyId != null ? teacher.academy.id.eq(academyId) : null;
    }

    private static BooleanExpression teacherNameContains(String teacherName) {
        if (StringUtils.hasText(teacherName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", teacher.name.toLowerCase())
                    .contains(teacherName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static BooleanExpression userStatusEq(UserStatus status) {
        return status != null ? teacher.status.eq(status) : null;
    }

    private OrderSpecifier<?> specifiedOrder(TeacherPageSearchCond cond) {
        String prop = cond.getProp();
        Order order = cond.getDir();

        return switch (prop) {
            case "name" -> new OrderSpecifier<>(order, teacher.name);
            case "status" -> new OrderSpecifier<>(order, teacher.status);
            default -> new OrderSpecifier<>(order, teacher.createdDateTime);
        };
    }

    public List<UUID> findAllIds(BooleanExpression... expressions) {
        return query.select(teacher.id)
                .from(teacher)
                .where(expressions)
                .fetch();
    }
}

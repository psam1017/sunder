package psam.portfolio.sunder.english.domain.teacher.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.exception.NoSuchTeacherException;

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
}

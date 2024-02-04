package psam.portfolio.sunder.english.web.student.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.student.model.entity.Student;
import psam.portfolio.sunder.english.web.student.exception.NoSuchStudentException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.web.student.model.entity.QStudent.*;

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
}

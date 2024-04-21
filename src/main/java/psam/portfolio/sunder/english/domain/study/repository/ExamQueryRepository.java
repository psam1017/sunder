package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchExamException;
import psam.portfolio.sunder.english.domain.study.model.entity.Exam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.study.model.entity.QExam.exam;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class ExamQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Exam getById(UUID uuid) {
        Exam entity = em.find(Exam.class, uuid);
        if (entity == null) {
            throw new NoSuchExamException();
        }
        return entity;
    }

    public Exam getOne(BooleanExpression... expressions) {
        Exam entity = query
                .select(exam)
                .from(exam)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchExamException();
        }
        return entity;
    }

    public Optional<Exam> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(exam)
                        .from(exam)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<Exam> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Exam.class, uuid));
    }

    public List<Exam> findAll(BooleanExpression... expressions) {
        return query.select(exam)
                .from(exam)
                .where(expressions)
                .fetch();
    }
}

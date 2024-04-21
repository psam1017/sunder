package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchExamException;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchExamWordException;
import psam.portfolio.sunder.english.domain.study.model.entity.Exam;
import psam.portfolio.sunder.english.domain.study.model.entity.ExamWord;
import psam.portfolio.sunder.english.domain.study.model.entity.QExamWord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.study.model.entity.QExam.exam;
import static psam.portfolio.sunder.english.domain.study.model.entity.QExamWord.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class ExamWordQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public ExamWord getById(UUID uuid) {
        ExamWord entity = em.find(ExamWord.class, uuid);
        if (entity == null) {
            throw new NoSuchExamWordException();
        }
        return entity;
    }

    public ExamWord getOne(BooleanExpression... expressions) {
        ExamWord entity = query
                .select(examWord)
                .from(examWord)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchExamWordException();
        }
        return entity;
    }

    public Optional<ExamWord> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(examWord)
                        .from(examWord)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<ExamWord> findById(UUID uuid) {
        return Optional.ofNullable(em.find(ExamWord.class, uuid));
    }

    public List<ExamWord> findAll(BooleanExpression... expressions) {
        return query.select(examWord)
                .from(examWord)
                .where(expressions)
                .fetch();
    }
}

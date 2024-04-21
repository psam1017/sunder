package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchPracticeException;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchPracticeWordException;
import psam.portfolio.sunder.english.domain.study.model.entity.Practice;
import psam.portfolio.sunder.english.domain.study.model.entity.PracticeWord;
import psam.portfolio.sunder.english.domain.study.model.entity.QPracticeWord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.study.model.entity.QPractice.practice;
import static psam.portfolio.sunder.english.domain.study.model.entity.QPracticeWord.practiceWord;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class PracticeWordQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public PracticeWord getById(UUID uuid) {
        PracticeWord entity = em.find(PracticeWord.class, uuid);
        if (entity == null) {
            throw new NoSuchPracticeWordException();
        }
        return entity;
    }

    public PracticeWord getOne(BooleanExpression... expressions) {
        PracticeWord entity = query
                .select(practiceWord)
                .from(practiceWord)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchPracticeWordException();
        }
        return entity;
    }

    public Optional<PracticeWord> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(practiceWord)
                        .from(practiceWord)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<PracticeWord> findById(UUID uuid) {
        return Optional.ofNullable(em.find(PracticeWord.class, uuid));
    }

    public List<PracticeWord> findAll(BooleanExpression... expressions) {
        return query.select(practiceWord)
                .from(practiceWord)
                .where(expressions)
                .fetch();
    }
}

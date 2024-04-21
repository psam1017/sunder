package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchPracticeException;
import psam.portfolio.sunder.english.domain.study.model.entity.ExamWord;
import psam.portfolio.sunder.english.domain.study.model.entity.Practice;
import psam.portfolio.sunder.english.domain.study.model.entity.QPractice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.study.model.entity.QExamWord.examWord;
import static psam.portfolio.sunder.english.domain.study.model.entity.QPractice.practice;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class PracticeQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Practice getById(UUID uuid) {
        Practice entity = em.find(Practice.class, uuid);
        if (entity == null) {
            throw new NoSuchPracticeException();
        }
        return entity;
    }

    public Practice getOne(BooleanExpression... expressions) {
        Practice entity = query
                .select(practice)
                .from(practice)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchPracticeException();
        }
        return entity;
    }

    public Optional<Practice> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(practice)
                        .from(practice)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<Practice> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Practice.class, uuid));
    }

    public List<Practice> findAll(BooleanExpression... expressions) {
        return query.select(practice)
                .from(practice)
                .where(expressions)
                .fetch();
    }
}

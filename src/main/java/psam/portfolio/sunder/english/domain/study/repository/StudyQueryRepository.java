package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchStudyException;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.study.model.entity.QStudy.study;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class StudyQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Study getById(UUID uuid) {
        Study entity = em.find(Study.class, uuid);
        if (entity == null) {
            throw new NoSuchStudyException();
        }
        return entity;
    }

    public Study getOne(BooleanExpression... expressions) {
        Study entity = query
                .select(study)
                .from(study)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchStudyException();
        }
        return entity;
    }

    public Optional<Study> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(study)
                        .from(study)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<Study> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Study.class, uuid));
    }

    public List<Study> findAll(BooleanExpression... expressions) {
        return query.select(study)
                .from(study)
                .where(expressions)
                .fetch();
    }
}

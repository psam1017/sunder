package psam.portfolio.sunder.english.domain.academy.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.exception.NoSuchAcademyShareException;
import psam.portfolio.sunder.english.domain.academy.model.entity.AcademyShare;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.academy.model.entity.QAcademyShare.academyShare;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class AcademyShareQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public AcademyShare getById(Long id) {
        AcademyShare entity = em.find(AcademyShare.class, id);
        if (entity == null) {
            throw new NoSuchAcademyShareException();
        }
        return entity;
    }

    public AcademyShare getOne(BooleanExpression... expressions) {
        AcademyShare entity = query.selectFrom(academyShare)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchAcademyShareException();
        }
        return entity;
    }

    public Optional<AcademyShare> findById(UUID uuid) {
        return Optional.ofNullable(
                em.find(AcademyShare.class, uuid)
        );
    }

    public Optional<AcademyShare> findOne(BooleanExpression... expression) {
        return Optional.ofNullable(
                query
                        .selectFrom(academyShare)
                        .where(expression)
                        .fetchOne()
        );
    }

    public List<AcademyShare> findAll(BooleanExpression... expression) {
        return query
                .selectFrom(academyShare)
                .where(expression)
                .fetch();
    }
}

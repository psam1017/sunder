package psam.portfolio.sunder.english.web.teacher.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.teacher.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.entity.QAcademy;
import psam.portfolio.sunder.english.web.teacher.exception.NoSuchAcademyException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.web.teacher.entity.QAcademy.*;

@RequiredArgsConstructor
@Transactional
@Repository
public class AcademyQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Academy getById(UUID uuid) {
        Academy entity = em.find(Academy.class, uuid);
        if (entity == null) {
            throw new NoSuchAcademyException();
        }
        return entity;
    }

    public Academy getOne(BooleanExpression... expressions) {
        Academy entity = query.selectFrom(academy)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchAcademyException();
        }
        return entity;
    }

    public Optional<Academy> findById(UUID uuid) {
        return Optional.ofNullable(
                em.find(Academy.class, uuid)
        );
    }

    public Optional<Academy> findOne(BooleanExpression... expression) {
        return Optional.ofNullable(
                query
                        .selectFrom(academy)
                        .where(expression)
                        .fetchOne()
        );
    }

    public List<Academy> findAll(BooleanExpression... expression) {
        return query
                .selectFrom(academy)
                .where(expression)
                .fetch();
    }
}

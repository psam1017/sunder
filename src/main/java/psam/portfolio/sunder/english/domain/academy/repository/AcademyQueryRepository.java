package psam.portfolio.sunder.english.domain.academy.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.exception.NoSuchAcademyException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPublicSearchCond;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.academy.model.entity.QAcademy.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    public List<Academy> findAllBySearchCond(AcademyPublicSearchCond cond) {
        return query.selectDistinct(academy)
                .from(academy)
                .where(
                        academyNameContains(cond.getAcademyName()),
                        openToPublicEq(true),
                        academyStatusEq(AcademyStatus.VERIFIED)
                )
                .orderBy(specifyOrder(cond))
                .offset(cond.getOffset())
                .limit(cond.getLimit())
                .fetch();
    }

    public long countBySearchCond(long contentSize, AcademyPublicSearchCond cond) {
        int size = cond.getSize();
        long offset = cond.getOffset();

        if (offset == 0) {
            if (size > contentSize) {
                return contentSize;
            }
            return this.countBySearchCondQuery(cond);
        }
        if (contentSize != 0 && size > contentSize) {
            return offset + contentSize;
        }
        return this.countBySearchCondQuery(cond);
    }

    private long countBySearchCondQuery(AcademyPublicSearchCond cond) {
        Long count = query.select(academy.countDistinct())
                .from(academy)
                .where(
                        academyNameContains(cond.getAcademyName()),
                        openToPublicEq(true),
                        academyStatusEq(AcademyStatus.VERIFIED)
                )
                .fetchOne();
        return count == null ? 0L : count;
    }

    private static BooleanExpression academyStatusEq(AcademyStatus status) {
        return academy.status.eq(status);
    }

    private static BooleanExpression openToPublicEq(Boolean openToPublic) {
        return openToPublic != null ? academy.openToPublic.eq(openToPublic) : null;
    }

    private BooleanExpression academyNameContains(String academyName) {
        if (StringUtils.hasText(academyName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", academy.name.toLowerCase())
                    .contains(academyName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private OrderSpecifier<?> specifyOrder(AcademyPublicSearchCond cond) {
        String prop = cond.getProp();
        Order order = cond.getDir();

        return switch (prop) {
            case "name" -> new OrderSpecifier<>(order, academy.name);
            default -> new OrderSpecifier<>(order, academy.createdDateTime);
        };
    }
}

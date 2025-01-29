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
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPublicPageSearchCond;

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

    public List<UUID> findAllIds(BooleanExpression... expression) {
        return query
                .select(academy.id)
                .from(academy)
                .where(expression)
                .fetch();
    }

    public List<Academy> findAllByPageSearchCond(AcademyPublicPageSearchCond cond) {
        return query.selectDistinct(academy)
                .from(academy)
                .where(
                        openToPublicEq(true),
                        academyStatusEq(AcademyStatus.VERIFIED),
                        academyNameContains(cond.getAcademyName()),
                        academyAddressContains(cond.getAcademyAddress())
                )
                .orderBy(specifyOrder(cond))
                .offset(cond.getOffset())
                .limit(cond.getLimit())
                .fetch();
    }

    public long countByPageSearchCond(long contentSize, AcademyPublicPageSearchCond cond) {
        if (cond.getSize() > contentSize) {
            if (cond.getOffset() == 0 || contentSize != 0) {
                return cond.getOffset() + contentSize;
            }
        }
        return this.countByPageSearchCondQuery(cond);
    }

    private long countByPageSearchCondQuery(AcademyPublicPageSearchCond cond) {
        Long count = query.select(academy.countDistinct())
                .from(academy)
                .where(
                        openToPublicEq(true),
                        academyStatusEq(AcademyStatus.VERIFIED),
                        academyNameContains(cond.getAcademyName()),
                        academyAddressContains(cond.getAcademyAddress())
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

    private static BooleanExpression academyNameContains(String academyName) {
        if (StringUtils.hasText(academyName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", academy.name.toLowerCase())
                    .contains(academyName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static BooleanExpression academyAddressContains(String academyAddress) {
        if (StringUtils.hasText(academyAddress)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", academy.address.street.concat(academy.address.detail).concat(academy.address.postalCode).toLowerCase())
                    .contains(academyAddress.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private OrderSpecifier<?> specifyOrder(AcademyPublicPageSearchCond cond) {
        String prop = cond.getProp();
        Order order = cond.getDir();

        return switch (prop) {
            case "name" -> new OrderSpecifier<>(order, academy.name);
            default -> new OrderSpecifier<>(order, academy.createdDateTime);
        };
    }

    public long countAll() {
        Long count = query.select(academy.count())
                .from(academy)
                .fetchOne();
        return count == null ? 0L : count;
    }
}

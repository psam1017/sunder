package psam.portfolio.sunder.english.web.academy.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.web.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.academy.exception.NoSuchAcademyException;
import psam.portfolio.sunder.english.web.academy.model.entity.Academy;
import psam.portfolio.sunder.english.web.academy.model.entity.QAcademy;
import psam.portfolio.sunder.english.web.academy.model.request.AcademyPublicSearchCond;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.web.academy.model.entity.QAcademy.*;

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

    public List<Academy> pageBySearchCond(AcademyPublicSearchCond cond) {
        return query.selectDistinct(academy)
                .from(academy)
                .where(
                        academyNameEq(cond.getAcademyName()),
                        openToPublicEq(true),
                        academyStatusEq(AcademyStatus.VERIFIED)
                )
                .orderBy(specifyOrder(cond))
                .offset(cond.getOffset())
                .limit(cond.getLimit())
                .fetch();
    }

    private OrderSpecifier<?> specifyOrder(AcademyPublicSearchCond cond) {
        String prop = cond.getProp();
        Order order = cond.getDir();

        if (prop.contains("name")) {
            return new OrderSpecifier<>(order, academy.name);
        }
        return new OrderSpecifier<>(order, academy.createdDateTime);
    }

    /**
     * referred to PageableExecutionUtils.getPage(List<T> content, Pageable pageable, LongSupplier totalSupplier);
     * count 쿼리를 생략할 수 있는 경우
     * 1. 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
     * 2. 마지막 페이지일 때(offset + 컨텐츠 사이즈)
     */
    public Long countBySearchCond(List<?> content, AcademyPublicSearchCond cond) {
        Integer page = cond.getPage();
        Integer size = cond.getSize();

        //noinspection ConstantValue
        boolean isPaged = page != null && size != null && page > 0 && size > 0;
        long offset = isPaged ? (long) (page - 1) * size : 0L;
        long contentSize = content.size();

        if (!isPaged || offset == 0) {
            if (!isPaged || size > contentSize) {
                return contentSize;
            }
            return this.countQuery(cond);
        }

        if (contentSize != 0 && size > contentSize) {
            return offset + contentSize;
        }
        return this.countQuery(cond);
    }

    private Long countQuery(AcademyPublicSearchCond cond) {
        Long count = query.select(academy.countDistinct())
                .from(academy)
                .where(
                        academyNameEq(cond.getAcademyName()),
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

    private BooleanExpression academyNameEq(String academyName) {
        return StringUtils.hasText(academyName) ? academy.name.containsIgnoreCase(academyName) : null;
    }
}

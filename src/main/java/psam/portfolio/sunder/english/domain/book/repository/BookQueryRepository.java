package psam.portfolio.sunder.english.domain.book.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.book.exception.NoSuchBookException;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.entity.QWord;
import psam.portfolio.sunder.english.domain.book.model.request.BookPageSearchCond;
import psam.portfolio.sunder.english.domain.book.model.response.BookFullResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.book.model.entity.QBook.book;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class BookQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Book getById(UUID uuid) {
        Book entity = em.find(Book.class, uuid);
        if (entity == null) {
            throw new NoSuchBookException();
        }
        return entity;
    }

    public Book getOne(BooleanExpression... expressions) {
        Book entity = query
                .select(book)
                .from(book)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchBookException();
        }
        return entity;
    }

    public Optional<Book> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Book.class, uuid));
    }

    public Optional<Book> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(book)
                        .from(book)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public List<Book> findAll(BooleanExpression... expressions) {
        return query.select(book)
                .from(book)
                .where(expressions)
                .fetch();
    }

    @SuppressWarnings("unused")
    public List<Book> findAllByPageSearchCond(UUID academyId, BookPageSearchCond cond) {
        return query.selectDistinct(book)
                .from(book)
                .where(
                        academyIdEqOrShared(academyId, cond.isShared()),
                        schoolGradeEq(cond.getSchoolGrade()),
                        createdDateTimeYearEq(cond.getYear()),
                        searchTextContains(cond.getSplitKeyword())
                )
                .orderBy(book.createdDateTime.desc())
                .offset(cond.getOffset())
                .limit(cond.getSize())
                .fetch();
    }

    public List<BookFullResponse> findAllDTOByPageSearchCond(UUID academyId, BookPageSearchCond cond) {
        QWord qWord = QWord.word;
        return query.select(Projections.constructor(BookFullResponse.class,
                        book.id,
                        book.publisher,
                        book.name,
                        book.chapter,
                        book.subject,
                        book.schoolGrade,
                        book.academy.id,
                        book.shared,
                        book.createdDateTime,
                        book.modifiedDateTime,
                        book.createdBy,
                        book.modifiedBy,
                        qWord.count().intValue().coalesce(0)
                ))
                .from(book)
                .leftJoin(book.words, qWord)
                .where(
                        academyIdEqOrShared(academyId, cond.isShared()),
                        schoolGradeEq(cond.getSchoolGrade()),
                        createdDateTimeYearEq(cond.getYear()),
                        searchTextContains(cond.getSplitKeyword())
                )
                .groupBy(book.id)
                .orderBy(book.createdDateTime.desc())
                .offset(cond.getOffset())
                .limit(cond.getSize())
                .fetch();
    }

    public long countByPageSearchCond(long contentSize, UUID academyId, BookPageSearchCond cond) {
        if (cond.getSize() > contentSize) {
            if (cond.getOffset() == 0 || contentSize != 0) {
                return cond.getOffset() + contentSize;
            }
        }
        return this.countByPageSearchCondQuery(academyId, cond);
    }

    private long countByPageSearchCondQuery(UUID academyId, BookPageSearchCond cond) {
        Long count = query.select(book.countDistinct())
                .from(book)
                .where(
                        academyIdEqOrShared(academyId, cond.isShared()),
                        schoolGradeEq(cond.getSchoolGrade()),
                        createdDateTimeYearEq(cond.getYear()),
                        searchTextContains(cond.getSplitKeyword())
                )
                .fetchOne();
        return count == null ? 0 : count;
    }

    private static BooleanExpression academyIdEqOrShared(UUID academyId, boolean shared) {
        BooleanExpression expression = book.academy.id.eq(academyId);
        return shared ? expression : expression
                .or(book.academy.id.isNull())
                .or(book.academy.academyShares.any().sharedAcademy.id.eq(academyId));
    }

    private BooleanExpression schoolGradeEq(Integer schoolGrade) {
        return schoolGrade == null ? null : book.schoolGrade.eq(schoolGrade);
    }

    private static BooleanExpression createdDateTimeYearEq(Integer year) {
        return year == null ? null : book.createdDateTime.year().eq(year);
    }

    private static BooleanExpression searchTextContains(String[] keywords) {
        if (keywords == null || keywords.length == 0) {
            return null;
        }
        return Arrays.stream(keywords)
                .map(book.searchText::contains)
                .reduce(BooleanExpression::and)
                .orElse(null);
    }
}

package psam.portfolio.sunder.english.domain.book.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.exception.NoSuchBookException;

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
}
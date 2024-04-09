package psam.portfolio.sunder.english.domain.book.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.domain.book.exception.NoSuchWordException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.book.model.entity.QWord.word;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class WordQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Word getById(UUID uuid) {
        Word entity = em.find(Word.class, uuid);
        if (entity == null) {
            throw new NoSuchWordException();
        }
        return entity;
    }

    public Word getOne(BooleanExpression... expressions) {
        Word entity = query
                .select(word)
                .from(word)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchWordException();
        }
        return entity;
    }

    public Optional<Word> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Word.class, uuid));
    }

    public Optional<Word> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(word)
                        .from(word)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public List<Word> findAll(BooleanExpression... expressions) {
        return query.select(word)
                .from(word)
                .where(expressions)
                .fetch();
    }
}
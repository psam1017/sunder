package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchStudyWordException;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;

import java.util.List;
import java.util.Optional;

import static psam.portfolio.sunder.english.domain.study.model.entity.QStudyWord.studyWord;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class StudyWordQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public StudyWord getById(Long id) {
        StudyWord entity = em.find(StudyWord.class, id);
        if (entity == null) {
            throw new NoSuchStudyWordException();
        }
        return entity;
    }

    public StudyWord getOne(BooleanExpression... expressions) {
        StudyWord entity = query
                .select(studyWord)
                .from(studyWord)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchStudyWordException();
        }
        return entity;
    }

    public Optional<StudyWord> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(studyWord)
                        .from(studyWord)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<StudyWord> findById(Long id) {
        return Optional.ofNullable(em.find(StudyWord.class, id));
    }

    public List<StudyWord> findAll(BooleanExpression... expressions) {
        return query.select(studyWord)
                .from(studyWord)
                .where(expressions)
                .fetch();
    }
}

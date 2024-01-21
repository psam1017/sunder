package psam.portfolio.sunder.english.web.student.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import psam.portfolio.sunder.english.web.student.entity.Student;
import psam.portfolio.sunder.english.web.student.entity.QStudent;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StudentQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Optional<Student> findById(long id) {
        return Optional.ofNullable(em.find(Student.class, id));
    }

    public Optional<Student> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(QStudent.student)
                        .from(QStudent.student)
                        .where(expressions)
                        .fetchOne()
        );
    }
    // TODO: 2024-01-13 getById, getOne, findList
}

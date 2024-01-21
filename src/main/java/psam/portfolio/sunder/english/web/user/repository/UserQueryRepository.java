package psam.portfolio.sunder.english.web.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import psam.portfolio.sunder.english.web.user.entity.User;
import psam.portfolio.sunder.english.web.user.exception.NoSuchUserException;
import psam.portfolio.sunder.english.web.user.entity.QUser;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public User getById(long id) {
        User entity = em.find(User.class, id);
        if (entity == null) {
            throw new NoSuchUserException();
        }
        return entity;
    }

    public User getOne(BooleanExpression... expressions) {
        User entity = query
                .select(QUser.user)
                .from(QUser.user)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchUserException();
        }
        return entity;
    }
}
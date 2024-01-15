package portfolio.sunder.web.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import portfolio.sunder.web.user.entity.User;
import portfolio.sunder.web.user.exception.NoSuchUserException;

import static portfolio.sunder.web.user.entity.QUser.user;

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
                .select(user)
                .from(user)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchUserException();
        }
        return entity;
    }
}
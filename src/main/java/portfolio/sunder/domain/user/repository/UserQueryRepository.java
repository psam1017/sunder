package portfolio.sunder.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import portfolio.sunder.domain.user.entity.User;
import portfolio.sunder.domain.user.exception.NoSuchUserException;

import static portfolio.sunder.domain.user.entity.QUser.user;

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
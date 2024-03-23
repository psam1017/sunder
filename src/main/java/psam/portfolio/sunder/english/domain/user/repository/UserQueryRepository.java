package psam.portfolio.sunder.english.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.user.exception.NoSuchUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class UserQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public User getById(UUID uuid) {
        User entity = em.find(User.class, uuid);
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

    public Optional<User> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(user)
                        .from(user)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<User> findById(UUID uuid) {
        return Optional.ofNullable(em.find(User.class, uuid));
    }

    public List<User> findAll(BooleanExpression... expressions) {
        return query.select(user)
                .from(user)
                .where(expressions)
                .fetch();
    }
}

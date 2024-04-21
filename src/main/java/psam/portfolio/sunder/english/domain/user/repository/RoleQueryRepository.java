package psam.portfolio.sunder.english.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.exception.NoSuchRoleException;
import psam.portfolio.sunder.english.domain.user.model.entity.Role;

import java.util.List;

import static psam.portfolio.sunder.english.domain.user.model.entity.QRole.role;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class RoleQueryRepository {

    private final JPAQueryFactory query;

    public Role getByName(RoleName roleName) {
        Role entity = query
                .selectFrom(role)
                .where(role.name.eq(roleName))
                .fetchOne();
        if (entity == null) {
            throw new NoSuchRoleException();
        }
        return entity;
    }

    public List<Role> findAll(BooleanExpression... expressions) {
        return query
                .selectFrom(role)
                .where(expressions)
                .fetch();
    }
}

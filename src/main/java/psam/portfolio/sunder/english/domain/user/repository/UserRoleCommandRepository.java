package psam.portfolio.sunder.english.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserRoleCommandRepository extends JpaRepository<UserRole, Long> {

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from UserRole ur
            where ur.user.id in :userIds
            """)
    void deleteAllByUserIdIn(List<UUID> userIds);
}

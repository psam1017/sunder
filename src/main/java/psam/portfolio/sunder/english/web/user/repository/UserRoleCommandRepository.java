package psam.portfolio.sunder.english.web.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.web.user.model.UserRole;

public interface UserRoleCommandRepository extends JpaRepository<UserRole, Long> {
}

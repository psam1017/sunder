package psam.portfolio.sunder.english.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.user.model.entity.Role;

public interface RoleCommandRepository extends JpaRepository<Role, Long> {
}

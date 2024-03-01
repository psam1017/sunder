package psam.portfolio.sunder.english.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.user.model.entity.User;

import java.util.UUID;

public interface UserCommandRepository extends JpaRepository<User, UUID> {
}

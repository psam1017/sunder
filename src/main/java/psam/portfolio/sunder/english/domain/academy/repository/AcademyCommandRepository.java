package psam.portfolio.sunder.english.domain.academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;

import java.util.UUID;

public interface AcademyCommandRepository extends JpaRepository<Academy, UUID> {
}

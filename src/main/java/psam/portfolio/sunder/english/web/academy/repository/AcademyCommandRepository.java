package psam.portfolio.sunder.english.web.academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.web.academy.model.entity.Academy;

import java.util.UUID;

public interface AcademyCommandRepository extends JpaRepository<Academy, UUID> {
}

package psam.portfolio.sunder.english.web.teacher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.web.teacher.model.Academy;

import java.util.UUID;

public interface AcademyCommandRepository extends JpaRepository<Academy, UUID> {
}

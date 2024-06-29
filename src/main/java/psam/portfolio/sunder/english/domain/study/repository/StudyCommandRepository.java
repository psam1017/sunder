package psam.portfolio.sunder.english.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;

import java.util.UUID;

public interface StudyCommandRepository extends JpaRepository<Study, UUID> {
}

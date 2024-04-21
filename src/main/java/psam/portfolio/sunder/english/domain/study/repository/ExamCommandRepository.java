package psam.portfolio.sunder.english.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.study.model.entity.Exam;

import java.util.UUID;

public interface ExamCommandRepository extends JpaRepository<Exam, UUID> {
}

package psam.portfolio.sunder.english.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.study.model.entity.ExamWord;

import java.util.UUID;

public interface PracticeCommandRepository extends JpaRepository<ExamWord, UUID> {
}

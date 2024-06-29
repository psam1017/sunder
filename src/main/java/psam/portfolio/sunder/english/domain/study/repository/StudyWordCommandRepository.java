package psam.portfolio.sunder.english.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;

import java.util.UUID;

public interface StudyWordCommandRepository extends JpaRepository<StudyWord, UUID> {
}

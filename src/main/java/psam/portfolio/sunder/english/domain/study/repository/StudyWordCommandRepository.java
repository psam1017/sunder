package psam.portfolio.sunder.english.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;

public interface StudyWordCommandRepository extends JpaRepository<StudyWord, Long> {
}

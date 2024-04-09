package psam.portfolio.sunder.english.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;

import java.util.UUID;

public interface WordCommandRepository extends JpaRepository<Word, UUID> {
}
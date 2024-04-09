package psam.portfolio.sunder.english.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;

import java.util.UUID;

public interface BookCommandRepository extends JpaRepository<Book, UUID> {
}
package psam.portfolio.sunder.english.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;

import java.util.List;
import java.util.UUID;

public interface BookCommandRepository extends JpaRepository<Book, UUID> {

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from Book b
            where b.academy.id in :academyIds
            """)
    void deleteAllByAcademyIdIn(List<UUID> academyIds);
}
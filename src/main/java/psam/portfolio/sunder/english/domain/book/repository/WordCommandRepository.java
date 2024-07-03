package psam.portfolio.sunder.english.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.book.model.enumeration.WordStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;

import java.util.UUID;

public interface WordCommandRepository extends JpaRepository<Word, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Word w
            set w.status = :status
            where w.book.id = :bookId
            """)
    void updateStatusByBookId(@Param("status") WordStatus status,
                              @Param("bookId") UUID bookId);
}
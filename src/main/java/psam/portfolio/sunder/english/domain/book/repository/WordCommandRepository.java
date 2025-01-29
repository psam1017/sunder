package psam.portfolio.sunder.english.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.book.enumeration.WordStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;

import java.util.List;
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

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from Word w
            where w.id in (
                select w.id
                from Word w
                join w.book b
                where b.academy.id in :academyIds
            )
            """)
    void deleteAllByAcademyIdIn(List<UUID> academyIds);
}
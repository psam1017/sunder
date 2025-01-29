package psam.portfolio.sunder.english.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;

import java.util.List;
import java.util.UUID;

public interface StudyWordCommandRepository extends JpaRepository<StudyWord, Long> {

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from StudyWord sw
            where sw.id in (
                select sw.id
                from StudyWord sw
                join sw.study s
                join s.student st
                where st.academy.id in :academyIds
            )
            """)
    void deleteAllByAcademyIdIn(List<UUID> academyIds);
}

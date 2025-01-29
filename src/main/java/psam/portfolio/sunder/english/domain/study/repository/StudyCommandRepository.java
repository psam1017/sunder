package psam.portfolio.sunder.english.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;

import java.util.List;
import java.util.UUID;

public interface StudyCommandRepository extends JpaRepository<Study, UUID> {

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from Study s
            where s.id in (
                select s.id
                from Study s
                join s.student st
                where st.academy.id in :academyIds
            )
            """)
    void deleteAllByAcademyIdIn(List<UUID> academyIds);
}

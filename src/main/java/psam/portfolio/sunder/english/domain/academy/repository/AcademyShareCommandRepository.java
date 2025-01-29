package psam.portfolio.sunder.english.domain.academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.AcademyShare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AcademyShareCommandRepository extends JpaRepository<AcademyShare, UUID> {

    @Modifying(flushAutomatically = true)
    @Query("""
            delete from AcademyShare s
            where s.sharedAcademy.id in :academyIds
            or s.sharingAcademy.id in :academyIds
            """)
    void deleteAllByAcademyIdIn(List<UUID> academyIds);
}

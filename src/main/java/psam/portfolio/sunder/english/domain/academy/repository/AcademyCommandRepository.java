package psam.portfolio.sunder.english.domain.academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AcademyCommandRepository extends JpaRepository<Academy, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("""
            delete from Academy c
            where c.status = :status
            and c.withdrawalAt <= :withdrawalAt
            """)
    void deleteAllByStatusAndWithdrawalAtBefore(@Param("status") AcademyStatus status,
                                                @Param("withdrawalAt") LocalDateTime withdrawalAt);
}

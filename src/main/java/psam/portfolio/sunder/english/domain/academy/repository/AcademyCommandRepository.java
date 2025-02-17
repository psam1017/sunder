package psam.portfolio.sunder.english.domain.academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AcademyCommandRepository extends JpaRepository<Academy, UUID> {

}

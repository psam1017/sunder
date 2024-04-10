package psam.portfolio.sunder.english.domain.teacher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;

import java.util.UUID;

public interface TeacherCommandRepository extends JpaRepository<Teacher, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("""
            update Teacher t
            set t.status = 'ACTIVE'
            where t.academy.id = :academyId
            """)
    void startActiveByAcademyId(@Param("academyId") UUID academyId);
}

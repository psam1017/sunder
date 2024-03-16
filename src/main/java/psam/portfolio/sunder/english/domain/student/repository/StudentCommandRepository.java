package psam.portfolio.sunder.english.domain.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;

import java.util.UUID;

public interface StudentCommandRepository extends JpaRepository<Student, Long> {

    @Modifying(clearAutomatically = true)
    @Query("""
           update Student s
           set s.status = 'ACTIVE'
           where s.academy.uuid = :academyId
           """)
    void startActiveByAcademyId(@Param("academyId") UUID academyId);
}

package psam.portfolio.sunder.english.domain.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;

import java.util.UUID;

public interface StudentCommandRepository extends JpaRepository<Student, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("""
            update Student s
            set s.status = 'ACTIVE'
            where s.academy.id = :academyId
            """)
    void startActiveByAcademyId(@Param("academyId") UUID academyId);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Student s
            set s.school.grade = s.school.grade + 1
            where s.school.grade < :grade
            """)
    void increaseGradeLessThen(@Param("grade") int grade);
}

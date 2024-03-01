package psam.portfolio.sunder.english.domain.teacher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;

import java.util.UUID;

public interface TeacherCommandRepository extends JpaRepository<Teacher, UUID> {
}

package psam.portfolio.sunder.english.web.teacher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;

import java.util.UUID;

public interface TeacherCommandRepository extends JpaRepository<Teacher, UUID> {
}

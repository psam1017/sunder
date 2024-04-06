package psam.portfolio.sunder.english.domain.student.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.exception.DuplicateAttendanceIdException;
import psam.portfolio.sunder.english.domain.student.model.entity.QStudent;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHInfo;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHStatus;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPOST;
import psam.portfolio.sunder.english.domain.student.repository.StudentCommandRepository;
import psam.portfolio.sunder.english.domain.student.repository.StudentQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.QTeacher;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.Role;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.repository.RoleQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserRoleCommandRepository;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.List;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.user.enumeration.UserStatus.*;
import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional
@Service
public class StudentCommandService {

    private final PasswordUtils passwordUtils;

    private final StudentCommandRepository studentCommandRepository;
    private final StudentQueryRepository studentQueryRepository;

    private final TeacherQueryRepository teacherQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserRoleCommandRepository userRoleCommandRepository;
    private final RoleQueryRepository roleQueryRepository;

    /**
     * 학생 등록 서비스
     *
     * @param teacherId 학원에 등록할 학생의 아이디
     * @param post      등록할 학생 정보
     * @return 등록에 성공한 학생 아이디
     */
    public UUID register(UUID teacherId, StudentPOST post) {

        // User 회원 정보 중복 체크
        userQueryRepository.findOne(
                user.loginId.eq(post.getLoginId()) // NotNull
                        .or(user.email.eq(post.getEmail())) // NotNull
                        .or(post.getPhone() != null ? user.phone.eq(post.getPhone()) : null), // nullable
                user.status.ne(PENDING),
                user.emailVerified.eq(true)
        ).ifPresent(user -> {
            throw new DuplicateUserException();
        });

        // Student 중복 체크
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Academy getAcademy = getTeacher.getAcademy();

        if (StringUtils.hasText(post.getAttendanceId())) {
            studentQueryRepository.findOne(
                    QStudent.student.academy.uuid.eq(getAcademy.getUuid()),
                    QStudent.student.attendanceId.eq(post.getAttendanceId())
            ).ifPresent(student -> {
                throw new DuplicateAttendanceIdException();
            });
        }

        // Student 등록
        List<Teacher> directors = teacherQueryRepository.findAll(
                QTeacher.teacher.academy.uuid.eq(getAcademy.getUuid()),
                QTeacher.teacher.status.in(ACTIVE, TRIAL),
                QTeacher.teacher.roles.any().role.name.eq(RoleName.ROLE_DIRECTOR)
        );
        UserStatus status = directors.get(0).getStatus();

        Student saveStudent = studentCommandRepository.save(post.toEntity(getAcademy, status, passwordUtils.encode(post.getLoginPw())));
        Role role = roleQueryRepository.getByName(RoleName.ROLE_STUDENT);
        userRoleCommandRepository.save(buildUserRole(saveStudent, role));

        return saveStudent.getUuid();
    }

    public UUID updateInfo(UUID userId, UUID studentId, StudentPATCHInfo patch) {
        return null;
    }

    public UserStatus changeStatus(UUID directorId, UUID studentId, StudentPATCHStatus patch) {
        return null;
    }

    private static UserRole buildUserRole(Student student, Role role) {
        return UserRole.builder()
                .user(student)
                .role(role)
                .build();
    }
}

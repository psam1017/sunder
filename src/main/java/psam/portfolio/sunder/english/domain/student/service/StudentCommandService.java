package psam.portfolio.sunder.english.domain.student.service;


import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.academy.exception.AcademyAccessDeniedException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
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
import psam.portfolio.sunder.english.domain.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.Role;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.repository.RoleQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserRoleCommandRepository;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;
import static psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus.ACTIVE;
import static psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus.TRIAL;

@RequiredArgsConstructor
@Transactional
@Service
public class StudentCommandService {

    private final PasswordUtils passwordUtils;

    private final StudentCommandRepository studentCommandRepository;
    private final StudentQueryRepository studentQueryRepository;

    private final AcademyQueryRepository academyQueryRepository;
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
                        .or(post.getEmail() != null ? user.email.eq(post.getEmail()) : null) // nullable
                        .or(post.getPhone() != null ? user.phone.eq(post.getPhone()) : null) // nullable
        ).ifPresent(user -> {
            throw new DuplicateUserException();
        });

        // Student 중복 체크
        Academy getAcademy = academyQueryRepository.getOne(
                QAcademy.academy.teachers.any().id.eq(teacherId)
        );

        if (StringUtils.hasText(post.getAttendanceId())) {
            studentQueryRepository.findOne(
                    QStudent.student.academy.id.eq(getAcademy.getId()),
                    QStudent.student.attendanceId.eq(post.getAttendanceId())
            ).ifPresent(student -> {
                throw new DuplicateAttendanceIdException();
            });
        }

        // Student 등록
        List<Teacher> directors = teacherQueryRepository.findAll(
                QTeacher.teacher.academy.id.eq(getAcademy.getId()),
                QTeacher.teacher.status.in(ACTIVE, TRIAL),
                QTeacher.teacher.roles.any().role.name.eq(RoleName.ROLE_DIRECTOR)
        );
        UserStatus status = directors.get(0).getStatus();

        Student saveStudent = studentCommandRepository.save(post.toEntity(getAcademy, status, passwordUtils.encode(post.getLoginPw())));
        Role role = roleQueryRepository.getByName(RoleName.ROLE_STUDENT);
        userRoleCommandRepository.save(buildUserRole(saveStudent, role));

        return saveStudent.getId();
    }

    /**
     * 학생 정보 수정 서비스
     *
     * @param teacherId 수정하는 선생님 아이디
     * @param studentId 수정할 학생 아이디
     * @param patch     수정할 학생 정보
     * @return 수정에 성공한 학생 아이디
     */
    public UUID updateInfo(UUID teacherId, UUID studentId, StudentPATCHInfo patch) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Student getStudent = studentQueryRepository.getById(studentId);
        if (!getTeacher.hasSameAcademy(getStudent)) {
            throw new AcademyAccessDeniedException();
        }

        // User 회원 정보 중복 체크
        if (StringUtils.hasText(patch.getPhone())) {
            userQueryRepository.findOne(user.phone.eq(patch.getPhone())).ifPresent(user -> {
                if (!Objects.equals(user.getId(), getStudent.getId())) {
                    throw new DuplicateUserException();
                }
            });
        }

        getStudent.setName(patch.getName());
        getStudent.setPhone(patch.getPhone());
        getStudent.setAddress(patch.getAddress());
        getStudent.setAttendanceId(patch.getAttendanceId());
        getStudent.setNote(patch.getNote());
        getStudent.setSchool(patch.getSchool());
        getStudent.setParent(patch.getParent());

        // 비밀번호는 값이 있을 때만 수정
        if (StringUtils.hasText(patch.getLoginPw())) {
            getStudent.setLoginPw(passwordUtils.encode(patch.getLoginPw()));
        }
        return getStudent.getId();
    }

    /**
     * 학생 상태 변경 서비스. 탈퇴 상태로 변경도 포함한다.
     *
     * @param teacherId 상태를 변경하는 선생님 아이디
     * @param studentId 상태를 변경할 학생 아이디
     * @param patch     변경할 상태 - 가능한 값 : PENDING, ACTIVE, WITHDRAWN
     * @return 학생 아이디와 변경된 상태
     */
    public UserStatus changeStatus(UUID teacherId, UUID studentId, StudentPATCHStatus patch) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Student getStudent = studentQueryRepository.getById(studentId);
        if (!getTeacher.hasSameAcademy(getStudent)) {
            throw new AcademyAccessDeniedException();
        }

        getStudent.changeStatus(patch.getStatus());
        return getStudent.getStatus();
    }

    private static UserRole buildUserRole(User user, Role role) {
        return UserRole.builder()
                .user(user)
                .role(role)
                .build();
    }
}

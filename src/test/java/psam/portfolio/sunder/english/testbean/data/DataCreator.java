package psam.portfolio.sunder.english.testbean.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.repository.StudentCommandRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.QRole;
import psam.portfolio.sunder.english.domain.user.model.entity.Role;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.repository.RoleCommandRepository;
import psam.portfolio.sunder.english.domain.user.repository.RoleQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserRoleCommandRepository;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.testbean.container.InfoContainer;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataCreator {

    private final InfoContainer uic;
    private final PasswordUtils passwordUtils;

    private final RoleCommandRepository roleCommandRepository;
    private final RoleQueryRepository roleQueryRepository;
    private final UserRoleCommandRepository userRoleCommandRepository;
    private final StudentCommandRepository studentCommandRepository;
    private final TeacherCommandRepository teacherCommandRepository;
    private final AcademyCommandRepository academyCommandRepository;

    public void createAllRoles() {
        RoleName[] roles = RoleName.values();
        for (RoleName rn : roles) {
            Role saveRole = Role.builder()
                    .name(rn)
                    .build();
            roleCommandRepository.save(saveRole);
        }
    }

    public Academy registerAcademy(AcademyStatus status) {
        Academy academy = Academy.builder()
                .name(uic.getUniqueAcademyName())
                .address(uic.getAnyAddress())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .openToPublic(true)
                .status(status)
                .build();
        return academyCommandRepository.save(academy);
    }

    public Academy registerAcademy(boolean openToPublic, AcademyStatus status) {
        Academy academy = Academy.builder()
                .name(uic.getUniqueAcademyName())
                .address(uic.getAnyAddress())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .openToPublic(openToPublic)
                .status(status)
                .build();
        return academyCommandRepository.save(academy);
    }

    public Teacher registerTeacher(UserStatus status, Academy academy) {
        String uniqueId = uic.getUniqueLoginId();
        Teacher teacher = Teacher.builder()
                .loginId(uniqueId)
                .loginPw(passwordUtils.encode(uic.getRawPassword()))
                .name("사용자" + uniqueId.substring(0, 3))
                .email(uic.getUniqueEmail())
                .emailVerified(true)
                .phone(uic.getUniquePhoneNumber())
                .address(uic.getAnyAddress())
                .status(status)
                .academy(academy)
                .build();
        return teacherCommandRepository.save(teacher);
    }

    public Teacher registerTeacher(String name, UserStatus status, Academy academy) {
        String uniqueId = uic.getUniqueLoginId();
        Teacher teacher = Teacher.builder()
                .loginId(uniqueId)
                .loginPw(passwordUtils.encode(uic.getRawPassword()))
                .name(name)
                .email(uic.getUniqueEmail())
                .emailVerified(true)
                .phone(uic.getUniquePhoneNumber())
                .address(uic.getAnyAddress())
                .status(status)
                .academy(academy)
                .build();
        return teacherCommandRepository.save(teacher);
    }

    public Student registerStudent(UserStatus status, Academy academy) {
        String uniqueId = uic.getUniqueLoginId();
        Student student = Student.builder()
                .loginId(uniqueId)
                .loginPw(passwordUtils.encode(uic.getRawPassword()))
                .name("사용자" + uniqueId.substring(0, 3))
                .email(uic.getUniqueEmail())
                .emailVerified(true)
                .phone(uic.getUniquePhoneNumber())
                .attendanceId(uic.getUniqueAttendanceId())
                .note("note about student")
                .address(uic.getAnyAddress())
                .school(uic.getAnySchool())
                .parent(uic.getAnyParent())
                .status(status)
                .academy(academy)
                .build();
        return studentCommandRepository.save(student);
    }

    public List<UserRole> createUserRoles(User user, RoleName... roleNames) {
        List<Role> roles = roleQueryRepository.findAll(
                QRole.role.name.in(roleNames)
        );
        List<UserRole> userRoles = new ArrayList<>();
        for (Role r : roles) {
            UserRole userRole = UserRole.builder().user(user).role(r).build();
            userRoles.add(userRole);
        }
        return userRoleCommandRepository.saveAll(userRoles);
    }
}

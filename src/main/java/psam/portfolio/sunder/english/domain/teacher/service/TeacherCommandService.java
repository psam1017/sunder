package psam.portfolio.sunder.english.domain.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.exception.SelfRoleModificationException;
import psam.portfolio.sunder.english.domain.teacher.exception.RoleDirectorRequiredException;
import psam.portfolio.sunder.english.domain.teacher.model.entity.QTeacher;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHInfo;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHStatus;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPUTRoles;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
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

import java.util.*;

import static psam.portfolio.sunder.english.domain.user.enumeration.UserStatus.*;
import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional
@Service
public class TeacherCommandService {

    private final PasswordUtils passwordUtils;

    private final TeacherCommandRepository teacherCommandRepository;
    private final TeacherQueryRepository teacherQueryRepository;

    private final UserQueryRepository userQueryRepository;
    private final UserRoleCommandRepository userRoleCommandRepository;
    private final RoleQueryRepository roleQueryRepository;

    /**
     * 선생님 등록 서비스
     *
     * @param teacherId   학원에 등록할 선생의 아이디
     * @param teacherPOST 등록할 선생님 정보
     * @return 등록에 성공한 선생님 아이디
     */
    public UUID register(UUID teacherId, TeacherPOST teacherPOST) {

        userQueryRepository.findOne(
                user.loginId.eq(teacherPOST.getLoginId()) // NotNull
                        .or(user.email.eq(teacherPOST.getEmail())) // NotNull
                        .or(teacherPOST.getPhone() != null ? user.phone.eq(teacherPOST.getPhone()) : null), // nullable
                user.status.ne(PENDING),
                user.emailVerified.eq(true)
        ).ifPresent(user -> {
            throw new DuplicateUserException();
        });

        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Academy getAcademy = getTeacher.getAcademy();
        List<Teacher> directors = teacherQueryRepository.findAll(
                QTeacher.teacher.academy.uuid.eq(getAcademy.getUuid()),
                QTeacher.teacher.status.in(ACTIVE, TRIAL),
                QTeacher.teacher.roles.any().role.name.eq(RoleName.ROLE_DIRECTOR)
        );

        Teacher buildTeacher = teacherCommandRepository.save(teacherPOST.toEntity(getAcademy, directors.get(0).getStatus(), passwordUtils.encode(teacherPOST.getLoginPw())));
        Role role = roleQueryRepository.getByName(RoleName.ROLE_TEACHER);
        userRoleCommandRepository.save(buildUserRole(buildTeacher, role));

        return buildTeacher.getUuid();
    }

    /**
     * 선생님 상태 변경 서비스. 탈퇴 상태로 변경도 포함한다.
     *
     * @param directorId 학원장 아이디
     * @param teacherId  상태를 변경할 선생님 아이디
     * @param patch      변경할 상태 - 가능한 값 : PENDING, ACTIVE, WITHDRAWN
     * @return 선생님 아이디와 변경된 상태
     */
    public UserStatus changeStatus(UUID directorId, UUID teacherId, TeacherPATCHStatus patch) {
        Teacher getDirector = teacherQueryRepository.getById(directorId);
        if (!getDirector.isDirector()) {
            throw new RoleDirectorRequiredException();
        }
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        getTeacher.changeStatus(patch.getStatus());
        return getTeacher.getStatus();
    }

    /**
     * 선생님 권한 변경 서비스
     *
     * @param directorId 학원장 아이디
     * @param teacherId  권한을 변경할 선생님 아이디
     * @param put        변경할 권한 - 가능한 값 : ROLE_TEACHER, ROLE_DIRECTOR
     * @return 선생님 아이디와 변경 완료된 권한 목록
     */
    public Set<RoleName> changeRoles(UUID directorId, UUID teacherId, TeacherPUTRoles put) {
        if (Objects.equals(directorId, teacherId)) {
            throw new SelfRoleModificationException();
        }

        Set<RoleName> requestRoleNames = new HashSet<>(put.getRoles());

        Teacher getDirector = teacherQueryRepository.getById(directorId);
        if (!getDirector.isDirector()) {
            throw new RoleDirectorRequiredException();
        }

        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Set<UserRole> userRoles = getTeacher.getRoles();

        // 요청 권한에 없는 현재 권한을 삭제
        Set<UserRole> deleteRoles = new HashSet<>(userRoles);
        userRoles.forEach(ur -> {
            Set<RoleName> roles = put.getRoles();
            if (roles.stream().noneMatch(rn -> Objects.equals(ur.getRoleName(), rn))) {
                deleteRoles.add(ur);
                roles.remove(ur.getRoleName());
            }
        });
        userRoleCommandRepository.deleteAll(deleteRoles);
        userRoles.removeAll(deleteRoles);

        // 현재 권한에 없는 새로운 권한을 추가
        put.getRoles().forEach(rn -> {
            if (userRoles.stream().noneMatch(ur -> Objects.equals(ur.getRoleName(), rn))) {
                Role getRole = roleQueryRepository.getByName(rn);
                UserRole saveUserRole = userRoleCommandRepository.save(buildUserRole(getTeacher, getRole));
                userRoles.add(saveUserRole);
            }
        });

        return requestRoleNames;
    }

    /**
     * 선생님 개인정보 변경 서비스
     *
     * @param teacherId 선생님 아이디
     * @param patch     변경할 개인정보
     * @return 개인정보 변경 완료된 선생님 아이디
     */
    public UUID updateInfo(UUID teacherId, TeacherPATCHInfo patch) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        getTeacher.setName(patch.getName());
        getTeacher.setEmail(patch.getEmail());
        getTeacher.setPhone(patch.getPhone());
        getTeacher.setAddress(patch.getAddress());
        return getTeacher.getUuid();
    }

    private static UserRole buildUserRole(Teacher teacher, Role role) {
        return UserRole.builder()
                .user(teacher)
                .role(role)
                .build();
    }
}

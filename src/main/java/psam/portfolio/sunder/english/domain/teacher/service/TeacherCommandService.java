package psam.portfolio.sunder.english.domain.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import psam.portfolio.sunder.english.domain.academy.exception.AcademyAccessDeniedException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.exception.SelfRoleModificationException;
import psam.portfolio.sunder.english.domain.teacher.exception.RoleDirectorRequiredException;
import psam.portfolio.sunder.english.domain.teacher.exception.SelfStatusModificationException;
import psam.portfolio.sunder.english.domain.teacher.model.entity.QTeacher;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHInfo;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPATCHStatus;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPOST;
import psam.portfolio.sunder.english.domain.teacher.model.request.TeacherPUTRoles;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.exception.TrialCannotChangeException;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.exception.DuplicateUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.Role;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.repository.RoleQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserRoleCommandRepository;
import psam.portfolio.sunder.english.infrastructure.mail.MailFailException;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.*;

import static psam.portfolio.sunder.english.domain.user.enumeration.RoleName.*;
import static psam.portfolio.sunder.english.domain.user.enumeration.UserStatus.*;
import static psam.portfolio.sunder.english.domain.user.model.entity.QUser.user;

@RequiredArgsConstructor
@Transactional
@Service
public class TeacherCommandService {

    private final PasswordUtils passwordUtils;
    private final MailUtils mailUtils;
    private final MessageSource messageSource;
    private final TemplateEngine templateEngine;

    private final TeacherCommandRepository teacherCommandRepository;
    private final TeacherQueryRepository teacherQueryRepository;

    private final UserQueryRepository userQueryRepository;
    private final UserRoleCommandRepository userRoleCommandRepository;
    private final RoleQueryRepository roleQueryRepository;

    /**
     * 선생님 등록 서비스
     * 등록 직후는 PENDING 상태이며, 본인인증을 통해 ACTIVE 상태로 변경할 수 있다.
     *
     * @param teacherId   학원에 등록할 선생의 아이디
     * @param teacherPOST 등록할 선생님 정보
     * @return 등록에 성공한 선생님 아이디
     */
    public UUID register(UUID teacherId, TeacherPOST teacherPOST) {

        // User 회원 정보 중복 체크
        userQueryRepository.findOne(
                user.loginId.eq(teacherPOST.getLoginId()) // NotNull
                        .or(user.email.eq(teacherPOST.getEmail())) // NotNull
                        .or(teacherPOST.getPhone() != null ? user.phone.eq(teacherPOST.getPhone()) : null) // nullable
        ).ifPresent(user -> {
            throw new DuplicateUserException();
        });

        // Teacher 등록
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Academy getAcademy = getTeacher.getAcademy();
        Teacher saveTeacher = teacherCommandRepository.save(teacherPOST.toEntity(getAcademy, PENDING, passwordUtils.encode(teacherPOST.getLoginPw())));
        Role role = roleQueryRepository.getByName(ROLE_TEACHER);
        userRoleCommandRepository.save(buildUserRole(saveTeacher, role));

        // 본인인증 링크를 이메일로 전송
        boolean mailResult = mailUtils.sendMail(
                saveTeacher.getEmail(),
                messageSource.getMessage("mail.verification.teacher.subject", null, Locale.getDefault()),
                setVerificationMailText(saveTeacher)
        );
        if (!mailResult) {
            throw new MailFailException();
        }

        return saveTeacher.getId();
    }

    /**
     * 선생님 가입 승인 서비스
     * 승인 이후의 상태는 학원장의 상태를 따라간다.
     *
     * @param teacherId 인증할 선생님 아이디
     * @return 선생님 가입 승인 여부
     */
    public boolean verifyTeacher(UUID teacherId) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        List<Teacher> directors = teacherQueryRepository.findAll(
                QTeacher.teacher.academy.teachers.any().id.eq(teacherId),
                QTeacher.teacher.status.in(ACTIVE, TRIAL),
                QTeacher.teacher.roles.any().role.name.eq(ROLE_DIRECTOR)
        );
        UserStatus status = directors.get(0).getStatus();

        getTeacher.changeStatus(status);
        return true;
    }

    /**
     * 선생님 상태 변경 서비스. 탈퇴 상태로 변경도 포함한다.
     *
     * @param directorId 학원장 아이디
     * @param teacherId  상태를 변경할 선생님 아이디
     * @param patch      변경할 상태 - 가능한 값 : ACTIVE, WITHDRAWN
     * @return 선생님 아이디와 변경된 상태
     */
    public UserStatus changeStatus(UUID directorId, UUID teacherId, TeacherPATCHStatus patch) {
        Teacher getDirector = teacherQueryRepository.getById(directorId);
        if (!getDirector.isDirector()) {
            throw new RoleDirectorRequiredException();
        }

        if (getDirector.isTrial() || getDirector.isTrialEnd()) {
            throw new TrialCannotChangeException();
        }

        if (Objects.equals(directorId, teacherId)) {
            throw new SelfStatusModificationException();
        }

        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        if (!getDirector.hasSameAcademy(getTeacher)) {
            throw new AcademyAccessDeniedException();
        }

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
        // 자기 자신의 권한 변경 불가
        if (Objects.equals(directorId, teacherId)) {
            throw new SelfRoleModificationException();
        }

        // 학원장만 권한 변경 가능
        Teacher getDirector = teacherQueryRepository.getById(directorId);
        if (!getDirector.isDirector()) {
            throw new RoleDirectorRequiredException();
        }

        // 다른 학원 선생님의 권한 변경 불가
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        if (!getDirector.hasSameAcademy(getTeacher)) {
            throw new AcademyAccessDeniedException();
        }

        // 기존 권한 삭제 후 새로운 권한 추가
        Set<UserRole> userRoles = getTeacher.getRoles();

        userRoleCommandRepository.deleteAll(userRoles);
        userRoles.clear();

        put.getRoles().forEach(rn -> {
            Role getRole = roleQueryRepository.getByName(rn);
            UserRole saveUserRole = userRoleCommandRepository.save(buildUserRole(getTeacher, getRole));
            userRoles.add(saveUserRole);
        });
        return put.getRoles();
    }

    /**
     * 선생님 개인정보 수정 서비스. 자기 자신의 정보만 수정할 수 있다.
     *
     * @param teacherId 선생님 아이디
     * @param patch     변경할 개인정보
     * @return 개인정보 변경 완료된 선생님 아이디
     */
    public UUID updateInfo(UUID teacherId, TeacherPATCHInfo patch) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);

        // User 회원 정보 중복 체크. 단, 자기 자신은 중복에서 제외
        if (StringUtils.hasText(patch.getPhone())) {
            userQueryRepository.findOne(
                    user.phone.eq(patch.getPhone()),
                    user.id.ne(getTeacher.getId())
            ).ifPresent(user -> {
                throw new DuplicateUserException();
            });
        }


        getTeacher.setName(patch.getName());
        getTeacher.setPhone(patch.getPhone());
        getTeacher.setAddress(patch.getAddress());
        return getTeacher.getId();
    }

    private static UserRole buildUserRole(User user, Role role) {
        return UserRole.builder()
                .user(user)
                .role(role)
                .build();
    }

    private String setVerificationMailText(Teacher teacher) {
        String url = messageSource.getMessage("mail.verification.teacher.url", new Object[]{teacher.getId()}, Locale.getDefault());

        Context context = new Context();
        context.setVariable("url", url);
        return templateEngine.process("mail-verification", context);
    }
}

package psam.portfolio.sunder.english.web.academy.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.global.pagination.PageInfo;
import psam.portfolio.sunder.english.web.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.academy.exception.OneParamToCheckAcademyDuplException;
import psam.portfolio.sunder.english.web.academy.model.entity.Academy;
import psam.portfolio.sunder.english.web.academy.model.entity.QAcademy;
import psam.portfolio.sunder.english.web.academy.model.request.AcademyPublicSearchCond;
import psam.portfolio.sunder.english.web.academy.model.response.AcademyFullResponse;
import psam.portfolio.sunder.english.web.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.student.model.entity.Student;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.teacher.model.response.TeacherFullResponse;
import psam.portfolio.sunder.english.web.teacher.model.response.TeacherPublicResponse;
import psam.portfolio.sunder.english.web.user.exception.NotAUserException;
import psam.portfolio.sunder.english.web.user.model.entity.User;
import psam.portfolio.sunder.english.web.user.repository.UserQueryRepository;

import java.util.*;

import static psam.portfolio.sunder.english.web.academy.model.entity.QAcademy.*;
import static psam.portfolio.sunder.english.web.user.enumeration.RoleName.ROLE_DIRECTOR;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AcademyQueryService {

    private final AcademyQueryRepository academyQueryRepository;
    private final UserQueryRepository userQueryRepository;

    /**
     * 학원 등록 시 중복 체크 서비스
     *
     * @param name  학원 이름
     * @param phone 학원 전화번호
     * @param email 학원 이메일
     * @return 중복 여부
     */
    public boolean checkDuplication(String name, String phone, String email) {
        boolean hasName = StringUtils.hasText(name);
        boolean hasPhone = StringUtils.hasText(phone);
        boolean hasEmail = StringUtils.hasText(email);

        if (!hasOnlyOne(hasName, hasPhone, hasEmail)) {
            throw new OneParamToCheckAcademyDuplException();
        }

        Optional<Academy> optAcademy = Optional.empty();
        if (hasName) {
            optAcademy = academyQueryRepository.findOne(
                    academy.name.eq(name),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        } else if (hasPhone) {
            optAcademy = academyQueryRepository.findOne(
                    academy.phone.eq(phone),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        } else if (hasEmail) {
            optAcademy = academyQueryRepository.findOne(
                    academy.email.eq(email),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        }
        return optAcademy.isEmpty();
    }

    private static boolean hasOnlyOne(boolean a, boolean b, boolean c) {
        return a ^ b ^ c && !(a && b && c);
    }

    /**
     * 학원 상세 정보 조회 서비스. 학원에 소속된 사용자가 자기 학원의 정보만 조회할 수 있다.
     *
     * @param userId 조회할 사용자 아이디
     * @param select 같이 조회할 정보 = {teacher}
     * @return 학원 상세 정보 + (선생 목록)
     * @apiNote 학생이 요청할 때와 선생이 요청할 때 응답스펙이 다르다.
     */
    public Map<String, Object> getDetail(UUID userId, String select) {

        // 사용자가 자기 학원을 조회
        User getUser = userQueryRepository.getById(userId);
        User entityUser = Hibernate.unproxy(getUser, User.class);

        Academy getAcademy;
        if (entityUser instanceof Teacher t) {
            getAcademy = t.getAcademy();
        } else if (entityUser instanceof Student s) {
            getAcademy = s.getAcademy();
        } else {
            throw new NotAUserException();
        }

        // 응답값 구성
        Map<String, Object> response = new HashMap<>();

        // 학원 정보 조회
        AcademyFullResponse academyFullResponse = AcademyFullResponse.from(getAcademy);
        response.put("academy", academyFullResponse);

        // 선생 정보 추가 조회
        if (StringUtils.hasText(select)) {
            select = select.length() > 20 ? select.substring(0, 20).toLowerCase() : select.toLowerCase();

            // 정렬 순서 : 원장 > 상태 > 이름
            if (select.contains("teacher")) {
                if (entityUser instanceof Teacher) {
                    List<TeacherFullResponse> teacherFullResponses = getAcademy.getTeachers().stream()
                            .map(TeacherFullResponse::from)
                            .sorted(Comparator.comparing((TeacherFullResponse t) ->
                                    t.getRoles().stream().anyMatch(r -> r == ROLE_DIRECTOR) ? 0 : 1
                            ).thenComparing((TeacherFullResponse t) ->
                                    switch (t.getStatus()) {
                                        case ACTIVE -> 0;
                                        case TRIAL -> 1;
                                        case PENDING -> 2;
                                        case WITHDRAWN -> 3;
                                        case FORBIDDEN -> 4;
                                        case TRIAL_END -> 5;
                                    }
                            ).thenComparing(TeacherFullResponse::getName))
                            .toList();
                    response.put("teachers", teacherFullResponses);
                } else {
                    List<TeacherPublicResponse> teacherPublicResponses = getAcademy.getTeachers().stream()
                            .map(TeacherPublicResponse::from)
                            .sorted(Comparator.comparing((TeacherPublicResponse t) ->
                                    t.getRoles().stream().anyMatch(r -> r == ROLE_DIRECTOR) ? 0 : 1
                            ).thenComparing((TeacherPublicResponse t) ->
                                    switch (t.getStatus()) {
                                        case ACTIVE -> 0;
                                        case TRIAL -> 1;
                                        case PENDING -> 2;
                                        case WITHDRAWN -> 3;
                                        case FORBIDDEN -> 4;
                                        case TRIAL_END -> 5;
                                    }
                            ).thenComparing(TeacherPublicResponse::getName))
                            .toList();
                    response.put("teachers", teacherPublicResponses);
                }
            }
        }
        return response;
    }

    /**
     * 학원 목록 조회 서비스
     * 단, openToPublic 이 true 인 학원만 조회할 수 있다.
     *
     * @param cond 검색 조건
     * @return 학원 목록과 페이징 정보
     */
    public Map<String, Object> getPublicList(AcademyPublicSearchCond cond) {
        List<Academy> academies = academyQueryRepository.pageBySearchCond(cond);
        Long count = academyQueryRepository.countBySearchCond(academies, cond);
        List<AcademyFullResponse> responses = academies.stream().map(AcademyFullResponse::from).toList();
        PageInfo pageInfo = new PageInfo(cond.getPage(), cond.getSize(), count, 10);
        return Map.of(
                "academies", responses,
                "pageInfo", pageInfo
        );
    }
}

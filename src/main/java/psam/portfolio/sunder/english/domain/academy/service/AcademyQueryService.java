package psam.portfolio.sunder.english.domain.academy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.exception.AcademyAccessDeniedException;
import psam.portfolio.sunder.english.domain.academy.exception.OneParamToCheckAcademyDuplException;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.request.AcademyPublicPageSearchCond;
import psam.portfolio.sunder.english.domain.academy.model.response.AcademyFullResponse;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherFullResponse;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherPublicResponse;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.global.pagination.PageInfo;

import java.util.*;

import static psam.portfolio.sunder.english.domain.academy.model.entity.QAcademy.academy;
import static psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName.ROLE_DIRECTOR;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AcademyQueryService {

    private static final int PAGE_SET_AMOUNT = 10;

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
     * @param academyId 조회할 학원 아이디
     * @param userId    조회할 사용자 아이디
     * @param select    같이 조회할 정보 = {teacher}
     * @return 학원 상세 정보 + (선생님 목록)
     * @apiNote 학생이 요청할 때와 선생이 요청할 때 응답스펙이 다르다.
     */
    public Map<String, Object> getDetail(UUID academyId, UUID userId, String select) {
        
        Academy getAcademy = academyQueryRepository.getById(academyId);

        // 사용자의 소속 확인
        User getUser = userQueryRepository.getById(userId);
        if (!getAcademy.hasUser(getUser)) {
            throw new AcademyAccessDeniedException();
        }

        // 응답값 구성
        Map<String, Object> response = new HashMap<>();

        // 학원 정보 조회
        AcademyFullResponse academyFullResponse = AcademyFullResponse.from(getAcademy);
        response.put("academy", academyFullResponse);

        // 선생님 정보 추가 조회
        if (StringUtils.hasText(select)) {
            select = select.length() > 20 ? select.substring(0, 20).toLowerCase() : select.toLowerCase();

            // 정렬 순서 : 학원장 > 상태 > 이름
            if (select.contains("teacher")) {
                List<Teacher> sortedTeachers = getAcademy.getTeachers().stream()
                        .sorted(Comparator.comparing((Teacher t) ->
                                t.getRoles().stream().anyMatch(r -> r.getRoleName() == ROLE_DIRECTOR) ? 0 : 1
                        ).thenComparing((Teacher t) ->
                                switch (t.getStatus()) {
                                    case ACTIVE -> 0;
                                    case TRIAL -> 1;
                                    case PENDING -> 2;
                                    case WITHDRAWN -> 3;
                                    case FORBIDDEN -> 4;
                                    case TRIAL_END -> 5;
                                }
                        ).thenComparing(Teacher::getName))
                        .toList();
                if (getUser instanceof Teacher) {
                    List<TeacherFullResponse> teacherFullResponses = sortedTeachers.stream().map(TeacherFullResponse::from).toList();
                    response.put("teachers", teacherFullResponses);
                } else {
                    List<TeacherPublicResponse> teacherPublicResponses = sortedTeachers.stream().map(TeacherPublicResponse::from).toList();
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
    public Map<String, Object> getPublicList(AcademyPublicPageSearchCond cond) {
        List<Academy> academies = academyQueryRepository.findAllByPageSearchCond(cond);
        long count = academyQueryRepository.countByPageSearchCond(academies.size(), cond);

        return Map.of(
                "academies", academies.stream().map(AcademyFullResponse::from).toList(),
                "pageInfo", new PageInfo(cond.getPage(), cond.getSize(), count, PAGE_SET_AMOUNT)
        );
    }
}

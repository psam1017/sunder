package psam.portfolio.sunder.english.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.study.model.entity.QStudy;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.request.StudyStatisticSearchCond;
import psam.portfolio.sunder.english.domain.study.model.response.StudyFullResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudySlicingResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudyStatisticResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudyWordFullResponse;
import psam.portfolio.sunder.english.domain.study.repository.StudyQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherPublicResponse;
import psam.portfolio.sunder.english.domain.user.exception.NotAUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.global.slicing.SlicingInfo;

import java.time.LocalDateTime;
import java.util.*;

import static psam.portfolio.sunder.english.domain.study.model.response.StudyStatisticResponse.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StudyQueryService {

    private final StudyQueryRepository studyQueryRepository;
    private final UserQueryRepository userQueryRepository;

    /**
     * 학습 목록 조회 서비스
     * 학생이라면 자신의 학습 목록을 조회할 수 있다.
     * 선생님이라면 같은 학원 학생들의 학습 목록을 조회할 수 있다.
     *
     * @param userId 사용자 아이디
     * @param cond   학습 목록 조회 조건
     * @return 학습 목록과 슬라이싱 정보
     */
    public Map<String, Object> getStudyList(UUID userId, StudySlicingSearchCond cond) {

        // 올바른 사용자인지 확인하고 학습 목록을 가져온다.
        User getUser = userQueryRepository.getById(userId);
        List<StudySlicingResponse> studies = null;
        if (getUser instanceof Student s) {
            studies = studyQueryRepository.findAllBySlicingSearchCond(s, cond);
        } else if (getUser instanceof Teacher t) {
            studies = studyQueryRepository.findAllBySlicingSearchCond(t, cond);
        }
        if (studies == null) {
            throw new NotAUserException();
        }

        // 슬라이싱 : 만약 리스트(size + 1)가 요청한 사이즈(size)보다 크다면, hasNext 를 true 로 설정하고 마지막 요소를 제거한다.
        boolean hasNext = studies.size() > cond.getSize();
        if (hasNext) {
            studies.remove(studies.size() - 1);
        }
        long lastSequence = studies.size() == 0 ? 0 : studies.get(studies.size() - 1).getSequence();

        return Map.of(
                "slicingInfo", new SlicingInfo(cond.getSize(), lastSequence, hasNext),
                "studies", studies
        );
    }

    /**
     * 학습 상세 정보 조회 서비스
     * 학생이라면 자신의 학습 목록을 조회할 수 있다. 단, 아직 제출하지 않은 학습은 answer 를 조회할 수 없다.
     * 선생님이라면 같은 학원 학생들의 학습 목록을 조회할 수 있다.
     *
     * @param userId  사용자 아이디
     * @param studyId 조회할 학습 아이디
     * @return 학습 상세 정보
     */
    public Map<String, Object> getDetail(UUID userId, UUID studyId) {

        Map<String, Object> responseMap = new HashMap<>();

        // 올바른 사용자인지 확인하고 학습을 가져온다.
        User getUser = userQueryRepository.getById(userId);
        QStudy qStudy = QStudy.study;
        Study getStudy = null;
        boolean isTeacher = false;
        if (getUser instanceof Student s) {
            getStudy = studyQueryRepository.getOne(
                    qStudy.id.eq(studyId),
                    qStudy.student.id.eq(s.getId())
            );
        } else if (getUser instanceof Teacher t) {
            isTeacher = true;
            getStudy = studyQueryRepository.getOne(
                    qStudy.id.eq(studyId),
                    qStudy.student.academy.id.eq(t.getAcademy().getId())
            );
        }
        if (getStudy == null) {
            throw new NotAUserException();
        }

        // 제출되었거나, 연습이거나, 선생님이라면 정답을 볼 수 있다.
        boolean canSeeAnswer = getStudy.isSubmitted() || getStudy.isPractice() || isTeacher;

        // study, studyWords, student, (teacher) 를 responseMap 에 담아 반환한다.
        responseMap.put("study", StudyFullResponse.from(getStudy));
        responseMap.put("studyWords", getStudy.getStudyWords().stream().map(sw -> StudyWordFullResponse.from(sw, canSeeAnswer)).toList());
        responseMap.put("student", StudentFullResponse.from(getStudy.getStudent(), false));
        if (getStudy.getTeacher() != null) {
            responseMap.put("teacher", TeacherPublicResponse.from(getStudy.getTeacher()));
        }
        return responseMap;
    }

    public Map<String, Object> getStudyStatistic(UUID userId, StudyStatisticSearchCond cond) {
        User getUser = userQueryRepository.getById(userId);
        UUID academyId = null;
        if (getUser instanceof Student s) {
            cond.changeStudentId(s.getId());
            academyId = s.getAcademy().getId();
        } else if (getUser instanceof Teacher t) {
            academyId = t.getAcademy().getId();
        }
        return queryStudyStatistic(cond, academyId);
    }

    public Map<String, Object> queryStudyStatistic(StudyStatisticSearchCond cond, UUID academyId) {

        Map<String, Object> response = new HashMap<>();

        // 공통 응답값
        response.put("oldHomeworks", studyQueryRepository.findOldHomeworks(cond, academyId));

        // group 에 의해 값이 없다면 0 으로 채워준다.
        // status
        List<CountByStatus> statuses = studyQueryRepository.countByStatus(cond, academyId);
        for (StudyStatus e : StudyStatus.values()) {
            if (e == StudyStatus.DELETED) {
                continue;
            }
            if (statuses.stream().noneMatch(c -> c.getStatus() == e)) {
                statuses.add(new CountByStatus(e, 0L));
            }
        }
        response.put("statuses", statuses);

        // type
        List<CountByType> types = studyQueryRepository.countByType(cond, academyId);
        for (StudyType e : StudyType.values()) {
            if (types.stream().noneMatch(c -> c.getType() == e)) {
                types.add(new CountByType(e, 0L));
            }
        }
        response.put("types", types);


        // classifications
        List<CountByClassification> classifications = studyQueryRepository.countByClassification(cond, academyId);
        for (StudyClassification e : StudyClassification.values()) {
            if (classifications.stream().noneMatch(c -> c.getClassification() == e)) {
                classifications.add(new CountByClassification(e, 0L));
            }
        }
        response.put("classifications", classifications);

        // targets
        List<CountByTarget> targets = studyQueryRepository.countByTarget(cond, academyId);
        for (StudyTarget e : StudyTarget.values()) {
            if (targets.stream().noneMatch(c -> c.getTarget() == e)) {
                targets.add(new CountByTarget(e, 0L));
            }
        }
        response.put("targets", targets);

        // days
        List<CountByDay> countByDays = studyQueryRepository.countByDay(cond, academyId);
        LocalDateTime startDateTime = cond.getStartDateTime();
        LocalDateTime endDateTime = cond.getEndDateTime();
        while (startDateTime.isBefore(endDateTime)) {
            boolean dateEmpty = true;
            for (CountByDay c : countByDays) {
                if (c.getStudyDate().isEqual(startDateTime.toLocalDate())) {
                    dateEmpty = false;
                    break;
                }
            }
            if (dateEmpty) {
                countByDays.add(new CountByDay(startDateTime.getDayOfYear(), 0L, 0L, 0L));
            }
            startDateTime = startDateTime.plusDays(1);
        }
        countByDays.sort(Comparator.comparing(CountByDay::getStudyDate));
        response.put("days", countByDays);

        // 선생님 전용 응답값
        if (cond.getStudentId() == null) {
            response.put("bestAnswerRates", studyQueryRepository.findBestStudentsByAnswerRate(cond, academyId));
            response.put("worstAnswerRates", studyQueryRepository.findWorstStudentsByAnswerRate(cond, academyId));
            response.put("bestStudyCounts", studyQueryRepository.findBestStudentsByStudyCount(cond, academyId));
            response.put("worstStudyCounts", studyQueryRepository.findWorstStudentsByStudyCount(cond, academyId));
        }
        return response;
    }
}

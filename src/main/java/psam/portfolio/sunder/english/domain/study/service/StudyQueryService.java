package psam.portfolio.sunder.english.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;
import psam.portfolio.sunder.english.domain.study.model.entity.QStudy;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.request.StudyStatisticSearchCond;
import psam.portfolio.sunder.english.domain.study.model.response.StudyFullResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudySlicingResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudyWordFullResponse;
import psam.portfolio.sunder.english.domain.study.repository.StudyQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.model.response.TeacherPublicResponse;
import psam.portfolio.sunder.english.domain.user.exception.NotAUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.global.slicing.SlicingInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        if (cond.getLastSequence() == null) {
            long count = studyQueryRepository.countAll();
            cond.setLastSequence(count + 1);
        }

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
        response.put("statuses", studyQueryRepository.countByStatus(cond, academyId));
        response.put("types", studyQueryRepository.countByType(cond, academyId));
        response.put("classifications", studyQueryRepository.countByClassification(cond, academyId));
        response.put("targets", studyQueryRepository.countByTarget(cond, academyId));
        response.put("days", studyQueryRepository.countByDay(cond, academyId));
        response.put("oldHomeworks", studyQueryRepository.findOldHomeworks(cond, academyId));

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

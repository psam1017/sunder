package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchStudyException;
import psam.portfolio.sunder.english.domain.study.model.entity.QStudyWord;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.request.StudyStatisticSearchCond;
import psam.portfolio.sunder.english.domain.study.model.response.StudySlicingResponse;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.study.model.entity.QStudy.study;
import static psam.portfolio.sunder.english.domain.study.model.response.StudyStatisticResponse.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class StudyQueryRepository {

    private final static int TOP_STUDENT_LIMIT = 3;
    private final static int NON_SUBMIT_STUDENT_LIMIT = 30;

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Study getById(UUID uuid) {
        Study entity = em.find(Study.class, uuid);
        if (entity == null) {
            throw new NoSuchStudyException();
        }
        return entity;
    }

    public Study getOne(BooleanExpression... expressions) {
        Study entity = query
                .select(study)
                .from(study)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchStudyException();
        }
        return entity;
    }

    public Optional<Study> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(study)
                        .from(study)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<Study> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Study.class, uuid));
    }

    public List<Study> findAll(BooleanExpression... expressions) {
        return query.select(study)
                .from(study)
                .where(expressions)
                .fetch();
    }

    // native query 로 @SqlRestriction 무시하기
    public long findNextSequenceOfLastStudy() {
        Object lastSequence = em.createNativeQuery("""
                        SELECT MAX(s.sequence)
                        FROM studies s
                        """)
                .getSingleResult();
        return lastSequence instanceof Long ls ? ls + 1 : 1;
    }

    public List<StudySlicingResponse> findAllBySlicingSearchCond(Student student, StudySlicingSearchCond cond) {
        return findAllBySlicingSearchCondQuery(student, null, cond);
    }

    public List<StudySlicingResponse> findAllBySlicingSearchCond(Teacher teacher, StudySlicingSearchCond cond) {
        return findAllBySlicingSearchCondQuery(null, teacher, cond);
    }

    // lastSequence 의 비교 방향과 정렬 방향이 일치해야 한다.
    private List<StudySlicingResponse> findAllBySlicingSearchCondQuery(Student student, Teacher teacher, StudySlicingSearchCond cond) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        return query.select(Projections.constructor(StudySlicingResponse.class,
                        study.id,
                        study.sequence,
                        study.title,
                        study.status,
                        study.type,
                        study.classification,
                        study.target,
                        study.submitDateTime,
                        study.createdDateTime,
                        study.student.id.as("studentId"),
                        study.student.attendanceId.as("attendanceId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        qStudyWord.correct
                                .when(true).then(1)
                                .otherwise(0)
                                .sum().intValue().as("correctCount"),
                        qStudyWord.count().intValue().as("totalCount")
                ))
                .distinct()
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        sequenceLt(cond.getLastSequence()),
                        studentIdEq(student),
                        academyIdEqByTeacher(teacher),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime()),
                        studyStatusEq(cond.getStudyStatus()),
                        titleContains(cond.getSplitStudyWord()),
                        studentNameContains(cond.getStudentName()),
                        studentSchoolGradeEq(cond.getSchoolGrade())
                )
                .groupBy(study.id)
                .orderBy(study.sequence.desc())
                .limit(cond.getLimit())
                .fetch();
    }

    private static BooleanExpression sequenceLt(Long sequence) {
        return sequence == null ? null : study.sequence.lt(sequence);
    }

    private static BooleanExpression studentIdEq(Student student) {
        return student == null ? null : study.student.id.eq(student.getId());
    }

    private BooleanExpression academyIdEqByTeacher(Teacher teacher) {
        return teacher == null ? null : study.student.academy.id.eq(teacher.getAcademy().getId());
    }

    private static BooleanExpression createdDateTimeGoe(LocalDateTime createdDateTime) {
        return createdDateTime == null ? null : study.createdDateTime.goe(createdDateTime);
    }

    private static BooleanExpression createdDateTimeLoe(LocalDateTime createdDateTime) {
        return createdDateTime == null ? null : study.createdDateTime.loe(createdDateTime);
    }

    private BooleanExpression studyStatusEq(StudyStatus studyStatus) {
        return studyStatus == null ? null : study.status.eq(studyStatus);
    }

    private static BooleanExpression titleContains(String[] titles) {
        if (titles == null || titles.length == 0) {
            return null;
        }
        return Arrays.stream(titles)
                .map(t -> study.title.toLowerCase().contains(t))
                .reduce(BooleanExpression::or)
                .orElse(null);
    }

    private static BooleanExpression studentNameContains(String studentName) {
        if (StringUtils.hasText(studentName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", study.student.name.toLowerCase())
                    .contains(studentName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static BooleanExpression studentSchoolGradeEq(Integer schoolGrade) {
        return schoolGrade == null ? null : study.student.school.grade.eq(schoolGrade);
    }

    public List<CountByStatus> countByStatus(StudyStatisticSearchCond cond, UUID academyId) {
        return query.select(Projections.constructor(CountByStatus.class,
                        study.status,
                        study.id.count()
                ))
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.status)
                .fetch();
    }

    public List<CountByType> countByType(StudyStatisticSearchCond cond, UUID academyId) {
        return query.select(Projections.constructor(CountByType.class,
                        study.type,
                        study.id.count()
                ))
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.type)
                .fetch();
    }

    public List<CountByClassification> countByClassification(StudyStatisticSearchCond cond, UUID academyId) {
        return query.select(Projections.constructor(CountByClassification.class,
                        study.classification,
                        study.id.count()
                ))
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.classification)
                .fetch();
    }

    public List<CountByTarget> countByTarget(StudyStatisticSearchCond cond, UUID academyId) {
        return query.select(Projections.constructor(CountByTarget.class,
                        study.target,
                        study.id.count()
                ))
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.target)
                .fetch();
    }

    public List<CountByDay> countByDay(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Integer> dayOfYear = study.createdDateTime.dayOfYear();
        return query.select(Projections.constructor(CountByDay.class,
                        dayOfYear,
                        study.id.countDistinct().as("studyCount"),
                        qStudyWord.correct
                                .when(true).then(1)
                                .otherwise(0)
                                .sum().longValue().as("correctCount"),
                        qStudyWord.count().as("totalCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(dayOfYear)
                .orderBy(dayOfYear.asc())
                .fetch();
    }

    public List<OldHomework> findOldHomeworks(StudyStatisticSearchCond cond, UUID academyId) {
        return query.select(Projections.constructor(OldHomework.class,
                        study.id.as("studyId"),
                        study.title,
                        study.status,
                        study.createdDateTime,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade")
                ))
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime()),
                        studyStatusEq(StudyStatus.ASSIGNED)
                )
                .orderBy(study.createdDateTime.asc())
                .limit(NON_SUBMIT_STUDENT_LIMIT)
                .fetch();
    }

    public List<TopStudent> findBestStudentsByAnswerRate(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Double> correctPercent =
                qStudyWord.correct
                        .when(true).then(1)
                        .otherwise(0)
                        .sum()
                        .divide(qStudyWord.count()).doubleValue();
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.countDistinct().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(correctPercent.desc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    public List<TopStudent> findWorstStudentsByAnswerRate(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Double> correctPercent =
                qStudyWord.correct
                        .when(true).then(1)
                        .otherwise(0)
                        .sum()
                        .divide(qStudyWord.count()).doubleValue();
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.countDistinct().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(correctPercent.asc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    public List<TopStudent> findBestStudentsByStudyCount(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Double> correctPercent =
                qStudyWord.correct
                        .when(true).then(1)
                        .otherwise(0)
                        .sum()
                        .divide(qStudyWord.count()).doubleValue();
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.countDistinct().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(studyWordCount.desc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    public List<TopStudent> findWorstStudentsByStudyCount(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Double> correctPercent =
                qStudyWord.correct
                        .when(true).then(1)
                        .otherwise(0)
                        .sum()
                        .divide(qStudyWord.count()).doubleValue();
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.countDistinct().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(studyWordCount.asc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    private BooleanExpression academyIdEq(UUID academyId) {
        return academyId == null ? null : study.student.academy.id.eq(academyId);
    }

    private BooleanExpression studentIdEq(UUID studentId) {
        return studentId == null ? null : study.student.id.eq(studentId);
    }

    private BooleanExpression statusNe(StudyStatus status) {
        return status == null ? null : study.status.ne(status);
    }
}
